package tech.sabtih.forumapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import tech.sabtih.forumapp.ProfileActivity;
import tech.sabtih.forumapp.R;
import tech.sabtih.forumapp.ThreadDetailActivity;
import tech.sabtih.forumapp.ThreadListActivity;

public class MyWebViewClient extends WebViewClient {
Context context;
    public MyWebViewClient(Context context) {
    this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        System.out.println(url);
        if (url.contains(context.getString(R.string.url)))
            if (url.contains("members")) {
                Log.d("url", "This is a member");


                String ttl = "";
                String userid = "";

                //String urlt = url.split(((ThreadDetailFragment) mListener  ). getString(R.string.url))[1];
                if (url.split("members/")[1].contains(".")) {
                    ttl = url.split("members/")[1].split("\\.")[0].replace("-", " ");
                    userid = url.split("members/")[1].split("\\.")[1].replace("/", " ");
                } else {
                    ttl = url.split("members/")[1].replace("/", " ");
                    userid = url.split("members/")[1].replace("/", " ");
                }
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("username", ttl);
               context. startActivity(intent);
            } else if (url.contains("threads")) {
                Log.d("url", "This is a thread");
                String urlt = url.split(context.getString(R.string.url))[1];
                String ttl = url.split("threads/")[1].split("\\.")[1].replace("/", " ");
                Intent intent = new Intent(context, ThreadDetailActivity.class);
                intent.putExtra("item_id", urlt);
                intent.putExtra("item_id", ttl);
                //  intent.putExtra("pages", "1");
                context. startActivity(intent);

            } else if (url.contains("forums")) {
                Log.d("url", "This is a forum");
                String urlt = url.split(context.getString(R.string.url))[1];
                String ttl = url.split("forums/")[1].split("\\.")[0].replace("-", " ");
                Intent intent = new Intent(context, ThreadListActivity.class);
                intent.putExtra("url", urlt);
                intent.putExtra("title", ttl);
                context.startActivity(intent);
            }

        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        try {

            InputStream inputStream = context.getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style);" +
                   // "var st = document.getElementsByClassName('SpoilerTarget').item(0);"+
                   // "st.style.display = \"none\";"+
                    "var spbtn = document.getElementsByClassName('bbCodeSpoilerContainer');" +
                    "for(sp in spbtn){" +
                    "var stgt = spbtn.item(sp).getElementsByClassName('SpoilerTarget').item(0); " +
                    "stgt.classList.add('hidden');" +
                    "stgt.classList.add('visuallyhidden');"+
                    "spbtn.item(sp).getElementsByClassName('bbCodeSpoilerButton').item(0).onclick = function(){ " +
                    "var stgt = this.parentElement.getElementsByClassName('SpoilerTarget').item(0);" +
                    "if (stgt.classList.contains('hidden')) {" +
                    "    stgt.classList.remove('hidden');" +
                    "    setTimeout(function () {" +
                    "      stgt.classList.remove('visuallyhidden');" +
                    "    }, 5);" +
                    "  } else {" +
                    "    stgt.classList.add('visuallyhidden');    " +
                    "    stgt.addEventListener('transitionend', function(e) {" +
                    "      stgt.classList.add('hidden');" +
                    "    }, {" +
                    "      capture: false," +
                    "      once: true," +
                    "      passive: false" +
                    "    });" +
                    "  }" +
                    "" +
                    "} " +
                    "" +
                    "}"+


                    "})()");

        } catch (IOException e) {
            e.printStackTrace();
        }
        // view.loadUrl("javascript:"+script);
        //    view.loadUrl("javascript:"+script2);
        //view.reload();


    }

}
