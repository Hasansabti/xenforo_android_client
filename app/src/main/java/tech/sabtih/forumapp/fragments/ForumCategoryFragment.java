package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import tech.sabtih.forumapp.Myforums;
import tech.sabtih.forumapp.adapters.MyForumCategoryRecyclerViewAdapter;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.MyForumsRecyclerViewAdapter;
import tech.sabtih.forumapp.dummy.DummyContent;
import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Forumcategory;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnForumsListInteractionListener}
 * interface.
 */
public class ForumCategoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    private OnForumsListInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForumCategoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ForumCategoryFragment newInstance(int columnCount) {
        ForumCategoryFragment fragment = new ForumCategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_forumcategory_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
               //
            } else {
              //  recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
          //  recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                    ((Myforums)mListener).onNewsScrollchanged(newState);

                    super.onScrollStateChanged(recyclerView, newState);


                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView,dx, dy);
                    ((Myforums)mListener).onNewsScrolled(dy);
                }
            });
          //  recyclerView.setAdapter(new MyForumCategoryRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        if(isAdded())
        new getFCat().execute();
        return view;
    }

    public void initFcats() {

        if(isAdded())
        new getFCat().execute();
    }

    private class getFCat extends AsyncTask<Void,Void, ArrayList<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Object> forums) {
            super.onPostExecute(forums);


            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            recyclerView.setAdapter(new MyForumsRecyclerViewAdapter(forums, mListener));
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... voids) {


            try {
                ArrayList<Object> mycats = new ArrayList<>();





                //Connect to the website
                Document document = null;
                document = Jsoup.connect("http://"+getString(R.string.url)+"/forums/")
                        .data("xf_session",sharedPreferences.getString("xf_session",""))
                        .cookie("xf_session",sharedPreferences.getString("xf_session","")).cookie("xf_user",sharedPreferences.getString("xf_user",""))
                        .get();
                System.out.println("sessoin: " + sharedPreferences.getString("xf_session",""));
                Element forums= document.select(".nodeList").first();




                Elements forumslist = forums.children();

                for(Element n : forumslist){
                    if(n.hasClass("category")) {

                        int id = Integer.parseInt(n.attr("id").split("\\.")[1]);
                        String title = n.select(".categoryNodeInfo").select(".nodeTitle").text();

                        Elements childs = n.select(".nodeList").first().children();


                      //  ArrayList<Forum> myforums = new ArrayList<>();
                        Forumcategory fc = new Forumcategory(0,title,null);
                        mycats.add(fc);

                        for(Element ch : childs){
                            int cid = 0;
                            if(ch.select(".nodeTitle").first() == null)
                                continue;
                            String ctitle = ch.select(".nodeTitle").first().text();
                            int discs  = -1;
                            int msgs = -1;
                            if(ch.select(".nodeStats")!= null) {
                                if(ch.select(".nodeStats").select("dd").first()!= null) {
                                    try {
                                        discs = Integer.parseInt(ch.select(".nodeStats").select("dd").first().text().replace(",", ""));
                                        msgs = Integer.parseInt(ch.select(".nodeStats").select("dd").last().text().replace(",", ""));
                                    }catch (NumberFormatException ex){
                                        Log.e("FCAT",ex.getMessage());

                                    }
                                }
                            }
                            String type = "forum";
                            if(ch.hasClass("link")){
                                type = "link";
                                continue;
                            }




                            String url = ch.select(".nodeTitle").select("a").attr("href");
                            String latest = ch.select(".lastThreadTitle").select("a").text();
                            boolean isread = ch.select(".nodeInfo").hasClass("unread");
                            String latestdate = ch.select(".lastThreadDate").text();
                            Forum frm = new Forum(cid,ctitle,discs,msgs,url,latest,isread,latestdate,type);
                            mycats.add(frm);

                        }




                    }




                }

                return mycats;



            } catch (IOException e) {
                e.printStackTrace();
            }




            return null;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForumsListInteractionListener) {
            mListener = (OnForumsListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameChangeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
