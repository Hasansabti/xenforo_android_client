package tech.sabtih.forumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ReplyActivity extends AppCompatActivity {

    TextView replyto;
    EditText myreply;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);


        sharedPreferences = getSharedPreferences("cookies", MODE_PRIVATE);
        replyto = findViewById(R.id.mReplyto);
        myreply = findViewById(R.id.mTreply);

        String reply = getIntent().getStringExtra("comment");

        replyto.setText(Html.fromHtml(reply));

        myreply.requestFocus();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.threadreply,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if(item.getItemId() == R.id.post){

            if(myreply.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(),"Message cannot be empty",Toast.LENGTH_SHORT).show();

                return true;
            }
            //post reply
            String url = getIntent().getStringExtra("url");
            String lastdate = getIntent().getStringExtra("lastdate");
            String lastkurl = getIntent().getStringExtra("lastkdate");
            String atthash = getIntent().getStringExtra("atthash");
            String rresolver = getIntent().getStringExtra("rresolver");
            postReply(myreply.getText().toString(),url,lastdate,lastkurl,atthash,rresolver);


        }else if(item.getItemId() == android.R.id.home){
            //safe draft

            finish();
        }

        return true;
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
    public void postReply(String msg,String url, String lastdate, String lastkdate, String atthash,String rresolver) {
        Request request = new Request.Builder()
                .url("https://" + getString(R.string.url) + "/"+url)
                .addHeader("Accept","application/json, text/javascript, */*; q=0.01")
                .addHeader("X-Ajax-Referer","http://" + getString(R.string.url) + "/"+url)
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")

                .post(new FormBody.Builder()
                        .add("_xfToken", sharedPreferences.getString("_xfToken", ""))
                        .add("_xfResponseType", "json")
                        .add("message_html", "<p>"+msg+"<p>")
                        //.add("_xfRequestUri","threads/what-happened-if-you-try-to-explode-the-tnt-in-the-farm.18/")
                        .add("_xfRelativeResolver", rresolver)
                        .add("attachment_hash", atthash)
                        .add("last_known_date", lastkdate)
                        .add("_xfNoRedirect", "1")
                        .add("last_date", "" + lastdate)
                        .build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("reply", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("reply",response.toString());
                        if(response.code() == 200){
                            Toast.makeText(getApplicationContext(),"Comment added",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Error:"+response.message(),Toast.LENGTH_SHORT).show();
                        }
                      //  sendbtn.setVisibility(View.VISIBLE);
                      //  sending.setVisibility(View.GONE);
                     //   msgbox.setText("");
                   //     getMessages();
                    }
                });

            }
        });

    }
/*
    Request header
Accept: application/json, text/javascript, /; q=0.01
    Accept-Encoding: gzip, deflate
    Accept-Language: en-US,en;q=0.9,ar;q=0.8
    Connection: keep-alive
    Content-Length: 473
    Content-Type: application/x-www-form-urlencoded; charset=UTF-8
    Cookie: _ga=GA1.2.458473462.1562499917; xf_user=7790%2C7ea106bcc72060488fdccce3a371ceb9505ac68b; _gid=GA1.2.2128167507.1577425977; xf_session=e26a0c6b4cd9c67f6240b99b0f63f5b3
    DNT: 1
    Host: itsjerryandharry.com
    Origin: http://itsjerryandharry.com
    Referer: http://itsjerryandharry.com/threads/owner-interruption.58597/page-236
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36
    X-Ajax-Referer: http://itsjerryandharry.com/threads/owner-interruption.58597/page-236
    X-Requested-With: XMLHttpRequest



    POST /threads/owner-interruption.58597/add-reply HTTP/1.1
Host: itsjerryandharry.com
Connection: keep-alive
Content-Length: 473
X-Ajax-Referer: http://itsjerryandharry.com/threads/owner-interruption.58597/page-236
Origin: http://itsjerryandharry.com
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
Accept: application/json, text/javascript, /*; q=0.01
    X-Requested-With: XMLHttpRequest
    DNT: 1
    Referer: http://itsjerryandharry.com/threads/owner-interruption.58597/page-236
    Accept-Encoding: gzip, deflate
    Accept-Language: en-US,en;q=0.9,ar;q=0.8
    Cookie: _ga=GA1.2.458473462.1562499917; xf_user=7790%2C7ea106bcc72060488fdccce3a371ceb9505ac68b; _gid=GA1.2.2128167507.1577425977; xf_session=e26a0c6b4cd9c67f6240b99b0f63f5b3

     */

    /*
    Form data

message_html: <p>1003</p>
_xfRelativeResolver: http://itsjerryandharry.com/threads/owner-interruption.58597/page-236
attachment_hash: c0e44e2b410e0379b0fe43e3792db4cd
last_date: 1577219843
last_known_date: 1577219843
_xfToken: 7790,1577676093,cc989b4befbbfb325b04e73335763c2c655568b0
_xfRequestUri: /threads/owner-interruption.58597/page-236
_xfNoRedirect: 1
_xfToken: 7790,1577676093,cc989b4befbbfb325b04e73335763c2c655568b0
_xfResponseType: json

message_html=%3Cp%3E1003%3C%2Fp%3E
&_xfRelativeResolver=http%3A%2F%2Fitsjerryandharry.com%2Fthreads%2Fowner-interruption.58597%2Fpage-236
&attachment_hash=c0e44e2b410e0379b0fe43e3792db4cd
&last_date=1577219843
&last_known_date=1577219843
&_xfToken=7790%2C1577676093%2Ccc989b4befbbfb325b04e73335763c2c655568b0
&_xfRequestUri=%2Fthreads%2Fowner-interruption.58597%2Fpage-236
&_xfNoRedirect=1&_xfToken=7790%2C1577676093%2Ccc989b4befbbfb325b04e73335763c2c655568b0
&_xfResponseType=json

     */

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
}
