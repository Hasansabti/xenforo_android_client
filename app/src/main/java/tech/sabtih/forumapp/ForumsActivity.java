package tech.sabtih.forumapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import tech.sabtih.forumapp.adapters.ViewPagerAdapter;
import tech.sabtih.forumapp.fragments.AccountFragment;
import tech.sabtih.forumapp.fragments.FeedFragment;
import tech.sabtih.forumapp.fragments.ForumCategoryFragment;
import tech.sabtih.forumapp.fragments.NewsFragment;
import tech.sabtih.forumapp.fragments.shoutbox;
import tech.sabtih.forumapp.listeners.OnAlertsupdatedListener;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.listeners.OnListFragmentInteractionListener;
import tech.sabtih.forumapp.models.Discussion;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Newsitem;

public class ForumsActivity extends AppCompatActivity implements  OnListFragmentInteractionListener, FeedFragment.OnListFeedInteractionListener, NewsFragment.OnNewsListFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener, OnForumsListInteractionListener, shoutbox.OnFragmentInteractionListener, OnAlertsupdatedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private NewsFragment news;
    private FeedFragment feeds;
    public AccountFragment account;
    public ForumCategoryFragment fcat;
    public shoutbox chat;

    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView navView;
    ViewPagerAdapter viewPagerAdapter;
    ConstraintLayout constraintLayout;

    RelativeLayout alertscont;
    RelativeLayout inboxcont;

    Button btnalerts;
    Button btninbox;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // mTextMessage.setText(R.string.title_home);
                    viewPager.setCurrentItem(0);
                    //   return true;
                    break;

                case R.id.navigation_feed:
                    viewPager.setCurrentItem(1);

                    break;
                case R.id.navigation_dashboard:
                    //   mTextMessage.setText(R.string.title_dashboard);
                    viewPager.setCurrentItem(2);
                    // fragment = fcat;
                    break;
                // return true;
                case R.id.navigation_chat:
                    //  mTextMessage.setText(R.string.title_notifications);
                    //   return true;

                    viewPager.setCurrentItem(3);
                    //shownavbar(true);
                    break;
                case R.id.navigation_notifications:
                    //  mTextMessage.setText(R.string.title_notifications);
                    //   return true;
                    viewPager.setCurrentItem(4);
                    break;
            }

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        createNotificationChannel();
        constraintLayout = findViewById(R.id.container);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);



        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //viewPager.setPadding(0,330,0,0);


        navView = findViewById(R.id.nav_view);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //  System.out.println("scroll changed");
                // Log.d("Scrolling",""+positionOffset +" mapped " + ((int)(positionOffset*240) ));
                if (position == 0)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 230 + ((int) (positionOffset * 10)), 200, 200)));
                if (position == 1)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 240, 200 - ((int) (positionOffset * 20)), 200)));
                if (position == 2)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 240, 180, 200 - ((int) (positionOffset * 20)))));
                if (position == 3)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 240, 180, 180)));

            }



            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                navView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navView.getMenu().getItem(position);
                // navView.animate().translationY(0).setDuration(200);
                shownavbar(true);

            }


            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("scroll state changed");
                View mview = ForumsActivity.this.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (mview == null) {
                    mview = new View(ForumsActivity.this);
                }
                InputMethodManager imm = (InputMethodManager) ForumsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mview.getWindowToken(), 0);
            }
        });


        setupViewPager(viewPager, savedInstanceState);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
    private void setupViewPager(ViewPager viewPager, Bundle savedInstanceState) {
        if (viewPagerAdapter == null) {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        }
        if (news == null) {
            news = new NewsFragment();
            viewPagerAdapter.addFragment(news);
        }

        if (feeds == null) {
            feeds = new FeedFragment();
            viewPagerAdapter.addFragment(feeds);
        }

        if (fcat == null) {
            fcat = new ForumCategoryFragment();
            viewPagerAdapter.addFragment(fcat);
        }
        if (chat == null) {
            chat = new shoutbox();
            viewPagerAdapter.addFragment(chat);
        }
        if (account == null) {
            account = new AccountFragment();
            viewPagerAdapter.addFragment(account);
        }


        viewPager.setAdapter(viewPagerAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_menu,menu);
        MenuItem alrtitem = menu.findItem(R.id.alerts);
        MenuItem inboxitem = menu.findItem(R.id.inbox);

        MenuItemCompat.setActionView(alrtitem,R.layout.alerts_button);
        MenuItemCompat.setActionView(inboxitem,R.layout.inbox_button);
        alertscont = (RelativeLayout) MenuItemCompat.getActionView(alrtitem);
        inboxcont = (RelativeLayout) MenuItemCompat.getActionView(inboxitem);

        btnalerts = alertscont.findViewById(R.id.button2);
        btninbox = inboxcont.findViewById(R.id.button1);

        btnalerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForumsActivity.this, Alerts.class);
                startActivity(intent);
            }
        });
        btninbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ForumsActivity.this,NewStoryActivity.class);
                startActivity(intent);
            }
        });

        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.alerts:
                Intent intent = new Intent(this, Alerts.class);
                startActivity(intent);
                return true;
            case R.id.inbox:
                //startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean up = false;
    public void shownavbar(boolean showed) {
        try {


            if (showed) {
                up = false;
                navView.animate().translationY(0).setDuration(200);
                ConstraintSet cons = new ConstraintSet();
                cons.clone(this, R.layout.activity_myforums);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.viewpager, ConstraintSet.BOTTOM, R.id.nav_view, ConstraintSet.TOP, 0);
                //  constraintSet.connect(R.id.imageView,ConstraintSet.TOP,R.id.check_answer1,ConstraintSet.TOP,0);
                AutoTransition transition = new AutoTransition();
                transition.setDuration(200);
                //transition.setInterpolator(new AccelerateDecelerateInterpolator());
                TransitionManager.beginDelayedTransition(constraintLayout, transition);
                constraintSet.applyTo(constraintLayout);
            } else {
                up = true;
                navView.animate().translationY(navView.getHeight()).setDuration(200);


                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.viewpager, ConstraintSet.BOTTOM, R.id.container, ConstraintSet.BOTTOM, 0);
                //  constraintSet.connect(R.id.imageView,ConstraintSet.TOP,R.id.check_answer1,ConstraintSet.TOP,0);
                // constraintSet.applyTo(constraintLayout);


                AutoTransition transition = new AutoTransition();
                transition.setDuration(200);
                //transition.setInterpolator(new AccelerateDecelerateInterpolator());
                TransitionManager.beginDelayedTransition(constraintLayout, transition);
                constraintSet.applyTo(constraintLayout);
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
    public void createNotification(String ttl,String msg){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ijah")
                .setSmallIcon(R.drawable.chat_unread)
                .setContentTitle(ttl)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                // .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(8, builder.build());


    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ItsJerryandharry";
            String description = "Test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ijah", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAccountInteraction(Uri uri) {

    }

    @Override
    public void loggedout() {
        fcat.initFcats();
        chat.loggedout();

    }

    @Override
    public void loggedin() {
        fcat.initFcats();
        chat.loggedin();

    }

    @Override
    public void onListFeedInteraction(Discussion item) {

    }

    @Override
    public void onNewsListFragmentInteraction(Newsitem item) {

    }

    @Override
    public void onNewsScrolled(int dy) {


        if (dy > 0 && navView.isShown()) {
            if (up == false) {
                up = true;
                //navView.setVisibility(View.GONE);

                //   shownavbar(false);

            }
        } else if (dy < 0) {

            if (up) {
                up = false;
                // navView.setVisibility(View.VISIBLE);
                //   shownavbar(true);


            }


        }
    }


    @Override
    public void onNewsScrollchanged(int state) {

        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            // navView.animate().translationY(0).setDuration(200);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAlertsUpdate(final int alerts, final int inbox) {

        ForumsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView numalerts = alertscont.findViewById(R.id.numalerts);
                TextView numinbox = inboxcont.findViewById(R.id.numinbox);


                if(alerts == 0) {
                    numalerts.setVisibility(View.INVISIBLE);
                    btnalerts.setBackground(getDrawable(R.drawable.menu_alerts));
                }else {
                    numalerts.setVisibility(View.VISIBLE);
                    btnalerts.setBackground(getDrawable(R.drawable.menu_alerts_unread));

                    if(Integer.parseInt(numalerts.getText().toString().trim()) < alerts){
                        Toast.makeText(getApplicationContext(),"You have " +(alerts-Integer.parseInt(numalerts.getText().toString().trim()))+" new Alert(s)",Toast.LENGTH_SHORT).show();
                        createNotification("Alerts","You have " +(alerts-Integer.parseInt(numalerts.getText().toString().trim()))+" new Alert(s)");

                    }

                }
                if(inbox == 0) {
                    numinbox.setVisibility(View.INVISIBLE);
                    btninbox.setBackground(getDrawable(R.drawable.menu_chat));
                }else {
                    numinbox.setVisibility(View.VISIBLE);
                    btninbox.setBackground(getDrawable(R.drawable.menu_chat_unread));
                    if(Integer.parseInt(numinbox.getText().toString().trim()) < inbox){
                        Toast.makeText(getApplicationContext(),"You have " +(inbox-Integer.parseInt(numinbox.getText().toString().trim()))+" new unread conversation(s)",Toast.LENGTH_SHORT).show();
                        //   createNotification("Inbox","You have " +(inbox-Integer.parseInt(numinbox.getText().toString().trim()))+" new unread conversation(s)");

                    }
                }

                numalerts.setText(""+alerts);
                numinbox.setText(""+inbox);
            }
        });


    }

    @Override
    public void onForumModelInteraction(Forum item) {
        if (item != null && item.getType().equals("forum")) {
            Intent intent = new Intent(this, ThreadListActivity.class);
            intent.putExtra("url", item.getUrl());
            intent.putExtra("title", item.getTitle());
            startActivity(intent);
        } else {

        }

    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(0);
        }
    }
    @Override
    public void onListFragmentInteraction(Newsitem item) {

    }
}
