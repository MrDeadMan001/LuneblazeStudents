package com.avadna.luneblaze.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.activities.search.SearchHomeActivity;
import com.avadna.luneblaze.helperClasses.KeyValuePair;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;

import com.avadna.luneblaze.utils.GlobalVariables;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.chat.NewConversationActivity;
import com.avadna.luneblaze.activities.chat.SearchConversationsActivity;
import com.avadna.luneblaze.activities.polls.CreatePollActivity;
import com.avadna.luneblaze.activities.profileInfo.InviteContactsActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.sessionCreation.SessionCreationPart1;
import com.avadna.luneblaze.adapters.MainActivityAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.home.NewsFeedFragment;
import com.avadna.luneblaze.fragments.home.NotificationsListFragment;
import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyLocation;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.services.AlternateMessagingService;
import com.avadna.luneblaze.utils.NonSwipingViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppBaseActivity {

    private ImageView iv_profile_info;
    private TextView tv_actionbar_title;
    private PojoLoginResponseData pojoLoginResponseData;

    private CoordinatorLayout cl_parent;
    private RelativeLayout rl_bottom_bar_wrapper;
    private ActionBar actionBar;
    private BottomNavigationView bot_nav_bar;
    private NonSwipingViewPager vp_pager;
    private MainActivityAdapter mainActivityAdapter;
    private View.OnClickListener onClickListener;
    private Animation animFadeIn, animFadeOut;
    private boolean isAnimRunning = false;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private Badge notificationBadge, friendBadge, messageBadge;
    private int unreadConversationCount = 0;
    private HashMap<String, String> unreadConversationsMap;

    // private FusedLocationProviderClient mFusedLocationClient;

    private ImageButton ib_filter;
    private ImageButton ib_search;
    private LinearLayout ll_search_filter_wrapper;
    private FloatingActionButton fb_create;
    private Dialog postCreationDialog;
    private Dialog sessionCreationDialog;
    private int currentTabPos = 2;

    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        preferenceUtils.setAppTourStatus(false);
        user_id = preferenceUtils.get_user_id();
        startService(new Intent(this, AlternateMessagingService.class));

        setUpGpsTracker();
        setupActionBar();
        initDispMetrics();
        initApis();
        initViews();
        initAnimations();
        initLists();
        initClickListener();
        setClickListener();
        initBroadCastReceiver();
       /* String encryptedApiUsername = AES.encrypt(AppKeys.RAZ_KEY, AppKeys.enKey);
        String encryptedApipassword = AES.encrypt(AppKeys.RAZ_SEC, AppKeys.enKey);*/

        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_MESSAGE_FCM));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_FRIEND_REQUEST));


        logAppOpenEvent();

        // clear the notification area when the app is opened

        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            hideContentFromGuest();
        }
    }

    private void logAppOpenEvent() {
        ArrayList<KeyValuePair> pairs=new ArrayList<>();
        commonFunctions.logFirebaseEvent(FirebaseAnalytics.Event.APP_OPEN,pairs);
    }

    private void hideContentFromGuest() {
        ll_search_filter_wrapper.setVisibility(View.GONE);
        fb_create.setVisibility(View.GONE);
    }

    private void setUpGpsTracker() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                    }, AppKeys.GET_LOCATION_PERMISSION);
        } else {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    if (location != null) {
                        MyLocationObject myLocationObject = new MyLocationObject(location.getLatitude(),
                                location.getLongitude());
                        preferenceUtils.saveUserCurrentLocation(myLocationObject);
                    }
                    //Got the location!
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppKeys.GET_LOCATION_PERMISSION: {
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        if (location != null) {
                            MyLocationObject myLocationObject = new MyLocationObject(location.getLatitude(),
                                    location.getLongitude());
                            preferenceUtils.saveUserCurrentLocation(myLocationObject);
                        }
                        //Got the location!
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(this, locationResult);
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getStringExtra("tab") != null) {
            if (getIntent().getStringExtra("tab").equals("chat")) {
                vp_pager.setCurrentItem(1);
                bot_nav_bar.setSelectedItemId(R.id.nav_message);
            } else if (getIntent().getStringExtra("tab").equals("requests")) {
                vp_pager.setCurrentItem(4);
                bot_nav_bar.setSelectedItemId(R.id.nav_friends);
            } else if (getIntent().getStringExtra("tab").equals(AppKeys.SESSION)) {
                vp_pager.setCurrentItem(3);
                bot_nav_bar.setSelectedItemId(R.id.nav_session);
            } else {
                vp_pager.setCurrentItem(2);
                bot_nav_bar.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        commonFunctions.createNotificationChannel();
        if (getIntent().getStringExtra("tab") != null) {
            if (getIntent().getStringExtra("tab").equals("chat")) {
                vp_pager.setCurrentItem(1);
                bot_nav_bar.setSelectedItemId(R.id.nav_message);
            } else if (getIntent().getStringExtra("tab").equals("requests")) {
                vp_pager.setCurrentItem(4);
                bot_nav_bar.setSelectedItemId(R.id.nav_friends);
            } else if (getIntent().getStringExtra("tab").equals(AppKeys.SESSION)) {
                vp_pager.setCurrentItem(3);
                bot_nav_bar.setSelectedItemId(R.id.nav_session);
            } else {
                vp_pager.setCurrentItem(2);
                bot_nav_bar.setSelectedItemId(R.id.nav_home);
            }
        }

        showBars();
        setProfileInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        logAppStartTime();
    }

    private void logAppStartTime() {
        Calendar calendar=Calendar.getInstance();
        GlobalVariables.appStartTime=calendar.getTime();
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.NEW_MESSAGE_FCM)) {
                 /*   String conversation_id = intent.getStringExtra("conversation_id");
                    unreadConversations = preferenceUtils.getUnreadConversations();
                    if (unreadConversations.containsKey(conversation_id)) {
                        int currCount = unreadConversations.get(conversation_id);
                        currCount++;
                        //unreadConversations.remove(conversation_id);
                        unreadConversations.put(conversation_id, currCount);
                    } else {
                        unreadConversations.put(conversation_id, 1);
                    }
                    preferenceUtils.saveUnreadConversations(unreadConversations);*/

                    unreadConversationsMap = preferenceUtils.getUnreadConversationsMap();
                    String conv_id = intent.getStringExtra("conversation_id");
                    if (!unreadConversationsMap.containsKey(conv_id)) {
                        unreadConversationsMap.put(conv_id, "0");
                    }
                    preferenceUtils.saveUnreadConversationsMap(unreadConversationsMap);
                    setBadgeValue(messageBadge, unreadConversationsMap.size() + preferenceUtils.getUnseenConvCount());
                    //messageBadge.setBadgeNumber(unreadConversationsMap.size() + preferenceUtils.getUnseenConvCount());

                } else if (intent.getAction().equals(Config.NEW_NOTIFICATION)) {
                    HashMap<String, String> notifIdMap = preferenceUtils.getNotificationIdMap();
                    String notificationId = intent.getStringExtra("notification_id");
                    if (!notifIdMap.containsKey(notificationId)) {
                        notifIdMap.put(notificationId, "0");
                    }
                    preferenceUtils.saveNotificationIdMap(notifIdMap);
                    setBadgeValue(notificationBadge, notifIdMap.size() + preferenceUtils.getNewNotificationCount());
                    //notificationBadge.setBadgeNumber(notifIdMap.size() + preferenceUtils.getNewNotificationCount());

                } else if (intent.getAction().equals(Config.NEW_FRIEND_REQUEST)) {
                    int friend_req_count = preferenceUtils.getNewFriendReqCount() + 1;
                    setBadgeValue(friendBadge, friend_req_count);
                    //friendBadge.setBadgeNumber(friend_req_count);
                    preferenceUtils.saveNewFriendReqCount(friend_req_count);
                }
            }
        };
    }


    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            // actionBar.setDisplayHomeAsUpEnabled(true);
            //  actionBar.setTitle(getString(R.string.home));
            //  actionBar.setIcon(R.drawable.ic_action_search);
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(MainActivity.this, R.color.app_theme_dark)));

            // actionBar.setDisplayShowTitleEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(MainActivity.this, R.color.app_theme_extra_dark));
        }
    }

    private void initDispMetrics() {

    }

    private void initApis() {


    }

    private void initAnimations() {
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRunning = true;
                bot_nav_bar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRunning = true;

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bot_nav_bar.setVisibility(View.GONE);
                if (rl_bottom_bar_wrapper.findViewById(R.id.bot_nav_bar) != null) {
                    rl_bottom_bar_wrapper.removeView(bot_nav_bar);
                }
                isAnimRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void initViews() {
        ll_search_filter_wrapper = (LinearLayout) findViewById(R.id.ll_search_filter_wrapper);

        ib_filter = (ImageButton) findViewById(R.id.ib_filter);
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        iv_profile_info = (ImageView) findViewById(R.id.iv_profile_info);
        tv_actionbar_title = (TextView) findViewById(R.id.tv_actionbar_title);

        fb_create = (FloatingActionButton) findViewById(R.id.fb_create);
        rl_bottom_bar_wrapper = (RelativeLayout) findViewById(R.id.rl_bottom_bar_wrapper);
        setProfileInfo();
        setUpPager();
        setUpBotNavBar();
    }


    private void setProfileInfo() {
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        Glide.with(MainActivity.this.getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(iv_profile_info);
    }


    private void setUpPager() {
        vp_pager = (NonSwipingViewPager) findViewById(R.id.vp_pager);
        mainActivityAdapter = new MainActivityAdapter(MainActivity.this, getSupportFragmentManager());
        vp_pager.setAdapter(mainActivityAdapter);
        vp_pager.setOffscreenPageLimit(5);

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

               /* if (i == 3) {
                    tv_create_session.setVisibility(View.VISIBLE);
                } else {
                    tv_create_session.setVisibility(View.GONE);
                }

                if (i == 3) {
                    rl_filter_wrapper.setVisibility(View.VISIBLE);
                } else {
                    rl_filter_wrapper.setVisibility(View.GONE);
                }
*/


                if (currentTabPos == 0 && i != 0) {
                    resetNotificationCounter();
                }

                if (currentTabPos == 1 && i != 1) {
                    resetMessageCounter();
                }

                if (i == 1) {
                    ib_filter.setVisibility(View.GONE);
                } else {
                    ib_filter.setVisibility(View.VISIBLE);
                }

                if (i == 0) {
                    ib_search.setVisibility(View.GONE);
                    ib_filter.setImageResource(R.drawable.ic_notification_read_white);
                } else if (i == 4) {
                    ib_search.setVisibility(View.GONE);
                    ib_filter.setImageResource(R.drawable.ic_add_phone_contact);
                } else {
                    ib_search.setVisibility(View.VISIBLE);
                    ib_filter.setImageResource(R.drawable.ic_filter_white);
                }

                switch (i) {
                    case 0:
                        setCreateButtonVisibility(false);
                        tv_actionbar_title.setText(getString(R.string.notifications));
                        break;
                    case 1:
                        setCreateButtonIcon(R.drawable.ic_plus_white);
                        tv_actionbar_title.setText(getString(R.string.messages));
                        break;
                    case 2:
                        setCreateButtonIcon(R.drawable.ic_create_white);
                        tv_actionbar_title.setText(getString(R.string.feeds));

                        break;
                    case 3:
                        setCreateButtonIcon(R.drawable.ic_session_white);
                        tv_actionbar_title.setText(getString(R.string.sessions));

                        break;
                    case 4:
                        setCreateButtonVisibility(false);
                        tv_actionbar_title.setText(getString(R.string.fellows));
                        break;

                    default:
                        setCreateButtonVisibility(false);
                        break;
                }
                currentTabPos = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        // bot_nav_bar.enableAnimation(false);
    }

    public void setCreateButtonIcon(int resourceID) {
        setCreateButtonVisibility(false);
        fb_create.setImageResource(resourceID);
        setCreateButtonVisibility(true);

    }

    public void setCreateButtonVisibility(boolean visible) {
        if (visible && !user_id.equals(AppKeys.GUEST_USER_ID)) {
            fb_create.show();
        } else {
            fb_create.hide();
        }
    }

    public void hideBars() {
        setCreateButtonVisibility(false);
        if (!isAnimRunning && bot_nav_bar.getVisibility() == View.VISIBLE) {
            bot_nav_bar.startAnimation(animFadeOut);
        }
    }

    public void showBars() {
        int currentPagerItem = vp_pager.getCurrentItem();
        if (currentPagerItem == 1 || currentPagerItem == 2 || currentPagerItem == 3) {
            setCreateButtonVisibility(true);
        }

        if (!isAnimRunning && bot_nav_bar.getVisibility() == View.GONE) {
            if (rl_bottom_bar_wrapper.findViewById(R.id.bot_nav_bar) == null) {
                rl_bottom_bar_wrapper.addView(bot_nav_bar);
            }
            bot_nav_bar.startAnimation(animFadeIn);
        }
    }


    private void initLists() {

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.iv_profile_info:
                        Intent profileInfoIntent = new Intent(MainActivity.this, ProfileInfoActivity.class);
                        startActivity(profileInfoIntent);
                        break;

                    case R.id.et_search_bar:

                        break;

                    case R.id.ib_search:
                        int pos = vp_pager.getCurrentItem();
                        if (pos == 1) {
                            Intent searchConvIntent = new Intent(MainActivity.this, SearchConversationsActivity.class);
                            startActivity(searchConvIntent);
                        } else if (pos == 3) {

                            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                            searchIntent.putExtra("tab_type", AppKeys.SESSION);
                            searchIntent.putExtra(AppKeys.SHOW_KEYBOARD, true);
                            startActivity(searchIntent);
                        } else {
                            Intent searchIntent = new Intent(MainActivity.this, SearchHomeActivity.class);
                            startActivity(searchIntent);
                        }
                        break;

                  /*  case R.id.tv_filter:
                        if (vp_pager.getCurrentItem() == 2) {
                            ((NewsFeedFragment) mainActivityAdapter.getRegisteredFragment(2)).openFilterDialog();
                        } else if (vp_pager.getCurrentItem() == 3) {
                            ((AllSessionListFragmentNew) mainActivityAdapter.getRegisteredFragment(3)).openAllSessionFilter();
                    *//*        Intent filterVerified = new Intent(Config.OPEN_NEWS_FEED_FILTER);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(filterVerified);*//*
                        }
                        break;*/

                    case R.id.ib_filter:
                        if (vp_pager.getCurrentItem() == 0) {
                            ((NotificationsListFragment) mainActivityAdapter.getRegisteredFragment(0)).markAllNotificationsSeen();
                        } else if (vp_pager.getCurrentItem() == 2) {
                            ((NewsFeedFragment) mainActivityAdapter.getRegisteredFragment(2)).openFilterDialog();
                        } else if (vp_pager.getCurrentItem() == 3) {
                            ((AllSessionListFragmentNew) mainActivityAdapter.getRegisteredFragment(3)).openAllSessionFilter();
                    /*        Intent filterVerified = new Intent(Config.OPEN_NEWS_FEED_FILTER);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(filterVerified);*/
                        } else if (vp_pager.getCurrentItem() == 4) {
                            Intent contactIntent = new Intent(MainActivity.this,
                                    InviteContactsActivity.class);
                            startActivity(contactIntent);
                        }
                        break;

                    case R.id.fb_create:
                        if (vp_pager.getCurrentItem() == 1) {
                            Intent newConversationIntent = new Intent(MainActivity.this, NewConversationActivity.class);
                            startActivity(newConversationIntent);
                        }
                        if (vp_pager.getCurrentItem() == 2) {
                            openPostCreationDialog();
                        }
                        if (vp_pager.getCurrentItem() == 3) {
                            openSessionCreateDialog();
                        }
                        break;

                    case R.id.ll_post_wrapper:
                        Intent postIntent = new Intent(MainActivity.this, PostCreationActivity.class);
                        //  postIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(postIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        //    overridePendingTransition(0,0);

                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_session_wrapper:
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        openSessionCreateDialog();
                        break;

                    case R.id.ll_initiate_wrapper:
                        Intent sessionInitiateIntent = new Intent(MainActivity.this,
                                SessionCreationPart1.class);
                        sessionInitiateIntent.putExtra("type", AppKeys.SESSION_INITIATE);
                        startActivity(sessionInitiateIntent);
                        if (sessionCreationDialog != null) {
                            sessionCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_conduct_wrapper:
                        Intent sessionConductIntent = new Intent(MainActivity.this,
                                SessionCreationPart1.class);
                        sessionConductIntent.putExtra("type", AppKeys.SESSION_CONDUCT);
                        startActivity(sessionConductIntent);
                        if (sessionCreationDialog != null) {
                            sessionCreationDialog.dismiss();
                        }
                        break;


                    case R.id.ll_article_wrapper:
                        Intent articleIntent = new Intent(MainActivity.this, CreateArticleActivity.class);
                        startActivity(articleIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_poll_wrapper:
                        Intent pollIntent = new Intent(MainActivity.this, CreatePollActivity.class);
                        startActivity(pollIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                }
            }
        };
    }

    private void setClickListener() {
        if (!user_id.equals(AppKeys.GUEST_USER_ID)) {
            iv_profile_info.setOnClickListener(onClickListener);
            ib_search.setOnClickListener(onClickListener);
            fb_create.setOnClickListener(onClickListener);
        }
        ib_filter.setOnClickListener(onClickListener);
    }

    private void setUpBotNavBar() {
        bot_nav_bar = (BottomNavigationView) findViewById(R.id.bot_nav_bar);
        // bot_nav_bar.setTextVisibility(false);
        // bot_nav_bar.enableItemShiftingMode(false);
        //bot_nav_bar.enableShiftingMode(false);
       /* bot_nav_bar.setIconTintList(0, getResources().getColorStateList(R.color.bot_nav_bar_tint_list));
        bot_nav_bar.setIconTintList(1, getResources().getColorStateList(R.color.bot_nav_bar_tint_list));
        bot_nav_bar.setIconTintList(2, getResources().getColorStateList(R.color.bot_nav_bar_tint_list));
        bot_nav_bar.setIconTintList(3, getResources().getColorStateList(R.color.bot_nav_bar_tint_list));
        bot_nav_bar.setIconTintList(4, getResources().getColorStateList(R.color.bot_nav_bar_tint_list));
        bot_nav_bar.size(12);*/
        bot_nav_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_notification:
                        //  et_search_bar.setText(getString(R.string.notifications));

                        resetNotificationCounter();

                        vp_pager.setCurrentItem(0);
                        break;
                    case R.id.nav_message:
                        //   et_search_bar.setText(getString(R.string.messages));
                        resetMessageCounter();
                        vp_pager.setCurrentItem(1);

                        break;
                    case R.id.nav_home:
                        //   et_search_bar.setText(getString(R.string.home));
                        vp_pager.setCurrentItem(2);

                        break;
                    case R.id.nav_session:
                        //   et_search_bar.setText(getString(R.string.sessions));
                        vp_pager.setCurrentItem(3);

                        break;
                    case R.id.nav_friends:
                        //   et_search_bar.setText(getString(R.string.friend_requests));
                        vp_pager.setCurrentItem(4);
                        setBadgeValue(friendBadge, 0);
                        //friendBadge.setBadgeNumber(0);
                        preferenceUtils.saveNewFriendReqCount(0);
                        break;

                }
                return true;
            }
        });

        int prefNotifCounter = preferenceUtils.getNewNotificationCount();

        notificationBadge = addBadgeAt(0, preferenceUtils.getNotificationIdMap().size()
                + preferenceUtils.getNewNotificationCount());

        friendBadge = addBadgeAt(4, preferenceUtils.getNewFriendReqCount());

        messageBadge = addBadgeAt(1, preferenceUtils.getUnreadConversationsMap().size()
                + preferenceUtils.getUnseenConvCount());

        String tabType = getIntent().getStringExtra("tab");
        int a1 = 5;

        if (getIntent().getStringExtra("tab") != null) {
            if (getIntent().getStringExtra("tab").equals("chat")) {
                vp_pager.setCurrentItem(1);
                bot_nav_bar.setSelectedItemId(R.id.nav_message);
            } else if (getIntent().getStringExtra("tab").equals("requests")) {
                vp_pager.setCurrentItem(4);
                bot_nav_bar.setSelectedItemId(R.id.nav_friends);
            } else if (getIntent().getStringExtra("tab").equals(AppKeys.SESSION)) {
                vp_pager.setCurrentItem(3);
                bot_nav_bar.setSelectedItemId(R.id.nav_session);
            } else {
                vp_pager.setCurrentItem(2);
                bot_nav_bar.setSelectedItemId(R.id.nav_home);
            }
        } else {
            vp_pager.setCurrentItem(2);
            bot_nav_bar.setSelectedItemId(R.id.nav_home);
        }

    }

    private void resetMessageCounter() {
        HashMap<String, String> unreadConversationMap = new HashMap<>();
        preferenceUtils.saveUnreadConversationsMap(unreadConversationMap);
        preferenceUtils.saveUnseenConvCount(0);
        setBadgeValue(messageBadge, unreadConversationMap.size());
    }

    private void resetNotificationCounter() {
        HashMap<String, String> notifIdMap = new HashMap<>();
        preferenceUtils.saveNotificationIdMap(notifIdMap);
        preferenceUtils.saveNewNotificationCount(0);
        setBadgeValue(notificationBadge, notifIdMap.size());
    }


    private void setBadgeValue(Badge badge, int value) {
        if (!user_id.equals(AppKeys.GUEST_USER_ID)) {
            badge.setBadgeNumber(value);
        }
    }

    private Badge addBadgeAt(int position, int number) {
        // add badge
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            number = 0;
        }
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bot_nav_bar.getChildAt(0);
        return new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(bottomNavigationMenuView.getChildAt(position));
    }


    private void openPostCreationDialog() {
        LinearLayout ll_post_wrapper, ll_session_wrapper, ll_article_wrapper, ll_poll_wrapper;
        postCreationDialog = new MyCustomThemeDialog(MainActivity.this, R.style.NoTitleDialogTheme);
        postCreationDialog.setContentView(R.layout.post_creation_dialog);
        ll_post_wrapper = (LinearLayout) postCreationDialog.findViewById(R.id.ll_post_wrapper);
        ll_session_wrapper = (LinearLayout) postCreationDialog.findViewById(R.id.ll_session_wrapper);
        ll_article_wrapper = (LinearLayout) postCreationDialog.findViewById(R.id.ll_article_wrapper);
        ll_poll_wrapper = (LinearLayout) postCreationDialog.findViewById(R.id.ll_poll_wrapper);

        ll_post_wrapper.setOnClickListener(onClickListener);
        ll_session_wrapper.setOnClickListener(onClickListener);
        ll_article_wrapper.setOnClickListener(onClickListener);
        ll_poll_wrapper.setOnClickListener(onClickListener);
        postCreationDialog.setCancelable(true);
        postCreationDialog.show();
    }

    private void openSessionCreateDialog() {
        LinearLayout ll_initiate_wrapper, ll_conduct_wrapper;
        sessionCreationDialog = new MyCustomThemeDialog(MainActivity.this, R.style.NoTitleDialogTheme);
        sessionCreationDialog.setContentView(R.layout.session_creation_dialog);
        ll_initiate_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_initiate_wrapper);
        ll_conduct_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_conduct_wrapper);
        ll_initiate_wrapper.setOnClickListener(onClickListener);
        ll_conduct_wrapper.setOnClickListener(onClickListener);
        sessionCreationDialog.setCancelable(true);
        sessionCreationDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logAppSessionEndDuration();
    }

    private void logAppSessionEndDuration() {
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        ArrayList<KeyValuePair> pairs=new ArrayList<>();

        pairs.add(new KeyValuePair(FirebaseAnalytics.Param.START_DATE,GlobalVariables.appStartTime.toString()));
        pairs.add(new KeyValuePair(FirebaseAnalytics.Param.END_DATE,date.toString()));
        float duration=(date.getTime()-GlobalVariables.appStartTime.getTime())/(1000f*60);
        pairs.add(new KeyValuePair(AppKeys.APP_ACTIVE_DURATION,String.valueOf(duration)));
        commonFunctions.logFirebaseEvent(AppKeys.APP_ACTIVE_DURATION_EVENT,pairs);
    }

    @Override
    public void onBackPressed() {
        if (vp_pager.getCurrentItem() == 2) {
            finish();
        } else {
            vp_pager.setCurrentItem(2);
            bot_nav_bar.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

}
