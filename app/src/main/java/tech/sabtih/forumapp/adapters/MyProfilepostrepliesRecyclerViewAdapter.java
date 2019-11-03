package tech.sabtih.forumapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;

import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnProfilePostInteractionListener;
import tech.sabtih.forumapp.models.Profilepost;
import tech.sabtih.forumapp.models.Profilepostmessage;
import tech.sabtih.forumapp.models.user.Simpleuser;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnProfilePostInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProfilepostrepliesRecyclerViewAdapter extends RecyclerView.Adapter<MyProfilepostrepliesRecyclerViewAdapter.ViewHolder> {

    private final List<Profilepostmessage> mValues;
    private final OnProfilePostInteractionListener mListener;

    public MyProfilepostrepliesRecyclerViewAdapter(List<Profilepostmessage> items, OnProfilePostInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profilepostreplies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        // holder.mIdView.setText(mValues.get(position).id);
        // holder.mContentView.setText(mValues.get(position).content);
        holder.username.setText(mValues.get(position).getUser().getName());
        holder.date.setText(mValues.get(position).getTime());
        //holder.message.setText(mValues.get(position).getMessage());
        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.message.setText(Html.fromHtml(holder.mItem.getMessage(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.message.setText(Html.fromHtml(holder.mItem.getMessage()));
        }
        Picasso.get().load("http://" + ((ProfileActivity) mListener).getString(R.string.url) + "/" + mValues.get(position).getUser().getAvatar()).into(holder.avatar);

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onProfilePostInteraction(holder.mItem);
                }
            }
        });
        */

        holder.usercont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.viewPPUser(holder.mItem.getUser());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final TextView username;
        final TextView date;
        final TextView message;

        final CircleImageView avatar;
        final TextView like;
        final TextView report;
        final ImageView repliesbtn;
        final View usercont;



        // public final TextView mIdView;
        //public final TextView mContentView;
        public Profilepostmessage mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            username = view.findViewById(R.id.msguser);
            date = view.findViewById(R.id.msg_date);
            message = view.findViewById(R.id.msg);
            avatar = (CircleImageView) view.findViewById(R.id.profilepic);
            like = view.findViewById(R.id.msg_like);
            report = view.findViewById(R.id.msg_report);

            // mIdView = (TextView) view.findViewById(R.id.item_number);
            //  mContentView = (TextView) view.findViewById(R.id.content);
            repliesbtn = view.findViewById(R.id.repliesbtn);

            usercont = view.findViewById(R.id.usercontainer);
        }

        // @Override
        //  public String toString() {
        //      return super.toString() + " '" + mContentView.getText() + "'";
        //  }
    }

}
