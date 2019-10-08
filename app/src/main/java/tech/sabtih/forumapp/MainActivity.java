package tech.sabtih.forumapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import tech.sabtih.forumapp.adapters.MyportalRecyclerViewAdapter;
import tech.sabtih.forumapp.listeners.OnListFragmentInteractionListener;
import tech.sabtih.forumapp.models.Newsitem;

public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Content().execute();
        rv = findViewById(R.id.mainlist);



    }

    @Override
    public void onListFragmentInteraction(Newsitem item) {

    }

    private class Content extends AsyncTask<Void, Void, ArrayList<Newsitem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progressDialog = new ProgressDialog(MainActivity.this);
          //  progressDialog.show();
        }

        @Override
        protected ArrayList<Newsitem> doInBackground(Void... voids) {
            try {
                ArrayList<Newsitem> newsl = new ArrayList<>();
                //Connect to the website
                Document document = Jsoup.connect("http://"+getString(R.string.url)+"/portal/").get();
                Element news= document.select("#recentNews").first();

                Elements newslist = news.children();

                for(Element n : newslist){
                    if(n.hasAttr("id")) {

                        int id = Integer.parseInt(n.attr("id"));
                        String date = n.select(".DateTime").text();
                        String title = n.select(".newsTitle").text();
                        String vl = n.select(".views").text().replace("(", "")
                                .replace("Views / ", "").replace("Likes)", "").replace(",","");
                        String maker = n.select(".username").first().text();
                        int views = Integer.parseInt(vl.split(" ")[0]);
                        int likes = Integer.parseInt(vl.split(" ")[1]);
                        int comments_num = Integer.parseInt(n.select(".comments").text().split(" ")[0]);
                        n.select(".newsText").select("img").attr("width", "100%");

                        String ntext = n.select(".newsText").html().replace("proxy.php", "http://"+getString(R.string.url)+"/proxy.php").replace(";hash","&hash");
                        System.out.println(ntext);
                        Newsitem ni = new Newsitem(id, date, title, false, maker, comments_num, ntext, views, likes);
                        newsl.add(ni);

                    }




                }
                //Get the logo source of the website
                //Element img = document.select("img").first();
                // Locate the src attribute
               // String imgSrc = img.absUrl("src");
                // Download image from URL
                //InputStream input = new java.net.URL(imgSrc).openStream();
                // Decode Bitmap
                //bitmap = BitmapFactory.decodeStream(input);




                //Get the title of the website
               String  title = document.title();
               System.out.println(title);
               return  newsl;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Newsitem> ns) {
            super.onPostExecute(ns);

            // Set the adapter
            if (rv instanceof RecyclerView) {
                Context context = rv.getContext();
                RecyclerView recyclerView = (RecyclerView) rv;

                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                recyclerView.setAdapter(new MyportalRecyclerViewAdapter(ns, (MainActivity)getParent()));
            }

           // imageView.setImageBitmap(bitmap);
          //  textView.setText(title);
          //  progressDialog.dismiss();
        }
    }


}

