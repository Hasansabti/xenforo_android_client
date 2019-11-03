package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.delight.android.webview.AdvancedWebView;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.ProfileAboutListAdapter;
import tech.sabtih.forumapp.models.user.UserInfo;
import tech.sabtih.forumapp.utils.MyWebViewClient;


public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private UserInfo userInfo;
    private String mParam2;
    RecyclerView aboutlist;
    RecyclerView interact;
    AdvancedWebView about;

   // private OnFragmentInteractionListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(UserInfo param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.setUserInfo(param1);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
         //   userInfo = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);


        interact = view.findViewById(R.id.interactlist);
        aboutlist = view.findViewById(R.id.aboutlist);
        about = view.findViewById(R.id.userabout);

        ProfileAboutListAdapter abadapter = new ProfileAboutListAdapter(userInfo.getAboutpairs());
        ProfileAboutListAdapter interactad = new ProfileAboutListAdapter(userInfo.getInteractpairs());

        interact.setLayoutManager(new LinearLayoutManager(interact.getContext()));
        aboutlist.setLayoutManager(new LinearLayoutManager(aboutlist.getContext()));

        interact.setAdapter(interactad);
        aboutlist.setAdapter(abadapter);
about.setWebViewClient(new MyWebViewClient(getContext()));

        about.loadHtml(userInfo.getAbout());


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    //    if (mListener != null) {
        //    mListener.onFragmentInteraction(uri);
     //   }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     //   if (context instanceof OnFragmentInteractionListener) {
     //       mListener = (OnFragmentInteractionListener) context;
      //  } else {
      //      throw new RuntimeException(context.toString()
      //              + " must implement OnFragmentInteractionListener");
      //  }
    }

    @Override
    public void onDetach() {
        super.onDetach();
     //   mListener = null;
    }


}
