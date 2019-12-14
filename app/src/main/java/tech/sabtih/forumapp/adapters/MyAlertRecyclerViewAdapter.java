package tech.sabtih.forumapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import tech.sabtih.forumapp.Alerts;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadDetailActivity;
import tech.sabtih.forumapp.fragments.AlertFragment.OnAlertInteractionListener;
import tech.sabtih.forumapp.dummy.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.fragments.ThreadDetailFragment;
import tech.sabtih.forumapp.models.Alert;
import tech.sabtih.forumapp.models.Forumcategory;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnAlertInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAlertRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> mValues;
    private final OnAlertInteractionListener mListener;

    public MyAlertRecyclerViewAdapter(List<Object> items, OnAlertInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView. ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_alert, parent, false);
            return new ViewHolder(view);
        }else if(viewType == 2) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_forumcategory, parent, false);
            return new ViewHolderCategory(view);
        }else{
            return null;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position) instanceof Alert) {


                return 1;
            } else {
                return 2;
            }

    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rvholder, int position) {
        if(mValues.get(position) instanceof Alert){
            final ViewHolder holder = (ViewHolder) rvholder;
            final Alert alert = (Alert) mValues.get(position);
            holder.mItem = alert;
            Picasso.get().load("http://" + ((Alerts) mListener).getString(R.string.url) + "/" + alert.getUser().getAvatar()).into(holder.avatar);
            holder.date.setText(alert.getDate());
           // holder.username.setText(alert.getUser().getName());
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.message.setText(Html.fromHtml(alert.getAlertext(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.message.setText(Html.fromHtml(alert.getAlertext()));
            }

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Alert_link",alert.getLink());
                    Intent intent = new Intent(((Alerts)mListener), ThreadDetailActivity.class);
                    intent.putExtra(ThreadDetailFragment.ARG_ITEM_ID, "" + alert.getLink());
                    //intent.putExtra("pages",""+item.getPages());
                    ((Alerts)mListener).startActivity(intent);
                }
            });
        }else if(mValues.get(position) instanceof String){
            ViewHolderCategory holder = (ViewHolderCategory) rvholder;
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
        public final TextView mIdView;
        public final TextView mContentView;
        public Alert mItem;

       // final TextView username;
        final TextView date;
        final TextView message;

        final CircleImageView avatar;

        final View usercont;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);


          //  username = view.findViewById(R.id.msguser);
            date = view.findViewById(R.id.msg_date);
            message = view.findViewById(R.id.msg);
            avatar = (CircleImageView) view.findViewById(R.id.profilepic);

            usercont = view.findViewById(R.id.usercontainer);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
