package tech.sabtih.forumapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


import tech.sabtih.forumapp.adapters.ScreenSlidePagerAdapter;
import tech.sabtih.forumapp.fragments.AccountFragment;
import tech.sabtih.forumapp.fragments.InfoFragment;
import tech.sabtih.forumapp.fragments.NameChangeFragment;
import tech.sabtih.forumapp.fragments.ProfileFragment;
import tech.sabtih.forumapp.fragments.ProfilepostrepliesFragment;
import tech.sabtih.forumapp.listeners.OnProfileInteractionListener;
import tech.sabtih.forumapp.listeners.OnProfilePostInteractionListener;
import tech.sabtih.forumapp.models.Newsitem;
import tech.sabtih.forumapp.models.Profilepost;
import tech.sabtih.forumapp.models.Profilepostmessage;
import tech.sabtih.forumapp.models.user.NameChange;
import tech.sabtih.forumapp.models.user.Simpleuser;
import tech.sabtih.forumapp.models.user.User;
import tech.sabtih.forumapp.models.user.UserInfo;
import tech.sabtih.forumapp.models.user.UserRank;

public class ProfileActivity extends AppCompatActivity implements AccountFragment.OnFragmentInteractionListener, OnProfilePostInteractionListener, OnProfileInteractionListener, NameChangeFragment.OnNameChangeInteractionListener {

    String userid;
    ImageView cover;

    ViewPager tviewpager;
    ScreenSlidePagerAdapter tadapter;
    private TabLayout tabLayout;
    AppBarLayout appbar;

    TabItem profiletab;
    TabItem namestab;

    SharedPreferences sharedPreferences;


    ProfileFragment prf;
    InfoFragment pinfo;
    NameChangeFragment ncf;
    ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
/*
        username = findViewById(R.id.username);
        customt = findViewById(R.id.custitle);
        status = findViewById(R.id.stat);
        points = findViewById(R.id.user_points);
        followers = findViewById(R.id.user_followers);
        following = findViewById(R.id.user_following);
        avatar = findViewById(R.id.avatarph);
        postsrv = findViewById(R.id.profileposts);
        */

        appbar = findViewById(R.id.myappbar);
        loading = findViewById(R.id.loading);


        cover = findViewById(R.id.htab_header);
        tviewpager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.htab_tabs);
        // tadapter = new ProfileTabAdapter(getSupportFragmentManager(),1);

        profiletab = findViewById(R.id.profile_tab);



        // tadapter.addFragment(new ProfileFragment(),"Profile",R.drawable.defaultuser);


         //tabLayout.setupWithViewPager(tviewpager);


