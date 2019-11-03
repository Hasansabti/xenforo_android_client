package tech.sabtih.forumapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadDetailActivity;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.adapters.MyThreadreplyRecyclerViewAdapter;
import tech.sabtih.forumapp.dummy.DummyContent;
import tech.sabtih.forumapp.listeners.OnTReplyInteractionListener;
import tech.sabtih.forumapp.models.Discussion;
import tech.sabtih.forumapp.models.user.Simpleuser;
import tech.sabtih.forumapp.models.Threadreply;
import tech.sabtih.forumapp.utils.MyWebViewClient;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

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
    NestedScrollView nsv;

    String script = "";
    String script2 = "";
    MyThreadreplyRecyclerViewAdapter adapter;

    String url;

    ShimmerFrameLayout loading;
    View threadcntnt;

    boolean multipage = false;
    int totalpages = 0;
    int page = 1;
    boolean isRecyclerViewWaitingtoLaadData = false;

    ArrayList<Threadreply> repliesarr;
    int scrollocation;
    Threadreply mymainpost;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ThreadDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getActivity().getSharedPreferences("cookies", MODE_PRIVATE);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //    mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//
            if (getArguments().getString("pages") != null)
                page = Integer.parseInt(getArguments().getString("pages"));
            if (getArguments().get("pages") != null) {
                totalpages = Integer.parseInt(getArguments().getString("pages").trim());
            } else {
                totalpages = 0;
            }
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


        forum = rootView.findViewById(R.id.forum);
        title = rootView.findViewById(R.id.ttitleph);
        replies = rootView.findViewById(R.id.repliesph);


        author = rootView.findViewById(R.id.tauthorph);
        date = rootView.findViewById(R.id.ttimeph);
        likes = rootView.findViewById(R.id.numlikes);
        reply = rootView.findViewById(R.id.reply);
        Report = rootView.findViewById(R.id.report);
        like = rootView.findViewById(R.id.likeph);
        content = rootView.findViewById(R.id.threadtext);
        threadreplies = rootView.findViewById(R.id.threadreplies);
        nsv = rootView.findViewById(R.id.thread_detail);
        loading = rootView.findViewById(R.id.loading);
        loading.startShimmerAnimation();
        threadcntnt = rootView.findViewById(R.id.thrdcntnt);
        threadcntnt.setVisibility(View.INVISIBLE);

        url = getArguments().getString(ARG_ITEM_ID);


        if(!url.contains("posts")) {
            url = "/threads/" + url + "/";
        }else{
            url = "/"+url;
        }
        if (url == null && getArguments().getString("url") != null) {
            url = getArguments().getString("url").replace("/threads/", "").split("\\.")[1].replace("/", "")+"/";
            Log.d("thread_url", "Opening url id: " + url);
        }

        if (url != null && url.contains("#") && !url.contains("posts")) {
            String targetpost = url.substring(url.indexOf("#"), url.length() - 1);
            Log.d("targeting_post", targetpost);
            url = url.substring(0, url.indexOf("#"));
        }
        Log.d("url",url);
        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Report post");

