package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.sabtih.forumapp.adapters.MyProfilepostrepliesRecyclerViewAdapter;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.dummy.DummyContent;
import tech.sabtih.forumapp.dummy.DummyContent.DummyItem;
import tech.sabtih.forumapp.listeners.OnProfilePostInteractionListener;
import tech.sabtih.forumapp.models.Profilepost;
import tech.sabtih.forumapp.models.Profilepostmessage;
import tech.sabtih.forumapp.models.user.Simpleuser;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnProfilePostInteractionListener}
 * interface.
 */
public class ProfilepostrepliesFragment extends DialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnProfilePostInteractionListener mListener;
    Profilepost pp;
    SharedPreferences sharedPreferences;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfilepostrepliesFragment(Profilepost pp) {
    this.pp = pp;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProfilepostrepliesFragment newInstance(int columnCount, Profilepost pp) {
        ProfilepostrepliesFragment fragment = new ProfilepostrepliesFragment(pp);
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
        View view = inflater.inflate(R.layout.fragment_profilepostreplies_list, container, false);

        // Set the adapter
        if(pp.getReplies() != null){
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

                recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new MyProfilepostrepliesRecyclerViewAdapter(pp.getReplies(), mListener));
        }
        }else{
getPPRepliies(""+pp.getId(),view);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfilePostInteractionListener) {
            mListener = (OnProfilePostInteractionListener) context;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }
    OkHttpClient client = new OkHttpClient().newBuilder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    final ArrayList<Cookie> oneCookie = new ArrayList<>(1);
                    oneCookie.add(createNonPersistentCookie("xf_session", sharedPreferences.getString("xf_session", "")));
                    oneCookie.add(createNonPersistentCookie("_ga", sharedPreferences.getString("_ga", "")));
                    oneCookie.add(createNonPersistentCookie("_gid", sharedPreferences.getString("_gid", "")));
                    oneCookie.add(createNonPersistentCookie("_gat", "1"));
                    oneCookie.add(createNonPersistentCookie("xf_user", sharedPreferences.getString("xf_user", "")));
                    return oneCookie;
                }
            })
            .build();
    public void getPPRepliies(String ppid, final View view){
        Request request = new Request.Builder()
                .url("http://" + getString(R.string.url) + "/profile-posts/"+ppid+"/comments?_xfToken="+sharedPreferences.getString("_xfToken", "")+"&_xfResponseType=json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                try {
                    ArrayList<Profilepostmessage> reps = null;
                    JSONObject json = new JSONObject(myResponse);
                    Document doc = Jsoup.parse(StringEscapeUtils.unescapeJson( json.getString("comments").replace("[","").replace("]","")));
                    if (!sharedPreferences.getString("xf_user", "").isEmpty()) {
                        int alerts = json.getInt("_visitor_alertsUnread");
                        int inbox = json.getInt("_visitor_conversationsUnread");
                    }
                    for(Element ppr : doc.body().select("li")){

                        if(ppr.hasClass("comment") && !ppr.attr("id").contains("Submit")){
                            if(reps == null){
                                reps = new ArrayList<>();
                            }


                            String pprid = ppr.attr("id").split("post-comment-")[1].trim();

                            String ppruserid = ppr.select("a").first().attr("href").split("\\.")[1].replace("/", "").trim();
                            String pprname = ppr.select(".commentContent").select(".username").first().text();
                            String ppravatar = ppr.select("img").first().attr("src");
                            Simpleuser ppruser = new Simpleuser(Integer.parseInt(ppruserid), pprname, ppravatar,0);

                            String pprtime = ppr.select(".DateTime").first().text();
                            String pprmessage = ppr.select("article").html();
                            int pprlikes = ppr.select(".LikeText").select("a").size();

                            Profilepostmessage ppmsg = new Profilepostmessage(Integer.parseInt(pprid),ppruser,pprtime,pprmessage,pprlikes);
                            reps.add(ppmsg);
                        }
                    }

                    final ArrayList<Profilepostmessage> finalReps = reps;
                    if(finalReps != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (view instanceof RecyclerView) {
                                Context context = view.getContext();
                                RecyclerView recyclerView = (RecyclerView) view;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                //linearLayoutManager.setReverseLayout(true);
                                linearLayoutManager.setStackFromEnd(true);

                                recyclerView.setLayoutManager(linearLayoutManager);

                                recyclerView.setAdapter(new MyProfilepostrepliesRecyclerViewAdapter(finalReps, mListener));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public Cookie createNonPersistentCookie(String name, String value) {
        return new Cookie.Builder()
                .domain(getString(R.string.url))
                .path("/chat")
                .name(name)
                .value(value)
                .httpOnly()
                // .secure()
                .build();
    }
    //comments url : http://itsjerryandharry.com/profile-posts/278392/comments?before=1571256747&_xfRequestUri=%2Fmembers%2Fitsharry.1%2F&_xfNoRedirect=1&_xfToken=7790%2C1572708206%2Ce58e7b979666aa86e57d60408282d06027ab3b46&_xfResponseType=json
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

}
