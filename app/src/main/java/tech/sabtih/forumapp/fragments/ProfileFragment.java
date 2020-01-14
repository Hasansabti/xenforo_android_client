package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.adapters.MyProfilepostRecyclerViewAdapter;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.MyRanksAdapter;
import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnProfileInteractionListener;
import tech.sabtih.forumapp.models.user.User;
import tech.sabtih.forumapp.models.user.UserRank;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnProfileInteractionListener}
 * interface.
 */
public class ProfileFragment extends Fragment {


    TextView username;
    TextView customt;
    TextView status;
    TextView points;
    TextView followers;
    TextView following;
    TextView lastseen;
    ImageView avatar;
    RecyclerView postsrv;
RecyclerView ranks;
    MyProfilepostRecyclerViewAdapter adapter;
    MyRanksAdapter radapter;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnProfileInteractionListener mListener;
    User user;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfileFragment(User user) {
        this.user = user;

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProfileFragment newInstance(int columnCount, User user) {
        ProfileFragment fragment = new ProfileFragment(user);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        username = view.findViewById(R.id.username);
        customt = view.findViewById(R.id.custitle);
        status = view.findViewById(R.id.stat);
        points = view.findViewById(R.id.user_points);
        followers = view.findViewById(R.id.user_followers);
        following = view.findViewById(R.id.user_following);
        avatar = view.findViewById(R.id.avatarph);
        postsrv = view.findViewById(R.id.profileposts);
        lastseen = view.findViewById(R.id.lastseen);
        ranks = view.findViewById(R.id.ranks);
        setUser(user);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            /// recyclerView.setAdapter(new MyProfilepostRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileInteractionListener) {
            mListener = (OnProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameChangeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public void setUser(User user) {
        customt.setText(user.getTitle());
        username.setText(user.getName());
        status.setText(user.getStatus());
        following.setText("" + user.getFollowing());
        followers.setText("" + user.getFollowers());
        points.setText("" + user.getMessages());
        lastseen.setText(user.getLastActivity());
        Picasso.get().load(user.getAvatar()).into(avatar);


        adapter = new MyProfilepostRecyclerViewAdapter(user.getProfileposts(), ((ProfileActivity)mListener));
        radapter = new MyRanksAdapter(user.getRanks(), (mListener));

        LinearLayoutManager layout = new LinearLayoutManager(postsrv.getContext());
        StaggeredGridLayoutManager rlayout = new StaggeredGridLayoutManager(2,1);


        //layout
        postsrv.setLayoutManager(layout);
        postsrv.setAdapter(adapter);

        ranks.setLayoutManager(rlayout);
        ranks.setAdapter(radapter);


        Log.d("Cover",getString(R.string.url)+"/"+ user.getCover());



    }

}
