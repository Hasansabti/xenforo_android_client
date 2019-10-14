package tech.sabtih.forumapp.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import im.delight.android.webview.AdvancedWebView;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadDetailActivity;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.adapters.MyThreadreplyRecyclerViewAdapter;
import tech.sabtih.forumapp.dummy.DummyContent;
import tech.sabtih.forumapp.listeners.OnTReplyInteractionListener;
import tech.sabtih.forumapp.models.Discussion;
import tech.sabtih.forumapp.models.Simpleuser;
import tech.sabtih.forumapp.models.Threadreply;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a single Thread detail screen.
 * This fragment is either contained in a {@link ThreadListActivity}
 * in two-pane mode (on tablets) or a {@link ThreadDetailActivity}
 * on handsets.
 */
public class ThreadDetailFragment extends Fragment implements OnTReplyInteractionListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    SharedPreferences sharedPreferences;
    ImageView avatar;
    TextView author;
    TextView forum;
    TextView title;
    TextView replies;
    TextView date;
    TextView likes;
    TextView reply;
    TextView Report;
    ImageView like;
    AdvancedWebView content;
    RecyclerView threadreplies;

    String script = "";
    String script2 = "";
    MyThreadreplyRecyclerViewAdapter adapter;

    ShimmerFrameLayout loading;
    View threadcntnt;

    /**
     * The dummy content this fragment is presenting.
     */
    private Discussion mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ThreadDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        sharedPreferences = getActivity(). getSharedPreferences("cookies", MODE_PRIVATE);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
        //    mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thread_detail, container, false);

        // Show the dummy content as text in a TextView.
       // if (mItem != null) {
           // ((TextView) rootView.findViewById(R.id.thread_detail)).setText(getArguments().getString(ARG_ITEM_ID));

            avatar = rootView.findViewById(R.id.avatarph);


            forum= rootView.findViewById(R.id.forum);
            title= rootView.findViewById(R.id.ttitleph);
            replies= rootView.findViewById(R.id.repliesph);



            author= rootView.findViewById(R.id.tauthorph);
            date= rootView.findViewById(R.id.ttimeph);
            likes= rootView.findViewById(R.id.numlikes);
            reply= rootView.findViewById(R.id.reply);
            Report= rootView.findViewById(R.id.report);
            like= rootView.findViewById(R.id.likeph);
            content= rootView.findViewById(R.id.threadtext);
            threadreplies = rootView.findViewById(R.id.threadreplies);
            loading = rootView.findViewById(R.id.loading);
            loading.startShimmerAnimation();
             threadcntnt = rootView.findViewById(R.id.thrdcntnt);
             threadcntnt.setVisibility(View.INVISIBLE);


            String url = getArguments().getString(ARG_ITEM_ID);
            new getThread().execute(url, url);
      //  }

        return rootView;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onTReplyInteraction(Threadreply mItem) {

    }


    private class getThread extends AsyncTask<String,Void, Discussion> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Discussion discussion) {
            super.onPostExecute(discussion);
            if(discussion != null){
              //  InputStream is = getActivity().getResources().openRawResource(R.raw.jquery);
               // InputStream is2 = getActivity().getResources().openRawResource(R.raw.xenforo);


              //  try {
                  //  script2 = IOUtils.toString(is2);
                   //  script = IOUtils.toString(is2);
               // } catch (IOException e) {
               //     e.printStackTrace();
               // }
                mItem = discussion;
                forum.setText(discussion.getForum());
                author.setText(discussion.getAuthor());
                date.setText(discussion.getCdate());
                title.setText(discussion.getTitle());


               // CookieManager cookieManager = CookieManager.getInstance();
               // cookieManager.setAcceptCookie(true);
               // CookieManager.getInstance().setAcceptThirdPartyCookies(content, true);


                content.getSettings().setJavaScriptEnabled(true);

               // content.clearCache(true);

                content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                content.setWebViewClient(new MyWebViewClient());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                    content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                //content.loadData(discussion.getTreplies().get(0).getText(), "text/html", "UTF-8");

                content.loadHtml(discussion.getTreplies().get(0).getText(),"http://"+getActivity().getString(R.string.url));

                Log.d("thread",discussion.getTreplies().get(0).getText());


                likes.setText(""+discussion.getTreplies().get(0).getLikes());
                replies.setText(""+(discussion.getTreplies().size()-1));
                Picasso.get().load("http://" + getActivity().getString(R.string.url) + "/" + discussion.getTreplies().get(0).getSu().getAvatar()).into(avatar);
                    Activity activity = ThreadDetailFragment. this.getActivity();
                    CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                   if ( appBarLayout != null) {
                       appBarLayout.setTitle(discussion.getTitle());
                }
                   discussion.getTreplies().remove(0);

                   adapter = new MyThreadreplyRecyclerViewAdapter(discussion.getTreplies(),ThreadDetailFragment.this, false);
                threadreplies.setLayoutManager(new LinearLayoutManager(threadreplies.getContext()));
                threadreplies.setAdapter(adapter);
                loading.setVisibility(View.GONE);
                loading.stopShimmerAnimation();
                threadcntnt.setVisibility(View.VISIBLE);
            }


            //

            //recyclerView.setAdapter(new ThreadListActivity.SimpleItemRecyclerViewAdapter(ThreadDetailActivity.this,forums, mTwoPane));
        }

        @Override
        protected Discussion doInBackground(String... urls) {


            try {





                //Connect to the website
                Document document = null;
                document = Jsoup.connect("http://"+getString(R.string.url)+"/threads/"+urls[0])
                        .data("xf_session",sharedPreferences.getString("xf_session",""))
                        .cookie("xf_session",sharedPreferences.getString("xf_session","")).cookie("xf_user",sharedPreferences.getString("xf_user",""))
                        .get();
                System.out.println("sessoin: " + sharedPreferences.getString("xf_session",""));
                Element thread= document.select("#messageList").first();

               // InputStream is = getActivity().getResources().openRawResource(R.raw.threadstyle);
                //String style = IOUtils.toString(is);


                Elements msglist = thread.children();
                int tid = Integer.parseInt(urls[0]);
                String title = document.select(".titleBar").select("h1").text();
                String forum = document.select(".crumb").last().text();
                String cdate = document.select(".DateTime").first().text();


                ArrayList<Threadreply> replies = new ArrayList<>();

                for(Element n : msglist){
                    if(n.hasClass("message")) {

                        String postid = n.attr("id").split("-")[1];
                        int id = Integer.parseInt(n.select(".postNumber").text().replace("#",""));
                        String username = n.select(".username").first().text();
                        String userid = n.select(".username").attr("href").split("\\.")[1].replace("/","");
                        String avatar = n.select(".avatar").select("img").attr("src");
                        int numlikes = 0;
                        if(n.select(".LikeText").first() != null) {
                            numlikes = n.select(".LikeText").select(".username").size();
                            if(n.select(".LikeText").select(".OverlayTrigger").first() != null){
                                String lother = n.select(".LikeText").select(".OverlayTrigger").first().text().split(" ")[0];
                                numlikes += Integer.parseInt(lother);

                            }


                        }
                        Simpleuser su = new Simpleuser(Integer.parseInt(userid),username,avatar);

                        String replyto = "0";
                        if(n.select(".bbCodeQuote").first() != null) {
                            Element e = n.select(".bbCodeQuote").first();

                                if(e.select(".attribution").first() != null){
                                     replyto = e.select(".attribution").select("a").attr("href").split("-")[1];
                                        if(document.select("[id=post-"+replyto+"]").first() != null) {
                                            e.remove();
                                        }

                                }



                        }
                        // Discussion d = new Discussion();


                       // IOUtils.closeQuietly(is); // don't forget to close your streams


                        String msgtext ="";

                         msgtext += n.select(".messageText").html().replace("proxy.php", "http://"+getString(R.string.url)+"/proxy.php").replace(";hash","&hash").replace("styles/","http://"+getString(R.string.url)+"/styles/");

                       // msgtext.concat(document.select("script").html());

                       // Log.d("HTML",msgtext);

                        boolean replying = false;
                        String date = n.select(".DateTime").text();
                        Threadreply rep = new Threadreply(id, postid,msgtext,numlikes,date,su);


                        Threadreply thr = checkreplies(replies,replyto);
                        if(thr != null){
                            if(thr.getReplies() == null){
                                thr.setReplies(new ArrayList<Threadreply>());
                            }

                            thr.getReplies().add(rep);

                        }else{

                            replies.add(rep);
                        }




                    }




                }



                Discussion dis = new Discussion(tid,urls[1],title,forum,replies.get(0).getSu().getName(),cdate,replies);


                return dis;



            } catch (IOException e) {
                e.printStackTrace();
            }




            return null;
        }
    }

    public Threadreply checkreplies(ArrayList<Threadreply> replies, String repid){

        if(replies != null && !repid.equals("0")){
            for(Threadreply r : replies){
                Threadreply thr1;

                if(r.getPostid().equalsIgnoreCase(repid)){
                    Log.d("rec","Found reply " + r.getPostid());
                    thr1 =  r;
                }else{
                    Log.d("serch","Searching for " + repid + " in "+r.getPostid());
                    thr1 = checkreplies(r.getReplies(),repid);
                }
                if(thr1 != null){
                    return thr1;
                }else{
                    continue;
                }
            }

            Log.d("serch","Reply " + repid + " not found ");
        }

        return null;


    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println(url);
            if(url.contains(getString(R.string.url)))
            if(url.contains("members")){
                Log.d("url","This is a member");

            }else if(url.contains("threads")){
                Log.d("url","This is a thread");
            }

            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {

            try {

                InputStream inputStream = getContext().getAssets().open("style.css");
                byte[] buffer = new byte[inputStream.available()];

                inputStream.read(buffer);
                inputStream.close();
                String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                view.loadUrl("javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()");

            } catch (IOException e) {
                e.printStackTrace();
            }
           // view.loadUrl("javascript:"+script);
        //    view.loadUrl("javascript:"+script2);
            //view.reload();



        }





    }
}
