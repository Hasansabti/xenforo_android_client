package tech.sabtih.forumapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.adapters.MyChatAdapter;
import tech.sabtih.forumapp.listeners.OnAlertsupdatedListener;
import tech.sabtih.forumapp.listeners.OnChatInteractionListener;
import tech.sabtih.forumapp.models.Chatmsg;
import tech.sabtih.forumapp.models.user.Simpleuser;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link shoutbox.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class shoutbox extends Fragment implements OnChatInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageButton sendbtn;
    EditText msgbox;
    ProgressBar sending;
    ImageButton tools;

    PopupWindow popupWindow;

    Map<String,String> emojis = new HashMap<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    private RecyclerView rv;
    private MyChatAdapter chatadapter;
    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    SharedPreferences spchat;
    ArrayList<Chatmsg> messages = new ArrayList<>();
    private int lastupdate = 0;
    private int onusers;
    View chateditor;

    public shoutbox() {
        // Required empty public constructor
        emojis.put(":cool:","&#128526");
        emojis.put(":smile:","&#128578");
        emojis.put(":what:","&#129320");
        emojis.put(":wink:","&#128521");
        emojis.put(":frown:","&#128543");
        emojis.put(":mad:","&#129324");
        emojis.put(":confused:","&#128533");
        emojis.put(":tongue:","&#128541");
        emojis.put(":grin:","&#128513");
        emojis.put(":eek:","&#128561");
        emojis.put(":oops:","&#128559");
        emojis.put(":rolleyes:","&#128580");
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sharedPreferences = getActivity().getSharedPreferences("cookies", MODE_PRIVATE);
        spchat = getActivity().getSharedPreferences("chat", MODE_PRIVATE);
        mHandler = new Handler();


    }
    //post: http://itsjerryandharry.com/taigachat/post.json

    /*

    message:  :what:
sidebar: 0
lastrefresh: 168393
color: 41BD50
room: 1
_xfRequestUri: /chat/
_xfNoRedirect: 1
_xfToken: 7790,1570988578,34147d550277de1097a78a60d719a9c1bd47f38f
_xfResponseType: json



delete:http://itsjerryandharry.com/taigachat/168394/delete
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_shoutbox, container, false);

        //new listchat().execute();
        tools = v.findViewById(R.id.tools);
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(tools);
            }
        });
        rv = v.findViewById(R.id.messages_view);
        //startRepeatingTask();
        sending = v.findViewById(R.id.sending);
        msgbox = v.findViewById(R.id.msgfield);
        msgbox.setTextColor(spchat.getInt("chatcolor", 0xFF000000));
        chateditor = v.findViewById(R.id.chateditor);

        if (sharedPreferences.getString("xf_user", "").isEmpty()){
            chateditor.setVisibility(View.GONE);
        }
        sendbtn = v.findViewById(R.id.sendbtn);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = msgbox.getText().toString();
                if (sharedPreferences.getString("xf_user", "").isEmpty()){
                    Toast.makeText(getContext(), "Error: You must be logged in", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (!text.isEmpty()) {
                    sendbtn.setVisibility(View.GONE);
                    sending.setVisibility(View.VISIBLE);

//Log.d("chat",text);
                    //  Log.d("chat",Integer.toHexString(spchat.getInt("chatcolor",0xFFFFFFFF)));
                    postmessage(text);


                } else {
                    Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("shoutbox","Getting messages");
                getMessages(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    public void deletemessage(final int id) {
        Request request = new Request.Builder()
                .url("http://" + getString(R.string.url) + "/taigachat/" + id + "/delete")
                .post(new FormBody.Builder()
                        .add("_xfToken", sharedPreferences.getString("_xfToken", ""))
                        .add("_xfResponseType", "json")
                        .add("_xfRequestUri", "/chat/")

                        .add("_xfNoRedirect", "1")

                        .build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("chat", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < messages.size(); i++) {
                            if (messages.get(i).getID() == id) {

                                messages.remove(i);
                                chatadapter.setItems(messages);
                                chatadapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
            }
        });
    }

    public void postmessage(String msg) {
        Request request = new Request.Builder()
                .url("http://" + getString(R.string.url) + "/taigachat/post.json")
                .post(new FormBody.Builder()
                        .add("_xfToken", sharedPreferences.getString("_xfToken", ""))
                        .add("_xfResponseType", "json")
                        .add("message", msg)
                        .add("color", Integer.toHexString(spchat.getInt("chatcolor", 0xFF000000)).replace("#", "").substring(2, 8))
                        .add("room", "1")
                        .add("sidebar", "0")
                        .add("_xfNoRedirect", "1")
                        .add("lastrefresh", "" + lastupdate)
                        .build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("chat", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendbtn.setVisibility(View.VISIBLE);
                        sending.setVisibility(View.GONE);
                        msgbox.setText("");
                        getMessages();
                    }
                });

            }
        });

    }

    public void getMessages() {


        Request request = new Request.Builder()
                .url("http://" + getString(R.string.url) + "/taigachat/list.json")
                .header("Referer","http://itsjerryandharry.com/forums/")
                .header("X-Ajax-Referer","http://itsjerryandharry.com/forums/")
                .header("X-Requested-With","XMLHttpRequest")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8").header("Accept","application/json, text/javascript, */*; q=0.01")
                .post(new FormBody.Builder()
                        .add("_xfToken", sharedPreferences.getString("_xfToken", ""))
                        .add("_xfResponseType", "json")
                        .add("room", "1")
                        .add("sidebar", "0")
                        .add("_xfNoRedirect", "1")
                        .add("_xfRequestUri", "/forums/")
                        .add("lastrefresh", "" + lastupdate)
                        .build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //  call.cancel();
                Log.e("chat", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                //Log.d("chatmsg", myResponse);
                try {
                    JSONObject json = new JSONObject(myResponse);
                    Document doc = Jsoup.parse(json.getString("templateHtml"));
                    if(json.has("lastrefresh"))
                    lastupdate = json.getInt("lastrefresh");
                    if (!sharedPreferences.getString("xf_user", "").isEmpty() && mListener != null) {
                        int alerts = json.getInt("_visitor_alertsUnread");
                        int inbox = json.getInt("_visitor_conversationsUnread");
                        ((OnAlertsupdatedListener) mListener).onAlertsUpdate(alerts, inbox);
                    }
                    //Log.d("chat","Responded: " + doc.html());
                    if (doc.body().child(0).is("li")) {
                        for (Element el : doc.body().children()) {
                            if (el.hasAttr("data-messageid")) {
                                //Log.d("chatmsg", el.text());
                                int ID = 0;
                                String msgid = el.attr("data-messageid");
                                if (!msgid.isEmpty())
                                    ID = Integer.parseInt(msgid);
                                String date = el.select(".DateTime").attr("data-timestamp");
                                int userid = 0;
                                if (!el.attr("data-userid").isEmpty())
                                    userid = Integer.parseInt(el.attr("data-userid"));
                                String name = el.select(".username").text();
                                String avatar = "";
                                if (el.select("a").first() != null)
                                    avatar = el.select("a").first().select("img").attr("src");

                                Simpleuser user = new Simpleuser(userid, name, avatar);
                                String textcolor = "#000000";
                                if (el.select("span").last() != null && el.select("span").last().hasAttr("style") && el.select("span").last().attr("style").contains("color"))
                                    textcolor = el.select("span").last().attr("style").split(":")[1].trim();
                                //Log.d("Emoji","Images: " + el.select("img").size());
                                if(el.select("img").size() >0){
                                 //   Log.d("Emoji",el.select(".taigachat_messagetext").select("img").first().attr("title").substring(el.select(".taigachat_messagetext").select("img").first().attr("title").indexOf(":"),el.select(".taigachat_messagetext").select("img").first().attr("title").indexOf(":",el.select(".taigachat_messagetext").select("img").first().attr("title").indexOf(":"))));

                                    for(Element emoj : el.select("img")){

                                        if(emoj.hasAttr("title")){
                                            Log.d("Emoji",emoj.attr("src") +" " + emoj.attr("title").substring(emoj.attr("title").indexOf(":"),emoj.attr("title").indexOf(":",emoj.attr("title").indexOf(":")+1))+":");
                                            if(emojis.containsKey(emoj.attr("title").substring(emoj.attr("title").indexOf(":"),emoj.attr("title").indexOf(":",emoj.attr("title").indexOf(":")+1))+":")){
                                                emoj.prepend(emojis.get(emoj.attr("title").substring(emoj.attr("title").indexOf(":"),emoj.attr("title").indexOf(":",emoj.attr("title").indexOf(":")+1))+":"));
                                            }

                                        }
                                    }
                                }
                                String msgtext = "";
                                if (el.select(".taigachat_messagetext").last() != null) {
                                    msgtext = el.select(".taigachat_messagetext").last().html();
                                  //  msgtext = msgtext.replace("<img src=\"styles/default/xenforo/clear.png\" class=\"mceSmilieSprite mceSmilie6\" alt=\":cool:\" title=\"Cool    :cool:\">", "&#128526")
                                  //          .replace("<img src=\"styles/default/xenforo/clear.png\" class=\"mceSmilieSprite mceSmilie12\" alt=\":what:\" title=\"Er... what?    :what:\">", "&#129320");


                                }


                                Chatmsg msg = new Chatmsg(ID, msgid, date, user, textcolor, msgtext);
                                if (!sharedPreferences.getString("user", "").isEmpty()) {

                                    if (userid == Integer.parseInt(sharedPreferences.getString("user", ""))) {
                                        msg.setMe(true);
                                    } else {
                                        msg.setMe(false);
                                    }
                                } else {
                                    msg.setMe(false);
                                }
                                boolean exist = false;

                                for (int i = 0; i < messages.size(); i++) {
                                    if (messages.get(i).getID() == ID) {

                                        exist = true;
                                    }

                                }
                                if (!exist)
                                    messages.add(msg);


                                //  System.out.println(el.select(".taigachat_messagetext").html());

                            }

                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (chatadapter == null) {
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    //linearLayoutManager.setReverseLayout(true);
                                    linearLayoutManager.setStackFromEnd(true);
                                    rv.setLayoutManager(linearLayoutManager);
                                    chatadapter = new MyChatAdapter(messages, shoutbox.this);
                                    rv.setAdapter(chatadapter);
                                    rv.smoothScrollToPosition(chatadapter.getItemCount());
                                    registerForContextMenu(rv);
                                } else {
                                    chatadapter.setItems(messages);
                                    chatadapter.notifyDataSetChanged();
                                    rv.smoothScrollToPosition(chatadapter.getItemCount());
                                }
                            }
                        });
                    } else {
                        Log.d("chatmsg", myResponse);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        deletemessage(Integer.parseInt(chatadapter.getSelectedID()));
        return super.onContextItemSelected(item);
    }

    public void showPopup(View v) {


        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.chat_tools_menu, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO do sth here on dismiss
            }
        });
        // popupView.orie

        ImageView color;
        color = popupView.findViewById(R.id.colorbtn);
        ImageButton bold = popupView.findViewById(R.id.boldbtn);
        ImageButton strike = popupView.findViewById(R.id.strikebtn);
        ImageButton italic = popupView.findViewById(R.id.italicbtn);
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (msgbox.getSelectionEnd() - msgbox.getSelectionStart() == 0) {

                    msgbox.getText().insert(msgbox.getSelectionStart(), "[b][/b]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                } else {
                    msgbox.getText().insert(msgbox.getSelectionStart(), "[b]");
                    msgbox.getText().insert(msgbox.getSelectionEnd(), "[/b]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                }
            }
        });
        strike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (msgbox.getSelectionEnd() - msgbox.getSelectionStart() == 0) {

                    msgbox.getText().insert(msgbox.getSelectionStart(), "[s][/s]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                } else {
                    msgbox.getText().insert(msgbox.getSelectionStart(), "[s]");
                    msgbox.getText().insert(msgbox.getSelectionEnd(), "[/s]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                }
            }
        });
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msgbox.getSelectionEnd() - msgbox.getSelectionStart() == 0) {

                    msgbox.getText().insert(msgbox.getSelectionStart(), "[i][/i]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                } else {
                    msgbox.getText().insert(msgbox.getSelectionStart(), "[i]");
                    msgbox.getText().insert(msgbox.getSelectionEnd(), "[/i]");
                    msgbox.setSelection(msgbox.getSelectionStart() - 4);
                }
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorpicker(view);
            }
        });

        popupWindow.showAsDropDown(v, 0, -v.getHeight() - popupWindow.getHeight() - 600);
    }

    public void showColorpicker(View v) {

        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(spchat.getInt("chatcolor", 0xFFFFFFFF))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()


                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        // changeBackgroundColor(selectedColor);
                        SharedPreferences.Editor editor = spchat.edit();
                        editor.putInt("chatcolor", selectedColor);
                        editor.commit();
                        msgbox.setTextColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {


        mHandler.removeCallbacks(mStatusChecker);
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
        stopRepeatingTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        startRepeatingTask();
    }

    @Override
    public void onChatInteraction(Chatmsg item) {

    }

    public void loggedout() {
        chateditor.setVisibility(View.GONE);
    }

    public void loggedin() {
        chateditor.setVisibility(View.VISIBLE);
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
        void onFragmentInteraction(Uri uri);
    }
}
