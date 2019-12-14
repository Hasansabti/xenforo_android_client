package tech.sabtih.forumapp.adapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import im.delight.android.webview.AdvancedWebView;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadListActivity;
import tech.sabtih.forumapp.fragments.ThreadDetailFragment;

import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnTReplyInteractionListener;
import tech.sabtih.forumapp.models.Threadreply;
import tech.sabtih.forumapp.utils.MyWebViewClient;
import tech.sabtih.forumapp.utils.PicassoImageGetter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnTReplyInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyThreadreplyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Threadreply> mValues;
    private final OnTReplyInteractionListener mListener;
    //RecyclerView.Adapter repliesadapter;
    String script;
    Boolean nested = false;
    int depth = 0;



    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public MyThreadreplyRecyclerViewAdapter(List<Threadreply> items, OnTReplyInteractionListener listener, boolean nested, int depth) {
        mValues = items;
        mListener = listener;
        this.nested = nested;
        try {

            InputStream is = ((ThreadDetailFragment) mListener).getActivity().getResources().openRawResource(R.raw.xenforo);

            script = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            script = "";
        }
        // mValues.remove(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      //  return new ViewHolder(view);

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_threadreply, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }
    @Override
    public int getItemViewType(int position) {
        return mValues.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder tholder, int position) {
        if (tholder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) tholder;
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


            holder.author.setText(holder.mItem.getSu().getName());
            holder.date.setText(holder.mItem.getDate());

            // holder.content.loadData(holder.mItem.getText(), "text/html; charset=utf-8", "UTF-8");
            //Log.d("Reply",holder.mItem.getText());
            if(holder.mItem.getText().contains("bbCodeSpoilerButton") || holder.mItem.getText().contains("bbCodeQuote")){
                holder.content.setWebViewClient(new MyWebViewClient(((ThreadDetailFragment) mListener).getActivity()));
                holder.content.getSettings().setJavaScriptEnabled(true);
                holder.content.setVisibility(View.VISIBLE);
                holder.plaincontent.setVisibility(View.GONE);
                holder.content.loadHtml(holder.mItem.getText(), "http://" + ((ThreadDetailFragment) mListener).getString(R.string.url));

            }else{
                holder.content.setVisibility(View.GONE);
                holder.plaincontent.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.plaincontent.setText(Html.fromHtml(holder.mItem.getText(), new PicassoImageGetter(holder.plaincontent,((ThreadDetailFragment)mListener).getResources(),Picasso.get(),false), null));
                } else {
                    holder.plaincontent.setText(Html.fromHtml(holder.mItem.getText(), new PicassoImageGetter(holder.plaincontent,((ThreadDetailFragment)mListener).getResources(),Picasso.get(),false), null));
                }
            }

            holder.likes.setText("" + holder.mItem.getLikes());
            holder.Report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(((ThreadDetailFragment) mListener).getContext());
                    builder.setTitle("Report post");

// Set up the input
                    final EditText input = new EditText(((ThreadDetailFragment) mListener).getContext());
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

            Picasso.get().load("http://" + ((ThreadDetailFragment) mListener).getActivity().getString(R.string.url) + "/" + holder.mItem.getSu().getAvatar()).into(holder.avatar);
            holder.replies.setHasFixedSize(true);

            if (mValues.get(position).getReplies() != null && mValues.get(position).getReplies().size() > 0 && depth < 5) {
                MyThreadreplyRecyclerViewAdapter repliesadapter = new MyThreadreplyRecyclerViewAdapter(mValues.get(position).getReplies(), mListener, true,depth+1);
                repliesadapter.setHasStableIds(true);
                Log.d("repliescount",""+mValues.get(position).getReplies().size());
                Log.d("replies","Adding replies");
                //repliesadapter = new ThreadReplyReplyAdapter(mValues.get(position).getReplies(),mListener);
                holder.replies.setLayoutManager(new LinearLayoutManager(holder.replies.getContext()));
                holder.replies.setAdapter(repliesadapter);

                //  Log.d("nestedreply",mValues.get(position).getPostid() + " has reples "+mValues.get(position).getReplies().size());
                holder.commentline.setVisibility(View.VISIBLE);
                repliesadapter.notifyDataSetChanged();

            } else {
               // holder.replies.setLayoutManager(new LinearLayoutManager(holder.replies.getContext()));
               // repliesadapter = null;
                //holder.replies = null;
                holder.commentline.setVisibility(View.GONE);
            }
            if (nested) {
                holder.cv.setCardElevation(0);
            }

            holder.userholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.viewUser(holder.mItem.getSu());
                }
            });
        } else if (tholder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) tholder;
            loadingViewHolder.progressBar.setIndeterminate(true);

        }

    }

    @Override
    public long getItemId(int position) {
        if(mValues.get(position) != null)
        return mValues.get(position).getId();
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Threadreply> getValues() {
        return mValues;
    }

    public void setValues(ArrayList<Threadreply> repliesarr) {
        this.mValues = repliesarr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Threadreply mItem;
        ImageView avatar;
        TextView author;
        CardView cv;

        View userholder;


        TextView date;
        TextView likes;
        TextView reply;
        TextView Report;
        ImageView like;
        RecyclerView replies;
        AdvancedWebView content;
        TextView plaincontent;
        View commentline;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            avatar = mView.findViewById(R.id.avatarph);
            cv = mView.findViewById(R.id.thrdcntnt);
            plaincontent = view.findViewById(R.id.plaintext);

            userholder = view.findViewById(R.id.userholder);


            author = mView.findViewById(R.id.tauthorph);
            date = mView.findViewById(R.id.ttimeph);
            likes = mView.findViewById(R.id.numlikes);
            reply = mView.findViewById(R.id.reply);
            Report = mView.findViewById(R.id.report);
            like = mView.findViewById(R.id.likeph);
            content = mView.findViewById(R.id.threadtext);
            replies = mView.findViewById(R.id.replyreplies);
            commentline = mView.findViewById(R.id.commentline);

        }


    }

    public class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }


}
