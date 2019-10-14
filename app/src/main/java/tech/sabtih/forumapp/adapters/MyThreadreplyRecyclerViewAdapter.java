package tech.sabtih.forumapp.adapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import im.delight.android.webview.AdvancedWebView;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.fragments.ThreadDetailFragment;

import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnTReplyInteractionListener;
import tech.sabtih.forumapp.models.Threadreply;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnTReplyInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyThreadreplyRecyclerViewAdapter extends RecyclerView.Adapter<MyThreadreplyRecyclerViewAdapter.ViewHolder> {

    private final List<Threadreply> mValues;
    private final OnTReplyInteractionListener mListener;
    RecyclerView.Adapter repliesadapter;
    String script;
    Boolean nested = false;
    public MyThreadreplyRecyclerViewAdapter(List<Threadreply> items, OnTReplyInteractionListener listener,boolean nested) {
        mValues = items;
        mListener = listener;
        this.nested = nested;
        try {

        InputStream is = ((ThreadDetailFragment)mListener).getActivity().getResources().openRawResource(R.raw.xenforo);

            script = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            script = "";
        }
        // mValues.remove(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_threadreply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onTReplyInteraction(holder.mItem);
                }
            }
        });



        holder. author.setText(holder.mItem.getSu().getName());
        holder. date.setText(holder.mItem.getDate());
        holder.content.setWebViewClient(new MyWebViewClient());
        holder.content.getSettings().setJavaScriptEnabled(true);
       // holder.content.loadData(holder.mItem.getText(), "text/html; charset=utf-8", "UTF-8");
        holder.content.loadHtml(holder.mItem.getText(),"http://"+((ThreadDetailFragment)mListener).getString(R.string.url));
        holder.likes.setText(""+holder.mItem.getLikes());

        Picasso.get().load("http://" + ((ThreadDetailFragment)mListener).getActivity().getString(R.string.url) + "/" + holder.mItem.getSu().getAvatar()).into(holder.avatar);

        if(mValues.get(position).getReplies() != null){
           repliesadapter = new MyThreadreplyRecyclerViewAdapter(mValues.get(position).getReplies(),mListener, true);

            //repliesadapter = new ThreadReplyReplyAdapter(mValues.get(position).getReplies(),mListener);
            holder.replies.setLayoutManager(new LinearLayoutManager(holder.replies.getContext()));
            holder.replies.setAdapter(repliesadapter);

          //  Log.d("nestedreply",mValues.get(position).getPostid() + " has reples "+mValues.get(position).getReplies().size());
            holder.commentline.setVisibility(View.VISIBLE);

        }else{
            holder.commentline.setVisibility(View.GONE);
        }
        if(nested){
            holder.cv.setCardElevation(0);
        }




    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Threadreply mItem;
        ImageView avatar;
        TextView author;
        CardView cv;



        TextView date;
        TextView likes;
        TextView reply;
        TextView Report;
        ImageView like;
        RecyclerView replies;
        AdvancedWebView content;
        View commentline;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            avatar = mView.findViewById(R.id.avatarph);
            cv = mView.findViewById(R.id.thrdcntnt);




            author= mView.findViewById(R.id.tauthorph);
            date= mView.findViewById(R.id.ttimeph);
            likes= mView.findViewById(R.id.numlikes);
            reply= mView.findViewById(R.id.reply);
            Report= mView.findViewById(R.id.report);
            like= mView.findViewById(R.id.likeph);
            content= mView.findViewById(R.id.threadtext);
            replies = mView.findViewById(R.id.replyreplies);
            commentline = mView.findViewById(R.id.commentline);

        }


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println(url);

            if(url.contains(((ThreadDetailFragment) mListener  ). getString(R.string.url)))
                if(url.contains("members")){
                    Log.d("url","This is a member");

                }else if(url.contains("threads")){
                    Log.d("url","This is a thread");
                }else if(url.contains("forums")){
                    Log.d("url","This is a forum");
                    String urlt = url.split(((ThreadDetailFragment) mListener  ). getString(R.string.url))[1];
                    String ttl = url.split("forums/")[1].split("\\.")[0].replace("-"," ");
                    Intent intent = new Intent(((ThreadDetailFragment) mListener  ).getContext(), ThreadListActivity.class);
                    intent.putExtra("url", urlt);
                    intent.putExtra("title", ttl);
                    ((ThreadDetailFragment) mListener  ).startActivity(intent);
                }

            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {

            try {

                InputStream inputStream = ((ThreadDetailFragment) mListener  ).getContext().getAssets().open("style.css");
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
              //  view.loadUrl("javascript:"+script);



            }





    }
}
