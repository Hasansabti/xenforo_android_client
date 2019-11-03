package tech.sabtih.forumapp.adapters;

import android.os.Build;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.sabtih.forumapp.fragments.ThreadDetailFragment;
import tech.sabtih.forumapp.utils.PicassoImageGetter;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.fragments.shoutbox;
import tech.sabtih.forumapp.listeners.OnChatInteractionListener;
import tech.sabtih.forumapp.models.Chatmsg;

public class MyChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Chatmsg> mValues;
    private final OnChatInteractionListener mListener;
    private String selectedid;

    public MyChatAdapter(List<Chatmsg> items, OnChatInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sent, parent, false);
            return new ViewHolderMe(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_recieved, parent, false);
            return new ViewHolderThem(view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        Chatmsg m = mValues.get(position);
        if (m.isMe()) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == 1) {
            initMe((ViewHolderMe) holder, position);
        } else {
            initOthers((ViewHolderThem) holder, position);
        }


    }

    private void initMe(final ViewHolderMe holder, int position) {
        holder.mItem = mValues.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.msg.setText(Html.fromHtml(holder.mItem.getMsg(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.msg.setText(Html.fromHtml(holder.mItem.getMsg()));
        }

        Date date = new Date((Long.parseLong( holder.mItem.getDate())*1000));
        DateFormat formatter = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a");
        if(!DateUtils.isToday(date.getTime())){
            formatter = new SimpleDateFormat("MMM dd, YYYY hh:mm a");
        }else{
            formatter = new SimpleDateFormat(" hh:mm a");
        }

        //formatter.setTimeZone(TimeZone.getTimeZone("GMT+0300"));
        String dateFormatted = formatter.format(date);
        holder.time.setText(dateFormatted);
        holder.mView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("Message Tools");
                contextMenu.add(0, view.getId(),0,"Delete Message");
            }

        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                selectedid = holder.mItem.getMsgid();
                return false;
            }
        });

    }

    private void initOthers(final ViewHolderThem holder, int position) {
        holder.mItem = mValues.get(position);
       // holder.msg.setText(holder.mItem.getMsg());

  /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.msg.setText(Html.fromHtml(holder.mItem.getMsg(),new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String s) {
                    return null;
                }
            }, Html.FROM_HTML_MODE_COMPACT));
        } else {
         */
  holder.msg.setMovementMethod(LinkMovementMethod.getInstance());
  holder.msg.setText(Html.fromHtml(holder.mItem.getMsg(), new PicassoImageGetter(holder.msg,((shoutbox)mListener).getResources(),Picasso.get()), null));

       // }

        Date date = new Date((Long.parseLong( holder.mItem.getDate())*1000));


        DateFormat formatter = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a");
        String dateFormatted = "";
        if(!DateUtils.isToday(date.getTime())){
           // formatter = new SimpleDateFormat("MMM dd, YYYY hh:mm a");
            dateFormatted = ThreadDetailFragment.getDisplayableTime(date.getTime());
        }else{
            formatter = new SimpleDateFormat(" hh:mm a");
            dateFormatted = formatter.format(date);
        }

        //formatter.setTimeZone(TimeZone.getTimeZone("GMT+0300"));


        holder.name.setText(holder.mItem.getUser().getName());
        holder.time.setText(dateFormatted);
        Picasso.get().load("http://" + ((shoutbox)mListener).getActivity().getString(R.string.url) + "/" + holder.mItem.getUser().getAvatar()).into(holder.avatar);

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setItems(ArrayList<Chatmsg> messages) {
        this.mValues = messages;
    }

    public class ViewHolderMe extends RecyclerView.ViewHolder {
        public final View mView;
        TextView msg;
        TextView time;
        //public final TextView mContentView;
        public Chatmsg mItem;

        public ViewHolderMe(View view) {
            super(view);
            mView = view;
            time = view.findViewById(R.id.time);
            msg = view.findViewById(R.id.message_body);
            //mContentView = (TextView) view.findViewById(R.id.forum_id);
        }


    }

    public String getSelectedID() {
        return selectedid;
    }

    public class ViewHolderThem extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView avatar;
        TextView name;
        TextView msg;
        TextView time;



        //public final TextView mContentView;
        public Chatmsg mItem;

        public ViewHolderThem(View view) {
            super(view);
            mView = view;
            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            msg = view.findViewById(R.id.message_body);



            //mContentView = (TextView) view.findViewById(R.id.forum_id);
        }


    }
}