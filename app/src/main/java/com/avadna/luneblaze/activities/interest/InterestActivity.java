package com.avadna.luneblaze.activities.interest;

import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;

import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.adapters.InterestListAdapter;
import com.avadna.luneblaze.adapters.InterestPagerAdapter;
import com.avadna.luneblaze.adapters.InviteListAdapter;
import com.avadna.luneblaze.adapters.MyFriendsListAdapter;
import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;
import com.avadna.luneblaze.fragments.interest.InterestQuestionListFragment;
import com.avadna.luneblaze.fragments.interest.InterestRelatedArticleListFragment;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponse;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestData;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestDetailsResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InterestActivity extends AppBaseActivity implements
        MyFriendsListAdapter.FriendListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback{

    CoordinatorLayout cl_parent;
    ProgressBar pb_loading_content;

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    AppBarLayout appBarLayout;
    boolean autoSelected = true;
    TabLayout tl_interest_tabs;
    ViewPager vp_interest_tab_pager;
    //ImageView iv_more_options;
    // Spinner sp_more_options;
    // List<String> more_options_list;
    // ArrayAdapter<String> spinnerAdapter;
    PojoInterestDetailsResponse pojoInterestDetailsResponse;

    List<PojoUserData> friendList;
    InviteListAdapter inviteListAdapter;

    List<PojoGetInterestListResponseDataListItem> receivedInterestList;
    InterestListAdapter interestListAdapter;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;


    ImageView iv_interest_image;
    TextView tv_interest_name, tv_interest_parent, tv_description, tv_follow, tv_num_followers;
    boolean wasDescriptionEllipsized = false;
    int ellipsizedTextLength;
    TextView tv_num_sessions, tv_num_articles, tv_num_questions;
    String interest_id = "";
    Dialog interestListDialog, inviteDialog;
    ProgressBar pb_loading_users;

    FloatingActionButton fab_filter;

    View.OnClickListener onClickListener;
    int iFollow = 0;
    int followerListOffset = 0;

    InterestPagerAdapter interestPagerAdapter;
    String notification_id;
    private boolean getFollowerApiCalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(InterestActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        setUpTabLayout();
        // setUpSpinner();
        interest_id = getIntent().getStringExtra("id");
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        hitGetInterestDetailsApi(preferenceUtils.get_user_id(), interest_id, "interest");

    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(InterestActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(InterestActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        fab_filter = (FloatingActionButton) findViewById(R.id.fab_filter);
        fab_filter.hide();
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        iv_interest_image = (ImageView) findViewById(R.id.iv_interest_image);
        tv_interest_name = (TextView) findViewById(R.id.tv_interest_name);
        tv_interest_parent = (TextView) findViewById(R.id.tv_interest_parent);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_follow = (TextView) findViewById(R.id.tv_follow);

        tv_num_followers = (TextView) findViewById(R.id.tv_num_followers);
        tv_num_sessions = (TextView) findViewById(R.id.tv_num_sessions);
        tv_num_articles = (TextView) findViewById(R.id.tv_num_articles);
        tv_num_questions = (TextView) findViewById(R.id.tv_num_questions);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // fab_filter.show();
                } else {
                    // fab_filter.hide();
                }
            }
        });

    }

    public void showFilter() {
        fab_filter.show();
    }

    public void hideFilter() {
        fab_filter.hide();
    }

    private void setClickListener() {
        tv_follow.setOnClickListener(onClickListener);
        tv_num_followers.setOnClickListener(onClickListener);
        tv_interest_parent.setOnClickListener(onClickListener);
        tv_num_sessions.setOnClickListener(onClickListener);
        tv_num_articles.setOnClickListener(onClickListener);
        tv_num_questions.setOnClickListener(onClickListener);
        fab_filter.setOnClickListener(onClickListener);
        iv_interest_image.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_follow:
                        if (iFollow == 0) {
                            hitFollowInterestApi(user_id, interest_id, "interest_follow");
                            tv_follow.setText(getString(R.string.following));
                            iFollow = 1;
                            pojoInterestDetailsResponse.data.totalAttendes++;
                            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                                    0, 0, 0);
                            tv_follow.setTextColor(ContextCompat.getColor(InterestActivity.this, R.color.app_theme_medium));

                        } else {
                            hitFollowInterestApi(user_id, interest_id, "interest_unfollow");
                            tv_follow.setText(getString(R.string.follow));
                            iFollow = 0;
                            pojoInterestDetailsResponse.data.totalAttendes--;
                            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                                    0, 0, 0);
                            tv_follow.setTextColor(ContextCompat.getColor(InterestActivity.this, R.color.med_grey));
                        }
                        tv_num_followers.setText(pojoInterestDetailsResponse.data.totalAttendes.toString()
                                + " " + getString(R.string.followers));

                        break;

                    case R.id.tv_num_followers:
                        openUpvoterListDialog("interestfollowers");
                    break;

                    case R.id.iv_interest_image:
                        openImageViewerDialog(pojoInterestDetailsResponse.data.interestData.image);
                        break;

                    case R.id.fab_filter:
                        int tabPos = vp_interest_tab_pager.getCurrentItem();
                        Fragment fragment = interestPagerAdapter.getRegisteredFragment(tabPos);

                        if (tabPos == 0) {
                            ((AllSessionListFragmentNew) fragment).openAllSessionFilter();
                         /*   int sessionTabPos = ((AllSessionListFragmentNew) fragment).getSelectedTabPosition();
                            switch (sessionTabPos) {
                                case 0:
                                    Intent filterVerified = new Intent(Config.OPEN_VERIFIED_SESSION_FILTER);
                                    filterVerified.putExtra(AppKeys.ACTIVITY_NAME, InterestActivity.this.getClass().getSimpleName());
                                    LocalBroadcastManager.getInstance(InterestActivity.this).sendBroadcast(filterVerified);
                                    break;
                                case 1:
                                    Intent filterAssigned = new Intent(Config.OPEN_ASSIGNED_SESSION_FILTER);
                                    filterAssigned.putExtra(AppKeys.ACTIVITY_NAME, InterestActivity.this.getClass().getSimpleName());
                                    LocalBroadcastManager.getInstance(InterestActivity.this).sendBroadcast(filterAssigned);
                                    break;

                                case 2:
                                    Intent filterOrganised = new Intent(Config.OPEN_ORGANISED_SESSION_FILTER);
                                    filterOrganised.putExtra(AppKeys.ACTIVITY_NAME, InterestActivity.this.getClass().getSimpleName());
                                    LocalBroadcastManager.getInstance(InterestActivity.this).sendBroadcast(filterOrganised);
                                    break;
                            }*/
                        } else if (tabPos == 1) {
                            ((InterestRelatedArticleListFragment) fragment).openFilterDialog();
                        } else if (tabPos == 2) {
                            ((InterestQuestionListFragment) fragment).openFilterDilaog();
                        }

                        break;

                    case R.id.tv_num_sessions:
                        vp_interest_tab_pager.setCurrentItem(0, true);
                        break;

                    case R.id.tv_num_articles:
                        vp_interest_tab_pager.setCurrentItem(1, true);
                        break;

                    case R.id.tv_num_questions:
                        vp_interest_tab_pager.setCurrentItem(2, true);
                        break;

                    case R.id.tv_share:
                        openShareSessionOnProfileDialog();
                        break;


                    case R.id.tv_interest_parent:
                        Intent interestIntent = new Intent(InterestActivity.this, InterestActivity.class);
                        interestIntent.putExtra("id", pojoInterestDetailsResponse.data.interestData.parentId);
                        startActivity(interestIntent);
                        break;
                }
            }
        };
    }

    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(InterestActivity.this, android.R.style.Theme_Black_NoTitleBar);
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

    private void openShareSessionOnProfileDialog() {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);
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
        View view = inflater.inflate(R.layout.share_interest_item, rl_item_holder, true);

        ImageView iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
        TextView tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
        TextView tv_interest_parent = (TextView) view.findViewById(R.id.tv_interest_parent);
        TextView tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
        TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
        TextView tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
        TextView tv_num_sessions = (TextView) view.findViewById(R.id.tv_num_sessions);
        TextView tv_num_articles = (TextView) view.findViewById(R.id.tv_num_articles);
        TextView tv_num_questions = (TextView) view.findViewById(R.id.tv_num_questions);

        Glide.with(getApplicationContext())
                .load(pojoInterestDetailsResponse.data.interestData.image)
                .apply(new RequestOptions().override(128, 128))
                .into(iv_cover_photo);

        tv_interest_name.setText(Html.fromHtml(pojoInterestDetailsResponse.data.interestData.text));
        tv_interest_parent.setText(pojoInterestDetailsResponse.data.interestData.parentInterest);
        //   tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoInterestDetailsResponse.data.interestData.addedOn));
        tv_description.setText(Html.fromHtml(pojoInterestDetailsResponse.data.interestData.description));
        tv_num_followers.setText("" + pojoInterestDetailsResponse.data.totalAttendes + " "
                + getString(R.string.followers));
        tv_num_sessions.setText("" + pojoInterestDetailsResponse.data.totalSessions);
        tv_num_articles.setText("" + pojoInterestDetailsResponse.data.totalArticles);
        tv_num_questions.setText("" + pojoInterestDetailsResponse.data.totalQues);

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, interest_id, "share_interest",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
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
                        hitGetUpvotersListApi(user_id, interest_id, reaction, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, interest_id, reaction, upvoterListOffset);
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


    private void setUpTabLayout() {
        tl_interest_tabs = (TabLayout) findViewById(R.id.tl_interest_tabs);
        tl_interest_tabs.addTab(tl_interest_tabs.newTab().setText("Sessions"));
        tl_interest_tabs.addTab(tl_interest_tabs.newTab().setText("Articles"));
        tl_interest_tabs.addTab(tl_interest_tabs.newTab().setText("Questions"));
        tl_interest_tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        vp_interest_tab_pager = (ViewPager) findViewById(R.id.vp_interest_tab_pager);
        tl_interest_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_interest_tab_pager.setCurrentItem(tab.getPosition());
                if (!autoSelected) {
                    appBarLayout.setExpanded(false, true);
                }
                autoSelected = false;
                showFilter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void hitGetInterestDetailsApi(String user_id, String interest_id, String type) {
        Call<PojoInterestDetailsResponse> call = apiService.getInterestDetails(user_id, interest_id,
                type, "upcoming");
        call.enqueue(new Callback<PojoInterestDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoInterestDetailsResponse> call, Response<PojoInterestDetailsResponse> response) {
                String message = "";
                if (response.body() != null && response.body().data != null) {
                    message = response.body().message;
                    pojoInterestDetailsResponse = response.body();
                    setDataOnViews();

                    interestPagerAdapter = new InterestPagerAdapter(InterestActivity.this,
                            getSupportFragmentManager(), pojoInterestDetailsResponse);

                    vp_interest_tab_pager.setAdapter(interestPagerAdapter);
                    vp_interest_tab_pager.setOffscreenPageLimit(3);
                    tl_interest_tabs.setupWithViewPager(vp_interest_tab_pager);

                 /*   vp_interest_tab_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            tl_interest_tabs.setScrollPosition(position, positionOffset, false);
                        }

                        @Override
                        public void onPageSelected(int position) {
                            TabLayout.Tab tab = tl_interest_tabs.getTabAt(position);
                            tab.select();
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });*/
                    cl_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<PojoInterestDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //cl_parent.setVisibility(View.VISIBLE);
                openNoConnectionDialog(getString(R.string.interest));
            }
        });

    }


    private void openNoConnectionDialog(String title) {

        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetInterestDetailsApi(preferenceUtils.get_user_id(), interest_id, "interest");
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
            e.printStackTrace();
        }

    }




    public void hitFollowInterestApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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

    private void setDataOnViews() {
        final PojoInterestData interestData = pojoInterestDetailsResponse.data.interestData;
        invalidateOptionsMenu();
        fab_filter.show();
        iFollow = pojoInterestDetailsResponse.data.iFollow;
        if (pojoInterestDetailsResponse.data.iFollow == 1) {
            tv_follow.setText(getString(R.string.following));
            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                    0, 0, 0);
            tv_follow.setTextColor(ContextCompat.getColor(InterestActivity.this, R.color.app_theme_medium));

        } else {
            tv_follow.setText(getString(R.string.follow));
            tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                    0, 0, 0);
            tv_follow.setTextColor(ContextCompat.getColor(InterestActivity.this, R.color.med_grey));

        }

        if (!interestData.image.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(interestData.image)
                    .into(iv_interest_image);
        }

        tv_interest_name.setText(interestData.text);
        tv_interest_parent.setText(interestData.parentInterest);
        tv_num_followers.setText(pojoInterestDetailsResponse.data.totalAttendes.toString() + " " + getString(R.string.followers));
        tv_num_articles.setText(pojoInterestDetailsResponse.data.totalArticles.toString());
        tv_num_questions.setText(pojoInterestDetailsResponse.data.totalQues.toString());
        tv_num_sessions.setText(pojoInterestDetailsResponse.data.totalSessions.toString());
        actionBar.setTitle(pojoInterestDetailsResponse.data.interestData.text);

        addShowMoreButton();


    }

    private void addShowMoreButton() {
        tv_description.setMaxLines(10);
        tv_description.invalidate();
        tv_description.setText(Html.fromHtml(pojoInterestDetailsResponse.data.interestData.description));
        tv_description.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_description.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int lines = tv_description.getLineCount();
                int ellCount = tv_description.getLayout().getEllipsisCount(lines - 1);
                if (ellCount > 0 || ellipsizedTextLength > 0) {
                    String visibleText = tv_description.getText().toString();

                    if (ellipsizedTextLength == 0) {
                        ellipsizedTextLength = visibleText.length() - ellCount - 9;
                    }

                    visibleText = visibleText.substring(0, ellipsizedTextLength);
                    int spanStart = visibleText.length() + 3;
                    visibleText = visibleText + "..." + getString(R.string.more);
                    tv_description.setText(visibleText);
                    SpannableString spannableString = new SpannableString(tv_description.getText());
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {

                            addShowLessButton();
                        }
                    };
                    spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_description.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_description.setText(spannableString, TextView.BufferType.SPANNABLE);
                }
            }
        });

    }


    private void addShowLessButton() {
        tv_description.setMaxLines(10000);
        String visibleText = Html.fromHtml(pojoInterestDetailsResponse.data.interestData.description).toString();
        tv_description.setText(visibleText);
        tv_description.invalidate();
        int spanStart = visibleText.length();
        visibleText = visibleText + getString(R.string.show_less);
        tv_description.setText(visibleText);
        SpannableString spannableString = new SpannableString(visibleText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                addShowMoreButton();
            }
        };
        spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_description.setMovementMethod(LinkMovementMethod.getInstance());
        tv_description.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoInterestDetailsResponse != null) {
            getMenuInflater().inflate(R.menu.interest_page_menu, menu);
            if (pojoInterestDetailsResponse.data.hasChildren.equals("No")) {
                menu.removeItem(R.id.interest_children);
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

            case R.id.interest_edit:
                final Dialog dialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);
                dialog.setContentView(R.layout.edit_content_dialog);
                final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
                TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
                TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
                tv_dialog_description.setText(pojoInterestDetailsResponse.data.interestData.text);

                tv_dialog_description.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_interest_follow_unfilled, 0, 0, 0);

                et_content.setHint(getString(R.string.enter_your_suggestion_here));
                int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                        getResources().getDisplayMetrics());
                et_content.setMinHeight(minHeight);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tv_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newData = et_content.getText().toString().trim();
                        if (newData.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            hitSuggestInterestCorrectionApi(user_id, interest_id,
                                    et_content.getText().toString().trim());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;

            case R.id.interest_children:
                openSubInterestDialog();
                break;

            case R.id.interest_share:
                shareTextUrl();
                break;

            case R.id.interest_share_on_timeline:
                openShareSessionOnProfileDialog();
                break;

            case R.id.interest_invite:
                Intent inviteIntent = new Intent(InterestActivity.this, InviteActivity.class);
                inviteIntent.putExtra("userType", "interest_invitation");
                inviteIntent.putExtra("id", interest_id);
                startActivity(inviteIntent);
                //  openInviteDialog();
                //  hitInviteFriendApi(user_id,"invite_interest","3",pojoInterestDetailsResponse.data.interestData.interestId);
                break;
            case R.id.interest_add_new:
                Intent createInterest = new Intent(InterestActivity.this, CreateInterestActivity.class);
                startActivity(createInterest);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void hitSuggestInterestCorrectionApi(String user_id, String interest_id, String remark) {
        Call<PojoNoDataResponse> call = apiService.suggestInterestCorrection(user_id, interest_id,
                remark);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
                //cl_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
            }
        });

    }

    private void openSubInterestDialog() {
        interestListDialog = new MyCustomThemeDialog(InterestActivity.this, R.style.NoTitleDialogTheme);
        interestListDialog.setContentView(R.layout.contact_list_dialog);
        interestListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tv_title = (TextView) interestListDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.sub_interests));
        pb_loading_users = (ProgressBar) interestListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.VISIBLE);
        ImageView iv_back = interestListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        RecyclerView rv_list;
        rv_list = (RecyclerView) interestListDialog.findViewById(R.id.rv_list);
        rv_list.setPadding(16, 32, 16, 32);
        rv_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        receivedInterestList = new ArrayList<>();
        interestListAdapter = new InterestListAdapter(InterestActivity.this, receivedInterestList);
        rv_list.setAdapter(interestListAdapter);
        hitGetInterestListApi(user_id, interest_id);
        interestListDialog.show();
    }

 /*   private void openInviteDialog() {

        inviteDialog = new MyCustomThemeDialog(InterestActivity.this);
        inviteDialog.setContentView(R.layout.contact_list_dialog);
        RecyclerView rv_list;

        rv_list=(RecyclerView)inviteDialog.findViewById(R.id.rv_list);
        rv_list.setPadding(16,32,16,32);
        rv_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        friendList=new ArrayList<>();
        inviteListAdapter=new InviteListAdapter(interest_id,friendList,InterestActivity.this, AppKeys.INVITE_INTEREST);
        rv_list.setAdapter(inviteListAdapter);
        hitGetFriendListApi(preferenceUtils.get_user_id());

        inviteDialog.show();
    }*/

    private void hitGetInterestListApi(String user_id, String parent_id) {
        Call<PojoGetInterestListResponse> call = apiService.getInterestListApi(user_id, parent_id);
        call.enqueue(new Callback<PojoGetInterestListResponse>() {
            @Override
            public void onResponse(Call<PojoGetInterestListResponse> call, Response<PojoGetInterestListResponse> response) {
                if (response.body().data != null) {
                    receivedInterestList.clear();
                    receivedInterestList.addAll(response.body().data.subinterest);
                    interestListAdapter.notifyDataSetChanged();
                    pb_loading_users.setVisibility(View.GONE);
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.no_sub_interest), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    interestListDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PojoGetInterestListResponse> call, Throwable t) {
                // Log error here since request failed
                interestListDialog.dismiss();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

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
                    message = response.body().message;

                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_interest_successfully),
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

    private void shareTextUrl() {
     /*   final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoInterestDetailsResponse.data.interestData.image)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                                                             DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(InterestActivity.this,
                                          AppKeys.SHARE_INTEREST, interest_id,
                                          pojoInterestDetailsResponse.data.interestData.text, bitmap);
                                  return false;
                              }
                          }
                ).submit();*/

        ShareContentOnMediaApps.shareContent(InterestActivity.this,
                AppKeys.SHARE_INTEREST, interest_id,
                pojoInterestDetailsResponse.data.interestData.text, null);
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
                if (response != null&&response.body()!=null) {
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
