package com.avadna.luneblaze.activities.organisation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.PostCreationActivity;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.adapters.organisation.OrganisationProfileInfoPagerAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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

public class OrganisationProfileInfoActivity extends AppBaseActivity implements
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {
    private ActionBar actionBar;

    private ImageView iv_user_dp;
    private TextView tv_organisation_name;
    private TextView tv_company_type;
    private TextView tv_city;
    private TextView tv_followers;
    private TextView tv_about_company;
    private TextView tv_website;
    private TextView tv_follow, tv_campus_drive, tv_sponsor_session;
    private TextView tv_analytics;
    private ImageView iv_edit;

    private TabLayout tl_organisation_tabs;
    private ViewPager vp_organisation_activity_pager;
    private String organisation_id = "2";

    private RecyclerView rv_upvoters;
    private LinearLayoutManager upvoterListLayoutManager;
    private UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    private List<PojoUserData> upvoterList;
    private boolean isGetUpvotersApiCalled = false;
    private int upvoterListOffset = 0;

    private ProgressBar pb_loading_content;
    private CoordinatorLayout cl_parent;
    private AppBarLayout appBarLayout;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String owner_id = "1";
    private PojoGetOrganisationInfoResponseData pojoGetOrganisationInfoResponseData;
    private OrganisationProfileInfoPagerAdapter organisationProfileInfoPagerAdapter;
    private View.OnClickListener onClickListener;
    private  BroadcastReceiver mRegistrationBroadcastReceiver;
    private FloatingActionButton fb_create_post;
    private  int ellipsizedTextLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation_profile_info);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        owner_id = getIntent().getStringExtra(AppKeys.OWNER_ID);
        if (owner_id == null) {
            owner_id = "1";
        }
        if (organisation_id == null || organisation_id.isEmpty()) {
            organisation_id = "7";
        }

        setUpActionBar();
        initViews();
        setUpTabLayout();
        iniClickListener();
        setClickListener();
        hitGetOrganisationInfoApi(user_id, organisation_id, "home");
        initBroadCastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.ORGANISATION_UPDATED));
    }


    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.ORGANISATION_UPDATED)) {
                    Type jsonType = new TypeToken<PojoGetOrganisationInfoResponseData>() {
                    }.getType();
                    String orgDataStr = intent.getStringExtra(AppKeys.ORGANISATION_DATA);
                    pojoGetOrganisationInfoResponseData = new Gson().fromJson(orgDataStr, jsonType);
                    if (pojoGetOrganisationInfoResponseData != null
                            && pojoGetOrganisationInfoResponseData.id.equals(organisation_id)) {
                        setDataOnViews();
                    }

                    hitGetOrganisationInfoApi(user_id, organisation_id, "home");
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications

                }
            }
        };
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Organisation");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(OrganisationProfileInfoActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(OrganisationProfileInfoActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void hitGetOrganisationInfoApi(final String user_id, final String organisation_id, String type) {
        Call<PojoGetOrganisationInfoResponse> call = apiService.getOrganisationInfo(user_id, organisation_id,
                type, "0");
        call.enqueue(new Callback<PojoGetOrganisationInfoResponse>() {
            @Override
            public void onResponse(Call<PojoGetOrganisationInfoResponse> call,
                                   Response<PojoGetOrganisationInfoResponse> response) {
                if (response != null && response.body() != null) {
                    pb_loading_content.setVisibility(View.GONE);
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pojoGetOrganisationInfoResponseData = response.body().data;
                        cl_parent.setVisibility(View.VISIBLE);
                        pb_loading_content.setVisibility(View.GONE);
                        owner_id = pojoGetOrganisationInfoResponseData.userId;
                        setUpTabLayout();
                        setDataOnViews();

                    } else {
                        openNoConnectionDialog(getString(R.string.organisation));
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetOrganisationInfoResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.organisation));

                //  cl_parent.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog = new ConnectionErrorDialog(OrganisationProfileInfoActivity.this,
                    R.style.AppTheme, title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetOrganisationInfoApi(user_id, organisation_id, "home");
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


    private void iniClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_campus_drive:
                        Intent campusDriveIntent = new Intent(OrganisationProfileInfoActivity.this,
                                CreateCampusDriveActivity.class);
                        campusDriveIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                        startActivity(campusDriveIntent);
                        break;

                    case R.id.tv_sponsor_session:
                        Intent sponsorListIntent = new Intent(OrganisationProfileInfoActivity.this,
                                SponsorSessionsListActivity.class);
                        sponsorListIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                        startActivity(sponsorListIntent);
                        break;

                    case R.id.tv_website:
                        commonFunctions.urlLoader(pojoGetOrganisationInfoResponseData.website);
                       /* Intent websiteIntent = new Intent(OrganisationProfileInfoActivity.this,
                                WebViewActivity.class);
                        websiteIntent.putExtra("url", pojoGetOrganisationInfoResponseData.website);
                        startActivity(websiteIntent);*/
                        break;

                    case R.id.tv_follow:
                        if (pojoGetOrganisationInfoResponseData.isFollowing == 1) {
                            pojoGetOrganisationInfoResponseData.totalfollower--;
                            hitFollowOrganisationApi(user_id, organisation_id, "organizer_unfollow");
                            setStatusFollowing(false);
                        } else {
                            pojoGetOrganisationInfoResponseData.totalfollower++;
                            hitFollowOrganisationApi(user_id, organisation_id, "organizer_follow");
                            setStatusFollowing(true);
                        }
                        tv_followers.setText(pojoGetOrganisationInfoResponseData.totalfollower + " " + getString(R.string.followers));
                        break;

                    case R.id.fb_create_post: {
                        Intent postIntent = new Intent(OrganisationProfileInfoActivity.this,
                                PostCreationActivity.class);
                        Type type = new TypeToken<PojoGetOrganisationInfoResponseData>() {
                        }.getType();
                        String dataStr = new Gson().toJson(
                                pojoGetOrganisationInfoResponseData, type);
                        postIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                        postIntent.putExtra(AppKeys.ORGANISATION_DATA, dataStr);
                        startActivity(postIntent);
                    }

                    break;

                    case R.id.tv_followers:
                        openUpvoterListDialog("organisation_followers");
                        break;

                    case R.id.iv_user_dp:
                        openImageViewerDialog();
                        break;


                    case R.id.tv_analytics:
                        Intent analyticsIntent = new Intent(OrganisationProfileInfoActivity.this,
                                AnalyticsActivity.class);
                        analyticsIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                        startActivity(analyticsIntent);
                        break;

                    case R.id.iv_edit: {
                        Intent editIntent = new Intent(OrganisationProfileInfoActivity.this,
                                CreateCompanyPageActivity.class);
                        editIntent.putExtra(AppKeys.ORGANISATION_ID, pojoGetOrganisationInfoResponseData.id);

                        Type type = new TypeToken<PojoGetOrganisationInfoResponseData>() {
                        }.getType();
                        String data = new Gson().toJson(pojoGetOrganisationInfoResponseData, type);
                        editIntent.putExtra(AppKeys.ORGANISATION_DATA, data);
                        startActivity(editIntent);
                    }

                    break;


                }
            }
        };
    }

    private void setStatusFollowing(boolean status) {
        if (status) {
            pojoGetOrganisationInfoResponseData.isFollowing = 1;
            tv_follow.setBackgroundResource(R.drawable.light_blue_rounded_button);
            tv_follow.setText(getString(R.string.following));
            tv_follow.setTextColor(ContextCompat
                    .getColor(OrganisationProfileInfoActivity.this, R.color.white));
        } else {
            pojoGetOrganisationInfoResponseData.isFollowing = 0;
            tv_follow.setBackgroundResource(R.drawable.app_theme_dark_border);
            tv_follow.setText(getString(R.string.follow));
            tv_follow.setTextColor(ContextCompat
                    .getColor(OrganisationProfileInfoActivity.this, R.color.med_grey));
        }
    }


    private void setClickListener() {
        tv_campus_drive.setOnClickListener(onClickListener);
        tv_sponsor_session.setOnClickListener(onClickListener);
        tv_follow.setOnClickListener(onClickListener);
        tv_analytics.setOnClickListener(onClickListener);
        tv_website.setOnClickListener(onClickListener);
        fb_create_post.setOnClickListener(onClickListener);
        tv_followers.setOnClickListener(onClickListener);
        iv_user_dp.setOnClickListener(onClickListener);
        iv_edit.setOnClickListener(onClickListener);
    }


    private void setUpTabLayout() {
        tl_organisation_tabs = (TabLayout) findViewById(R.id.tl_organisation_tabs);
        tl_organisation_tabs.addTab(tl_organisation_tabs.newTab().setText(getString(R.string.posts)));
        tl_organisation_tabs.addTab(tl_organisation_tabs.newTab().setText(getString(R.string.boosted)));
        tl_organisation_tabs.addTab(tl_organisation_tabs.newTab().setText(getString(R.string.job_posts)));
        tl_organisation_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        boolean canSeeAdsTab = false;
        if (owner_id.equals(user_id)) {
            canSeeAdsTab = true;
        }

        organisationProfileInfoPagerAdapter = new OrganisationProfileInfoPagerAdapter(OrganisationProfileInfoActivity.this,
                getSupportFragmentManager(), organisation_id, canSeeAdsTab);
        vp_organisation_activity_pager = (ViewPager) findViewById(R.id.vp_organisation_activity_pager);
        vp_organisation_activity_pager.setAdapter(organisationProfileInfoPagerAdapter);
        vp_organisation_activity_pager.setOffscreenPageLimit(3);
        tl_organisation_tabs.setupWithViewPager(vp_organisation_activity_pager);

        tl_organisation_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_organisation_activity_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setDataOnViews() {
        if (pojoGetOrganisationInfoResponseData != null) {
            invalidateOptionsMenu();
            try {
                if (!pojoGetOrganisationInfoResponseData.logo.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(pojoGetOrganisationInfoResponseData.logo)
                            .apply(new RequestOptions().override(256, 256))
                            .placeholder(R.drawable.ic_company_placeholder)
                            .into(iv_user_dp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            tv_organisation_name.setText(pojoGetOrganisationInfoResponseData.name);
            tv_company_type.setText(pojoGetOrganisationInfoResponseData.typename);
            tv_city.setText(pojoGetOrganisationInfoResponseData.city);
            tv_about_company.setText(Html.fromHtml(pojoGetOrganisationInfoResponseData.description));
            ellipsizedTextLength = 0;
            addShowMoreButton();

            if (pojoGetOrganisationInfoResponseData.totalfollower == 0) {
                tv_followers.setVisibility(View.GONE);
            } else {
                tv_followers.setVisibility(View.VISIBLE);
                tv_followers.setText(pojoGetOrganisationInfoResponseData.totalfollower + " " + getString(R.string.followers));
            }

            if (pojoGetOrganisationInfoResponseData.website.isEmpty()) {
                tv_website.setVisibility(View.GONE);
            } else {
                tv_website.setVisibility(View.VISIBLE);
                tv_website.setText(pojoGetOrganisationInfoResponseData.website);
            }

            if (user_id.equals(pojoGetOrganisationInfoResponseData.userId)) {
                tv_follow.setVisibility(View.GONE);
                tv_campus_drive.setVisibility(View.VISIBLE);
                tv_sponsor_session.setVisibility(View.VISIBLE);
                if (pojoGetOrganisationInfoResponseData.currentplan.equals("0")) {
                    tv_analytics.setVisibility(View.GONE);
                } else {
                    tv_analytics.setVisibility(View.VISIBLE);
                }
            } else {
                tv_follow.setVisibility(View.VISIBLE);
                updateFollowStatus();
                tv_campus_drive.setVisibility(View.GONE);
                tv_sponsor_session.setVisibility(View.GONE);
                tv_analytics.setVisibility(View.GONE);
            }
        }
    }


    private void updateFollowStatus() {
        if (pojoGetOrganisationInfoResponseData != null) {
            if (pojoGetOrganisationInfoResponseData.isFollowing == 1) {
                setStatusFollowing(true);
            } else {
                setStatusFollowing(false);
            }
        }
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        fb_create_post = (FloatingActionButton) findViewById(R.id.fb_create_post);
        fb_create_post.setVisibility(View.GONE);
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        iv_user_dp = (ImageView) findViewById(R.id.iv_user_dp);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_edit.setVisibility(View.GONE);
        tv_organisation_name = (TextView) findViewById(R.id.tv_organisation_name);
        tv_company_type = (TextView) findViewById(R.id.tv_company_type);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        tv_follow.setVisibility(View.GONE);
        tv_analytics = (TextView) findViewById(R.id.tv_analytics);
        tv_analytics.setVisibility(View.GONE);
        tv_followers = (TextView) findViewById(R.id.tv_followers);
        tv_website = (TextView) findViewById(R.id.tv_website);
        tv_about_company = (TextView) findViewById(R.id.tv_about_company);

        tv_campus_drive = (TextView) findViewById(R.id.tv_campus_drive);
        tv_sponsor_session = (TextView) findViewById(R.id.tv_sponsor_session);
    }

    public void setFlotingButtonVisible(boolean visible) {
        if (visible && user_id.equals(owner_id)) {
            fb_create_post.show();
        } else {
            fb_create_post.hide();
        }
    }

    private void addShowMoreButton() {
        tv_about_company.setMaxLines(5);
        tv_about_company.invalidate();
        tv_about_company.setText(Html.fromHtml(pojoGetOrganisationInfoResponseData.description));
        tv_about_company.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_about_company.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int lines = tv_about_company.getLineCount();
                int ellCount = tv_about_company.getLayout().getEllipsisCount(lines - 1);
                if (ellCount > 0 || ellipsizedTextLength > 0) {
                    String visibleText = tv_about_company.getText().toString();

                    if (ellipsizedTextLength == 0) {
                        ellipsizedTextLength = visibleText.length() - ellCount - 9;
                    }

                    visibleText = visibleText.substring(0, ellipsizedTextLength);
                    int spanStart = visibleText.length() + 3;
                    visibleText = visibleText + "..." + getString(R.string.more);
                    tv_about_company.setText(visibleText);
                    SpannableString spannableString = new SpannableString(tv_about_company.getText());
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {

                            addShowLessButton();
                        }
                    };
                    spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_about_company.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_about_company.setText(spannableString, TextView.BufferType.SPANNABLE);
                }
            }
        });

    }


    private void addShowLessButton() {
        tv_about_company.setMaxLines(10000);
        String visibleText = Html.fromHtml(pojoGetOrganisationInfoResponseData.description).toString();
        tv_about_company.setText(visibleText);
        tv_about_company.invalidate();
        int spanStart = visibleText.length() + 1;
        visibleText = visibleText + " " + getString(R.string.show_less);
        tv_about_company.setText(visibleText);
        SpannableString spannableString = new SpannableString(visibleText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                addShowMoreButton();
            }
        };
        spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_about_company.setMovementMethod(LinkMovementMethod.getInstance());
        tv_about_company.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    private void openImageViewerDialog() {
        Dialog imageViewerDialog;
        ImageView iv_back, iv_edit;
        PhotoView iv_photo;
        imageViewerDialog = new MyCustomThemeDialog(OrganisationProfileInfoActivity.this,
                android.R.style.Theme_Black_NoTitleBar);
        imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
        iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
        iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);
        iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);

        iv_photo.setImageDrawable(iv_user_dp.getDrawable());
        if (pojoGetOrganisationInfoResponseData != null && !pojoGetOrganisationInfoResponseData.logo.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(pojoGetOrganisationInfoResponseData.logo)
                    .apply(new RequestOptions().override(720, 720))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(iv_photo);
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewerDialog.dismiss();
            }
        });

        // if (!user_id.equals(targetUserId)) {
        iv_edit.setVisibility(View.GONE);
       /* } else {
            iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestStoragePermission();
                }
            });
        }*/

        imageViewerDialog.show();


    }


    private void hitFollowOrganisationApi(String user_id, String id, String connect_type) {
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


    private void openUpvoterListDialog(String reaction) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.followers));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(this.getString(R.string.followers));

        rv_upvoters = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        upvoterListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        upvoterList = new ArrayList<>();
        rv_upvoters.setLayoutManager(upvoterListLayoutManager);
        upvotersAndAttendeesListAdapter = new UpvotersAndAttendeesListAdapter(this, upvoterList);

        rv_upvoters.setAdapter(upvotersAndAttendeesListAdapter);
        rv_upvoters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    if (!isGetUpvotersApiCalled && upvoterListLayoutManager.findLastVisibleItemPosition() > upvoterList.size() - 2) {
                        hitGetUpvotersListApi(user_id, organisation_id, reaction, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, organisation_id, reaction, upvoterListOffset);
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetOrganisationInfoResponseData != null) {
            if (pojoGetOrganisationInfoResponseData.userId.equals(user_id)) {
                fb_create_post.setVisibility(View.VISIBLE);
                iv_edit.setVisibility(View.VISIBLE);
            } else {
                getMenuInflater().inflate(R.menu.organisation_page_side_options, menu);
                fb_create_post.setVisibility(View.GONE);
            }
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

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_organisation", organisation_id);
                return true;

        }
        return super.onOptionsItemSelected(item);
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