package com.avadna.luneblaze.activities.sessions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.sessions.SessionPollFragment;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.activities.SessionAcceptFormActivity;
import com.avadna.luneblaze.activities.sessionCreation.SessionCreationPart1;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.adapters.session.VerifiedSessionPagerAdapter;
import com.avadna.luneblaze.fragments.sessions.SessionDiscussionFragment;
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
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class VerifiedSessionActivity extends AppBaseActivity {
    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private AppBarLayout appBarLayout;
    private TabLayout tl_tabs;
    private ViewPager vp_tab_item_pager;
    private VerifiedSessionPagerAdapter verifiedSessionPagerAdapter;

    private String USER = "user";
    private String OTHER = "other";
    private String userType = USER;
    private boolean followingTaggedInterests = false;
    private boolean questionNotify = false;
    private boolean followingVenue = false;
    private String session_id = "126";
    private String dataStr;
    private String tabType;
    private String action;
    private String discussion_id;
    private PojoSessionDetailsResponseData pojoSessionDetailsResponseData = null;
    private long calenderEventID;
    private HashMap<String, Long> calenderEventMap = new HashMap<>();

    private TextView tv_session_name, tv_sharer_name;
    private TextView tv_num_attending, tv_num_shares;
    private TextView tv_attend, tv_invite, tv_share, tv_accept;
    private final int ACCEPT_SESSION_CODE = 100;
    private ImageView iv_cover_photo;

    private RelativeLayout rl_new_comment_wrapper;
    private EditText et_new_comment;
    private ImageButton ib_send;

    private final String STATUS_ATTEND = "attend";
    private final String STATUS_ATTENDING = "attending";
    private final String STATUS_WAITING = "waiting";
    private String currentAttendStatus = STATUS_ATTEND;

    private ProgressBar pb_edit_topic_api_status;
    private RelativeLayout rl_edit_topic_parent_wrapper;
    private Dialog editTopicDialog;
    private OrganisedSessionTopicsListAdapter discussionTopicListAdapter;
    private List<PojoTopic> discussionTopicList;
    private OrganisedSessionTopicsListAdapter preRequisiteListAdapter;
    private List<PojoTopic> preRequisiteList;

    //  Menu menu;
    private View.OnClickListener onClickListener;

    private DisplayMetrics displayMetrics;
    private Dialog sessionCreationDialog;

    private int imageFixedWidth;
    private int imageFixedHeight;
    private int maxSessionImageHeight;
    private int minSessionImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_session);
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
            session_id = "126";
        }
        tabType = getIntent().getStringExtra("type");
        if (tabType != null && tabType.equals("discussion")) {
            discussion_id = getIntent().getStringExtra("discussion_id");
        }

        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();
        setUpTabLayout();
        dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            Type type = new TypeToken<PojoSessionDetailsResponseData>() {
            }.getType();
            pojoSessionDetailsResponseData = new Gson().fromJson(dataStr, type);
            session_id = pojoSessionDetailsResponseData.sessionsId;
            setDataOnViews();

        } else {
            hitGetSessionDetailsApi(user_id, session_id, "home");
        }


        //  initMoreOptionsSpinner();
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


    }


    public void setAppBarExpanded(boolean value) {
        appBarLayout.setExpanded(value, true);
    }

    public void updateAttendStatus(String status) {
        if (tv_attend != null) {
            currentAttendStatus = status;
            if (status.equals(STATUS_ATTEND)) {
                tv_attend.setTextColor(ContextCompat.getColor(VerifiedSessionActivity.this, R.color.med_grey));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);
                tv_attend.setText(R.string.attend);

            } else if (status.equals(STATUS_ATTENDING)) {
                tv_attend.setTextColor(ContextCompat.getColor(VerifiedSessionActivity.this, R.color.app_theme_medium));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                tv_attend.setText(R.string.attending);

            } else if (status.equals(STATUS_WAITING)) {
                tv_attend.setTextColor(ContextCompat.getColor(VerifiedSessionActivity.this, R.color.gmail_button));
                tv_attend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tv_attend.setText(R.string.waiting);
            }
        }

    }


    private void initViews() {
        tv_session_name = (TextView) findViewById(R.id.tv_session_name);
        tv_sharer_name = (TextView) findViewById(R.id.tv_sharer_name);
        tv_num_attending = (TextView) findViewById(R.id.tv_num_attending);
        tv_num_shares = (TextView) findViewById(R.id.tv_num_shares);
        tv_attend = (TextView) findViewById(R.id.tv_attend);
        tv_invite = (TextView) findViewById(R.id.tv_invite);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_accept = (TextView) findViewById(R.id.tv_accept);
        iv_cover_photo = (ImageView) findViewById(R.id.iv_cover_photo);
        rl_new_comment_wrapper = (RelativeLayout) findViewById(R.id.rl_new_comment_wrapper);
        rl_new_comment_wrapper.setVisibility(View.GONE);
        et_new_comment = (EditText) findViewById(R.id.et_new_comment);
        ib_send = (ImageButton) findViewById(R.id.ib_send);
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonFunctions.isGuestUser()) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    switch (v.getId()) {
                        case R.id.tv_attend:
                            try {
                                ((SessionPollFragment) (verifiedSessionPagerAdapter.getRegisteredFragment(1))).setAskConfirmDialog(true);
                            } catch (Exception e) {

                            }
                            if (currentAttendStatus.equals(STATUS_ATTENDING)
                                    || currentAttendStatus.equals(STATUS_WAITING)) {
                                TextView tv_dialog_description, tv_no, tv_yes;
                                final Dialog confirmationDialog;
                                confirmationDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
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
                                        currentAttendStatus = STATUS_ATTENDING;
                                        tv_attend.setTextColor(ContextCompat.getColor(VerifiedSessionActivity.this, R.color.med_grey));
                                        try {
                                            if (verifiedSessionPagerAdapter != null) {
                                                ((SessionPollFragment) verifiedSessionPagerAdapter
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
                                //  hitAttendApi(user_id, currentItem.sessionsId, "attend");
                                TextView tv_dialog_description;
                                Button bt_ok;
                                final Dialog confirmationDialog;
                                confirmationDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
                                confirmationDialog.setContentView(R.layout.message_dialog);
                                tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                                bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                                bt_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        vp_tab_item_pager.setCurrentItem(1);
                                        try {
                                            ((SessionPollFragment) (verifiedSessionPagerAdapter.getRegisteredFragment(1))).setAskConfirmDialog(false);
                                        } catch (Exception e) {

                                        }
                                        confirmationDialog.dismiss();
                                    }
                                });
                                tv_dialog_description.setText(R.string.cast_vote_to_attend);
                                confirmationDialog.show();

                            }
                      /*  }
                        else {
                           commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.date_passed), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
*/
                            break;

                        case R.id.tv_share:
                            openShareSessionOnProfileDialog();
                            //hitShareContentApi(user_id, session_id, "share_session");
                            break;

                        case R.id.ll_initiate_wrapper:
                            Intent sessionInitiateIntent = new Intent(VerifiedSessionActivity.this,
                                    SessionCreationPart1.class);
                            sessionInitiateIntent.putExtra("type", AppKeys.SESSION_INITIATE);
                            startActivity(sessionInitiateIntent);
                            if (sessionCreationDialog != null) {
                                sessionCreationDialog.dismiss();
                            }
                            break;

                        case R.id.ll_conduct_wrapper:
                            Intent sessionConductIntent = new Intent(VerifiedSessionActivity.this,
                                    SessionCreationPart1.class);
                            sessionConductIntent.putExtra("type", AppKeys.SESSION_CONDUCT);
                            startActivity(sessionConductIntent);
                            if (sessionCreationDialog != null) {
                                sessionCreationDialog.dismiss();
                            }
                            break;


                        case R.id.ib_send:
                            SessionDiscussionFragment frag = (SessionDiscussionFragment)
                                    getSupportFragmentManager().findFragmentByTag("android:switcher:"
                                            + R.id.vp_tab_item_pager + ":" + 2);
                            frag.addDiscussion(et_new_comment.getText().toString().trim());
                            et_new_comment.getText().clear();
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_new_comment.getWindowToken(), 0);
                            break;

                        case R.id.tv_accept:
                            if (pojoSessionDetailsResponseData.iAccept) {
                                showMessageDialog(getString(R.string.you_cannot_send_accept_request_more_than_once));
                            } else {
                                Intent sessionAcceptIntent = new Intent(VerifiedSessionActivity.this,
                                        SessionAcceptFormActivity.class);
                                sessionAcceptIntent.putExtra("id", session_id);
                                startActivityForResult(sessionAcceptIntent, ACCEPT_SESSION_CODE);
                            }

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

        int targetWidth = (int) (displayMetrics.widthPixels * 0.95f);
        int targetHeight = (int) (displayMetrics.widthPixels * 0.55f);

        if (pojoSessionDetailsResponseData.imgDimensions != null
                && !pojoSessionDetailsResponseData.imgDimensions.isEmpty()) {
            String imgDimen = pojoSessionDetailsResponseData.imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            targetHeight = (int) ((1f * height * targetWidth) / width);

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


    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this, android.R.style.Theme_Black_NoTitleBar);
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

    private void setClickListeners() {
        tv_attend.setOnClickListener(onClickListener);
        tv_accept.setOnClickListener(onClickListener);
        tv_share.setOnClickListener(onClickListener);
        ib_send.setOnClickListener(onClickListener);
        iv_cover_photo.setOnClickListener(onClickListener);
    }

    public void setCommentText(final String comment) {
        if (et_new_comment != null && et_new_comment.requestFocus()) {
            et_new_comment.setText(comment);
            et_new_comment.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_new_comment, InputMethodManager.SHOW_FORCED);
                }
            }, 200);
        }

    }

    private void setDataOnViews() {
        //  menu.clear();
        if (pojoSessionDetailsResponseData != null) {


          /*  if(action!=null&&action.equals("attend")){
                //   openAttendMessageDialog();
                if(pojoSessionDetailsResponseData.totalAllowedMembers
                        .equals(pojoSessionDetailsResponseData.totalAttend)){
                    showMessageDialog(getString(R.string.all_seats_full));
                }
            }*/


            if (pojoSessionDetailsResponseData.iAttend) {
                updateAttendStatus("attending");
            } else {
                updateAttendStatus("attend");
            }
            if (pojoSessionDetailsResponseData.iWait != null && pojoSessionDetailsResponseData.iWait) {
                updateAttendStatus("waiting");
            }
            invalidateOptionsMenu();

            if (tabType != null && (tabType.equals(AppKeys.ATTEND) || tabType.equals(AppKeys.UNATTEND))) {
                tv_attend.callOnClick();
            }

            action = getIntent().getStringExtra("action");
            if (action != null) {
                if (action.equals(AppKeys.ADD_COMMENT)) {
                    setCommentText("");
                }
            }

          /*
            if (pojoSessionDetailsResponseData.createdBy.userId.equals(user_id)) {
                getMenuInflater().inflate(R.menu.verified_session_initiator_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.verified_session_other_menu, menu);
            }*/

            tv_session_name.setText(Html.fromHtml(pojoSessionDetailsResponseData.title));
            tv_sharer_name.setText(pojoSessionDetailsResponseData.createdBy.userFullname);
            tv_num_shares.setText("" + pojoSessionDetailsResponseData.totalShares + " " + getString(R.string.shares));
            tv_num_attending.setText("" + pojoSessionDetailsResponseData.totalAttend + " " + getString(R.string.attending));

            if (pojoSessionDetailsResponseData.iAccept) {
                tv_accept.setText(getString(R.string.requested));
                tv_accept.setTextColor(ContextCompat.getColor(this, R.color.app_theme_medium));
                tv_accept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);

            } else {
                tv_accept.setText(getString(R.string.accept));
                tv_accept.setTextColor(ContextCompat.getColor(this, R.color.med_grey));
                tv_accept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);

            }

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

    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog messageDialog;
        messageDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
        messageDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) messageDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) messageDialog.findViewById(R.id.bt_ok);
        tv_dialog_description.setText(message);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VerifiedSessionActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VerifiedSessionActivity.this, R.color.status_bar_color));
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

    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.removeAllTabs();
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.details)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.polls)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.discussions)));

        //tl_tabs.addTab(tl_tabs.newTab().setText("Team"));

        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        vp_tab_item_pager = (ViewPager) findViewById(R.id.vp_tab_item_pager);

        //userType 1 in pager adapter is to tell its Verified session
        verifiedSessionPagerAdapter = new VerifiedSessionPagerAdapter(this,
                getSupportFragmentManager(), session_id, discussion_id);
        vp_tab_item_pager.setAdapter(verifiedSessionPagerAdapter);

        vp_tab_item_pager.setOffscreenPageLimit(3);

        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_tab_item_pager.setCurrentItem(tab.getPosition());
                appBarLayout.setExpanded(false, true);

                //todo why was comment box added here?

                if (tab.getPosition() == 2&&!commonFunctions.isGuestUser()) {
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
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  this.menu = menu;
      /*  if (userType.equals(USER)) {
            getMenuInflater().inflate(R.menu.verified_session_initiator_menu, menu);

        } else if (userType.equals(OTHER)) {
            getMenuInflater().inflate(R.menu.verified_session_other_menu, menu);

        }*/
        if (pojoSessionDetailsResponseData != null) {
            if (pojoSessionDetailsResponseData.createdBy.userId.equals(user_id)) {
                getMenuInflater().inflate(R.menu.verified_session_initiator_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.verified_session_other_menu, menu);
            }
        }

        if(commonFunctions.isGuestUser()){
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

            case R.id.invite:
                //  openInviteDialog();
                // hitInviteFriendApi(user_id,"invite_session","3",session_id);
                Intent inviteIntent = new Intent(VerifiedSessionActivity.this, InviteActivity.class);
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


            case R.id.follow_interest:
                if (followingTaggedInterests) {
                    hitSessionReactionApi(user_id, session_id, "interest_unfollow");
                    followingTaggedInterests = false;
                    item.setTitle(R.string.follow_tagged_interests);
                } else {
                    hitSessionReactionApi(user_id, session_id, "interest_follow");
                    followingTaggedInterests = true;
                    item.setTitle(R.string.unfollow_tagged_interests);
                }

                // hitReportContentApi(user_id,"interest_follow","3");
                break;

            case R.id.send_conduct_request:
                Intent conductIntent = new Intent(VerifiedSessionActivity.this, InviteActivity.class);
                conductIntent.putExtra("userType", "session_conduct_user_list");
                conductIntent.putExtra("id", session_id);
                startActivity(conductIntent);
                //openConductRequestDialog();
                //hitInviteFriendApi(user_id,"session_conduct_request","3",session_id);
                break;

            case R.id.edit:
                Intent editIntent = new Intent(VerifiedSessionActivity.this, EditTopicsActivity.class);
                Type type = new TypeToken<PojoSessionDetailsResponseData>() {
                }.getType();
                String data = new Gson().toJson(pojoSessionDetailsResponseData, type);
                editIntent.putExtra("data", data);
                startActivity(editIntent);
                //  openEditTopicsDialog();
                break;

            case R.id.delete:
                if (pojoSessionDetailsResponseData.createdBy.userId.equals(user_id)) {
                    hitSessionReactionApi(user_id, session_id, "delete_session");
                }
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_session", session_id);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void openSessionCreateDialog() {
        LinearLayout ll_initiate_wrapper, ll_conduct_wrapper;
        sessionCreationDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
        sessionCreationDialog.setContentView(R.layout.session_creation_dialog);
        ll_initiate_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_initiate_wrapper);
        ll_conduct_wrapper = (LinearLayout) sessionCreationDialog.findViewById(R.id.ll_conduct_wrapper);
        ll_initiate_wrapper.setOnClickListener(onClickListener);
        ll_conduct_wrapper.setOnClickListener(onClickListener);
        sessionCreationDialog.setCancelable(true);
        sessionCreationDialog.show();
    }


    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponseData = response.body().data;
                    setDataOnViews();
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.session));
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


    public void hitAttendApi(String user_id, String id, final String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);
        tabType = null;
        commonFunctions.openProgressDialog(getString(R.string.please_wait));
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
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void sendAttendBroadcast(String status) {
        Intent registrationComplete = new Intent(Config.SESSION_ATTENDED_STATUS);
        registrationComplete.putExtra(AppKeys.ATTEND_STATUS, status);
        registrationComplete.putExtra(AppKeys.SESSION_ID, session_id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
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
        switch (requestCode) {
            case AppKeys.ADD_TO_CALENDER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    addSessionToCalender();

                }
                return;
            }


            case AppKeys.REMOVE_FROM_CALENDER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    removeSessionFromCalender();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
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
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

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
                                  ShareContentOnMediaApps.shareContent(VerifiedSessionActivity.this,
                                          AppKeys.SHARE_SESSION, session_id, pojoSessionDetailsResponseData.title,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();
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
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }



   /* private void openInviteDialog() {

        inviteDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
        inviteDialog.setContentView(R.layout.contact_list_dialog);
        RecyclerView rv_list;

        rv_list=(RecyclerView)inviteDialog.findViewById(R.id.rv_list);
        rv_list.setPadding(16,32,16,32);
        rv_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        friendList=new ArrayList<>();
        inviteListAdapter=new InviteListAdapter(session_id,friendList,VerifiedSessionActivity.this,AppKeys.INVITE_SESSION);
        rv_list.setAdapter(inviteListAdapter);
        hitGetFriendListApi(preferenceUtils.get_user_id());

        inviteDialog.show();
    }*/

  /*  private void openConductRequestDialog() {

        inviteDialog = new MyCustomThemeDialog(VerifiedSessionActivity.this);
        inviteDialog.setContentView(R.layout.contact_list_dialog);
        RecyclerView rv_list;

        rv_list=(RecyclerView)inviteDialog.findViewById(R.id.rv_list);
        rv_list.setPadding(16,32,16,32);
        rv_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        friendList=new ArrayList<>();
        inviteListAdapter=new InviteListAdapter(session_id,friendList,VerifiedSessionActivity.this, AppKeys.CONDUCT_SESSION);
        rv_list.setAdapter(inviteListAdapter);
        hitGetFriendListApi(preferenceUtils.get_user_id());

        inviteDialog.show();
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == ACCEPT_SESSION_CODE) {
            if (resultCode == 1) {
                pojoSessionDetailsResponseData.iAccept = true;
                setDataOnViews();
            }
        }
    }


}
