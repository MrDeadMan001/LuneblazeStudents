package com.avadna.luneblaze.activities.venue;

import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.adapters.VenueImagePagerAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.adapters.MyFriendsListAdapter;
import com.avadna.luneblaze.adapters.venue.VenueSlotListAdapter;
import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoInvite.PojoInviteFriendResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.utils.NotificationUtils;
import com.bumptech.glide.Glide;
import com.avadna.luneblaze.adapters.venue.VenuePagerAdapter;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

//todo add marker name in map
public class VenueActivity extends AppBaseActivity implements
        MyFriendsListAdapter.FriendListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {
    ActionBar actionBar;
    CoordinatorLayout cl_parent;
    ProgressBar pb_loading_content;
    FloatingActionButton fab_filter;

    AppBarLayout appBarLayout;
    boolean autoSelected = false;
    TabLayout tl_tabs;
    ViewPager vp_tab_item_pager;
    VenuePagerAdapter venuePagerAdapter;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    PojoVenueData pojoVenueData;

    TextView tv_venue_name, tv_follow;
    TextView tv_session_count, tv_follower_count, tv_attendee_count;
    ViewPager vp_cover_photos;
    List<PojoVenuePhoto> venuePhotos;
    VenueImagePagerAdapter venueImagePagerAdapter;

    RatingBar rb_venue_rating;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;
    Dialog followersListDialog;
    /*
        List<String> more_options_list;
        ArrayAdapter<String> spinnerAdapter;
        Spinner sp_more_options;
        ImageView iv_more_options;*/
    String USER = "user";
    String OTHER = "other";
    String type = USER;
    String venue_id = "5";

    private static final String TAG = VenueActivity.class.getSimpleName();
    private TextView txtRegId, txtMessage;
    View.OnClickListener onClickListener;
    DisplayMetrics displayMetrics;
    String notification_id;

    Handler radioButtonHandler;
    Runnable radioButtonRunnable;
    int onlineStatusDelay = 2000;
    boolean handlerRunning = false;
    int counter = 0;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onResume() {
        super.onResume();

        NotificationUtils.clearNotifications(getApplicationContext());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_venue2);
        preferenceUtils = new PreferenceUtils(VenueActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initBroadCastReceiver();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        venue_id = getIntent().getStringExtra("id");
        if (venue_id == null || venue_id.isEmpty()) {
            venue_id = "5";
        }
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        setupActionBar();
        initBarSizes();
        initDispMetrics();
        initApis();
        initViews();
        initLists();
        initClickListener();
        setClickListener();
        setTextChangedListeners();
        initFocusChangeListener();
        setFocusChangeListener();
        hitGetVenueByIdApi(user_id, venue_id);

        radioButtonHandler = new Handler();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.VENUE_PHOTOS_UPDATED));

    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.VENUE_PHOTOS_UPDATED)) {
                    getNewVenuePhotos(user_id, venue_id);
                }
            }
        };
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VenueActivity.this, R.color.app_theme_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VenueActivity.this, R.color.app_theme_extra_dark));
            }
            actionBar.setDisplayShowTitleEnabled(true);
        }
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float maxRange = appBarLayout.getTotalScrollRange();
                actionBar.setBackgroundDrawable(new ColorDrawable
                        (Color.argb(((int) (25 + (Math.abs(verticalOffset) / maxRange) * 230)), 11, 108, 115)));
                if (verticalOffset < -360) {
                    if (pojoGetVenueDetailsResponse != null) {
                        actionBar.setTitle(pojoGetVenueDetailsResponse.data.venueData.venueName);
                    }
                } else {
                    actionBar.setTitle("");
                }
            }
        });
    }


    private void initBarSizes() {
    }

    private void initDispMetrics() {
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initViews() {
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        fab_filter = (FloatingActionButton) findViewById(R.id.fab_filter);
        fab_filter.hide();
        setUpTabLayout();
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        tv_venue_name = (TextView) findViewById(R.id.tv_venue_name);
        tv_session_count = (TextView) findViewById(R.id.tv_session_count);
        tv_follower_count = (TextView) findViewById(R.id.tv_follower_count);
        tv_attendee_count = (TextView) findViewById(R.id.tv_attendee_count);
        vp_cover_photos = (ViewPager) findViewById(R.id.vp_cover_photos);
        rb_venue_rating = (RatingBar) findViewById(R.id.rb_venue_rating);

        rb_venue_rating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    openRatingDialog();
                }
                return true;
            }
        });
    }

    private void setDataOnViews() {
        invalidateOptionsMenu();
        tv_venue_name.setText(pojoVenueData.venueName);

        setUpCoverPhotoList();

        if (!pojoVenueData.totalRating.isEmpty()) {
            rb_venue_rating.setRating(Float.parseFloat(pojoVenueData.totalRating));
        }

        if (pojoVenueData.iFollow == 1) {
            tv_follow.setText(getString(R.string.following));
            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                    0, 0, 0);
        } else {
            tv_follow.setText(getString(R.string.follow));
            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                    0, 0, 0);
        }
        tv_attendee_count.setText(pojoVenueData.sessionAttendCount.toString());
        tv_follower_count.setText(pojoVenueData.followersCount.toString());
        tv_session_count.setText(pojoVenueData.sessionCount.toString());
    }

    private void setUpCoverPhotoList() {
        counter = 0;
        venuePhotos = new ArrayList<>();
        if (pojoGetVenueDetailsResponse.data.allphoto == null) {
            PojoVenuePhoto pojoVenuePhoto = new PojoVenuePhoto(pojoGetVenueDetailsResponse.data.venueData.cover_photo, "");
            venuePhotos.add(pojoVenuePhoto);

        } else {
            venuePhotos.addAll(pojoGetVenueDetailsResponse.data.allphoto);
        }
        vp_cover_photos = (ViewPager) findViewById(R.id.vp_cover_photos);
        venueImagePagerAdapter = new VenueImagePagerAdapter(VenueActivity.this, venuePhotos);

        vp_cover_photos.setAdapter(venueImagePagerAdapter);

        if (!handlerRunning) {
            radioButtonRunnable = new Runnable() {
                public void run() {
                    //buttonList.get(counter % 5).setChecked(true);
                    if (pojoGetVenueDetailsResponse != null && vp_cover_photos != null && !venuePhotos.isEmpty()) {
                        vp_cover_photos.setCurrentItem(counter % venuePhotos.size());
                        counter++;
                        radioButtonHandler.postDelayed(this, onlineStatusDelay);
                    }
                }
            };
            radioButtonHandler.postDelayed(radioButtonRunnable, 2000);
            handlerRunning = true;
        }

    }

    private void initLists() {
        //   iniMoreOptionsSpinner();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_follower_count:
                        openUserListDialog("venuefollowers");
                        break;

                    case R.id.rb_venue_rating:
                        openRatingDialog();
                        break;

                    case R.id.tv_attendee_count:
                        openUserListDialog("venue_attendees");
                        break;

                    case R.id.tv_session_count:
                        vp_tab_item_pager.setCurrentItem(1);
                        break;

                    case R.id.fab_filter:
                        ((AllSessionListFragmentNew) venuePagerAdapter.getRegisteredFragment(1)).openAllSessionFilter();
                      /*  Fragment fragment = venuePagerAdapter.getRegisteredFragment(1);
                        int sessionTabPos = ((VenueBasedSessionListFragment) fragment).getSelectedTabPosition();
                        switch (sessionTabPos) {
                            case 0:
                                Intent filterVerified = new Intent(Config.OPEN_VERIFIED_SESSION_FILTER);
                                filterVerified.putExtra(AppKeys.ACTIVITY_NAME, VenueActivity.this.getClass().getSimpleName());
                                LocalBroadcastManager.getInstance(VenueActivity.this).sendBroadcast(filterVerified);
                                break;

                            case 1:
                                Intent filterAssigned = new Intent(Config.OPEN_ASSIGNED_SESSION_FILTER);
                                filterAssigned.putExtra(AppKeys.ACTIVITY_NAME, VenueActivity.this.getClass().getSimpleName());
                                LocalBroadcastManager.getInstance(VenueActivity.this).sendBroadcast(filterAssigned);
                                break;

                            case 2:
                                Intent filterorganised = new Intent(Config.OPEN_ORGANISED_SESSION_FILTER);
                                filterorganised.putExtra(AppKeys.ACTIVITY_NAME, VenueActivity.this.getClass().getSimpleName());
                                LocalBroadcastManager.getInstance(VenueActivity.this).sendBroadcast(filterorganised);
                                break;
                        }*/

                        break;

                    case R.id.tv_follow:
                        //todo change ifollow to the field in api response of venue data
                        if (pojoVenueData.iFollow == 1) {
                            hitFollowVenueApi(user_id, venue_id, "remove_notify_me_venue");
                            pojoVenueData.followersCount--;
                            tv_follow.setText(getString(R.string.follow));
                            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                                    0, 0, 0);
                            pojoVenueData.iFollow = 0;
                        } else {
                            hitFollowVenueApi(user_id, venue_id, "notify_me_venue");
                            pojoVenueData.followersCount++;
                            tv_follow.setText(getString(R.string.following));
                            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                                    0, 0, 0);
                            pojoVenueData.iFollow = 1;
                        }
                        tv_follower_count.setText(pojoVenueData.followersCount.toString());
                        break;

                    case R.id.iv_cover_photo:
                        if (pojoVenueData != null) {
                            openImageViewerDialog(pojoVenueData.cover_photo);
                        }
                        break;


                }
            }
        };
    }

    private void openRatingDialog() {
        Dialog ratingDialog = new MyCustomThemeDialog(VenueActivity.this, R.style.NoTitleDialogTheme);
        ratingDialog.setContentView(R.layout.venue_four_rating_layout);

        RatingBar rb_seating, rb_accessibility, rb_staff, rb_ambiance;
        rb_seating = (RatingBar) ratingDialog.findViewById(R.id.rb_seating);
        rb_accessibility = (RatingBar) ratingDialog.findViewById(R.id.rb_accessibility);
        rb_staff = (RatingBar) ratingDialog.findViewById(R.id.rb_staff);
        rb_ambiance = (RatingBar) ratingDialog.findViewById(R.id.rb_ambiance);

        TextView tv_title=(TextView) ratingDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.rate_venue));

        rb_seating.setRating(pojoVenueData.rating1);
        rb_accessibility.setRating(pojoVenueData.rating2);
        rb_staff.setRating(pojoVenueData.rating3);
        rb_ambiance.setRating(pojoVenueData.rating4);


        rb_seating.setIsIndicator(true);
        rb_accessibility.setIsIndicator(true);
        rb_staff.setIsIndicator(true);
        rb_ambiance.setIsIndicator(true);

        ratingDialog.setCancelable(true);

        TextView tv_submit = (TextView) ratingDialog.findViewById(R.id.tv_submit);
        tv_submit.setVisibility(View.GONE);
        ratingDialog.show();
    }


    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(VenueActivity.this, android.R.style.Theme_Black_NoTitleBar);
        imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
        ImageView iv_back, iv_edit;
        PhotoView iv_photo;
        iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);

        iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
        iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);

        iv_edit.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewerDialog.dismiss();
            }
        });

        Glide.with(VenueActivity.this.getApplicationContext())
                .load(path)
                .into(iv_photo);

        imageViewerDialog.show();

    }

    private void openShareVenueOnProfileDialog() {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);
        ;
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);
        et_share_title.setVisibility(View.GONE);
        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);
        rl_item_holder = (RelativeLayout) shareDialog.findViewById(R.id.rl_item_holder);

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);
        Glide.with(getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.share_venue_item, rl_item_holder, true);


        ImageView iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
        TextView tv_venue_name = (TextView) view.findViewById(R.id.tv_venue_name);
        RatingBar rb_venue_rating = (RatingBar) view.findViewById(R.id.rb_venue_rating);
        TextView tv_session_count = (TextView) view.findViewById(R.id.tv_session_count);
        TextView tv_follower_count = (TextView) view.findViewById(R.id.tv_follower_count);
        TextView tv_attendee_count = (TextView) view.findViewById(R.id.tv_attendee_count);


        Glide.with(getApplicationContext())
                .load(pojoVenueData.cover_photo)
                .apply(new RequestOptions().override((int) (displayMetrics.widthPixels * 0.8f),
                        (int) (displayMetrics.widthPixels * 0.5f)))
                .into(iv_cover_photo);

        tv_venue_name.setText(pojoVenueData.venueName);
        rb_venue_rating.setRating(Float.parseFloat(pojoVenueData.totalRating));
        tv_session_count.setText("" + pojoVenueData.sessionCount);
        tv_follower_count.setText("" + pojoVenueData.followersCount);
        tv_attendee_count.setText("" + pojoVenueData.venueAttendesCount);


        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, pojoVenueData.venueId, "share_venue",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
    }

    private void setClickListener() {
        tv_follower_count.setOnClickListener(onClickListener);
        tv_attendee_count.setOnClickListener(onClickListener);
        tv_follow.setOnClickListener(onClickListener);
        tv_session_count.setOnClickListener(onClickListener);
        fab_filter.setOnClickListener(onClickListener);
        rb_venue_rating.setOnClickListener(onClickListener);
    }

    private void setTextChangedListeners() {
    }

    private void initFocusChangeListener() {

    }

    private void setFocusChangeListener() {

    }

    public void showFilter() {
        fab_filter.show();
    }

    public void hideFilter() {
        fab_filter.hide();
    }

    private void setUpTabLayout() {

        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.details)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.sessions)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.team)));
        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (vp_tab_item_pager != null) {
                    vp_tab_item_pager.setCurrentItem(tab.getPosition());
                }
                if (!autoSelected) {
                    appBarLayout.setExpanded(false, true);
                }
                autoSelected = false;

                if (tab.getPosition() == 1) {
                    fab_filter.show();
                } else {
                    fab_filter.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void openUserListDialog(String reaction) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });

        rv_upvoters = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        upvoterListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        upvoterList = new ArrayList<>();
        rv_upvoters.setLayoutManager(upvoterListLayoutManager);
        upvotersAndAttendeesListAdapter = new UpvotersAndAttendeesListAdapter(this, upvoterList);

        rv_upvoters.setAdapter(upvotersAndAttendeesListAdapter);

        if (reaction.equals("venuefollowers")) {
            tv_title.setText(this.getString(R.string.followers));
        } else {
            tv_title.setText(this.getString(R.string.attendees));
        }

        rv_upvoters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    if (!isGetUpvotersApiCalled
                            && upvoterListLayoutManager.findLastVisibleItemPosition() > upvoterList.size() - 2) {
                        hitGetUpvotersListApi(user_id, venue_id, reaction, ++upvoterListOffset);
                    }
                }
            }
        });
        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, venue_id, reaction, upvoterListOffset);
        userListDialog.show();
    }

    private void hitGetUpvotersListApi(String user_id, final String id, String reaction, int offset) {
        Call<PojoUpvotersAndAttendeesListResponse> call = apiService.getUpvotersAndAttendees(user_id,
                id, reaction, String.valueOf(offset));
        if (!isGetUpvotersApiCalled) {
            upvotersAndAttendeesListAdapter.setShowProgressBar(true);
            isGetUpvotersApiCalled = true;
            call.enqueue(new Callback<PojoUpvotersAndAttendeesListResponse>() {
                @Override
                public void onResponse(Call<PojoUpvotersAndAttendeesListResponse> call,
                                       Response<PojoUpvotersAndAttendeesListResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        if (offset == 0) {
                            upvoterList.clear();
                        }
                        upvoterList.addAll(response.body().data);
                        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
                    }
                    isGetUpvotersApiCalled = false;
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoUpvotersAndAttendeesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                    isGetUpvotersApiCalled = false;

                }
            });
        }
    }


    private void hitGetVenueByIdApi(String user_id, String venue_id) {

        Call<PojoGetVenueDetailsResponse> call = apiService.getVenueDetailsFromIdApi(user_id, venue_id,
                "upcoming");
        call.enqueue(new Callback<PojoGetVenueDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoGetVenueDetailsResponse> call, Response<PojoGetVenueDetailsResponse> response) {
                String message = "";
                if (response.body() != null && response.body().data != null) {
                    message = response.body().message;
                    pojoGetVenueDetailsResponse = response.body();
                    pojoVenueData = pojoGetVenueDetailsResponse.data.venueData;
                    setDataOnViews();
                    venuePagerAdapter = new VenuePagerAdapter(VenueActivity.this,
                            getSupportFragmentManager(), pojoGetVenueDetailsResponse);
                    vp_tab_item_pager = (ViewPager) findViewById(R.id.vp_tab_item_pager);
                    vp_tab_item_pager.setAdapter(venuePagerAdapter);

                    vp_tab_item_pager.setOffscreenPageLimit(3);
                    vp_tab_item_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            tl_tabs.setScrollPosition(position, positionOffset, false);
                        }

                        @Override
                        public void onPageSelected(int position) {
                            TabLayout.Tab tab = tl_tabs.getTabAt(position);
                            tab.select();
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    cl_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<PojoGetVenueDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.venue));
            }
        });
    }


    private void getNewVenuePhotos(String user_id, String venue_id) {
        Call<PojoGetVenueDetailsResponse> call = apiService.getVenueDetailsFromIdApi(user_id, venue_id,
                "upcoming");
        call.enqueue(new Callback<PojoGetVenueDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoGetVenueDetailsResponse> call, Response<PojoGetVenueDetailsResponse> response) {
                String message = "";
                if (response.body().data != null) {
                    pojoGetVenueDetailsResponse.data.allphoto = response.body().data.allphoto;
                    setUpCoverPhotoList();
                }

            }

            @Override
            public void onFailure(Call<PojoGetVenueDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetVenueByIdApi(user_id, venue_id);
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }

    }

    private void hitFollowVenueApi(String user_id, String venue_id, String connection_type) {
        {
            Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, venue_id, connection_type);
            call.enqueue(new Callback<PojoUserConnectResponse>() {
                @Override
                public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    }
                }

                @Override
                public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetVenueDetailsResponse != null) {
            getMenuInflater().inflate(R.menu.venue_page_menu, menu);
            if (!canUpdateData()) {
                menu.removeItem(R.id.update_photo);
            }
        }
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    private boolean canUpdateData() {
        for (int i = 0; i < pojoGetVenueDetailsResponse.data.teams.size(); i++) {
            if (pojoGetVenueDetailsResponse.data.teams.get(i).userId.equals(user_id)
                    && pojoGetVenueDetailsResponse.data.teams.get(i).role.equals("Ambassadors")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.venue_report:
                commonFunctions.openReportDialog(user_id, "report_venue", pojoVenueData.venueId);
                break;

            case R.id.venue_invite:
                Intent inviteIntent = new Intent(VenueActivity.this, InviteActivity.class);
                inviteIntent.putExtra("userType", "venue_invitation");
                inviteIntent.putExtra("id", venue_id);
                startActivity(inviteIntent);
                // hitInviteFriendApi(user_id,"invite_venue","3",pojoVenueData.venueId);
                break;

            case R.id.share_to:
                shareTextUrl();
                break;

            case R.id.share_timeline:
                openShareVenueOnProfileDialog();
                break;

            case R.id.slots:
                openSlotsListDialog();
                break;

            case R.id.update_photo:
                Type type = new TypeToken<List<PojoVenuePhoto>>() {
                }.getType();
                String dataStr = new Gson().toJson(venuePhotos, type);
                Intent imgViewerIntent = new Intent(VenueActivity.this, VenueImageEditActivity.class);
                imgViewerIntent.putExtra("data", dataStr);
                imgViewerIntent.putExtra(AppKeys.VENUE_ID, venue_id);
                imgViewerIntent.putExtra("position", 0);
                startActivity(imgViewerIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void openSlotsListDialog() {
        Dialog slotsListDialog = new MyCustomThemeDialog(VenueActivity.this, R.style.NoTitleDialogTheme);
        ;
        slotsListDialog.setContentView(R.layout.simple_recyclerview_dialog);
        TextView tv_dialog_header = (TextView) slotsListDialog.findViewById(R.id.tv_dialog_header);
        tv_dialog_header.setText(getString(R.string.permitted_dates));
        tv_dialog_header.setVisibility(View.VISIBLE);

        RecyclerView rv_list = (RecyclerView) slotsListDialog.findViewById(R.id.rv_list);
        rv_list.setLayoutManager
                (new LinearLayoutManager(VenueActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_list.setNestedScrollingEnabled(false);


        int arraySize = 0;
        for (int i = 0; i < pojoGetVenueDetailsResponse.data.days.size(); i++) {
            if (!pojoGetVenueDetailsResponse.data.days.get(i).slots.isEmpty()) {
                arraySize++;
            }
        }
        final int[] daysOfWeek = new int[arraySize];
        int counter = 0;
        for (int i = 0; i < pojoGetVenueDetailsResponse.data.days.size(); i++) {
            if (!pojoGetVenueDetailsResponse.data.days.get(i).slots.isEmpty()) {
                daysOfWeek[counter++] = i;
            }
        }

        rv_list.setAdapter(new VenueSlotListAdapter(VenueActivity.this, Integer.parseInt(venue_id),
                daysOfWeek, pojoGetVenueDetailsResponse.data.days));

        slotsListDialog.show();
    }

    private void hitInviteFriendApi(String user_id, String type, String friend_id, String interestId) {
        Call<PojoInviteFriendResponse> call = apiService.inviteFriends(user_id, type, friend_id, interestId);
        call.enqueue(new Callback<PojoInviteFriendResponse>() {
            @Override
            public void onResponse(Call<PojoInviteFriendResponse> call, Response<PojoInviteFriendResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoInviteFriendResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_venue_successfully), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    private void shareTextUrl() {
        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoVenueData.cover_photo)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                                                             DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(VenueActivity.this,
                                          AppKeys.SHARE_VENUE, pojoVenueData.venueId,
                                          pojoVenueData.venueName, bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        radioButtonHandler.removeCallbacks(radioButtonRunnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    @Override
    public void onFriendListMethodCallback(int position, PojoUserData item, String type) {

    }

    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
        if (upvoterList.get(position).connection.equals("add")
                || upvoterList.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "follow", position);
            upvoterList.get(position).connection = "followed";

        } else if (upvoterList.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "unfollow", position);
            upvoterList.get(position).connection = "add";
        }
        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
    }

    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
