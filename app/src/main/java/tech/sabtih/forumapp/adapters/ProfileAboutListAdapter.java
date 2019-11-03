package tech.sabtih.forumapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.sabtih.forumapp.fragments.NameChangeFragment.OnNameChangeInteractionListener;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.models.user.NameChange;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnNameChangeInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProfileAboutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  List<NameChange> mValues;
    private LinkedHashMap<String,String> kvpairs;
    private  OnNameChangeInteractionListener mListener;

    private final int VIEW_TYPE_KP = 0;
    private final int VIEW_TYPE_NAMECHANGE = 1;

    public ProfileAboutListAdapter(List<NameChange> items, OnNameChangeInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }
    public ProfileAboutListAdapter(LinkedHashMap<String,String> items) {
        kvpairs = items;
       // mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        return mValues == null ? VIEW_TYPE_KP : VIEW_TYPE_NAMECHANGE;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        if (viewType == VIEW_TYPE_KP) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_pair, parent, false);
            return new KeyValueHolder(view);
        } else if (viewType == VIEW_TYPE_NAMECHANGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_namechange, parent, false);
            return new NameChangeHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderr, int position) {
        if(holderr instanceof KeyValueHolder){
            final KeyValueHolder holder = (KeyValueHolder) holderr;

            String value = (new ArrayList<String>(kvpairs.keySet())).get(position);
            holder.key.setText(value);
            holder.value.setMovementMethod(LinkMovementMethod.getInstance());
            holder.value.setText(kvpairs.get(value));

        }else if(holderr instanceof NameChangeHolder) {
            final NameChangeHolder holder = (NameChangeHolder) holderr;
            holder.mItem = mValues.get(position);
            holder.oldname.setText(mValues.get(position).getOldname());
            holder.newname.setText(mValues.get(position).getNewname());
            holder.date.setText(mValues.get(position).getDate());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onNameChangeInteraction(holder.mItem);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mValues == null ? (kvpairs == null ? 0 : kvpairs.size()) :  mValues.size();
    }


    public class KeyValueHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView key;
        public final TextView value;

        public NameChange mItem;

        public KeyValueHolder(View view) {
            super(view);
            mView = view;
            key = (TextView) view.findViewById(R.id.key);
            value = (TextView) view.findViewById(R.id.value);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + value.getText() + "'";
        }
    }

    public class NameChangeHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView oldname;
        public final TextView newname;
        public final TextView date;
        public NameChange mItem;

        public NameChangeHolder(View view) {
            super(view);
            mView = view;
            oldname = (TextView) view.findViewById(R.id.key);
            newname = (TextView) view.findViewById(R.id.value);
            date = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + oldname.getText() + "'";
        }
    }
}