//highLightCurrentTab(0);


        sharedPreferences = getSharedPreferences("cookies", MODE_PRIVATE);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        namestab = tabLayout.findViewById(R.id.namestab);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Starting New Private Conversation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                //Log.d("App Bar state: ",""+i);
                if (i >= -100) {
                    fab.show();
                } else {
                    fab.hide();
                }

            }
        });
        tviewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    //   toolbar.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this,
                    //           R.color.colorAccent));
                    tviewpager.setCurrentItem(0);
                } else if (tab.getPosition() == 1) {
                    //     toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    //            android.R.color.darker_gray));
                    tviewpager.setCurrentItem(1);
                }else if (tab.getPosition() == 2) {
                    //     toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    //            android.R.color.darker_gray));
                    tviewpager.setCurrentItem(2);
                }

                else {
                    //     toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    //             R.color.colorPrimary));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (getIntent().hasExtra("userid")) {
            String username = getIntent().getStringExtra("username");
            toolbar.setTitle(username);

            userid = getIntent().getStringExtra("userid");


            new Accountdata().execute(userid.trim());

        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // navigateUpTo(new Intent(this, ThreadListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setupViewpager(ViewPager viewPager, User user) {
        if (tadapter == null) {
            tadapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        }
        prf = new ProfileFragment(user);

        pinfo = InfoFragment.newInstance(user.getPinfo(), "");
        tadapter.addFragment(prf);
        tadapter.addFragment(pinfo);
        if(user.getNameChanges() != null) {
            ncf = NameChangeFragment.newInstance(user.getNameChanges());
            tadapter.addFragment(ncf);
        }else{
            tabLayout.removeTabAt(2);
        }
        viewPager.setAdapter(tadapter);


    }
    @Override
    public void onBackPressed() {
        if (tviewpager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            tviewpager.setCurrentItem(0);
        }
    }
    @Override
    public void onAccountInteraction(Uri uri) {

    }

    @Override
    public void loggedout() {

    }

    @Override
    public void loggedin() {

    }


    @Override
    public void onProfileLoaded(User user) {

    }

    @Override
    public void onProfilePostInteraction(Profilepost profilepost) {

    }

    @Override
    public void viewPPComments(Profilepost mItem) {
        if(mItem.getNumReplies() > 0) {
            FragmentManager fm = getSupportFragmentManager();
            ProfilepostrepliesFragment editNameDialogFragment = ProfilepostrepliesFragment.newInstance(10, mItem);
            editNameDialogFragment.show(fm, "fragment_edit_name");
        }
    }

    @Override
    public void viewPPUser(Simpleuser mItem) {
        if(!(""+mItem.getId()).equals(userid) ) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userid", "" + mItem.getId());
            intent.putExtra("username", mItem.getName());
            startActivity(intent);
        }

    }

    @Override
    public void onNameChangeInteraction(NameChange item) {

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
                Document document = Jsoup.connect("http://" + getString(R.string.url) + "/members/" + args[0]).cookie("xf_session", sharedPreferences.getString("xf_session", "")).cookie("xf_user", sharedPreferences.getString("xf_user", "")).cookie("xf_session_admin", sharedPreferences.getString("xf_session_admin", "")).get();
                document.select("img").attr("style", "max-width:100%");
                String status = "";
                int id = Integer.parseInt(args[0]);
                String username = document.select(".username").first().text();
                String title = "";
                if (document.select(".userTitle").first() != null)
                    title = document.select(".userTitle").first().text();
                if (document.select("#UserStatus").first() != null) {
                    document.select("#UserStatus").first().children().select(".DateTime").remove();
                    status = document.select("#UserStatus").first().text();
                }
                int following = 0;
                int followers = 0;
                if (document.select(".followBlocks").first() != null)
                    for (Element followb : document.select(".followBlocks").first().children()) {
                        if (followb.select(".text").text().equals("Following")) {
                            following = Integer.parseInt(followb.select(".count").first().text());
                        } else if (followb.select(".text").text().equals("Followers")) {
                            followers = Integer.parseInt(followb.select(".count").last().text());
                        }

                    }

                //   if(document.select(".infoblock").first().children().first().children() == 4)
                int points = 0;
                int messages = 0;
                int likes = 0;


                for (Element e : document.select(".infoblock").first().select("dl")) {
                    if (e.select("dt").text().contains("Last Activity:")) {

                    }
                    if (e.select("dt").text().contains("Joined:")) {

                    }
                    if (e.select("dt").text().contains("Messages")) {
                        messages = Integer.parseInt(e.select("dd").first().text().replace(",", ""));

                    }
                    if (e.select("dt").text().contains("Likes Received:")) {
                        likes = Integer.parseInt(e.select("dd").first().text().replace(",", ""));

                    }
                    if (e.select("dt").text().contains("Trophy Points::")) {
                        points = Integer.parseInt(e.select("dd").first().text().replace(",", ""));

                    }
                    if (e.select("dt").text().contains("Warning Points:")) {

                    }

                }
                //  int points = Integer.parseInt( document.select(".infoblock").first().select("dl").get(4).select("a").first().text());
                // int messages = Integer.parseInt( document.select(".infoblock").first().select("dl").get(2).select("dd").first().text().replace(",",""));
                // int likes = Integer.parseInt( document.select(".infoblock").first().select("dl").get(2).select("dd").first().text().replace(",",""));
                String avatar = document.select(".avatarScaler").select("img").attr("src");
                String cover = "";
                if (document.select(".BRPCProfileImage").first() != null)
                    cover = document.select(".BRPCProfileImage").first().attr("src");


                Element profileposts = document.selectFirst("#ProfilePostList");

                ArrayList<Profilepost> pposts = new ArrayList<>();

                if (!profileposts.children().first().attr("id").equals("NoProfilePosts")) {
                    for (Element post : profileposts.children()) {
                        ArrayList<Profilepostmessage> reps = null;
                        String ppuserid = post.select("a").first().attr("href").split("\\.")[1].replace("/", "").trim();
                        String ppname = post.select(".messageContent").select(".username").first().text();
                        String ppavatar = post.select("img").first().attr("src");

                        Simpleuser ppuser = new Simpleuser(Integer.parseInt(ppuserid), ppname, ppavatar);

                        String ppid = post.attr("id").split("post-")[1].trim();
                        int pplikes = 0;
                        if (post.select(".LikeText").first() != null)
                            pplikes = post.select(".LikeText").first().children().size();
                        String time = post.select(".DateTime").first().text();
                        String message = post.select(".baseHtml").html();
                        if(post.select(".messageResponse").first().select("#commentSubmit-"+ppid) != null){
                            post.select(".messageResponse").first().select("#commentSubmit-"+ppid).remove();
                        }
                        int pprplies = post.select(".messageResponse").first().children().size()-1;
                        if(pprplies < 4 && pprplies > 0){
                            for(Element ppr : post.select(".messageResponse").first().children()){
                                if(ppr.hasClass("comment") && !ppr.attr("id").contains("Submit")){
                                    if(reps == null){
                                        reps = new ArrayList<>();
                                    }
                                    Log.d("ppcomment_id",ppr.attr("id"));
                                    String pprid = ppr.attr("id").split("post-comment-")[1].trim();

                                    String ppruserid = ppr.select("a").first().attr("href").split("\\.")[1].replace("/", "").trim();
                                    String pprname = ppr.select(".commentContent").select(".username").first().text();
                                    String ppravatar = ppr.select("img").first().attr("src");
                                    Simpleuser ppruser = new Simpleuser(Integer.parseInt(ppruserid), pprname, ppravatar);

                                    String pprtime = ppr.select(".DateTime").first().text();
                                    String pprmessage = ppr.select("article").html();
                                    int pprlikes = ppr.select(".LikeText").select("a").size();

                                    Profilepostmessage ppmsg = new Profilepostmessage(Integer.parseInt(pprid),ppruser,pprtime,pprmessage,pprlikes);
                                    reps.add(ppmsg);
                                }
                            }


                        }

                        Profilepost pp = new Profilepost(Integer.parseInt(ppid), pplikes, ppuser, time, message);
                        pp.setNumReplies(pprplies);
                        pp.setReplies(reps);
                        pposts.add(pp);



                    }
                } else {

                    //no posts

                }
                ArrayList<UserRank> userranks = new ArrayList<>();

                Element ranks = document.selectFirst(".userBanners");
                if (ranks != null && ranks.children().size() > 0) {
                    for (Element rank : ranks.children()) {
                        int rid = 0;
                        String rtitle = rank.select("strong").text();
                        String rcolor = "";

                        userranks.add(new UserRank(rid, rtitle, rcolor));
                    }
                }
                LinkedHashMap<String, String> aboutpairs = null;
                if (document.selectFirst("#info").select(".aboutPairs").first() != null) {
                    aboutpairs = new LinkedHashMap<>();
                    for (Element ap : document.selectFirst("#info").select(".aboutPairs").first().children()) {
                        aboutpairs.put(ap.select("dt").text(), ap.select("dd").text());
                    }
                }
                LinkedHashMap<String, String> interactpairs = new LinkedHashMap<>();
                for (Element ap : document.selectFirst("#info").select(".contactInfo").first().children()) {
                    if(ap.select("dt").text().contains("Content") || ap.select("dt").text().contains("Convers"))
                        continue;
                    interactpairs.put(ap.select("dt").text(), ap.select("dd").text());
                }
                String about = document.select("#info").select(".baseHtml").html().replace("\"proxy.php", "\"http://" + getString(R.string.url) + "/proxy.php").replace(";hash", "&hash").replace("styles/", "http://" + getString(R.string.url) + "/styles/");;
                UserInfo userinfo = new UserInfo(about);
                userinfo.setAboutpairs(aboutpairs);
                userinfo.setInteractpairs(interactpairs);

                //namechange
                ArrayList<NameChange> namechanges = null;
               // Log.d("Namechange",);
               // System.out.println(document.body().html());
                if(document.select("#usernameHistory").first() != null){

                    namechanges = new ArrayList<>();
                    for(Element nc : document.select("#usernameHistory").select("ul").first().children()){
                        namechanges.add(new NameChange(nc.select("b").get(1).text(),nc.select("b").get(2).text(),nc.select("b").get(0).text()));
                    }

                }else {
                    Log.d("Namechange","No changes");
                }
                String lastseen ="-";
                if(document.select(".lastActivity").first() != null)
                    lastseen = document.select(".lastActivity").select("dd").text();


                User user = new User(id, username, title, status, points, followers, following, messages, avatar);
                user.setProfileposts(pposts);
                user.setCover(cover);
                user.setRanks(userranks);
                user.setPinfo(userinfo);
                user.setNameChanges(namechanges);
                user.setLastActivity(lastseen);

                return user;

            } catch (HttpStatusException e) {
                e.printStackTrace();
                if (e.getStatusCode() == 403) {
                    Log.d("User_Error",e.getMessage());
                    ProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("This member limits who may view their full profile.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                            alertDialog.show();

                        }
                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null) {


                //  setUser(user);
                setTitle(user.getName());
                //newsitems.addAll(ns);
                //adapter.notifyDataSetChanged();
                Picasso.get().load("http://" + getString(R.string.url) + "/" + user.getCover()).into(cover);
                setupViewpager(tviewpager, user);
                loading.setVisibility(View.GONE);

            }

        }
    }


/*
    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(tadapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(tadapter.getSelectedTabView(position));
    }
    */
}
