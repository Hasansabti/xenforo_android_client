package tech.sabtih.forumapp.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.fragments.ProfileFragment;
import tech.sabtih.forumapp.listeners.OnProfileInteractionListener;
import tech.sabtih.forumapp.models.Forumcategory;
import tech.sabtih.forumapp.models.user.UserRank;

public class MyRanksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final List<UserRank> mValues;
    private final OnProfileInteractionListener mListener;

    public MyRanksAdapter(List<UserRank> items, OnProfileInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView. ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_rank, parent, false);
            return new ViewHolder(view);
        }else if(viewType == 2) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_rank, parent, false);
            return new ViewHolder(view);
        }else{
            return null;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position) instanceof UserRank) {


            return 1;
        } else {
            return 2;
        }

    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rvholder, int position) {
        if(mValues.get(position) instanceof UserRank){
            final ViewHolder holder = (ViewHolder) rvholder;
            final UserRank rank = (UserRank) mValues.get(position);
            holder.mItem = rank;


            holder.mContentView.setText(rank.getTitle());
          //  holder.mContentView.setTextColor(Color.parseColor(""+rank.getColor()));

            ViewCompat.setBackgroundTintList(holder.mContentView,ColorStateList.valueOf(Color.parseColor(rank.getColor())));

        }else if(mValues.get(position) instanceof UserRank){
            MyAlertRecyclerViewAdapter.ViewHolderCategory holder = (MyAlertRecyclerViewAdapter.ViewHolderCategory) rvholder;
            holder.title.setText(""+mValues.get(position));


        }
        //  holder.mItem = mValues.get(position);
        //  // holder.mIdView.setText(mValues.get(position).id);
        // holder.mContentView.setText(mValues.get(position).content);

        //   rvholder.mView.setOnClickListener(new View.OnClickListener() {
        //      @Override
        //     public void onClick(View v) {
        //           if (null != mListener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //  mListener.onListFeedInteraction(holder.mItem);
        //           }
        //        }
        //    });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolderCategory extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;

        //  public final TextView mContentView;
        public Forumcategory mItem;


        public ViewHolderCategory(View view) {
            super(view);
            mView = view;
            title = view.findViewById(R.id.cattitle);

        }


    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mContentView;
        public UserRank mItem;





        public ViewHolder(View view) {
            super(view);
            mView = view;

            mContentView = (TextView) view.findViewById(R.id.mRank);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
