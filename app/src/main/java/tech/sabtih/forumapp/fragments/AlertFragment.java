package tech.sabtih.forumapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import tech.sabtih.forumapp.Login;
import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.MyAlertRecyclerViewAdapter;
import tech.sabtih.forumapp.adapters.MyForumsRecyclerViewAdapter;
import tech.sabtih.forumapp.dummy.dummy.DummyContent;
import tech.sabtih.forumapp.dummy.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.models.Alert;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Forumcategory;
import tech.sabtih.forumapp.models.user.Simpleuser;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAlertInteractionListener}
 * interface.
 */
public class AlertFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnAlertInteractionListener mListener;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlertFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlertFragment newInstance(int columnCount) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("cookies", MODE_PRIVATE);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            Context context = view.getContext();
          //  RecyclerView recyclerView = (RecyclerView) view;
            recyclerView =(RecyclerView) view;
            if (mColumnCount <= 1) {
        //        recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
         //       recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
          //  recyclerView.setAdapter(new MyAlertRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        new getAlerts().execute();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAlertInteractionListener) {
            mListener = (OnAlertInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInboxInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class getAlerts extends AsyncTask<Void, Void, ArrayList<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Object> alrts) {
            super.onPostExecute(alrts);
            if(alrts != null) {
                MyAlertRecyclerViewAdapter adapter = new MyAlertRecyclerViewAdapter(alrts, mListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            //

            //  recyclerView.setAdapter(new MyForumsRecyclerViewAdapter(forums, mListener));
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... voids) {


            try {
                ArrayList<Object> alerts = new ArrayList<>();


                //Connect to the website
                Document document = null;
                document = Jsoup.connect("http://" + getString(R.string.url) + "/account/alerts")
                        .data("xf_session", sharedPreferences.getString("xf_session", ""))
                        .cookie("xf_session", sharedPreferences.getString("xf_session", "")).cookie("xf_user", sharedPreferences.getString("xf_user", ""))
                        .get();
                //  System.out.println("sessoin: " + sharedPreferences.getString("xf_session",""));
                Element alertssection = document.select(".alertsScroller").first();


                Elements alertsgroups = alertssection.children();

                for (Element ag : alertsgroups) {

                    if (ag.hasClass("alertGroup")) {
                        String heading = ag.select(".textHeading").text();
                        alerts.add(heading);
                        Elements alertslist = ag.select("ol").first().children();
                        for (Element al : alertslist) {
                            String userid = al.select("a").first().attr("href").split("\\.")[1].replace("/", "").trim();
                            String useravatar = al.select("img").attr("src");
                            String username = al.select("h3").select("a").first().text();
                            Simpleuser su = new Simpleuser(Integer.parseInt(userid), username, useravatar);

                           // al.select("h3").select("a").first().remove();
                            String alertid = al.attr("id").replace("alert","").trim();
                            String date = al.select(".timeRow").text();
                            String alerttext = al.select("h3").html();
                            String alertlink = al.select("h3").select(".PopupItemLink").attr("href");
                            Alert alrt = new Alert(Integer.parseInt(alertid),su,alerttext,date);
                            alrt.setLink(alertlink);
                            alerts.add(alrt);


                        }





                    }


                }

                return alerts;


            }catch (final HttpStatusException e) {
                e.printStackTrace();
                if (e.getStatusCode() == 403) {
                    Log.d("Alert_Error",e.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final Intent intent = new Intent(getActivity(), Login.class);
                            intent.putExtra("type", "login");
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("You must be logged in to do that.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            getActivity(). finish();
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();

                        }
                    });
                    return null;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
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
    public interface OnAlertInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
