package com.avadna.luneblaze.activities.settingActivities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.SuggestedFollowPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFeedActivity extends AppBaseActivity {
    ActionBar actionBar;
    LinearLayout ll_interest_following_wrapper, ll_person_following_wrapper;
    TextView tv_num_person_following, tv_interest_num_following;
    View.OnClickListener onClickListener;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    ProgressBar pb_loading_content;
    CoordinatorLayout cl_parent;

    TabLayout tl_tabs;
    ViewPager vp_pager;
    SuggestedFollowPagerAdapter friendsPagerAdapter;
    int personCount = 0;
    int interestCount = 0;
    int apiCount = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_feed);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        hitGetFollowingListApi(user_id, "manage-feed");

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.manage_feed));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ManageFeedActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ManageFeedActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        ll_person_following_wrapper = (LinearLayout) findViewById(R.id.ll_person_following_wrapper);
        ll_interest_following_wrapper = (LinearLayout) findViewById(R.id.ll_interest_following_wrapper);
        tv_num_person_following = (TextView) findViewById(R.id.tv_num_person_following);
        tv_interest_num_following = (TextView) findViewById(R.id.tv_interest_num_following);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        setUpTabLayout();
        //  initSuggestedFeedList();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_person_following_wrapper: {
                        Intent followingListActivityIntent =
                                new Intent(ManageFeedActivity.this, FollowingListActivity.class);
                        followingListActivityIntent.putExtra("tabNum", 1);
                        startActivity(followingListActivityIntent);
                    }
                    return;

                    case R.id.ll_interest_following_wrapper: {
                        Intent followingListActivityIntent =
                                new Intent(ManageFeedActivity.this, FollowingListActivity.class);
                        followingListActivityIntent.putExtra("tabNum", 0);

                        startActivity(followingListActivityIntent);
                    }
                    return;
                }
            }
        };
    }

    private void setClickListener() {
        ll_interest_following_wrapper.setOnClickListener(onClickListener);
        ll_person_following_wrapper.setOnClickListener(onClickListener);

    }


    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.interests)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.people)));
        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (vp_pager != null) {
                    vp_pager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        friendsPagerAdapter = new SuggestedFollowPagerAdapter(ManageFeedActivity.this, getSupportFragmentManager());
        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
        vp_pager.setAdapter(friendsPagerAdapter);

        vp_pager.setOffscreenPageLimit(2);
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void hitGetFollowingListApi(String user_id, String type) {
        Call<PojoUserSettingsResponse> call = apiService.getUserSettings(user_id, type);
        call.enqueue(new Callback<PojoUserSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        personCount = response.body().data.followers.size();
                        interestCount = response.body().data.relatedInterestArr.size();
                        tv_num_person_following.setText("" + personCount);
                        tv_interest_num_following.setText("" + interestCount);
                    } else {
                        commonFunctions.setToastMessage(ManageFeedActivity.this, "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    cl_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ManageFeedActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //ll_parent.setVisibility(View.VISIBLE);
                openNoConnectionDialog(getString(R.string.manage_feed));
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog .setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetFollowingListApi(user_id, "manage-feed");
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();connectionErrorDialog.setTitle(title);
        }
        catch (Exception e){

        }


    }

    public void increaseCount(String type) {
        switch (type) {
            case AppKeys.USER:
                personCount++;
                tv_num_person_following.setText("" + personCount);
                break;

            case AppKeys.INTEREST:
                interestCount++;
                tv_interest_num_following.setText("" + interestCount);
                break;
        }
    }

    public void decreaseCount(String type) {
        switch (type) {
            case AppKeys.USER:
                personCount--;
                tv_num_person_following.setText("" + personCount);
                break;

            case AppKeys.INTEREST:
                interestCount--;
                tv_interest_num_following.setText("" + interestCount);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
