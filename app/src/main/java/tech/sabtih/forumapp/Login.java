package tech.sabtih.forumapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Login extends AppCompatActivity {
WebView wv;
ProgressBar pb;
    String type;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("J&H Login");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        wv = findViewById(R.id.weblgn);
        wv.setWebViewClient(new MyWebViewClient());
        wv.getSettings().setDomStorageEnabled(true);
        type = getIntent().getStringExtra("type");
        if(type.equals("login")) {
            wv.loadUrl("http://"+getString(R.string.url)+"/login");
        }else{
            wv.loadUrl("http://"+getString(R.string.url)+"/logout");
        }
        wv.setVisibility(View.GONE);
        pb = findViewById(R.id.progressBar);
        sharedPref = getSharedPreferences("cookies", MODE_PRIVATE);
        editor = sharedPref.edit();


    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println(url);
            if ((url).contains("logout") || (url).contains("login")) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);

                for (String s : CookieManager.getInstance().getCookie(url).split(" ")) {
                    String[] cookies = s.split("=");
                    if (!cookies[1].isEmpty()) {
                        Log.d("Login", "All the cookies in a string:" + cookies[0] + " " + cookies[1]);
                        editor.putString(cookies[0], cookies[1]);
                    }

                }

            editor.commit();

            if(url.contains("http://"+getString(R.string.url)+"")){
                wv.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                return false;
            }
            if(sharedPref.getString("_xfToken","").isEmpty()) {
                setResult(RESULT_CANCELED,null);
            }else{
                setResult(RESULT_OK,null);
            }
            finish();
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {
            pb.setVisibility(View.GONE);

//xf_user

            if(url.contains("logout")){
                editor.putString("_xfToken", "");
                editor.putString("xf_user", "");

            }else {
                for (String s : CookieManager.getInstance().getCookie(url).split(" ")) {
                    String[] cookies = s.split("=");
                    if (!cookies[1].isEmpty()) {
                        Log.d("Login", "cookies after finish are: " + cookies[0] + " " + cookies[1]);
                        editor.putString(cookies[0], cookies[1]);
                    }

                }
            }
            editor.commit();


            super.onPageFinished(view, url);
           // Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
            WebSettings webSettings = wv.getSettings();
            webSettings.setJavaScriptEnabled(true);
            if(url.contains("login")) {
                view.loadUrl("javascript:(function() { " +

                        "var head = document.getElementsByTagName('header')[0].style.display='none'; " +
                        "var head = document.getElementsByTagName('footer')[0].style.display='none'; " +
                        "var head = document.getElementsByClassName('breadBoxTop')[0].style.display='none'; " +
                        "var head = document.getElementsByClassName('breadBoxBottom')[0].style.display='none'; " +
                        "for(i in document.getElementsByTagName('a')){if(document.getElementsByTagName('a')[i].style != null) document.getElementsByTagName('a')[i].style.display='none'; }" +
                        "document.getElementById('content').style.padding=0;" +
                        "document.getElementsByTagName('html')[0].style.backgroundColor='white'; " +
                        "var head = document.getElementsByName('register')[0].setAttribute('disabled', '');" +
                        "document.getElementsByName('remember')[0].checked = true"+
                        "})()");
                wv.setVisibility(View.VISIBLE);
            }else if(url.contains("logout")) {
                view.loadUrl("javascript:(function() { " +

                        "var head = document.getElementsByTagName('header')[0].style.display='none'; " +
                        "var head = document.getElementsByTagName('footer')[0].style.display='none'; " +
                        "var head = document.getElementsByClassName('breadBoxTop')[0].style.display='none'; " +
                        "var head = document.getElementsByClassName('breadBoxBottom')[0].style.display='none'; " +
                        "for(i in document.getElementsByTagName('a')){if(document.getElementsByTagName('a')[i].style != null && !document.getElementsByTagName('a')[i].classList.contains('button')) document.getElementsByTagName('a')[i].style.display='none'; }" +
                        "document.getElementById('content').style.padding=0;" +
                        "document.getElementsByTagName('html')[0].style.backgroundColor='white'; " +
                        "var head = document.getElementsByName('register')[0].setAttribute('disabled', '');" +

                        "})()");
                wv.setVisibility(View.VISIBLE);
            }else{

                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(sharedPref.getString("_xfToken","").isEmpty()) {
                            setResult(RESULT_CANCELED,null);
                        }else{
                            setResult(RESULT_OK,null);
                        }
                        finish();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Document doc = Jsoup.connect("http://"+getString(R.string.url)+"/portal").cookie("xf_session",sharedPref.getString("xf_session","")).get();
                                    String token = doc.getElementsByAttributeValue("name","_xfToken").first().attr("value");


                            String userid = "";
                            if(!token.isEmpty()){
                                userid = doc.select(".fl").first().select("a").first().attr("href").split("\\.")[1].replace("/","");
                                editor.putString("user",userid);

                            }
                            editor.putString("_xfToken", token);
                            editor.commit();
                            System.out.println("Setting token: " + token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            }




        }
    }
}
