package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import tech.sabtih.forumapp.Login;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.models.Newsitem;
import tech.sabtih.forumapp.models.User;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button loginbtn;
    Button loginbtn2;
    Button logoutbtn;
    TextView username;
    TextView customt;
    TextView status;
    TextView points;
    TextView followers;
    TextView following;
    ImageView avatar;

    SharedPreferences sharedPreferences;
    View v;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("cookies", MODE_PRIVATE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    if(sharedPreferences.getString("_xfToken","").isEmpty()){
        v = inflater.inflate(R.layout.fragment_account_login, container, false);



    }else{
        v = inflater.inflate(R.layout.fragment_account, container, false);



    }


        // Inflate the layout for this fragment
        return v;
    }

    public void initAccount(){
        if(getActivity() != null) {
            if (sharedPreferences.getString("_xfToken", "").isEmpty()) {

                initVisitorViews();
                System.out.println("Not Logged in " + sharedPreferences.getString("_xfToken", ""));

            } else {

                inituserViews();


            }
        }
    }

    public void initVisitorViews(){
        loginbtn = v.findViewById(R.id.lgnbtn2);
        if(loginbtn != null) {
            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), Login.class);
                    intent.putExtra("type", "login");
                    startActivity(intent);

                }
            });
        }
    }
    public void inituserViews(){
        username = v.findViewById(R.id.username);
        customt = v.findViewById(R.id.custitle);
        status = v.findViewById(R.id.stat);
        points = v.findViewById(R.id.user_points);
        followers = v.findViewById(R.id.user_followers);
        following = v.findViewById(R.id.user_following);
        avatar = v.findViewById(R.id.avatarph);





        logoutbtn = v.findViewById(R.id.logoutbtn);
        loginbtn2 = v.findViewById(R.id.lgn1);
        if(loginbtn2 != null)
        loginbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Login.class);
                intent.putExtra("type", "login");
                startActivityForResult(intent,1);
            }
        });
        if(logoutbtn != null)
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Login.class);
                intent.putExtra("type", "logout");
                // startActivity(intent);
                startActivityForResult(intent,1);
            }
        });

        if(!sharedPreferences.getString("user","").isEmpty())
        new Accountdata().execute(sharedPreferences.getString("user",""));
    }



    private class Accountdata extends AsyncTask<String, Void, User> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = new ProgressDialog(MainActivity.this);
            //  progressDialog.show();

        }

        @Override
        protected User doInBackground(String... args) {
            try {
                ArrayList<Newsitem> newsl = new ArrayList<>();
                //Connect to the website
                Document document = Jsoup.connect("http://"+getString(R.string.url)+"/members/"+args[0]).cookie("xf_session",sharedPreferences.getString("xf_session","")).cookie("xf_user",sharedPreferences.getString("xf_user","")).get();
                String status = "";
                int id = Integer.parseInt( args[0]);
                String username = document.select(".username").first().text();
                String title = document.select(".userTitle").first().text();
                if(document.select("#UserStatus").first() != null) {
                     status = document.select("#UserStatus").first().text();
                }
                int following = Integer.parseInt( document.select(".followBlocks").first().select(".count").first().text());
                int followers = Integer.parseInt( document.select(".followBlocks").first().select(".count").last().text());
                int points = Integer.parseInt( document.select(".infoblock").first().select("dl").get(4).select("a").first().text());
                int messages = Integer.parseInt( document.select(".infoblock").first().select("dl").get(2).select("dd").first().text().replace(",",""));
                int likes = Integer.parseInt( document.select(".infoblock").first().select("dl").get(2).select("dd").first().text().replace(",",""));

                User user = new User(id,username,title,status,points,followers,following, messages);










                return  user;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user != null)

            setUser(user);
            //newsitems.addAll(ns);
            //adapter.notifyDataSetChanged();


        }
    }

    public void setUser(User user){
        customt.setText(user.getTitle());
        username.setText(user.getName());
        status.setText(user.getStatus());
        following.setText(""+user.getFollowing());
        followers.setText(""+user.getFollowers());
        points.setText(""+user.getMessages());



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            System.out.println("Logged out");
            v= setViewLayout(R.layout.fragment_account_login);
            initVisitorViews();


            mListener.loggedout();
        }else if(resultCode == RESULT_OK) {
            System.out.println("Logged in");
            v=setViewLayout(R.layout.fragment_account);
            inituserViews();
            mListener.loggedin();
        }
        System.out.println("Result" + requestCode +" " + resultCode);
    }
    private View setViewLayout(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(v);
        return v;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAccountInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sharedPreferences.getString("_xfToken","").isEmpty()){
            System.out.println("Logged out");
            v= setViewLayout(R.layout.fragment_account_login);
            initVisitorViews();
           // mListener.loggedout();
        }else{
            System.out.println("Logged out");
            v=setViewLayout(R.layout.fragment_account);
            inituserViews();
            //mListener.loggedin();
        }
        System.out.println("Token " + sharedPreferences.getString("_xfToken",""));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAccountInteraction(Uri uri);
        void loggedout();
        void loggedin();
    }
}
