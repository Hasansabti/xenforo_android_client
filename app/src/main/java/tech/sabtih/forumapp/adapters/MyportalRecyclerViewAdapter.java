package tech.sabtih.forumapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import tech.sabtih.forumapp.Myforums;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.ThreadDetailActivity;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.fragments.ThreadDetailFragment;
import tech.sabtih.forumapp.listeners.OnListFragmentInteractionListener;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.models.Newsitem;
import tech.sabtih.forumapp.utils.MyWebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Newsitem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyportalRecyclerViewAdapter extends RecyclerView.Adapter<MyportalRecyclerViewAdapter.ViewHolder> {

    private final List<Newsitem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyportalRecyclerViewAdapter(ArrayList<Newsitem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_portal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).getMaker());

        holder.mContentView.setText(mValues.get(position).getTitle());
        holder.mywv.setWebViewClient(new MyWebViewClient(((Myforums) mListener  )));
        WebSettings webSettings = holder.mywv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        holder.time.setText(mValues.get(position).getDate());

        holder.mywv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //holder.mywv.getSettings().setUseWideViewPort(true);
        holder.mywv.loadData(mValues.get(position).getText(), "text/html; charset=utf-8", "UTF-8");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Newsitem mItem;
        public WebView mywv;
        public TextView time;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mywv = view.findViewById(R.id.wvtest);
            mIdView = (TextView) view.findViewById(R.id.item_maker);
            mContentView = (TextView) view.findViewById(R.id.newstitle);
            time = view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private class MyWebViewClienttt extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().contains("itsjerryandharry.com")) {
                // This is my website, so do not override; let my WebView load the page
                              if(url.contains("members")){
                    Log.d("url","This is a member");



                    //String urlt = url.split(((ThreadDetailFragment) mListener  ). getString(R.string.url))[1];

                                  String ttl = "";
                                  String userid = "";

                                  //String urlt = url.split(((ThreadDetailFragment) mListener  ). getString(R.string.url))[1];
                                  if(url.split("members/")[1].contains(".")) {
                                      ttl = url.split("members/")[1].split("\\.")[0].replace("-", " ");
                                      userid = url.split("members/")[1].split("\\.")[1].replace("/", " ");
                                  }else{
                                      ttl = url.split("members/")[1].replace("/", " ");
                                      userid = url.split("members/")[1].replace("/", " ");
                                  }
                    Intent intent = new Intent(((Myforums) mListener  ), ProfileActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("username", ttl);
                    ((Myforums) mListener  ).startActivity(intent);
                }else if(url.contains("threads")){
                    Log.d("url","This is a thread");
                    String urlt = url.split(((Myforums) mListener  ). getString(R.string.url))[1];
                    String ttl = url.split("threads/")[1].split("\\.")[1].replace("/"," ");
                    Intent intent = new Intent(((Myforums) mListener  ), ThreadDetailActivity.class);
                    intent.putExtra("item_id", urlt);
                    intent.putExtra("item_id", ttl);
                    ((Myforums) mListener  ).startActivity(intent);

                }else if(url.contains("forums")){
                    Log.d("url","This is a forum");
                    String urlt = url.split(((Myforums) mListener  ). getString(R.string.url))[1];
                    String ttl = url.split("forums/")[1].split("\\.")[0].replace("-"," ");
                    Intent intent = new Intent(((Myforums) mListener  ), ThreadListActivity.class);
                    intent.putExtra("url", urlt);
                    intent.putExtra("title", ttl);
                    ((Myforums) mListener  ).startActivity(intent);
                }
                return true;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
            //finish();
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {


        }
    }
}
