package tech.sabtih.forumapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tech.sabtih.forumapp.adapters.MyForumsRecyclerViewAdapter;
import tech.sabtih.forumapp.fragments.ThreadDetailFragment;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Simplethread;
import tech.sabtih.forumapp.models.user.Simpleuser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An activity representing a list of Threads. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ThreadDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ThreadListActivity extends AppCompatActivity implements OnForumsListInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    RecyclerView subforumrv;
    TextView nothreads;
    private ShimmerFrameLayout mShimmerViewContainer;
    SimpleItemRecyclerViewAdapter threadsadapter;
    public ArrayList<Object> subforums = new ArrayList<>();
    public ArrayList<Object> threadslist = new ArrayList<>();
    boolean isRecyclerViewWaitingtoLaadData = false;
    boolean loadedAllItems = false;
    boolean multipage = false;
    int totalpages = 0;
    int page = 1;
    NestedScrollView nsv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        sharedPreferences = getSharedPreferences("cookies", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        if (findViewById(R.id.thread_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        recyclerView = findViewById(R.id.thread_list);
        subforumrv = findViewById(R.id.subforum);
        nothreads = findViewById(R.id.nothreads);
        nsv = findViewById(R.id.mynsv);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        final String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            setTitle(title);
        }
        if (url != null && url.contains("forums")) {
            new getFCat().execute(url);

        } else {
            Toast.makeText(this, "Unexpected error: forum not recognized", Toast.LENGTH_SHORT).show();
            finish();


        }


        if (nsv != null)
            nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
                    if (scrollY > oldScrollY) {
                        Log.i(TAG, "Scroll DOWN");
                    }
                    if (scrollY < oldScrollY) {
                        Log.i(TAG, "Scroll UP");
                    }

                    if (scrollY == 0) {
                        Log.i(TAG, "TOP SCROLL");
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if (!isRecyclerViewWaitingtoLaadData && multipage) //check for scroll down
                        {
                            isRecyclerViewWaitingtoLaadData = true;


                            if (page < totalpages) {
                                page++;
                                new getFCat().execute(url + "page-" + page);
                                Log.d("ThreadList", "Loading page:" + page);

                            }

                        }
                    }
                }

            });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //  recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
    }

    @Override
    public void onForumModelInteraction(Forum item) {
        if (item != null && item.getType().equals("forum")) {
            Intent intent = new Intent(this, ThreadListActivity.class);
            intent.putExtra("url", item.getUrl());
            intent.putExtra("title", item.getTitle());
            startActivity(intent);
        } else {

        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class getFCat extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer stage) {
            super.onPostExecute(stage);
            if (threadslist.size() == 0) {
                nothreads.setVisibility(View.VISIBLE);
            }

            if (stage == 0) {


                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                threadsadapter = new SimpleItemRecyclerViewAdapter(ThreadListActivity.this, threadslist, mTwoPane);
                recyclerView.setAdapter(threadsadapter);


                if (subforums != null && subforums.size() > 0) {
                    subforumrv.setLayoutManager(new LinearLayoutManager(subforumrv.getContext()));
                    subforumrv.setAdapter(new MyForumsRecyclerViewAdapter(subforums, ThreadListActivity.this));
                }

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            } else if (stage == 1) {

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


                //Connect to the website
                Document document = null;
                document = Jsoup.connect("http://" + getString(R.string.url) + "/" + urls[0])
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


                Element threads = document.select(".discussionListItems").first();


                Elements msglist = threads.children();

                for (Element n : msglist) {
                    if (n.hasClass("discussionListItem") && ! n.hasClass("deleted")) {

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

                    }


                }


                if (document.select(".nodeList").first() != null) {
                    Elements sfchilds = document.select(".nodeList").first().children();


                    ArrayList<Object> myforums = new ArrayList<>();
                    for (Element ch : sfchilds) {
                        int cid = 0;
                        String ctitle = ch.select(".nodeTitle").first().text();
                        int discs = -1;
                        int msgs = -1;
                        if (ch.select(".nodeStats") != null) {
                            if (ch.select(".nodeStats").select("dd").first() != null) {
                                try {
                                    discs = Integer.parseInt(ch.select(".nodeStats").select("dd").first().text().replace(",", ""));
                                    msgs = Integer.parseInt(ch.select(".nodeStats").select("dd").last().text().replace(",", ""));
                                } catch (NumberFormatException ex) {
                                    Log.e("FCAT", ex.getMessage());

                                }
                            }
                        }
                        String type = "forum";
                        if (ch.hasClass("link")) {
                            type = "link";
                        }


                        String url = ch.select(".nodeTitle").select("a").attr("href");
                        String latest = ch.select(".lastThreadTitle").select("a").text();
                        boolean isread = ch.select(".nodeInfo").hasClass("unread");
                        String latestdate = ch.select(".lastThreadDate").text();
                        Forum frm = new Forum(cid, ctitle, discs, msgs, url, latest, isread, latestdate, type);
                        myforums.add(frm);

                    }
                    subforums = myforums;
                }
                if (urls[0].contains("page")) {
                    stage = 1;
                    for (int i = 0; i < threadslist.size(); i++) {
                        if (threadslist.get(i) == null) {
                            threadslist.remove(i);
                        }
                    }

                    if (threadarray.size() == 0) {


                    } else {
                        threadslist.addAll(threadarray);
                        if (page < totalpages)
                            threadslist.add(null);
                    }
                } else {
                    stage = 0;

                    threadslist = threadarray;
                    if (multipage)
                        threadslist.add(null);
                }

                return stage;


            } catch (IOException e) {
                e.printStackTrace();
            }


            return 0;
        }


    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final AppCompatActivity mParentActivity;
        private List<Object> mValues;
        private final boolean mTwoPane;

        // for load more
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private final int VIEW_TYPE_NOTE = 2;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Simplethread item = (Simplethread) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ThreadDetailFragment.ARG_ITEM_ID, "" + item.getID());
                    arguments.putString("pages", "" + item.getPages());
                    ThreadDetailFragment fragment = new ThreadDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.thread_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ThreadDetailActivity.class);
                    intent.putExtra(ThreadDetailFragment.ARG_ITEM_ID, "" + item.getID());
                    intent.putExtra("pages",""+item.getPages());

                    context.startActivity(intent);
                }
            }
        };

        public SimpleItemRecyclerViewAdapter(AppCompatActivity parent,
                                             ArrayList<Object> items,
                                             boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_list_content, parent, false);
                return new ViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
                return new ViewHolderLoading(view);
            }
            else if (viewType == VIEW_TYPE_NOTE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_holder, parent, false);
                return new ViewHolderText(view);
            }
            return null;


        }


        @Override
        public int getItemViewType(int position) {
            return mValues.get(position) == null ? VIEW_TYPE_LOADING : (mValues.get(position) instanceof String ? VIEW_TYPE_NOTE : VIEW_TYPE_ITEM);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof ViewHolder && mValues.get(position) instanceof Simplethread) {
                final ViewHolder myholder = (ViewHolder) holder;
                myholder.mitem = (Simplethread) mValues.get(position);
                myholder.title.setText(myholder.mitem.getTitle());
                if (myholder.mitem.isIsread()) {
                    myholder.title.setTextColor(Color.GRAY);
                    //Log.d("isread", mValues.get(position).getTitle() + " True");
                } else {
                    myholder.title.setTextColor(ContextCompat.getColor(mParentActivity, R.color.colorAccent));
                    //  Log.d("isread", mValues.get(position).getTitle() + " False");
                }

                myholder.author.setText(myholder.mitem.getMaker().getName());
                myholder.replies.setText("" + myholder.mitem.getReplies());
                myholder.views.setText("" + myholder.mitem.getViews());
                myholder.threadcard.setTag(mValues.get(position));
                myholder.threadcard.setOnClickListener(mOnClickListener);
                myholder.userframe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(mParentActivity, ProfileActivity.class);
                        intent.putExtra("userid", ""+myholder.mitem.getMaker().getId());
                        intent.putExtra("username", ""+myholder.mitem.getMaker().getName());
                        mParentActivity.startActivity(intent);
                    }
                });


                myholder.date.setText(myholder.mitem.getStartdate());
                if (myholder.mitem.isSticky()) {
                    myholder.sticky.setVisibility(View.VISIBLE);

                } else {
                    myholder.sticky.setVisibility(View.GONE);
                }
                Picasso.get().load("http://" + mParentActivity.getString(R.string.url) + "/" + myholder.mitem.getMakeravatar()).into(myholder.avatar);
                //Log.d("Avatar","http://"+ mParentActivity.getString(R.string.url)+"/"+mValues.get(position).getMakeravatar());
            } else if (holder instanceof ViewHolderLoading) {
                ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);

            }else if (holder instanceof ViewHolderText) {
                String note = (String) mValues.get(position);
                ViewHolderText noteholder = (ViewHolderText) holder;
                noteholder.textview.setText(note);


            }
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void setList(ArrayList<Object> threadslist) {
            this.mValues = threadslist;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView author;
            final TextView views;
            final TextView replies;
            final TextView comment;
            final TextView date;
            final ImageView avatar;
            final ImageView like;
            final ImageView sticky;
            final View threadcard;
            final View userframe;
            public Simplethread mitem;

            ViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.ttitleph);
                author = (TextView) view.findViewById(R.id.tauthorph);
                views = (TextView) view.findViewById(R.id.viewsph);
                replies = (TextView) view.findViewById(R.id.repliesph);
                comment = (TextView) view.findViewById(R.id.report);
                date = view.findViewById(R.id.ttimeph);
                avatar = view.findViewById(R.id.avatarph);
                like = view.findViewById(R.id.likeph);
                sticky = view.findViewById(R.id.sticky);
                threadcard = view.findViewById(R.id.threadcard);
                userframe = view.findViewById(R.id.user_frame);
            }
        }


        public class ViewHolderLoading extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public ViewHolderLoading(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
            }
        }

        public class ViewHolderText extends RecyclerView.ViewHolder {
            public TextView textview;

            public ViewHolderText(View view) {
                super(view);
                textview =  view.findViewById(R.id.mytextholder);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mShimmerViewContainer != null)
            mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
