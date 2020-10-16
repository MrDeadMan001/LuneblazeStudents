package com.avadna.luneblaze.activities.sessions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;

import com.avadna.luneblaze.services.LuneBlazeUploadService;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.activities.sessionCreation.SessionCreationPart1;
import com.avadna.luneblaze.adapters.session.AssignedSessionPagerAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.sessions.SessionDiscussionFragment;
import com.avadna.luneblaze.fragments.sessions.SessionPollFragment;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AssignedSessionActivity extends AppBaseActivity {

    private static final int PICK_MUlTIPLE_IMAGES_CODE = 122;
    private static final int PICK_FILE_PERMISSION = 123;
    private static final int PICK_VIDEO_PERMISSION = 124;

    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String session_id = "134";
    private String tabType;
    private String action;
    private int notifId;
    private String discussion_id;

    //   SwipeRefreshLayout srl_refresh;
    private AppBarLayout appBarLayout;
    private TabLayout tl_tabs;
    private ViewPager vp_tab_item_pager;
    private AssignedSessionPagerAdapter assignedSessionPagerAdapter;

    private List<String> more_options_list;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner sp_more_options;
    private ImageView iv_more_options;
    private String SHARER = "sharer";
    private String OTHER = "other";
    private String userType = SHARER;


   // private boolean followingTaggedInterests = false;
    private boolean questionNotify = false;
    private boolean followingVenue = false;
    private PojoSessionDetailsResponseData pojoSessionDetailsResponseData = null;
    private String dataStr;
    private long calenderEventID;
    private HashMap<String, Long> calenderEventMap = new HashMap<>();

    private TextView tv_session_name, tv_sharer_name;
    private TextView tv_num_attending, tv_num_shares;
    private TextView tv_attend, tv_invite, tv_share;
    private ImageView iv_cover_photo;
    private ImageView iv_session_live;

    private View.OnClickListener onClickListener;

    private RelativeLayout rl_new_comment_wrapper;
    private EditText et_new_comment;
    private ImageButton ib_send;

    private final String STATUS_ATTEND = "attend";
    private final String STATUS_ATTENDING = "attending";
    private final String STATUS_WAITING = "waiting";
    private final String STATUS_SHARING = "sharing";


    private String currentAttendStatus = STATUS_ATTEND;

    private ProgressBar pb_edit_topic_api_status;
    private RelativeLayout rl_edit_topic_parent_wrapper;
    private Dialog editTopicDialog;

    private DisplayMetrics displayMetrics;
    private BroadcastReceiver mBroadcastReceiver;
    private Dialog roomDialog;
    private Dialog sessionCreationDialog;

    private int imageFixedWidth;
    private int imageFixedHeight;
    private int maxSessionImageHeight;
    private int minSessionImageHeight;
    private final int PICK_IMAGE_PERMISSION = 169;

    // Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_session);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.5f);
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * 0.6f);
        minSessionImageHeight = (int) (displayMetrics.widthPixels * 0.4f);

        session_id = getIntent().getStringExtra("id");
        if (session_id == null) {
            session_id = "188";
        }

        tabType = getIntent().getStringExtra("type");
        if (tabType != null && tabType.equals("discussion")) {
            discussion_id = getIntent().getStringExtra("discussion_id");
        }
        notifId = getIntent().getIntExtra("notifId", 999999);
        setUpActionBar();
        setUpTabLayout();
        initViews();
        initClickListener();
        setClickListener();
        if (tabType != null) {
            if (tabType.equals("details")) {
                vp_tab_item_pager.setCurrentItem(0, true);
            } else if (tabType.equals("poll") || tabType.equals(AppKeys.ATTEND) || tabType.equals(AppKeys.UNATTEND)) {
                vp_tab_item_pager.setCurrentItem(1, true);
            } else if (tabType.equals("comments") || tabType.equals("discussion")) {
                vp_tab_item_pager.setCurrentItem(2, true);
            } else if (tabType.equals("question")) {
                vp_tab_item_pager.setCurrentItem(3, true);
            }
        }

        dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            Type type = new TypeToken<PojoSessionDetailsResponseData>() {
            }.getType();
            pojoSessionDetailsResponseData = new Gson().fromJson(dataStr, type);
            setDataOnViews();
        } else {
            hitGetSessionDetailsApi(user_id, session_id, "home");
        }

        action = getIntent().getStringExtra("action");
        if (action != null) {
            if (action.equals(AppKeys.ADD_COMMENT)) {
                setCommentText("");
            } else if (action.equals(AppKeys.FINALISE_NO)) {
                hitAttendApi(user_id, session_id, "unattend");
                hitGetSessionDetailsApi(user_id, session_id, "home");
            } else if (action.equals(AppKeys.SESSION_RATING)) {
                openRatingDialog();
            }
        }
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.SESSION_RELOAD));
        // initMoreOptionsSpinner();
    }


    private void openRatingDialog() {
        Dialog ratingDialog = new MyCustomThemeDialog(AssignedSessionActivity.this,
                R.style.NoTitleDialogTheme);
        ratingDialog.setContentView(R.layout.venue_four_rating_layout);

        RatingBar rb_seating, rb_accessibility, rb_staff, rb_ambiance;
        rb_seating = (RatingBar) ratingDialog.findViewById(R.id.rb_seating);
        rb_accessibility = (RatingBar) ratingDialog.findViewById(R.id.rb_accessibility);
        rb_staff = (RatingBar) ratingDialog.findViewById(R.id.rb_staff);
        rb_ambiance = (RatingBar) ratingDialog.findViewById(R.id.rb_ambiance);
        TextView tv_title = (TextView) ratingDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.rate_session));
        ratingDialog.setCancelable(true);

        TextView tv_submit = (TextView) ratingDialog.findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commonFunctions.isGuestUser()) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    String venueId = "0";
                    if (!pojoSessionDetailsResponseData.venues.isEmpty()) {
                        venueId = pojoSessionDetailsResponseData.venues.get(0).venueId;
                    }
                    hitSubmitVenueRatingApi(user_id, session_id, venueId,
                            String.valueOf(rb_seating.getRating()), String.valueOf(rb_accessibility.getRating()),
                            String.valueOf(rb_staff.getRating()), String.valueOf(rb_ambiance.getRating()));
                    ratingDialog.dismiss();
                }
            }
        });
        ratingDialog.show();
    }

    private void hitSubmitVenueRatingApi(String user_id, String session_id, String venue_id,
                                         String seating, String access, String staff, String ambiance) {
        Call<PojoNoDataResponse> call = apiService.submitVenueRating(user_id, session_id,
                venue_id, seating, access, staff, "5", ambiance);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.thankyou_for_response), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.session));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initBroadCastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.SESSION_RELOAD)) {
                    hitGetSessionDetailsApi(user_id, session_id, "home");
                }
            }
        };
    }


    public void setAppBarExpanded(boolean value) {
        appBarLayout.setExpanded(value, true);
    }


    public void updateAttendStatus(String status) {

        if (tv_attend != null) {
            currentAttendStatus = status;
            if (status.equals(STATUS_ATTEND)) {
                tv_attend.setTextColor(ContextCompat.getColor(AssignedSessionActivity.this, R.color.med_grey));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);
                tv_attend.setText(R.string.attend);
            } else if (status.equals(STATUS_ATTENDING)) {
                tv_attend.setTextColor(ContextCompat.getColor(AssignedSessionActivity.this, R.color.app_theme_medium));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                tv_attend.setText(R.string.attending);

            } else if (status.equals(STATUS_WAITING)) {
                tv_attend.setTextColor(ContextCompat.getColor(AssignedSessionActivity.this, R.color.gmail_button));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tv_attend.setText(R.string.waiting);
            } else if (status.equals(STATUS_SHARING)) {
                tv_attend.setText(getString(R.string.sharing));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_session_sharing, 0, 0, 0);
                tv_attend.setEnabled(false);
                tv_attend.setTextColor(ContextCompat.getColor(AssignedSessionActivity.this, R.color.app_theme_medium));
            }
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AssignedSessionActivity.this, R.color.black_translucent_20)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AssignedSessionActivity.this, R.color.status_bar_color));
            }
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float maxRange = appBarLayout.getTotalScrollRange();
                actionBar.setBackgroundDrawable(new ColorDrawable
                        (Color.argb(((int) (25 + (Math.abs(verticalOffset) / maxRange) * 230)), 11, 108, 115)));
                int adjustPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                        getResources().getDisplayMetrics());

                if (verticalOffset < adjustPx - maxRange) {
                    if (pojoSessionDetailsResponseData != null) {
                        actionBar.setTitle(Html.fromHtml(pojoSessionDetailsResponseData.title));
                    }
                } else {
                    actionBar.setTitle("");
                }
            }
        });
    }

    private void initViews() {
        //srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        tv_attend = (TextView) findViewById(R.id.tv_attend);
        tv_session_name = (TextView) findViewById(R.id.tv_session_name);
        tv_sharer_name = (TextView) findViewById(R.id.tv_sharer_name);
        tv_num_attending = (TextView) findViewById(R.id.tv_num_attending);
        tv_num_shares = (TextView) findViewById(R.id.tv_num_shares);
        tv_invite = (TextView) findViewById(R.id.tv_invite);
        tv_share = (TextView) findViewById(R.id.tv_share);
        iv_cover_photo = (ImageView) findViewById(R.id.iv_cover_photo);
        iv_session_live = (ImageView) findViewById(R.id.iv_session_live);
        iv_session_live.setVisibility(View.GONE);
        rl_new_comment_wrapper = (RelativeLayout) findViewById(R.id.rl_new_comment_wrapper);
        rl_new_comment_wrapper.setVisibility(View.GONE);
        et_new_comment = (EditText) findViewById(R.id.et_new_comment);
        ib_send = (ImageButton) findViewById(R.id.ib_send);

     /*   srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitGetSessionDetailsApi(user_id, session_id, "home");
                setUpTabLayout();
            }
        });*/

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonFunctions.isGuestUser()) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    switch (v.getId()) {
                        case R.id.tv_attend:
                      /*  Intent venuePollIntent = new Intent(AssignedSessionActivity.this, VenuePollActivity.class);
                        startActivity(venuePollIntent);*/
                            try {
                                ((SessionPollFragment) (assignedSessionPagerAdapter.getRegisteredFragment(1))).setAskConfirmDialog(true);
                            } catch (Exception e) {

                            }
                            if (currentAttendStatus.equals(STATUS_ATTENDING) || currentAttendStatus.equals(STATUS_WAITING)) {
                                TextView tv_dialog_description, tv_no, tv_yes;
                                final Dialog confirmationDialog;
                                confirmationDialog = new MyCustomThemeDialog(AssignedSessionActivity.this);
                                confirmationDialog.setContentView(R.layout.yes_no_dialog);
                                tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                                tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
                                tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

                                tv_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmationDialog.dismiss();
                                    }
                                });

                                tv_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        hitAttendApi(user_id, session_id, "unattend");
                                        updateAttendStatus(STATUS_ATTEND);
                                        //   tv_attend.setTextColor(ContextCompat.getColor(AssignedSessionActivity.this, R.color.med_grey));
                                        try {
                                            if (assignedSessionPagerAdapter != null) {
                                                ((SessionPollFragment) assignedSessionPagerAdapter
                                                        .getRegisteredFragment(1)).removeCheckedFromAllOptions();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        confirmationDialog.dismiss();

                                    }
                                });

                                tv_dialog_description.setText(R.string.uncast_vote_on_unattend);
                                confirmationDialog.show();
                            } else {
                                if (true) {
                                    //    if (pojoSessionDetailsResponseData.finalizedData.equals("0")) {
                                    //  hitAttendApi(user_id, currentItem.sessionsId, "attend");
                                    TextView tv_dialog_description;
                                    Button bt_ok;
                                    final Dialog confirmationDialog;
                                    confirmationDialog = new MyCustomThemeDialog(AssignedSessionActivity.this);
                                    confirmationDialog.setContentView(R.layout.message_dialog);
                                    tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                                    bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                                    bt_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            vp_tab_item_pager.setCurrentItem(1);
                                            try {
                                                ((SessionPollFragment) (assignedSessionPagerAdapter.getRegisteredFragment(1))).setAskConfirmDialog(false);
                                            } catch (Exception e) {

                                            }
                                            confirmationDialog.dismiss();
                                        }
                                    });
                                    tv_dialog_description.setText(R.string.cast_vote_to_attend);
                                    confirmationDialog.show();
                                } else {

                                }
                            }

                            break;

                        case R.id.ib_send:
                            SessionDiscussionFragment frag = (SessionDiscussionFragment)
                                    assignedSessionPagerAdapter.getRegisteredFragment(2);
                            frag.addDiscussion(et_new_comment.getText().toString().trim());
                            et_new_comment.getText().clear();
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_new_comment.getWindowToken(), 0);
                            break;


                        case R.id.tv_share:
                            openShareSessionOnProfileDialog();
                            break;

                        case R.id.ll_initiate_wrapper:
                            Intent sessionInitiateIntent = new Intent(AssignedSessionActivity.this,
                                    SessionCreationPart1.class);
                            sessionInitiateIntent.putExtra("type", AppKeys.SESSION_INITIATE);
                            startActivity(sessionInitiateIntent);
                            if (sessionCreationDialog != null) {
                                sessionCreationDialog.dismiss();
                            }
                            break;

                        case R.id.ll_conduct_wrapper:
                            Intent sessionConductIntent = new Intent(AssignedSessionActivity.this,
                                    SessionCreationPart1.class);
                            sessionConductIntent.putExtra("type", AppKeys.SESSION_CONDUCT);
                            startActivity(sessionConductIntent);
                            if (sessionCreationDialog != null) {
                                sessionCreationDialog.dismiss();
                            }
                            break;

                        case R.id.tv_invite:
                            Intent inviteIntent = new Intent(AssignedSessionActivity.this,
                                    InviteActivity.class);
                            inviteIntent.putExtra("userType", "invite_session_user_list");
                            inviteIntent.putExtra("id", session_id);
                            startActivity(inviteIntent);
                            break;

                        case R.id.iv_cover_photo:
                            if (pojoSessionDetailsResponseData != null) {
                                openImageViewerDialog(pojoSessionDetailsResponseData.coverPhoto);
                            }
                            break;
                    }
                }
            }
        };
    }

    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(AssignedSessionActivity.this, android.R.style.Theme_Black_NoTitleBar);
        imageViewerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

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

        Glide.with(getApplicationContext())
                .load(path)
                .into(iv_photo);

        imageViewerDialog.show();


    }

    private void setClickListener() {
        tv_attend.setOnClickListener(onClickListener);
        ib_send.setOnClickListener(onClickListener);
        tv_share.setOnClickListener(onClickListener);
        iv_cover_photo.setOnClickListener(onClickListener);
        tv_invite.setOnClickListener(onClickListener);
    }

    public void setCommentText(String comment) {
        if (!commonFunctions.isGuestUser()) {
            rl_new_comment_wrapper.setVisibility(View.VISIBLE);
        }
        et_new_comment.setText(comment);
        if (et_new_comment != null && et_new_comment.requestFocus()) {
            et_new_comment.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_new_comment, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 400);
        }
    }


    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.removeAllTabs();
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.details)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.polls)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.discussions)));
        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        vp_tab_item_pager = (ViewPager) findViewById(R.id.vp_tab_item_pager);

        //userType 2 in pager adapter is to tell its Organised session
        assignedSessionPagerAdapter = new AssignedSessionPagerAdapter(this, getSupportFragmentManager(),
                session_id, discussion_id);

        vp_tab_item_pager.setAdapter(assignedSessionPagerAdapter);
        vp_tab_item_pager.setOffscreenPageLimit(3);
        tl_tabs.setupWithViewPager(vp_tab_item_pager);


        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                appBarLayout.setExpanded(false, true);
                if (tab.getPosition() == 2 && !commonFunctions.isGuestUser()) {
                    rl_new_comment_wrapper.setVisibility(View.VISIBLE);
                } else {
                    rl_new_comment_wrapper.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
/*
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
        });*/
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                commonFunctions.closeProgressDialog();
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponseData = response.body().data;
                    setDataOnViews();
                    //srl_refresh.setRefreshing(false);
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    openNoConnectionDialog(getString(R.string.session));
                }
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSessionDetailsApi(user_id, session_id, "home");
                    connectionErrorDialog.dismiss();
                    setUpTabLayout();

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


    public void hitAttendApi(final String user_id, String id, final String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);
        commonFunctions.openProgressDialog(getString(R.string.please_wait));
        tabType = null;
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                commonFunctions.closeProgressDialog();
                String message = "";
                if (response != null) {
                    sendAttendBroadcast("0");
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    int num = Integer.parseInt(pojoSessionDetailsResponseData.totalAttend);
                    if (connect_type.equals("attend")) {
                        num++;
                    } else {
                        num--;
                    }
                    pojoSessionDetailsResponseData.totalAttend = String.valueOf(num);
                    tv_num_attending.setText("" + pojoSessionDetailsResponseData.totalAttend + " " + getString(R.string.attending));

                    if (connect_type.equals("attend")) {
                        requestCalenderPermission("add");
                        // addSessionToCalender();
                    }
                    if (connect_type.equals("unattend")) {
                        requestCalenderPermission("remove");
                    }
                }
                hitGetSessionDetailsApi(user_id, session_id, "home");
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed
                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }
        });
    }

    private void sendAttendBroadcast(String status) {
        Intent registrationComplete = new Intent(Config.SESSION_ATTENDED_STATUS);
        registrationComplete.putExtra(AppKeys.ATTEND_STATUS, status);
        registrationComplete.putExtra(AppKeys.SESSION_ID, session_id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void setDataOnViews() {
        if (pojoSessionDetailsResponseData != null) {
            if (pojoSessionDetailsResponseData.is_session_live != null && pojoSessionDetailsResponseData.is_session_live.equals("1")) {
                iv_session_live.setVisibility(View.VISIBLE);
            } else {
                iv_session_live.setVisibility(View.GONE);
            }
            if (action != null && action.equals(AppKeys.FINALISE_NO)) {
                pojoSessionDetailsResponseData.iAttend = false;
            }
            invalidateOptionsMenu();

            if (pojoSessionDetailsResponseData.sharer != null
                    && !pojoSessionDetailsResponseData.sharer.isEmpty()
                    && pojoSessionDetailsResponseData.sharer.get(0).userId.equals(user_id)) {
                updateAttendStatus(STATUS_SHARING);

            } else {
                if (pojoSessionDetailsResponseData.iAttend) {
                    updateAttendStatus(STATUS_ATTENDING);
                } else {
                    updateAttendStatus(STATUS_ATTEND);

                }
                if (pojoSessionDetailsResponseData.iWait != null && pojoSessionDetailsResponseData.iWait) {
                    updateAttendStatus(STATUS_WAITING);
                }
            }

            if (tabType != null && (tabType.equals(AppKeys.ATTEND) || tabType.equals(AppKeys.UNATTEND))) {
                tv_attend.callOnClick();
            }

            tv_session_name.setText(Html.fromHtml(pojoSessionDetailsResponseData.title));
            tv_sharer_name.setText(pojoSessionDetailsResponseData.createdBy.userFullname);
            tv_num_shares.setText("" + pojoSessionDetailsResponseData.totalShares + " " + getString(R.string.shares));
            tv_num_attending.setText("" + pojoSessionDetailsResponseData.totalAttend + " " + getString(R.string.attending));

            if (!pojoSessionDetailsResponseData.coverPhoto.isEmpty()) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;
                if (pojoSessionDetailsResponseData.imgDimensions != null
                        && !pojoSessionDetailsResponseData.imgDimensions.isEmpty()) {
                    ImageDimensions dimen = getScaledDimensions(pojoSessionDetailsResponseData.imgDimensions);
                    height = dimen.height;
                    width = dimen.width;
                }

                if (height > maxSessionImageHeight) {
                    height = maxSessionImageHeight;
                }

                if (height < minSessionImageHeight) {
                    height = minSessionImageHeight;
                }

                iv_cover_photo.getLayoutParams().height = height;
                iv_cover_photo.getLayoutParams().width = width;

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.override(width, height);
                requestOptions.placeholder(R.drawable.placeholder);

                Glide.with(getApplicationContext())
                        .load(pojoSessionDetailsResponseData.coverPhoto)
                        .apply(requestOptions)
                        .into(iv_cover_photo);
            }
        }
    }

    public ImageDimensions getScaledDimensions(String imgDimen) {
        ImageDimensions dimen = new ImageDimensions();
        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);

        if (targetHeight > maxSessionImageHeight) {
            targetHeight = maxSessionImageHeight;
        }
        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }


    private void requestCalenderPermission(String type) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            if (type.equals("add")) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR},
                        AppKeys.ADD_TO_CALENDER);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else if (type.equals("remove")) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR},
                        AppKeys.REMOVE_FROM_CALENDER);
            }
        } else {
            addSessionToCalender();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            switch (requestCode) {
                case AppKeys.ADD_TO_CALENDER:
                    addSessionToCalender();
                    return;

                case AppKeys.REMOVE_FROM_CALENDER:
                    removeSessionFromCalender();
                    return;

                case PICK_IMAGE_PERMISSION:
                    addSessionPhotos();
                    break;

                case PICK_FILE_PERMISSION:
                    addSessionFiles();
                    break;

                case PICK_VIDEO_PERMISSION:
                    addSessionVideos();
                    break;

            }
        } else {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.grant_permission_request),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        }

    }

    private void addSessionToCalender() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(pojoSessionDetailsResponseData.eventDate.get(0).value);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        int year = convertedDate.getYear();
        int month = convertedDate.getMonth();
        int day = convertedDate.getDate();

        long calID = 1;
        long startMillis = 0;
        long endMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(1900 + year, month + 1, day);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(1900 + year, month + 1, day);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, pojoSessionDetailsResponseData.title);
        // values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        if (!pojoSessionDetailsResponseData.venues.isEmpty())
            values.put(CalendarContract.Events.EVENT_LOCATION, pojoSessionDetailsResponseData.venues.get(0).venueName);

        String timezone = beginTime.getTimeZone().getID();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            if (uri != null) {
                calenderEventID = Long.parseLong(uri.getLastPathSegment());
                calenderEventMap = preferenceUtils.getAttendingSessionCalenderIds();
                calenderEventMap.put(pojoSessionDetailsResponseData.sessionsId, calenderEventID);
                preferenceUtils.saveAttendingSessionCalenderIds(calenderEventMap);
            }
        }
    }

    public void removeSessionFromCalender() {
        calenderEventMap = preferenceUtils.getAttendingSessionCalenderIds();
        if (calenderEventMap.containsKey(pojoSessionDetailsResponseData.sessionsId)) {
            long eventID = calenderEventMap.get(pojoSessionDetailsResponseData.sessionsId);
            Uri deleteUri = null;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(eventID)));
            int rows = getContentResolver().delete(deleteUri, null, null);
            // commonFunctions.setToastMessage(this, "Event deleted", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            calenderEventMap.remove(pojoSessionDetailsResponseData.sessionsId);
            preferenceUtils.saveAttendingSessionCalenderIds(calenderEventMap);
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this.menu = menu;
      /*  if (finalised) {
            if (userType.equals(SHARER)) {
                getMenuInflater().inflate(R.menu.final_assigned_sharer_menu, menu);
            } else if (userType.equals(OTHER)) {
                getMenuInflater().inflate(R.menu.final_assigned_other_menu, menu);
            }
        } else {
            if (userType.equals(SHARER)) {
                getMenuInflater().inflate(R.menu.non_final_assigned_sharer_menu, menu);

            } else if (userType.equals(OTHER)) {
                getMenuInflater().inflate(R.menu.non_final_assigned_other_menu, menu);
            }
        }*/

        if (pojoSessionDetailsResponseData != null) {
            boolean iAmCoordinator = false;
            if (pojoSessionDetailsResponseData.coOrdinatorsData != null) {
                for (int i = 0; i < pojoSessionDetailsResponseData.coOrdinatorsData.size(); i++) {
                    if (user_id.equals(pojoSessionDetailsResponseData.coOrdinatorsData.get(i).userId)) {
                        iAmCoordinator = true;
                    }
                }
            }

            //if session is finalised
            if (pojoSessionDetailsResponseData.finalizedData.equals("1")) {
                //if i am presenter/sharer
                if (pojoSessionDetailsResponseData.presentors != null &&
                        !pojoSessionDetailsResponseData.presentors.isEmpty() &&
                        user_id.equals(pojoSessionDetailsResponseData.presentors.get(0).userId)) {
                    getMenuInflater().inflate(R.menu.final_assigned_sharer_menu, menu);
                    //if i am not the coordinator
                    if (!iAmCoordinator) {
                        menu.removeItem(R.id.assign_room);
                        menu.removeItem(R.id.attendance);
                        menu.removeItem(R.id.add_photos);
                    }
                }

                //if i am not the presenter/sharer
                else {
                    //todo comment below menu after testing
                    getMenuInflater().inflate(R.menu.finalised_coordinator_not_sharer_menu, menu);

                    //todo enable below code after testing
                   /* if (iAmCoordinator) {
                        getMenuInflater().inflate(R.menu.finalised_coordinator_not_sharer_menu, menu);
                        if (pojoSessionDetailsResponseData.is_session_live.equals("0")) {
                            menu.removeItem(R.id.end_session);
                            menu.removeItem(R.id.add_photos);
                            menu.removeItem(R.id.add_files);
                            menu.removeItem(R.id.add_video);
                        }
                        if (pojoSessionDetailsResponseData.is_session_live.equals("1")) {
                            menu.removeItem(R.id.start_session);
                        }
                    } else {
                        getMenuInflater().inflate(R.menu.final_assigned_other_menu, menu);
                    }
                    if (!pojoSessionDetailsResponseData.presentors.isEmpty()
                            && pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id)) {
                        menu.removeItem(R.id.follow_sharer);
                    } else if (!pojoSessionDetailsResponseData.presentors.isEmpty()) {
                        String name = pojoSessionDetailsResponseData.presentors.get(0).userFullname;
                        if (name.contains(" ")) {
                            name = name.substring(0, name.indexOf(" "));
                        }
                        menu.getItem(2).setTitle(getString(R.string.follow) + " " + name);
                    }

                    menu.removeItem(R.id.end_session);
                    menu.removeItem(R.id.add_photos);
                    menu.removeItem(R.id.add_files);
                    menu.removeItem(R.id.add_video);
                    menu.removeItem(R.id.start_session);*/

                }

            } else if (!pojoSessionDetailsResponseData.presentors.isEmpty()) {
                if (user_id.equals(pojoSessionDetailsResponseData.presentors.get(0).userId)) {
                    getMenuInflater().inflate(R.menu.non_final_assigned_sharer_menu, menu);
                } else {
                    getMenuInflater().inflate(R.menu.non_final_assigned_other_menu, menu);
                    if (pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id)) {
                        menu.removeItem(R.id.follow_sharer);
                    } else {
                        String name = pojoSessionDetailsResponseData.presentors.get(0).userFullname;
                        if (name.contains(" ")) {
                            name = name.substring(0, name.indexOf(" "));
                        }
                        menu.getItem(1).setTitle(getString(R.string.follow) + " " + name);
                    }
                }
            }
        }
        if (commonFunctions.isGuestUser()) {
            menu.clear();
        }
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.join:
                hitSessionReactionApi(user_id, session_id, "session_join");
                break;

            case R.id.invite:
                Intent inviteIntent = new Intent(AssignedSessionActivity.this, InviteActivity.class);
                inviteIntent.putExtra("userType", "invite_session_user_list");
                inviteIntent.putExtra("id", session_id);
                startActivity(inviteIntent);
                break;

            case R.id.share_to:
                shareTextUrl();
                break;

            case R.id.create_session:
                openSessionCreateDialog();
                break;

            case R.id.start_session:
                hitStartSessionApi(user_id, session_id, "1");
                break;

            case R.id.end_session:
                hitStartSessionApi(user_id, session_id, "0");
                break;

            case R.id.add_photos:
                requestStoragePermission(PICK_IMAGE_PERMISSION);
                break;

            case R.id.add_files:
                requestStoragePermission(PICK_FILE_PERMISSION);
                break;

            case R.id.add_video:
                requestStoragePermission(PICK_VIDEO_PERMISSION);
                break;

            case R.id.attendance:
                Intent attendanceIntent = new Intent(AssignedSessionActivity.this, MarkSessionAttendeesActivity.class);
                attendanceIntent.putExtra(AppKeys.SESSION_ID, session_id);
                startActivity(attendanceIntent);
                break;

      /*      case R.id.follow_venue:
                if (followingVenue) {
                    hitFollowVenueApi(user_id, "1", "remove_notify_me_venue");
                    followingVenue = false;
                    item.setTitle(R.string.follow_venue);
                } else {
                    hitFollowVenueApi(user_id, "1", "notify_me_venue");
                    followingVenue = true;
                    item.setTitle(R.string.unFollow_venue);
                }
                break;*/

            case R.id.follow_sharer:
                if (pojoSessionDetailsResponseData != null && !pojoSessionDetailsResponseData.presentors.isEmpty())
                    hitFollowSharerApi(user_id, pojoSessionDetailsResponseData.presentors.get(0).userId, "follow");
                break;

            case R.id.postpone:
            case R.id.finalise:
                vp_tab_item_pager.setCurrentItem(1);
                SessionPollFragment frag = (SessionPollFragment) assignedSessionPagerAdapter.getRegisteredFragment(1);
                appBarLayout.setExpanded(false, true);
                frag.initFinaliseProcess();
                break;

            case R.id.follow_interest:
                hitSessionReactionApi(user_id, session_id, "follow_session_interest");

                /*if (followingTaggedInterests) {
                    hitSessionReactionApi(user_id, session_id, "interest_unfollow");
                    followingTaggedInterests = false;
                    item.setTitle(R.string.follow_tagged_interests);
                } else {
                    hitSessionReactionApi(user_id, session_id, "follow_session_interest");
                    followingTaggedInterests = true;
                   // item.setTitle(R.string.unfollow_tagged_interests);
                }*/
                break;


            case R.id.notify_question:
                if (questionNotify) {
                    hitSessionReactionApi(user_id, session_id, "remove_question_notify");
                    questionNotify = false;
                    item.setTitle(R.string.question_notify);
                } else {
                    hitSessionReactionApi(user_id, session_id, "question_notify");
                    questionNotify = true;
                    item.setTitle(R.string.remove_question_notify);
                }
                break;


            case R.id.leave:
                hitSessionReactionApi(user_id, session_id, "leave");
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_post", session_id);
                break;

            case R.id.assign_room:
                openUpdateRoomDialog();
                break;

            case R.id.edit:
                Intent editIntent = new Intent(AssignedSessionActivity.this, EditTopicsActivity.class);
                Type type = new TypeToken<PojoSessionDetailsResponseData>() {
                }.getType();
                String data = new Gson().toJson(pojoSessionDetailsResponseData, type);
                editIntent.putExtra("data", data);
                startActivity(editIntent);

                //   openEditTopicsDialog();
                break;

            case R.id.delete:
                if (pojoSessionDetailsResponseData.createdBy.userId.equals(user_id)) {
                    hitSessionReactionApi(user_id, session_id, "delete_session");
                }
                break;


          /*  case R.id.assign_room:
                openEditTopicsDialog();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestStoragePermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            switch (requestCode) {
                case PICK_IMAGE_PERMISSION:
                    addSessionPhotos();
                    break;

                case PICK_FILE_PERMISSION:
                    addSessionFiles();
                    break;

                case PICK_VIDEO_PERMISSION:
                    addSessionVideos();
                    break;
            }

        }
    }


    private void addSessionPhotos() {
        Intent uploadIntent = new Intent(AssignedSessionActivity.this, UploadSessionPhotosActivity.class);
        uploadIntent.putExtra(AppKeys.SESSION_ID, session_id);
        startActivity(uploadIntent);

       /* Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MUTILPLE_IMAGES_CODE);*/
    }

    private void addSessionFiles() {
        Intent uploadIntent = new Intent(AssignedSessionActivity.this, UploadSessionFilesActivity.class);
        uploadIntent.putExtra(AppKeys.SESSION_ID, session_id);
        startActivity(uploadIntent);

       /* Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MUTILPLE_IMAGES_CODE);*/
    }

    private void addSessionVideos() {
        Intent uploadIntent = new Intent(AssignedSessionActivity.this, UploadSessionVideosActivity.class);
        uploadIntent.putExtra(AppKeys.SESSION_ID, session_id);
        startActivity(uploadIntent);

       /* Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MUTILPLE_IMAGES_CODE);*/
    }

    private void hitStartSessionApi(String user_id, String session_id, String live) {
        Call<PojoNoDataResponse> call = apiService.startSession(user_id, session_id, live);
        if (live.equals("1")) {
            commonFunctions.openProgressDialog(getString(R.string.starting_session));
        } else {
            commonFunctions.openProgressDialog(getString(R.string.ending_session));
        }
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (live.equals("1")) {
                        commonFunctions.setToastMessage(AssignedSessionActivity.this,
                                getApplicationContext().getString(R.string.session_started_successfully),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        hitGetSessionDetailsApi(user_id, session_id, "home");
                    } else {
                        Intent sessionEnd = new Intent(Config.SESSION_ENDED);
                        sessionEnd.putExtra(AppKeys.SESSION_ID, session_id);
                        LocalBroadcastManager.getInstance(AssignedSessionActivity.this).sendBroadcast(sessionEnd);

                        Intent organisedIntent = new Intent(AssignedSessionActivity.this, SessionLoaderActivity.class);
                        organisedIntent.putExtra(AppKeys.ID, session_id);
                        startActivity(organisedIntent);

                        finish();
                    }
                } else {
                    commonFunctions.setToastMessage(AssignedSessionActivity.this,
                            getApplicationContext().getString(R.string.something_went_wrong_try_again),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
                commonFunctions.closeProgressDialog();

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(AssignedSessionActivity.this,
                        getApplicationContext().getString(R.string.something_went_wrong_try_again),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void openSessionCreateDialog() {
        LinearLayout ll_initiate_wrapper, ll_conduct_wrapper;
        sessionCreationDialog = new MyCustomThemeDialog(AssignedSessionActivity.this);
        sessionCreationDialog.setContentView(R.layout.session_creation_dialog);
        ll_initiate_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_initiate_wrapper);
        ll_conduct_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_conduct_wrapper);
        ll_initiate_wrapper.setOnClickListener(onClickListener);
        ll_conduct_wrapper.setOnClickListener(onClickListener);
        sessionCreationDialog.setCancelable(true);
        sessionCreationDialog.show();
    }


    private void openUpdateRoomDialog() {
        roomDialog = new MyCustomThemeDialog(AssignedSessionActivity.this);
        roomDialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) roomDialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) roomDialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) roomDialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) roomDialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(R.string.update_room);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitAssignRoomApi(session_id, et_content.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    et_content.requestFocus();
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    roomDialog.dismiss();
                }
            }
        });
        roomDialog.show();
    }

    private void hitAssignRoomApi(final String session_id, String place) {
        Call<PojoNoDataResponse> call = apiService.assignSessionRoom(session_id, place);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    Intent updateSessionDetailsIntent = new Intent(Config.SESSION_DETAILS_UPDATED);
                    LocalBroadcastManager.getInstance(AssignedSessionActivity.this)
                            .sendBroadcast(updateSessionDetailsIntent);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed

                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
                // ll_parent.setVisibility(View.VISIBLE);
            }
        });
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
                    if (AssignedSessionActivity.this != null) {
                        commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            });


        }
    }

    private void hitFollowSharerApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }
        });
    }


    private void shareTextUrl() {
        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoSessionDetailsResponseData.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {

                                  ShareContentOnMediaApps.shareContent(AssignedSessionActivity.this,
                                          AppKeys.SHARE_SESSION, session_id, pojoSessionDetailsResponseData.title,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }

    private void openShareSessionOnProfileDialog() {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(this);
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);
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
        View view = inflater.inflate(R.layout.share_verified_session_item, rl_item_holder, true);
        ImageView iv_session_photo = (ImageView) view.findViewById(R.id.iv_session_photo);
        TextView tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
        TextView tv_session_time_stamp = (TextView) view.findViewById(R.id.tv_session_time_stamp);

        int targetWidth = (int) (displayMetrics.widthPixels * 0.85f);
        int targetHeight;

        if (pojoSessionDetailsResponseData.imgDimensions != null && !pojoSessionDetailsResponseData.imgDimensions.isEmpty()) {
            String imgDimen = pojoSessionDetailsResponseData.imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            targetHeight = (int) ((1f * height * targetWidth) / width);

        } else {
            targetHeight = (int) (displayMetrics.widthPixels * 0.5f);
        }

        iv_cover_photo.getLayoutParams().height = targetHeight;
        iv_cover_photo.getLayoutParams().width = targetWidth;

        Glide.with(getApplicationContext())
                .load(pojoSessionDetailsResponseData.coverPhoto)
                .apply(new RequestOptions().override(targetWidth, targetHeight))
                .into(iv_session_photo);

        tv_session_name.setText(Html.fromHtml(pojoSessionDetailsResponseData.title));

        tv_session_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoSessionDetailsResponseData.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, pojoSessionDetailsResponseData.sessionsId,
                        "share_session", et_share_title.getText().toString().trim());
                if (et_share_title.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_share_title.getWindowToken(), 0);
                }
                shareDialog.dismiss();
            }
        });
        shareDialog.show();

    }

    private void hitSessionReactionApi(String user_id, String session_id, String reaction) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, session_id, reaction);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }
        });
    }


    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_session_successfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (AssignedSessionActivity.this != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MUlTIPLE_IMAGES_CODE && resultCode == RESULT_OK) {
            List<String> imagesPaths = new ArrayList<>();
            imagesPaths.clear();

            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                imagesPaths.add(commonFunctions.getRealPathFromUri(data.getClipData().getItemAt(i).getUri()));
            }

            for (int i = 0; i < imagesPaths.size(); i++) {
                LuneBlazeUploadService.uploadFile(AssignedSessionActivity.this, session_id,
                        imagesPaths.get(i), "123", AppKeys.TYPE_IMAGE);
            }
        }
    }
}
