package tech.sabtih.forumapp.adapters;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.models.Forum;

import java.util.List;


public class MyForumsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Forum> mValues;
    private final OnForumsListInteractionListener mListener;

    public MyForumsRecyclerViewAdapter(List<Forum> items, OnForumsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_forum_link, parent, false);
            return new ViewHolderLink(view);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_forums, parent, false);
            return  new ViewHolderForum(view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        Forum f = mValues.get(position);
        if(f.getType().equals("link")){
            return 1;
        }else{
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == 1){
            initForumLink((ViewHolderLink) holder,position);
        }else {
            initForum((ViewHolderForum) holder,position);
        }


    }

    private void initForum(final ViewHolderForum holder, int position){
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(""+mValues.get(position).getTitle());
        if(holder.mItem.getMessages() == -1){
            holder.messages.setText("-");
        }else {
            holder.messages.setText("" + holder.mItem.getMessages());
        }

        if(holder.mItem.getDiscussions() == -1) {
            holder.discussions.setText("-");
        }else{
            holder.discussions.setText("" + holder.mItem.getDiscussions());
        }
        holder.latestdate.setText(holder.mItem.getLatestdate());
        if(holder.mItem.isUnread()){
            holder.frmimage.setImageResource(R.drawable.chat_unread);
          //  holder.frmimage.setColorFilter(ContextCompat.getColor(holder.mView.getContext(), R.color.frm_unread), PorterDuff.Mode.SRC_IN);
            holder.mIdView.setTypeface(null, Typeface.BOLD);


        }else{
            holder.frmimage.setImageResource(R.drawable.chat_read);
            holder.mIdView.setTypeface(null, Typeface.NORMAL);
           // holder.frmimage.setColorFilter(ContextCompat.getColor(holder.mView.getContext(), R.color.frm_read), PorterDuff.Mode.SRC_IN);
        }

        // holder.mContentView.setText(mValues.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onForumModelInteraction(holder.mItem);
                }
            }
        });
    }

    private void initForumLink(final ViewHolderLink holder, int position){


        holder.mIdView.setText(""+mValues.get(position).getTitle());



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onForumModelInteraction(holder.mItem);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolderForum extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView discussions;
        public final TextView messages;
        public final TextView latestdate;
        public final TextView latestlbl;
        public final ImageView frmimage;

        //public final TextView mContentView;
        public Forum mItem;

        public ViewHolderForum(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.forum_title);
            discussions = view.findViewById(R.id.discuss);
            messages = view.findViewById(R.id.msgs);
            latestdate = view.findViewById(R.id.fr_latest);
            latestlbl = view.findViewById(R.id.latestlbl);
            frmimage = view.findViewById(R.id.frm_img);

            //mContentView = (TextView) view.findViewById(R.id.forum_id);
        }


    }
    public class ViewHolderLink extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;

        public final TextView latestdate;
        public final ImageView frmimage;

        //public final TextView mContentView;
        public Forum mItem;

        public ViewHolderLink(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.forum_title);

            latestdate = view.findViewById(R.id.fr_latest);
            frmimage = view.findViewById(R.id.frm_img);

            //mContentView = (TextView) view.findViewById(R.id.forum_id);
        }


    }
}