// Set up the input
                final EditText input = new EditText(getContext());
                input.setHint("Report Reason");

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reason = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");
                    if (!isRecyclerViewWaitingtoLaadData && multipage) //check for scroll down
                    {
                        isRecyclerViewWaitingtoLaadData = true;


                        if (page > 1) {
                            page--;
                            new getThread().execute(url.trim(), "" + page);
                            Log.d("ThreadList", "Loading page:" + page);

                        }

                    }
                }
            }
        });


        new getThread().execute(url.trim(), "" + page);

        //  }

        return rootView;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onTReplyInteraction(Threadreply mItem) {

    }


    private class getThread extends AsyncTask<String, Void, Discussion> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scrollocation = nsv.getScrollY();
        }

        @Override
        protected void onPostExecute(Discussion discussion) {
            super.onPostExecute(discussion);
            if (discussion != null) {

                //mItem = discussion;
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
                content.setWebViewClient(new MyWebViewClient(getContext()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                    content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                //content.loadData(discussion.getTreplies().get(0).getText(), "text/html", "UTF-8");

                content.loadHtml(mymainpost.getText(), "http://" + getActivity().getString(R.string.url));

                // Log.d("thread", discussion.getTreplies().get(0).getText());


                likes.setText("" + mymainpost.getLikes());
               // replies.setText("" + (discussion.getTreplies().size() - 1));
                Picasso.get().load("http://" + getActivity().getString(R.string.url) + "/" + mymainpost.getSu().getAvatar()).into(avatar);
                Activity activity = ThreadDetailFragment.this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(discussion.getTitle());
                }
                // if (page == 1)
                //   discussion.getTreplies().remove(0);
                if (adapter == null) {
                    // Collections.reverse(discussion.getTreplies());
                    adapter = new MyThreadreplyRecyclerViewAdapter(discussion.getTreplies(), ThreadDetailFragment.this, false);
                    LinearLayoutManager llm = new LinearLayoutManager(threadreplies.getContext());
                    // llm.setStackFromEnd(true);
                    // llm.setReverseLayout(true);
                    threadreplies.setLayoutManager(llm);
                    threadreplies.setAdapter(adapter);


                } else {
                    adapter.setValues(repliesarr);
                    adapter.notifyDataSetChanged();
                }


                loading.setVisibility(View.GONE);
                loading.stopShimmerAnimation();
                threadcntnt.setVisibility(View.VISIBLE);
            }


            //
            isRecyclerViewWaitingtoLaadData = false;
            //recyclerView.setAdapter(new ThreadListActivity.SimpleItemRecyclerViewAdapter(ThreadDetailActivity.this,forums, mTwoPane));
        }

        @Override
        protected Discussion doInBackground(String... urls) {


            try {


                //Connect to the website


                Document document = null;
                urls[0] = urls[0].replace(" ","/");
                String myurl = "";
                if(urls[0].contains("posts")){
                    myurl = "http://" + getString(R.string.url)  + urls[0].replace("//","/");
                }else{
                    if(urls[0].contains("page")){
                        myurl = "http://" + getString(R.string.url) + urls[0].replace(" ","/").replace("//","/");
                    }else {
                        myurl = "http://" + getString(R.string.url) + urls[0].replace("//","/") + "page-" + urls[1];
                    }
                    }

                document = Jsoup.connect(URLDecoder.decode( myurl, "UTF-8"))
                        .data("xf_session", sharedPreferences.getString("xf_session", ""))
                        .cookie("xf_session", sharedPreferences.getString("xf_session", "")).cookie("xf_user", sharedPreferences.getString("xf_user", ""))
                        .get();
                System.out.println("sessoin: " + sharedPreferences.getString("xf_session", ""));
                Element thread = document.select("#messageList").first();

                // InputStream is = getActivity().getResources().openRawResource(R.raw.threadstyle);
                //String style = IOUtils.toString(is);
                document.select("img").attr("style", "max-width:100%");


                if (document.select(".PageNav").first() != null && document.select(".pageNavHeader").first() != null) {
                    multipage = true;
                    String pages = document.select(".pageNavHeader").first().text();
                    totalpages = Integer.parseInt(pages.split("of")[1].trim());
                } else {
                    multipage = false;
                    if (repliesarr != null)
                        mymainpost = repliesarr.get(0);
                }

                if (totalpages > 1 && totalpages != 0 && mymainpost == null) {

                     myurl = "";
                    if(urls[0].contains("posts")){
                        myurl = "http://" + getString(R.string.url)  + urls[0].replace("//","/");
                    }else{
                        if(urls[0].contains("page")){
                            myurl = "http://" + getString(R.string.url) + urls[0].replace("//","/").substring(0,urls[0].indexOf("/page")) .replace("/page-","/").replace(" ","/");
                        }else {
                            myurl = ("http://" + getString(R.string.url) + urls[0].replace("//","/") + "page-1") ;
                        }
                    }
                    Document mainthread = Jsoup.connect(myurl).data("xf_session", sharedPreferences.getString("xf_session", ""))
                            .cookie("xf_session", sharedPreferences.getString("xf_session", "")).cookie("xf_user", sharedPreferences.getString("xf_user", "")).get();
                    //  Log.d("Thethread",mainthread.html());

                    Element posts = mainthread.select("#messageList").first();
                    Element mainpost = posts.children().first();

                    if (mainpost.hasClass("message")) {
                        Threadreply main = parseTR(mainpost, mainthread, repliesarr);
                        if (main != null) {
                            //  if(repliesarr == null)
                            //    repliesarr = new ArrayList<>();
                            // repliesarr.add(main);

                            mymainpost = main;
                        }

                    }


                } else {
                    mymainpost = parseTR(thread.children().get(0), document, repliesarr);

                }
                Elements msglist = thread.children();
                int tid = Integer.parseInt(urls[0].split("/")[2].replace("/",""));
                String title = document.select(".titleBar").select("h1").text();
                String forum = document.select(".crumb").last().text();
                String cdate = document.select(".DateTime").first().text();

                String rdate = "";
                boolean replying = false;
                if (document.select(".DateTime").first().hasAttr("data-time")) {
                    String timelng = document.select(".DateTime").first().select(".DateTime").attr("data-time");

                    Date dateo = new Date((Long.parseLong(timelng.trim()) * 1000));
                    //  rdate = DateUtils.getRelativeTimeSpanString(getContext(), (Long) dateo.getTime()).toString();
                    rdate = getDisplayableTime(dateo.getTime());

                } else {
                    String date = document.select(".DateTime").first().select(".DateTime").text();
                    Date dateo = new SimpleDateFormat("MMM d, yyyy").parse(date);
                    // rdate = DateUtils.getRelativeTimeSpanString(getContext(), (Long) dateo.getTime()).toString();
                    rdate = getDisplayableTime(dateo.getTime());
                    //Log.d("rdate",rdate);
                }

                //  Date dateo = new SimpleDateFormat("MMM dd, yyyy").parse(cdate);
                // String rdate = DateUtils.getRelativeTimeSpanString(getContext(),(Long)dateo.getTime()).toString();
                // String rdate = getDisplayableTime(dateo.getTime());


                Log.d("rdate", rdate);
                //Log.d("date",dateo.toString());


                if (repliesarr == null) {
                    repliesarr = new ArrayList<>();


                } else {
                    for (int i = 0; i < repliesarr.size(); i++) {
                        if (repliesarr.get(i) == null) {
                            repliesarr.remove(i);
                        }
                    }
                }
                ArrayList<Threadreply> trr = new ArrayList<>();
                //Collections.reverse(repliesarr);
                for (int i = 0; i < msglist.size(); i++) {
                    Element n = msglist.get(i);
                    if (n.hasClass("message")) {

                        Threadreply rep = parseTR(n, document, trr);
                        if (rep != null) {
                            if (rep.getId() != 1)
                                trr.add(rep);
                        }


                    }
                }

                Collections.reverse(trr);
                repliesarr.addAll(trr);
                if (page > 1) {
                    repliesarr.add(null);
                }

                Discussion dis = new Discussion(tid, urls[1], title, forum, mymainpost.getSu().getName(), rdate, repliesarr);


                return dis;


            }catch (final HttpStatusException e) {
                e.printStackTrace();
                if (e.getStatusCode() == 403) {
                    Log.d("User_Error",e.getMessage());
                    ThreadDetailFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(ThreadDetailFragment.this.getActivity()).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("You do not have permission to view this page or perform this action.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ThreadDetailFragment.this.getActivity().finish();
                                        }
                                    });
                            alertDialog.show();

                        }
                    });

                }

            }  catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return null;
        }
    }


    public Threadreply parseTR(Element n, Document document, ArrayList<Threadreply> reps) throws ParseException {
        String postid = n.attr("id").split("-")[1];
        int id = Integer.parseInt(n.select(".postNumber").text().replace("#", ""));
        String username = n.select(".username").first().text();
        String userid = n.select(".username").attr("href").split("\\.")[1].replace("/", "");
        String avatar = n.select(".avatar").select("img").attr("src");
        int numlikes = 0;
        if (n.select(".LikeText").first() != null) {
            numlikes = n.select(".LikeText").select(".username").size();
            if (n.select(".LikeText").select(".OverlayTrigger").first() != null) {
                String lother = n.select(".LikeText").select(".OverlayTrigger").first().text().split(" ")[0];
                numlikes += Integer.parseInt(lother);

            }


        }
        Simpleuser su = new Simpleuser(Integer.parseInt(userid), username, avatar);

        String replyto = "0";
        if (n.select(".bbCodeQuote").first() != null) {
            Element e = n.select(".bbCodeQuote").first();

            if (e.select(".attribution").first() != null) {
                replyto = e.select(".attribution").select("a").attr("href").split("-")[1];
                if (document.select("[id=post-" + replyto + "]").first() != null) {
                    e.remove();
                }

            }


        }
        // Discussion d = new Discussion();


        // IOUtils.closeQuietly(is); // don't forget to close your streams


        String msgtext = "";

        msgtext += n.select(".messageText").html().replace("proxy.php", "http://" + getString(R.string.url) + "/proxy.php").replace(";hash", "&hash").replace("styles/", "http://" + getString(R.string.url) + "/styles/");

        // msgtext.concat(document.select("script").html());

        // Log.d("HTML",msgtext);
        String rdate = "";
        boolean replying = false;
        if (n.select(".DateTime").hasAttr("data-time")) {
            String timelng = n.select(".DateTime").attr("data-time");

            Date dateo = new Date((Long.parseLong(timelng.trim()) * 1000));
            //rdate = DateUtils.getRelativeTimeSpanString(getContext(), (Long) dateo.getTime()).toString();
            rdate = getDisplayableTime(dateo.getTime());

        } else {
            String date = n.select(".DateTime").text();
            Date dateo = new SimpleDateFormat("MMM d, yyyy").parse(date);
            // rdate = DateUtils.getRelativeTimeSpanString(getContext(), (Long) dateo.getTime()).toString();
            rdate = getDisplayableTime(dateo.getTime());
            //Log.d("rdate",rdate);
        }
        Threadreply rep = new Threadreply(id, postid, msgtext, numlikes, rdate, su);


        Threadreply thr = checkreplies(reps, replyto);
        if (thr != null && thr.getId() != mymainpost.getId()) {
            if (thr.getReplies() == null) {
                thr.setReplies(new ArrayList<Threadreply>());

            }

            thr.getReplies().add(rep);
            return null;

        } else {

            // repliesarr.add(rep);
            return rep;
        }


    }

    public Threadreply checkreplies(List<Threadreply> replies, String repid) {

        if (replies != null && !repid.equals("0")) {
            for (Threadreply r : replies) {
                Threadreply thr1;
                if (r != null) {
                    if (r.getPostid().equalsIgnoreCase(repid)) {
                        Log.d("rec", "Found reply " + r.getPostid());
                        thr1 = r;
                    } else {
                        Log.d("serch", "Searching for " + repid + " in " + r.getPostid());
                        thr1 = checkreplies(r.getReplies(), repid);
                    }
                    if (thr1 != null) {
                        return thr1;
                    } else {
                        continue;
                    }
                }
            }

            Log.d("serch", "Reply " + repid + " not found ");
        }

        return null;


    }

    public static String getDisplayableTime(long delta) {
        long difference = 0;
        Long mDate = java.lang.System.currentTimeMillis();
        Date date = new Date(delta);

        if (mDate > delta) {
            difference = mDate - delta;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long weeks = days / 7;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 0) {
                return "not yet";
            } else if (seconds < 60) {
                return "a moment ago";
                //  return seconds == 1 ? "one second ago" : seconds + " seconds ago";
            } else if (seconds < 120) {
                return "a moment ago";
            } else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ago";
            } else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24 * 60 * 60
            {
                return hours + " hours ago";
            } else if (days > 6 && days < 30) // 30 * 24 * 60 * 60
            {
                if(weeks == 1){
                    return "a week ago";
                }
                return weeks + " weeks ago";
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                DateFormat formatter = new SimpleDateFormat(" hh:mm a");
                String dateFormatted = formatter.format(date);
                return "yesterday at" +dateFormatted;
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                DateFormat formatter = new SimpleDateFormat("hh:mm a");
                String dateFormatted = formatter.format(date);
               // return days + " days ago";
                return new SimpleDateFormat("EEEE").format(date)+" at " + dateFormatted;
            }
            else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {

                return months <= 1 ? "a month ago" : months + " months ago";
            } else {

                return years <= 1 ? "a year ago" : years + " years ago";
            }
        }
        return null;
    }

    private class MyWebViewClienttt extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println(url);
            if (url.contains(getString(R.string.url)))
                if (url.contains("members")) {
                    Log.d("url", "This is a member");


                    String ttl = "";
                    String userid = "";

                    //String urlt = url.split(((ThreadDetailFragment) mListener  ). getString(R.string.url))[1];
                    if (url.split("members/")[1].contains(".")) {
                        ttl = url.split("members/")[1].split("\\.")[0].replace("-", " ");
                        userid = url.split("members/")[1].split("\\.")[1].replace("/", " ");
                    } else {
                        ttl = url.split("members/")[1].replace("/", " ");
                        userid = url.split("members/")[1].replace("/", " ");
                    }
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("username", ttl);
                    startActivity(intent);
                } else if (url.contains("threads")) {
                    Log.d("url", "This is a thread");
                    String urlt = url.split(getString(R.string.url))[1];
                    String ttl = url.split("threads/")[1].split("\\.")[1].replace("/", " ");
                    Intent intent = new Intent(getContext(), ThreadDetailActivity.class);
                    intent.putExtra("item_id", urlt);
                    intent.putExtra("item_id", ttl);
                    //  intent.putExtra("pages", "1");
                    startActivity(intent);

                } else if (url.contains("forums")) {
                    Log.d("url", "This is a forum");
                    String urlt = url.split(getString(R.string.url))[1];
                    String ttl = url.split("forums/")[1].split("\\.")[0].replace("-", " ");
                    Intent intent = new Intent(getContext(), ThreadListActivity.class);
                    intent.putExtra("url", urlt);
                    intent.putExtra("title", ttl);
                    startActivity(intent);
                }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

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
