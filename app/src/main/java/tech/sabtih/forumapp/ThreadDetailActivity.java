package tech.sabtih.forumapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import tech.sabtih.forumapp.models.Discussion;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Forumcategory;
import tech.sabtih.forumapp.models.Simpleuser;

/**
 * An activity representing a single Thread detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ThreadListActivity}.
 */
public class ThreadDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ThreadDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ThreadDetailFragment.ARG_ITEM_ID));
            ThreadDetailFragment fragment = new ThreadDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.thread_detail_container, fragment)
                    .commit();
        }
    }

/*
    private class getFCat extends AsyncTask<String,Void, ArrayList<Discussion>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Discussion> forums) {
            super.onPostExecute(forums);


            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

            recyclerView.setAdapter(new ThreadListActivity.SimpleItemRecyclerViewAdapter(ThreadListActivity.this,forums, mTwoPane));
        }

        @Override
        protected ArrayList<Discussion> doInBackground(String... urls) {


            try {
                ArrayList<Discussion> threads = new ArrayList<>();





                //Connect to the website
                Document document = null;
                document = Jsoup.connect("http://"+getString(R.string.url)+"/threads/"+urls[0])
                        .data("xf_session",sharedPreferences.getString("xf_session",""))
                        .cookie("xf_session",sharedPreferences.getString("xf_session","")).cookie("xf_user",sharedPreferences.getString("xf_user",""))
                        .get();
                System.out.println("sessoin: " + sharedPreferences.getString("xf_session",""));
                Element thread= document.select(".messageList").first();




                Elements msglist = thread.children();

                for(Element n : msglist){
                    if(n.hasClass("message")) {

                        String postid = n.attr("id");
                        int id = Integer.parseInt(n.select("postNumber").text().replace("#",""));
                        String username = n.select(".username").text();
                        String userid = n.select(".username").attr("href").split("\\.")[1].replace("/","");
                        String avatar = n.select(".avatar").select("img").attr("href");
                        Simpleuser su = new Simpleuser(Integer.parseInt(userid),username,avatar);

                        Discussion d = new Discussion()




                        String title = n.select(".categoryNodeInfo").select(".nodeTitle").text();

                        Elements childs = n.select(".nodeList").first().children();


                        ArrayList<Forum> myforums = new ArrayList<>();
                        for(Element ch : childs){
                            int cid = 0;
                            String ctitle = ch.select(".nodeTitle").first().text();
                            int discs  = -1;
                            int msgs = -1;
                            if(ch.select(".nodeStats")!= null) {
                                if(ch.select(".nodeStats").select("dd").first()!= null) {
                                    try {
                                        discs = Integer.parseInt(ch.select(".nodeStats").select("dd").first().text().replace(",", ""));
                                        msgs = Integer.parseInt(ch.select(".nodeStats").select("dd").last().text().replace(",", ""));
                                    }catch (NumberFormatException ex){

                                    }
                                }
                            }
                            String type = "forum";
                            if(ch.hasClass("link")){
                                type = "link";
                            }




                            String url = ch.select(".nodeTitle").select("a").attr("href");
                            String latest = ch.select(".lastThreadTitle").select("a").text();
                            boolean isread = ch.select(".nodeInfo").hasClass("unread");
                            String latestdate = ch.select(".lastThreadDate").text();
                            Forum frm = new Forum(cid,ctitle,discs,msgs,url,latest,!isread,latestdate,type);
                            myforums.add(frm);

                        }


                        Forumcategory fc = new Forumcategory(0,title,myforums);
                        threads.add(fc);

                    }




                }

                return threads;



            } catch (IOException e) {
                e.printStackTrace();
            }




            return null;
        }
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
           // navigateUpTo(new Intent(this, ThreadListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
