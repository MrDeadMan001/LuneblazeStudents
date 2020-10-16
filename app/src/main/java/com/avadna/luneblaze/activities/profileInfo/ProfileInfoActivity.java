package com.avadna.luneblaze.activities.profileInfo;

import android.Manifest;
import android.app.Dialog;

import com.avadna.luneblaze.update.activities.settings.SettingsActivityNew;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.UserActivity;
import com.avadna.luneblaze.activities.chat.ChatActivity;
import com.avadna.luneblaze.adapters.profileInfo.EducationSmallListAdapter;
import com.avadna.luneblaze.adapters.profileInfo.ProfileInfoPagerAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponseData;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserWorkListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoWorkHistory;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProfileInfoActivity extends AppBaseActivity {
    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String targetUserId;
    private PojoProfileInfoResponseData pojoProfileInfoResponseData;

    private ProgressBar pb_loading_content;
    private CoordinatorLayout cl_parent;
    private AppBarLayout appBarLayout;
    private TextView tv_username, tv_user_designation, tv_institution, tv_area;
    private TextView tv_field_of_interest;
    private TextView tv_goal;
    private TextView tv_add_fellow, tv_mutual_friends_count, tv_message;
    private ImageView iv_user_dp;
    private ImageButton ib_edit_user_dp;

    //private ImageView iv_cover_photo;
    private ImageView iv_profile_menu_options;
    private PopupMenu popupMenu;

    private Dialog editProfileDialog;


    private String changeImageType;
    private Dialog imageViewerDialog;
    private ImageView iv_back, iv_edit;
    private PhotoView iv_photo;
    private Dialog confirmationDialog;

    private LinearLayout ll_people_influenced_wrapper;
    private LinearLayout ll_session_engaged_wrapper;
    private LinearLayout ll_influenced_session_wrapper;
    private TextView tv_num_people_influenced;
    private TextView tv_num_session_engaged;

    private LinearLayout ll_education_experience;
    private TextView tv_education_more;
    private RecyclerView rv_education_list;
    private EducationSmallListAdapter educationSmallListAdapter;
    private List<PojoWorkHistory> workHistoryList;

    private RelativeLayout rl_fellows_button_wrapper, rl_interests_button_wrapper, rl_venues_button_wrapper;
    // private  TextView tv_friends_button, tv_interests_button, tv_venues;
    private TextView tv_fellow_count, tv_interest_count, tv_venue_count;

    private TabLayout tl_user_activity_tabs;
    private ViewPager vp_profile_activity_pager;

    private View.OnClickListener onClickListener;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private final int UPDATE_PROFILE = 1;
    private String notification_id;

    private boolean showSessionTab = true;
    private boolean profileUpdated = false;

    private static final int SELECT_VIDEO_REQUEST_CODE = 1002;
    private static final int SELECT_FILE_REQUEST_CODE = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        targetUserId = getIntent().getStringExtra("target_user_id");

        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        setUpActionBar();
        // setImagePicker();
        initViews();
        initClickListener();
        setClickListener();
        initEducationList();
        hitGetUserProfileApi(user_id, targetUserId, "dashboard");
        initBroadCastReceiver();
        hitGetUserWorkListApi(targetUserId, targetUserId, "work_history");

        if (user_id.equals(targetUserId)) {
            pojoProfileInfoResponseData = preferenceUtils.getUserProfileInfo();
            if (pojoProfileInfoResponseData != null && pojoProfileInfoResponseData.profile != null) {
                setDataOnViews();
                hideViewsBasedOnPrivacy();
                cl_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
                hitGetUserProfileApi(user_id, targetUserId, "dashboard");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PROFILE_UPDATED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.WORK_HISTORY_UPDATED));
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.PROFILE_UPDATED)) {
                    hitGetUserProfileApi(user_id, targetUserId, "dashboard");
                }
                if (intent.getAction().equals(Config.WORK_HISTORY_UPDATED)) {
                    hitGetUserWorkListApi(targetUserId, targetUserId, "work_history");
                }
            }
        };
    }

    public void initEducationList() {
        workHistoryList = new ArrayList<>();
        rv_education_list = (RecyclerView) findViewById(R.id.rv_education_list);
        rv_education_list.setLayoutManager(new LinearLayoutManager(ProfileInfoActivity.this,
                RecyclerView.VERTICAL, false));
        educationSmallListAdapter = new EducationSmallListAdapter(ProfileInfoActivity.this,
                workHistoryList, "small");
        rv_education_list.setAdapter(educationSmallListAdapter);
        rv_education_list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_info));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ProfileInfoActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ProfileInfoActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        rl_fellows_button_wrapper = (RelativeLayout) findViewById(R.id.rl_fellows_button_wrapper);
        rl_interests_button_wrapper = (RelativeLayout) findViewById(R.id.rl_interests_button_wrapper);
        rl_venues_button_wrapper = (RelativeLayout) findViewById(R.id.rl_venues_button_wrapper);

        tv_fellow_count = (TextView) findViewById(R.id.tv_fellow_count);
        tv_interest_count = (TextView) findViewById(R.id.tv_interest_count);
        tv_venue_count = (TextView) findViewById(R.id.tv_venue_count);

        ib_edit_user_dp = (ImageButton) findViewById(R.id.ib_edit_user_dp);
        ib_edit_user_dp.setVisibility(View.GONE);

        iv_user_dp = (ImageView) findViewById(R.id.iv_user_dp);
        Glide.with(getApplicationContext())
                .load(R.drawable.placeholder)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(iv_user_dp);
        //  iv_cover_photo = (ImageView) findViewById(R.id.iv_cover_photo);
        iv_profile_menu_options = (ImageView) findViewById(R.id.iv_profile_menu_options);

        /*if (user_id.equals(targetUserId)) {
            iv_profile_menu_options.setImageResource(R.drawable.ic_edit_grey);
        } else*/
        {
            iv_profile_menu_options.setImageResource(R.drawable.ic_dropdown_gray);
        }

        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_user_designation = (TextView) findViewById(R.id.tv_user_designation);
        tv_institution = (TextView) findViewById(R.id.tv_institution);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_field_of_interest = (TextView) findViewById(R.id.tv_field_of_interest);
        tv_add_fellow = (TextView) findViewById(R.id.tv_add_fellow);

        ll_people_influenced_wrapper = (LinearLayout) findViewById(R.id.ll_people_influenced_wrapper);
        ll_session_engaged_wrapper = (LinearLayout) findViewById(R.id.ll_session_engaged_wrapper);
        ll_influenced_session_wrapper = (LinearLayout) findViewById(R.id.ll_influenced_session_wrapper);

        tv_mutual_friends_count = (TextView) findViewById(R.id.tv_mutual_friends_count);
        tv_message = (TextView) findViewById(R.id.tv_message);

        tv_num_people_influenced = (TextView) findViewById(R.id.tv_num_people_influenced);
        tv_num_session_engaged = (TextView) findViewById(R.id.tv_num_session_engaged);

        ll_education_experience = (LinearLayout) findViewById(R.id.ll_education_experience);
        tv_education_more = (TextView) findViewById(R.id.tv_education_more);

        if (user_id.equals(targetUserId)) {
            tv_add_fellow.setVisibility(View.GONE);
            tv_message.setVisibility(View.GONE);
            tv_mutual_friends_count.setVisibility(View.GONE);
        }

        //  tv_profile_dashboard= (TextView) findViewById(R.id.tv_profile_dashboard);
    }

    private void initPopUpMenu() {

        popupMenu = new PopupMenu(this, iv_profile_menu_options);
        MenuInflater inflater = popupMenu.getMenuInflater();
        popupMenu.getMenu().clear();

        if (user_id.equals(targetUserId)) {
            inflater.inflate(R.menu.profile_option_dropdown_user_menu, popupMenu.getMenu());
        } else {
            inflater.inflate(R.menu.profile_option_dropdown_other_menu, popupMenu.getMenu());
            if (pojoProfileInfoResponseData.profile.iFollow) {
                popupMenu.getMenu().getItem(0).setTitle(getString(R.string.following));
                popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_follow_rss_filled);
            } else {
                popupMenu.getMenu().getItem(0).setTitle(getString(R.string.follow));
                popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_follow_rss_unfilled);
            }

            if (pojoProfileInfoResponseData.profile.iSessionBlock.equals("Yes")) {
                popupMenu.getMenu().getItem(4).setTitle(getString(R.string.session_blocked));
            }
        }

        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.follow:
                        if (pojoProfileInfoResponseData.profile.iFollow) {
                            hitUpdateUserConnectionApi(user_id, targetUserId, "unfollow");
                            popupMenu.getMenu().getItem(0).setTitle(getString(R.string.follow));
                            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_follow_rss_unfilled);
                        } else {
                            hitUpdateUserConnectionApi(user_id, targetUserId, "follow");
                            popupMenu.getMenu().getItem(0).setTitle(getString(R.string.following));
                            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_follow_rss_filled);
                        }
                        pojoProfileInfoResponseData.profile.iFollow = !pojoProfileInfoResponseData.profile.iFollow;
                        break;

                    case R.id.share:
                        shareProfile();
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_user", targetUserId);
                        break;

                    case R.id.block:
                        openConfirmationDialog(AppKeys.TYPE_BLOCK);
                        break;

                    case R.id.session_block:
                        openConfirmationDialog(AppKeys.TYPE_SESSION_BLOCK);
                        break;

                    case R.id.edit:
                        Intent intent = new Intent(ProfileInfoActivity.this, EditProfileActivity.class);
                        startActivityForResult(intent, UPDATE_PROFILE);
                        // openEditProfileDialog();
                        break;

                }
                return false;
            }
        });
    }


    public void shareProfile() {
        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoProfileInfoResponseData.profile.userPicture)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(ProfileInfoActivity.this,
                                          AppKeys.SHARE_USER, targetUserId, pojoProfileInfoResponseData.profile.userFullname,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }

    private void openConfirmationDialog(final String type) {
        TextView tv_dialog_description, tv_yes, tv_no;
        confirmationDialog = new MyCustomThemeDialog(ProfileInfoActivity.this);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

        if (type.equals(AppKeys.TYPE_BLOCK)) {
            tv_dialog_description.setText(getString(R.string.block_confirmation_dialog_description));
        } else if (type.equals(AppKeys.TYPE_SESSION_BLOCK)) {
            if (pojoProfileInfoResponseData.profile.iSessionBlock.equals("Yes")) {
                tv_dialog_description.setText(getString(R.string.session_unblock_dialog_confirmation));
            } else {
                tv_dialog_description.setText(getString(R.string.session_block_dialog_confirmation));
            }

        } else if (type.equals(AppKeys.TYPE_FELLOW)) {
            tv_dialog_description.setText(getString(R.string.fellow_remove_confirmation));
        } else if (type.equals(AppKeys.FELLOW_REQUEST_CANCEL)) {
            tv_dialog_description.setText(getString(R.string.cancel_fellow_request_confirmation));

        }
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(AppKeys.TYPE_BLOCK)) {
                    hitUpdateUserConnectionApi(user_id, targetUserId, "block");
                    confirmationDialog.dismiss();
                    finish();
                } else if (type.equals(AppKeys.TYPE_SESSION_BLOCK)) {
                    if (pojoProfileInfoResponseData.profile.iSessionBlock.equals("Yes")) {
                        hitUpdateUserConnectionApi(user_id, targetUserId, "session_unblock");
                        popupMenu.getMenu().getItem(4).setTitle(getString(R.string.session_block));
                        pojoProfileInfoResponseData.profile.iSessionBlock = "No";
                        confirmationDialog.dismiss();
                    } else {
                        hitUpdateUserConnectionApi(user_id, targetUserId, "session_block");
                        popupMenu.getMenu().getItem(4).setTitle(getString(R.string.session_blocked));
                        pojoProfileInfoResponseData.profile.iSessionBlock = "Yes";
                        confirmationDialog.dismiss();
                    }

                } else if (type.equals(AppKeys.TYPE_FELLOW)) {
                    hitUpdateUserConnectionApi(user_id, targetUserId, "friend-remove");
                    pojoProfileInfoResponseData.profile.connection = "add";
                    pojoProfileInfoResponseData.profile.weFriends = false;
                    setConnection();
                    confirmationDialog.dismiss();
                } else if (type.equals(AppKeys.FELLOW_REQUEST_CANCEL)) {
                    pojoProfileInfoResponseData.profile.iRequest = false;
                    hitUpdateUserConnectionApi(user_id, targetUserId, "friend-cancel");
                    setConnection();
                    confirmationDialog.dismiss();
                }

            }
        });
        confirmationDialog.show();
    }

    private void initTabLayout() {
        tl_user_activity_tabs = (TabLayout) findViewById(R.id.tl_user_activity_tabs);
        tl_user_activity_tabs.removeAllTabs();

        if (!user_id.equals(targetUserId)) {
            if (pojoProfileInfoResponseData.profile.privacySession.equals(AppKeys.ME)) {
                showSessionTab = false;
            } else if (pojoProfileInfoResponseData.profile.privacySession.equals(AppKeys.FRIENDS)) {
                if (!(pojoProfileInfoResponseData.profile.connection.equals("remove"))) {
                    showSessionTab = false;
                }
            } else if (pojoProfileInfoResponseData.profile.privacySession.equals(AppKeys.PUBLIC)) {
                if (pojoProfileInfoResponseData.profile.sessionBlock.equals("Yes")) {
                    showSessionTab = false;
                }
            }
        }
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.posts)));

        if (showSessionTab) {
            tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.sessions)));
        }
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.articles)));
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.polls)));
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.questions)));
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.answers)));
        tl_user_activity_tabs.addTab(tl_user_activity_tabs.newTab().setText(getString(R.string.activity)));
        tl_user_activity_tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tl_user_activity_tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void setDataInViewPager() {
        vp_profile_activity_pager = (ViewPager) findViewById(R.id.vp_profile_activity_pager);
        ProfileInfoPagerAdapter profileInfoPagerAdapter = new ProfileInfoPagerAdapter(ProfileInfoActivity.this,
                getSupportFragmentManager(), pojoProfileInfoResponseData, targetUserId, showSessionTab);
        vp_profile_activity_pager.setAdapter(profileInfoPagerAdapter);

        vp_profile_activity_pager.setOffscreenPageLimit(5);


        vp_profile_activity_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tl_user_activity_tabs.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tl_user_activity_tabs.getTabAt(position);
                tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tl_user_activity_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_profile_activity_pager.setCurrentItem(tab.getPosition());

                //profile updated flag is used to prevent auto collapse of appbarlayout
                //todo need to find better fix
                appBarLayout.setExpanded(false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rl_fellows_button_wrapper:

                        if (targetUserId.equals(user_id)) {
                            Intent friendsIntent = new Intent(ProfileInfoActivity.this,
                                    MyFriendsActivity.class);
                            startActivity(friendsIntent);
                        } else {
                            Intent friendsIntent = new Intent(ProfileInfoActivity.this,
                                    FriendListActivity.class);
                            friendsIntent.putExtra("target_user_id", targetUserId);
                            startActivity(friendsIntent);
                        }
                        break;

                    case R.id.rl_interests_button_wrapper:
                        Intent interestListActivity = new Intent(ProfileInfoActivity.this,
                                UserInterestListActivity.class);
                        interestListActivity.putExtra("target_user_id", targetUserId);
                        startActivity(interestListActivity);
                        break;

                    case R.id.rl_venues_button_wrapper:
                        Intent venueIntent = new Intent(ProfileInfoActivity.this,
                                UserVenueListActivity.class);
                        venueIntent.putExtra("target_user_id", targetUserId);
                        startActivity(venueIntent);
                        break;

                    case R.id.iv_user_dp:
                      /*  Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(videoIntent, SELECT_VIDEO_REQUEST_CODE);*/
                        openImageViewerDialog(AppKeys.PROFILE_IMG);
                        break;

                    case R.id.ib_edit_user_dp:
                        openImageViewerDialog(AppKeys.PROFILE_IMG);
                        break;

                    case R.id.iv_cover_photo:
                        openImageViewerDialog(AppKeys.COVER_IMG);
                        break;

                    case R.id.iv_profile_menu_options:
                       /* if (targetUserId.equals(user_id)) {
                            Intent intent = new Intent(ProfileInfoActivity.this,
                                    EditProfileActivity.class);
                            startActivityForResult(intent, UPDATE_PROFILE);
                            // openEditProfileDialog();
                        } else */
                    {
                        popupMenu.show();
                    }
                    break;

                    case R.id.tv_education_more:
                        Intent educationIntent = new Intent(ProfileInfoActivity.this, WorkAndEducationActivity.class);
                        String dataStr = new Gson().toJson(pojoProfileInfoResponseData);
                        educationIntent.putExtra("data", dataStr);
                        educationIntent.putExtra(AppKeys.TARGET_USER_ID, targetUserId);
                        startActivity(educationIntent);
                        break;

                    case R.id.tv_profile_dashboard:
                        Intent userActivityIntent = new Intent(ProfileInfoActivity.this, UserActivity.class);
                        startActivity(userActivityIntent);
                        break;

                    case R.id.tv_add_fellow:

                        if (pojoProfileInfoResponseData.profile.weFriends) {
                            openConfirmationDialog(AppKeys.TYPE_FELLOW);
                        } else if (pojoProfileInfoResponseData.profile.iRequest) {
                            openConfirmationDialog(AppKeys.FELLOW_REQUEST_CANCEL);

                        } else if (pojoProfileInfoResponseData.profile.heRequest) {
                            openRequestChoiceDialog();
                        } else {
                            hitAddFriendApi(user_id, targetUserId, "friend-add");
                            pojoProfileInfoResponseData.profile.iRequest = true;
                            setConnection();
                        }
                        break;


                    case R.id.tv_message:
                        boolean found = false;
                        List<PojoConversationListItem> conversationList = preferenceUtils.getAllMessageList();
                        for (int i = 0; i < conversationList.size(); i++) {
                            if (conversationList.get(i).groupData.groupId == null) {
                                if (conversationList.get(i).ids.equals(targetUserId)) {
                                    found = true;
                                    Intent chatIntent = new Intent(ProfileInfoActivity.this, ChatActivity.class);
                                    chatIntent.putExtra("cid", conversationList.get(i).conversationId);
                                    startActivity(chatIntent);
                                }
                            }
                        }
                        if (!found) {
                            Intent chatIntent = new Intent(ProfileInfoActivity.this, ChatActivity.class);
                            chatIntent.putExtra("cid", "");
                            chatIntent.putExtra(AppKeys.TARGET_USER_ID, targetUserId);
                            startActivity(chatIntent);
                            // hitCreateIndividualConversationApi(user_id, targetUserId);
                        }
                        break;


                }
            }


        };
    }

    private void openRequestChoiceDialog() {
        TextView tv_dialog_description, tv_no, tv_yes;
        final BottomSheetDialog confirmationDialog;
        confirmationDialog = new BottomSheetDialog(ProfileInfoActivity.this);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setText(getString(R.string.decline));
        tv_yes.setText(getString(R.string.accept));

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitUpdateUserConnectionApi(user_id, targetUserId, "friend-decline");
                confirmationDialog.dismiss();
                pojoProfileInfoResponseData.profile.heRequest = false;
                setConnection();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitUpdateUserConnectionApi(user_id, targetUserId, "friend-accept");
                confirmationDialog.dismiss();
                pojoProfileInfoResponseData.profile.heRequest = false;
                pojoProfileInfoResponseData.profile.weFriends = true;
                setConnection();
            }
        });
        tv_dialog_description.setText(R.string.respond_to_fellow_request);
        confirmationDialog.show();
    }

    private void openImageViewerDialog(String type) {
        changeImageType = type;
        imageViewerDialog = new MyCustomThemeDialog(ProfileInfoActivity.this, android.R.style.Theme_Black_NoTitleBar);
        imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
        iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
        iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);
        iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);
 /*       if (type.equals(AppKeys.COVER_IMG)) {
            iv_photo.setImageDrawable(iv_cover_photo.getDrawable());
            if (pojoProfileInfoResponseData != null && !pojoProfileInfoResponseData.profile.userCover.isEmpty()) {
                Glide.with(getApplicationContext())
                        .load(pojoProfileInfoResponseData.profile.userCover)
                        .apply(new RequestOptions().override(1024, 1024))
                        .into(iv_photo);
            }

        } else*/
        if (type.equals(AppKeys.PROFILE_IMG)) {
            iv_photo.setImageDrawable(iv_user_dp.getDrawable());
            if (pojoProfileInfoResponseData != null && !pojoProfileInfoResponseData.profile.userPicture.isEmpty()) {
                Glide.with(getApplicationContext())
                        .load(pojoProfileInfoResponseData.profile.userPicture)
                        .apply(new RequestOptions().override(720, 720))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .placeholder(R.drawable.blank_profile_male)
                        .into(iv_photo);
            }
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewerDialog.dismiss();
            }
        });

        if (!user_id.equals(targetUserId)) {
            iv_edit.setVisibility(View.GONE);
        } else {
            iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestStoragePermission();
                }
            });
        }


        imageViewerDialog.show();


    }

    private void requestStoragePermission() {
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
                        AppKeys.STORAGE_READ_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(ProfileInfoActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppKeys.STORAGE_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1, 1)
                            .start(ProfileInfoActivity.this);
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void setClickListener() {
        rl_fellows_button_wrapper.setOnClickListener(onClickListener);
        rl_interests_button_wrapper.setOnClickListener(onClickListener);
        rl_venues_button_wrapper.setOnClickListener(onClickListener);
        //   iv_cover_photo.setOnClickListener(onClickListener);
        iv_user_dp.setOnClickListener(onClickListener);
        ib_edit_user_dp.setOnClickListener(onClickListener);
        iv_profile_menu_options.setOnClickListener(onClickListener);
        tv_message.setOnClickListener(onClickListener);
        tv_education_more.setOnClickListener(onClickListener);
        tv_add_fellow.setOnClickListener(onClickListener);
        //  tv_profile_dashboard.setOnClickListener(onClickListener);
    }

    private void hitGetUserProfileApi(final String user_id, final String targetUserId, String type) {
        Call<PojoProfileInfoResponse> call = apiService.getUserProfile(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoProfileInfoResponse>() {
            @Override
            public void onResponse(Call<PojoProfileInfoResponse> call, Response<PojoProfileInfoResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pojoProfileInfoResponseData = response.body().data;
                        if (user_id.equals(targetUserId)) {
                            preferenceUtils.saveUserProfileInfo(pojoProfileInfoResponseData);
                            PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
                            if (pojoLoginResponseData != null) {
                                pojoLoginResponseData.userPicture = pojoProfileInfoResponseData.profile.userPicture;
                                preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                            }
                        }
                        if (pojoProfileInfoResponseData != null && pojoProfileInfoResponseData.profile != null) {
                            setDataOnViews();
                            hideViewsBasedOnPrivacy();
                            cl_parent.setVisibility(View.VISIBLE);
                            pb_loading_content.setVisibility(View.GONE);
                        }
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.you_cannot_view_this_profile),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        pb_loading_content.setVisibility(View.GONE);
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<PojoProfileInfoResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                if (!user_id.equals(targetUserId) || pojoProfileInfoResponseData == null) {
                    openNoConnectionDialog(getString(R.string.profile_info));
                }
                //  cl_parent.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
            }

        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog = new ConnectionErrorDialog(ProfileInfoActivity.this,
                    R.style.AppTheme, title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetUserProfileApi(user_id, targetUserId, "dashboard");
                    cl_parent.setVisibility(View.GONE);
                    pb_loading_content.setVisibility(View.VISIBLE);
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

    private void setDataOnViews() {
        if (pojoProfileInfoResponseData != null) {
            if (!profileUpdated) {
                initPopUpMenu();
                initTabLayout();
                setDataInViewPager();
                invalidateOptionsMenu();
            }

            if (pojoProfileInfoResponseData.profile.nooffriends == null || pojoProfileInfoResponseData.profile.nooffriends == 0) {
                tv_fellow_count.setText("");
            } else {
                tv_fellow_count.setText("(" + pojoProfileInfoResponseData.profile.nooffriends + ")");
            }
            if (pojoProfileInfoResponseData.profile.noofinterests == null || pojoProfileInfoResponseData.profile.noofinterests == 0) {
                tv_interest_count.setText("");
            } else {
                tv_interest_count.setText("(" + pojoProfileInfoResponseData.profile.noofinterests + ")");
            }
            if (pojoProfileInfoResponseData.profile.noofvenue == null || pojoProfileInfoResponseData.profile.noofvenue == 0) {
                tv_venue_count.setText("");
            } else {
                tv_venue_count.setText("(" + pojoProfileInfoResponseData.profile.noofvenue + ")");
            }


            for (int i = 0; i < tl_user_activity_tabs.getTabCount(); i++) {
                if (tl_user_activity_tabs.getTabAt(i) != null && tl_user_activity_tabs.getTabAt(i).getText() != null) {

                    if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.sessions)) && pojoProfileInfoResponseData.totalSessionCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.totalSessionCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.questions)) && pojoProfileInfoResponseData.questionsCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.questionsCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.answers)) && pojoProfileInfoResponseData.answersCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.answersCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.posts)) && pojoProfileInfoResponseData.postsCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.postsCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.articles)) && pojoProfileInfoResponseData.articlesCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.articlesCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.polls)) && pojoProfileInfoResponseData.pollsCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.pollsCount + ")");
                    } else if (tl_user_activity_tabs.getTabAt(i).getText().toString()
                            .equals(getString(R.string.activity)) && pojoProfileInfoResponseData.activityCount != 0) {
                        tl_user_activity_tabs.getTabAt(i)
                                .setText(tl_user_activity_tabs.getTabAt(i).getText()
                                        + "(" + pojoProfileInfoResponseData.activityCount + ")");
                    }
                }
            }


            tv_username.setText(pojoProfileInfoResponseData.profile.userFullname);
            tv_user_designation.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
            tv_institution.setText(pojoProfileInfoResponseData.profile.userWorkPlace);
            //why cant use foi==null here?
            if (pojoProfileInfoResponseData.profile.currentFOI == null ||
                    pojoProfileInfoResponseData.profile.currentFOI.equalsIgnoreCase("null")
                    || pojoProfileInfoResponseData.profile.currentFOI.isEmpty()) {
                tv_field_of_interest.setVisibility(View.GONE);
            } else {
                tv_field_of_interest.setVisibility(View.VISIBLE);
                tv_field_of_interest.setText(pojoProfileInfoResponseData.profile.currentFOI);
            }
            tv_area.setText(pojoProfileInfoResponseData.profile.userCurrentCity);

            setConnection();

            try {
                if (!pojoProfileInfoResponseData.profile.userPicture.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(pojoProfileInfoResponseData.profile.userPicture)
                            .apply(new RequestOptions().override(256, 256))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .placeholder(R.drawable.blank_profile_male)
                            .into(iv_user_dp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (targetUserId.equals(user_id)) {
                ib_edit_user_dp.setVisibility(View.VISIBLE);
            } else {
                ib_edit_user_dp.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.mutualFriendsCount == null
                    || pojoProfileInfoResponseData.profile.mutualFriendsCount == 0) {
                tv_mutual_friends_count.setVisibility(View.GONE);
            } else {
                tv_mutual_friends_count.setText("(" + pojoProfileInfoResponseData.profile.mutualFriendsCount + " "
                        + getString(R.string.common) + ")");
            }

            if (pojoProfileInfoResponseData.profile.peopleInfluencednew == 0) {
                ll_people_influenced_wrapper.setVisibility(View.GONE);
            } else {
                ll_people_influenced_wrapper.setVisibility(View.VISIBLE);
            }

            if (pojoProfileInfoResponseData.totalSessionCount == 0) {
                ll_session_engaged_wrapper.setVisibility(View.GONE);
            } else {
                ll_session_engaged_wrapper.setVisibility(View.VISIBLE);
            }

            if (pojoProfileInfoResponseData.totalSessionCount == 0 && pojoProfileInfoResponseData.profile.peopleInfluencednew == 0) {
                ll_influenced_session_wrapper.setVisibility(View.GONE);
            } else {
                ll_influenced_session_wrapper.setVisibility(View.VISIBLE);
            }
            tv_num_people_influenced.setText("" + pojoProfileInfoResponseData.profile.peopleInfluencednew);
            tv_num_session_engaged.setText("" + pojoProfileInfoResponseData.totalSessionCount);

            profileUpdated = true;
        }

    }

    private void setConnection() {
        if (pojoProfileInfoResponseData.profile.weFriends != null && pojoProfileInfoResponseData.profile.weFriends) {
            tv_add_fellow.setText(R.string.fellows);
            tv_add_fellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_friend_filled,
                    0, 0, 0);
            tv_mutual_friends_count.setVisibility(View.GONE);
            tv_add_fellow.setBackgroundResource(R.color.transparent);

        } else if (pojoProfileInfoResponseData.profile.iRequest != null && pojoProfileInfoResponseData.profile.iRequest) {
            tv_add_fellow.setText(getString(R.string.requested));
            tv_add_fellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_invite_filled, 0,
                    0, 0);
            tv_mutual_friends_count.setVisibility(View.GONE);
            tv_add_fellow.setBackgroundResource(R.color.transparent);

        } else if (pojoProfileInfoResponseData.profile.heRequest != null && pojoProfileInfoResponseData.profile.heRequest) {
            tv_add_fellow.setText(getString(R.string.respond));
            tv_add_fellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow, 0,
                    0, 0);
            tv_add_fellow.setBackgroundResource(R.color.transparent);

        } else {
            tv_add_fellow.setText(getString(R.string.add_fellow));
            tv_add_fellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow, 0,
                    0, 0);
            tv_add_fellow.setBackgroundResource(R.drawable.app_theme_border);

        }



      /*  if (pojoProfileInfoResponseData.profile.connection.equals("add")) {
            tv_add_fellow.setText(getString(R.string.add_fellow));
        } else if (pojoProfileInfoResponseData.profile.connection.equals("remove")) {
            tv_add_fellow.setText(getString(R.string.remove_fellow));
            tv_mutual_friends_count.setVisibility(View.GONE);
        }
        else if (pojoProfileInfoResponseData.profile.connection.equals("cancel")) {
            tv_add_fellow.setText(getString(R.string.requested));
            tv_add_fellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_invite_filled,0,0,0);
            tv_mutual_friends_count.setVisibility(View.GONE);
        }*/
    }

    public void hideViewsBasedOnPrivacy() {
        //target is me
        if (pojoProfileInfoResponseData.profile.connection.equals("me") || targetUserId.equals(user_id)) {
            tv_add_fellow.setVisibility(View.GONE);
            tv_message.setVisibility(View.GONE);
            tv_mutual_friends_count.setVisibility(View.GONE);
        }

        //target is my friend
        else if (pojoProfileInfoResponseData.profile.connection.equals("remove")) {
            if (pojoProfileInfoResponseData.profile.privacyMessage.equals("me")) {
                tv_message.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyLocation.equals("me")) {
                tv_area.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyBasic.equals("me")) {
                ll_education_experience.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyFriends.equals("me")) {
                rl_fellows_button_wrapper.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.privacyChoices.equals("me")) {
                rl_interests_button_wrapper.setVisibility(View.GONE);
                rl_venues_button_wrapper.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyWork.equals("me")) {
                tv_user_designation.setVisibility(View.GONE);
                tv_institution.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyEducation.equals("me")) {
                ll_education_experience.setVisibility(View.GONE);
            }
        }

        //target is not my friend
        else /*if (pojoProfileInfoResponseData.profile.connection.equals("add"))*/ {
            if (!pojoProfileInfoResponseData.profile.privacyConnectionRequest.equals("public")) {
                tv_add_fellow.setVisibility(View.GONE);
                tv_mutual_friends_count.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.privacyMessage.equals("public")) {
                tv_message.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.userPrivacyLocation.equals("public")) {
                tv_area.setVisibility(View.GONE);
            }
            if (pojoProfileInfoResponseData.profile.userPrivacyBasic.equals("public")) {
                ll_education_experience.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.userPrivacyFriends.equals("public")) {
                rl_fellows_button_wrapper.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.privacyChoices.equals("public")) {
                rl_interests_button_wrapper.setVisibility(View.GONE);
                rl_venues_button_wrapper.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.userPrivacyWork.equals("public")) {
                tv_user_designation.setVisibility(View.GONE);
                tv_institution.setVisibility(View.GONE);
            }
            if (!pojoProfileInfoResponseData.profile.userPrivacyEducation.equals("public")) {
                ll_education_experience.setVisibility(View.GONE);
            }
        }
    }


   /* private void hitCreateIndividualConversationApi(String user_id, String recipient) {
        Call<PojoCreateNewConversationResponse> call = apiService.createConversation(user_id, recipient);
        pb_loading_content.setVisibility(View.VISIBLE);
        cl_parent.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoCreateNewConversationResponse>() {
            @Override
            public void onResponse(Call<PojoCreateNewConversationResponse> call,
                                   Response<PojoCreateNewConversationResponse> response) {
                String message = response.body().message;
                if (response.body().data != null) {
                    Intent chatIntent = new Intent(ProfileInfoActivity.this, ChatActivity.class);
                    chatIntent.putExtra("cid", response.body().data);
                    startActivity(chatIntent);
                    pb_loading_content.setVisibility(View.GONE);
                    cl_parent.setVisibility(View.VISIBLE);
                } else {
                    commonFunctions.setToastMessage(ProfileInfoActivity.this, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    cl_parent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoCreateNewConversationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ProfileInfoActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
                cl_parent.setVisibility(View.VISIBLE);
            }
        });
    }*/

    private void hitGetUserWorkListApi(final String user_id, String targetUserId, String type) {
        Call<PojoUserWorkListResponse> call = apiService.getUserWorkList(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoUserWorkListResponse>() {
            @Override
            public void onResponse(Call<PojoUserWorkListResponse> call, Response<PojoUserWorkListResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    workHistoryList.clear();
                    for (int i = 0; i < response.body().data.size(); i++) {
                        if (workHistoryList.size() < 3) {
                            workHistoryList.add(response.body().data.get(i));
                        }
                    }
                    educationSmallListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PojoUserWorkListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitUpdateUserConnectionApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitAddFriendApi(String user_id, String target_id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.addFriendApi(user_id, target_id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
             /*   if (changeImageType.equals(AppKeys.COVER_IMG)) {
                    iv_cover_photo.setImageURI(resultUri);
                } else*/
                File imgFile = commonFunctions.getScaledDownImage(result.getUri().getPath());
                PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
                if (pojoLoginResponseData != null) {
                    pojoLoginResponseData.userPicture = pojoProfileInfoResponseData.profile.userPicture;
                    preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                }

                pojoProfileInfoResponseData.profile.userPicture = imgFile.getPath();
                preferenceUtils.saveUserProfileInfo(pojoProfileInfoResponseData);
                if (changeImageType.equals(AppKeys.PROFILE_IMG)) {
                    Glide.with(getApplicationContext())
                            .load(pojoProfileInfoResponseData.profile.userPicture)
                            .apply(new RequestOptions().override(256, 256))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .placeholder(R.drawable.blank_profile_male)
                            .into(iv_user_dp);


                    if (imageViewerDialog.isShowing() && iv_photo != null) {
                        Glide.with(getApplicationContext())
                                .load(pojoProfileInfoResponseData.profile.userPicture)
                                .apply(new RequestOptions().override(720, 720))
                                .apply(bitmapTransform(new CropCircleTransformation()))
                                .into(iv_photo);
                    }
                    hitUploadImageApi(user_id, imgFile);

                    // iv_user_dp.setImageURI(resultUri);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == UPDATE_PROFILE) {
            profileUpdated = true;
            hitGetUserProfileApi(user_id, targetUserId, "dashboard");
        }
       /* if (requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = commonFunctions.getRealPathFromUri(data.getData());
            VideoUploadService.uploadFile(ProfileInfoActivity.this, "121",
                    path,"123",AppKeys.VIDEO);
        }
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getData().getPath();
            VideoUploadService.uploadFile(ProfileInfoActivity.this, "121",
                    path,"123",AppKeys.FILE);
        }*/
    }


    public void hitUploadImageApi(final String user_id, File image) {

        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(image)), image);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", image.getName(), mFile);
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), user_id);

        Call<PojoNoDataResponse> call = apiService.updateProfileDp(fileToUpload, userIdBody);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.message() != null) {
                    commonFunctions.setToastMessage(getApplicationContext(), response.body().message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserProfileApi(user_id, targetUserId, "dashboard");

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (targetUserId.equals(user_id) && pojoProfileInfoResponseData != null) {
            getMenuInflater().inflate(R.menu.profile_info_menu, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.profile_info_settings:
                Intent settingsIntent = new Intent(ProfileInfoActivity.this, SettingsActivityNew.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }


}
