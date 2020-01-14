package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.models.Discussion;
import tech.sabtih.forumapp.models.Simplethread;
import tech.sabtih.forumapp.models.user.Simpleuser;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFeedInteractionListener}
 * interface.
 */
public class FeedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFeedInteractionListener mListener;
    RecyclerView recyclerView;
    private ShimmerFrameLayout mShimmerViewContainer;
    TextView nothreads;
    TextView showRecent;
    ThreadListActivity.SimpleItemRecyclerViewAdapter threadsadapter;
    public ArrayList<Object> threadslist = new ArrayList<>();
    boolean isRecyclerViewWaitingtoLaadData = false;
    boolean loadedAllItems = false;
    boolean multipage = false;
    int totalpages = 0;
    int page = 1;
    String newposts;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout srl;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FeedFragment newInstance(int columnCount) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("cookies", MODE_PRIVATE);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        recyclerView = view.findViewById(R.id.thread_list);

        nothreads = view.findViewById(R.id.nothreads);
        showRecent = view.findViewById(R.id.mRecentbtn);

        showRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srl.setRefreshing(true);
                nothreads.setVisibility(View.GONE);
                showRecent.setVisibility(View.GONE);
                new getFeeds().execute("recent");
            }
        });


        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        srl = view.findViewById(R.id.swiperefresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getFeeds().execute("update");
            }
        });


        new getFeeds().execute("new");

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFeedInteractionListener) {
            mListener = (OnListFeedInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFeedInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private class getFeeds extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer stage) {
            super.onPostExecute(stage);

            if(stage == 5){
                srl.setRefreshing(false);
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                nothreads.setVisibility(View.VISIBLE);
                showRecent.setVisibility(View.VISIBLE);

                return;

            }

            if (threadslist.size() == 0) {

                nothreads.setVisibility(View.VISIBLE);
                showRecent.setVisibility(View.VISIBLE);



            }else{
                srl.setRefreshing(false);
                nothreads.setVisibility(View.GONE);
                showRecent.setVisibility(View.GONE);
            }

            if (stage == 0) {


                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                threadsadapter = new ThreadListActivity.SimpleItemRecyclerViewAdapter((AppCompatActivity) getActivity(), threadslist, false);
                recyclerView.setAdapter(threadsadapter);




                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else if (stage == 1) {
                srl.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
                threadsadapter.setList(threadslist);
                threadsadapter.notifyDataSetChanged();
            }
            isRecyclerViewWaitingtoLaadData = false;
        }

        @Override
        protected Integer doInBackground(String... urls) {
            int stage = 0;

            try {
                ArrayList<Object> threadarray = new ArrayList<>();
String url = "http://" + getString(R.string.url) + "/find-new/threads";
if(urls[0].equalsIgnoreCase("recent")){
    url = "http://" + getString(R.string.url) + "/"+newposts;
}
                //Connect to the website
                Document document = null;
                document = Jsoup.connect(url)
                        .data("xf_session", sharedPreferences.getString("xf_session", ""))
                        .cookie("xf_session", sharedPreferences.getString("xf_session", "")).cookie("xf_user", sharedPreferences.getString("xf_user", ""))
                        .get();
                System.out.println("sessoin: " + sharedPreferences.getString("xf_session", ""));
                if (document.select(".PageNav").first() != null) {
                    multipage = true;
                    String pages = document.select(".pageNavHeader").first().text();
                    totalpages = Integer.parseInt(pages.split("of")[1].trim());
                } else
                    multipage = false;

                if(document.select(".mainContent").select(".section").first().html().contains("no unread")){
                     newposts = document.select(".mainContent").select(".section").first().select("a").attr("href");
                    return 5;
                }
                Element threads = document.select(".discussionListItems").first();


                Elements msglist = threads.children();

                for (Element n : msglist) {
                    if (n.hasClass("discussionListItem") && n.hasAttr("id")) {

                        String tid = n.attr("id").split("-")[1];


                        int ID = Integer.parseInt(tid.trim());
                        String title = n.select(".title").text();
                        String maker = n.select(".username").first().text();
                        int makerid = Integer.parseInt( n.select(".posterAvatar").select("a").first().attr("href").split("\\.")[1].replace("/","").trim());
                        String latestreplydate = n.select(".DateTime").last().attr("data-time");
                        String startdate = "";
                        if (n.select(".DateTime").first().hasAttr("data-time")) {
                            startdate = n.select(".DateTime").first().attr("data-time");

                            if (!startdate.isEmpty()) {
                                Date date = new Date((Long.parseLong(startdate) * 1000));
                                DateFormat formatter;
                                if (!DateUtils.isToday(date.getTime())) {
                                    long diff = TimeUnit.DAYS.convert(date.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                                    if (diff == -1) {
                                        formatter = new SimpleDateFormat("hh:mm a");
                                        startdate = "Yesterday at " + formatter.format(date);
                                    } else {
                                        formatter = new SimpleDateFormat("MMM dd, YYYY hh:mm a");
                                        startdate = formatter.format(date);
                                    }
                                } else {
                                    formatter = new SimpleDateFormat("hh:mm a");
                                    startdate = formatter.format(date);
                                }


                                // Log.d("daydif",title+" "+diff);
                                //formatter.setTimeZone(TimeZone.getTimeZone("GMT+0300"));
                                //startdate = formatter.format("Yesterday at "+date);


                            } else {
                                startdate = n.select(".DateTime").first().text();
                            }
                        } else {
                            startdate = n.select(".DateTime").first().text();
                        }
                        int pages = 1;
                        if(n.select(".itemPageNav").first() != null){
                            pages = Integer.parseInt(n.select(".itemPageNav").first().children().last().text());
                        }

                        String lastreplyname = n.select(".lastPostInfo").select("a").first().text();
                        String makeravatarn = n.select("img").first().attr("src");
                        int replies = Integer.parseInt(n.select(".stats").select("dd").first().text().replace(",", ""));
                        int views = Integer.parseInt(n.select(".stats").select("dd").last().text().replace(",", ""));
                        boolean isread = n.select(".unreadLink").first() == null;
                        boolean isticky = n.select(".sticky").first() != null;

                        Simpleuser su = new Simpleuser(makerid,maker,makeravatarn,0);
                        Simplethread st = new Simplethread(ID, title, su, latestreplydate, startdate, lastreplyname, makeravatarn, replies, views, isread, isticky);
                        st.setPages(pages);

                        threadarray.add(st);

                    }else if(n.select(".noteRow").first() != null){
                        String nodemsg = n.select(".noteRow").text();
                        threadarray.add(nodemsg);

                    }


                }
                if(urls[0].equalsIgnoreCase("update")){
                    stage = 1;
                }else if (urls[0].equalsIgnoreCase("new")){
                    stage = 0;
                }




                    threadslist = threadarray;
                  ///  if (multipage)
                   //     threadslist.add(null);

                return stage;


            } catch (IOException e) {
                e.printStackTrace();
            }


            return 0;
        }


    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFeedInteractionListener {
        // TODO: Update argument type and name
        void onListFeedInteraction(Discussion item);
    }
}
