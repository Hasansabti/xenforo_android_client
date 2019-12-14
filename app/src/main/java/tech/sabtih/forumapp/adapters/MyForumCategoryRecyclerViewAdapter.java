package tech.sabtih.forumapp.adapters;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.sabtih.forumapp.Myforums;
import tech.sabtih.forumapp.R;

import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.models.Forumcategory;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnForumsListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyForumCategoryRecyclerViewAdapter extends RecyclerView.Adapter<MyForumCategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Forumcategory> mValues;
    private final OnForumsListInteractionListener mListener;

    public MyForumCategoryRecyclerViewAdapter(List<Forumcategory> items, OnForumsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_forumcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
       // holder.mIdView.setText(mValues.get(position).id);
      //  holder.mContentView.setText(mValues.get(position).content);
        holder.title.setText(holder.mItem.getTitle());

     //   holder.flist.setLayoutManager(new LinearLayoutManager(holder.flist.getContext()));
     //   holder.flist.setAdapter(new MyForumsRecyclerViewAdapter(holder.mItem.getChilds(),(Myforums)mListener));
     //   holder.flist.addItemDecoration(new DividerItemDecoration(holder.flist.getContext(), DividerItemDecoration.VERTICAL));
        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFeedInteraction(holder.mItem);
                }
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
      //  public final RecyclerView flist;
      //  public final TextView mContentView;
        public Forumcategory mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = view.findViewById(R.id.cattitle);
            //mIdView = (TextView) view.findViewById(R.id.item_number);
          //  mContentView = (TextView) view.findViewById(R.id.content);
          //  flist = view.findViewById(R.id.forums_list);
          //  if(mItem != null)
           // flist.initForums(mItem.getChilds());
        }


    }
}
