package tech.sabtih.forumapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.inputmethod.InputMethodManager;

import tech.sabtih.forumapp.adapters.ViewPagerAdapter;
import tech.sabtih.forumapp.fragments.AccountFragment;
import tech.sabtih.forumapp.fragments.ForumCategoryFragment;
import tech.sabtih.forumapp.fragments.NewsFragment;
import tech.sabtih.forumapp.fragments.shoutbox;
import tech.sabtih.forumapp.listeners.OnAlertsupdatedListener;
import tech.sabtih.forumapp.listeners.OnForumsListInteractionListener;
import tech.sabtih.forumapp.listeners.OnListFragmentInteractionListener;
import tech.sabtih.forumapp.models.Forum;
import tech.sabtih.forumapp.models.Forumcategory;
import tech.sabtih.forumapp.models.Newsitem;

public class Myforums extends AppCompatActivity implements OnListFragmentInteractionListener, NewsFragment.OnNewsListFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener, OnForumsListInteractionListener, shoutbox.OnFragmentInteractionListener, OnAlertsupdatedListener {


    private NewsFragment news;
    public AccountFragment account;
    public ForumCategoryFragment fcat;
    public shoutbox chat;
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView navView;
    ViewPagerAdapter viewPagerAdapter;
    ConstraintLayout constraintLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // mTextMessage.setText(R.string.title_home);
                    viewPager.setCurrentItem(0);
                    //   return true;
                    break;
                case R.id.navigation_dashboard:
                    //   mTextMessage.setText(R.string.title_dashboard);
                    viewPager.setCurrentItem(1);
                    fragment = fcat;
                    break;
                // return true;
                case R.id.navigation_chat:
                    //  mTextMessage.setText(R.string.title_notifications);
                    //   return true;

                    viewPager.setCurrentItem(2);
                    //shownavbar(true);
                    break;
                case R.id.navigation_notifications:
                    //  mTextMessage.setText(R.string.title_notifications);
                    //   return true;
                    viewPager.setCurrentItem(3);
                    break;
            }
            if (fragment != null) {

                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myforums);
        constraintLayout = findViewById(R.id.container);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);



        navView = findViewById(R.id.nav_view);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //  System.out.println("scroll changed");
                // Log.d("Scrolling",""+positionOffset +" mapped " + ((int)(positionOffset*240) ));
                if (position == 0)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(240, 230 + ((int) (positionOffset * 10)), 200, 200)));
                if (position == 1)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(240, 240, 200 - ((int) (positionOffset * 20)), 200)));
                if (position == 2)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(240, 240, 180, 200 - ((int) (positionOffset * 20)))));
                if (position == 3)
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(240, 240, 180, 180)));

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
                View mview = Myforums.this.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (mview == null) {
                    mview = new View(Myforums.this);
                }
                InputMethodManager imm = (InputMethodManager) Myforums.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
    public void onListFragmentInteraction(Newsitem item) {

    }

    @Override
    public void onNewsListFragmentInteraction(Newsitem item) {

    }

    boolean up = false;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_menu,menu);
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

    @Override
    public void onNewsScrollchanged(int state) {

        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            // navView.animate().translationY(0).setDuration(200);
        }
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAlertsUpdate(int alerts, int inbox) {

    }
}
