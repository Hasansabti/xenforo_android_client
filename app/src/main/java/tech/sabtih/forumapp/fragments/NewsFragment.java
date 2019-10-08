package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tech.sabtih.forumapp.Myforums;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.listeners.EndlessRecyclerViewScrollListener;
import tech.sabtih.forumapp.adapters.MyportalRecyclerViewAdapter;
import tech.sabtih.forumapp.listeners.OnListFragmentInteractionListener;
import tech.sabtih.forumapp.models.Newsitem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsFragment extends Fragment {
    RecyclerView recyclerView;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnNewsListFragmentInteractionListener mListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Newsitem> newsitems;
    MyportalRecyclerViewAdapter adapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    private class Content extends AsyncTask<String, Void, ArrayList<Newsitem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = new ProgressDialog(MainActivity.this);
            //  progressDialog.show();

        }

        @Override
        protected ArrayList<Newsitem> doInBackground(String... args) {
            try {
                ArrayList<Newsitem> newsl = new ArrayList<>();
                //Connect to the website
                Document document = Jsoup.connect("http://"+getString(R.string.url)+"/articles/?page="+args[0]).data("xf_session",sharedPref.getString("xf_session","")).cookie("xf_session",sharedPref.getString("xf_session","")).cookie("xf_user",sharedPref.getString("xf_user","")).get();
                Element news= document.select("#recentNews").first();


                String token= document.getElementsByAttributeValue("name","_xfToken").first().attr("value");
                System.out.println("Recorded token: " + token );
                String userid = "";
                if(!token.isEmpty()){
                    userid = document.select(".fl").first().select("a").first().attr("href").split("\\.")[1].replace("/","");
                    editor.putString("user",userid);

                }
                editor.putString("_xfToken", token);
                editor.commit();

                System.out.println("User id: " + userid);

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
                        n.select(".newsText").select("img").attr("style", "max-width:100%");

                        String ntext = n.select(".newsText").html().replace("proxy.php", "http://"+getString(R.string.url)+"/proxy.php").replace(";hash","&hash").replace("styles/","http://"+getString(R.string.url)+"/styles/");
                      //  System.out.println(ntext);
                        Newsitem ni = new Newsitem(id, date, title, false, maker, comments_num, ntext, views, likes);
                        newsl.add(ni);

                    }




                }



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
            newsitems.addAll(ns);
            adapter.notifyDataSetChanged();

            if(((Myforums) mListener).fcat != null)
                ((Myforums) mListener).fcat.initFcats();

                if(((Myforums) mListener).account != null)
                ((Myforums) mListener).account.initAccount();



        }
    }



    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NewsFragment newInstance(int columnCount) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity(). getSharedPreferences("cookies", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view;
             recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                 @Override
                 public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                    mListener.onNewsScrollchanged(newState);

                     super.onScrollStateChanged(recyclerView, newState);


                 }

                 @Override
                 public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                     super.onScrolled(recyclerView,dx, dy);
                     mListener.onNewsScrolled(dy);
                 }
             });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            newsitems = new ArrayList<>();
            adapter = new MyportalRecyclerViewAdapter(newsitems, (Myforums)getActivity());

            recyclerView.setAdapter(adapter);

            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadNextDataFromApi(page);
                }
            };
            recyclerView.addOnScrollListener(scrollListener);
             new Content().execute("1");

        }
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        new Content().execute(""+(offset+1));
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewsListFragmentInteractionListener) {
            mListener = (OnNewsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnNewsListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewsListFragmentInteraction(Newsitem item);
        void onNewsScrolled(int dy);
        void onNewsScrollchanged(int state);
    }


}
