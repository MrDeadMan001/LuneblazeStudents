package com.avadna.luneblaze.activities.settingActivities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.FollowingPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingListActivity extends AppBaseActivity {
    LinearLayout ll_parent;
    ProgressBar pb_loading_content;

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String targetUserId;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;

    TabLayout tl_tabs;
    ViewPager vp_pager;
    FollowingPagerAdapter followingPagerAdapter;

    int tabNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_following_list);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        targetUserId = getIntent().getStringExtra("target_user_id");
        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        tabNum = getIntent().getIntExtra("tabNum", 0);
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        hitGetFollowingListApi(targetUserId, "manage-feed");
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.following));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(FollowingListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(FollowingListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);

        setUpTabLayout();
    }

    private void initClickListener() {

    }


    private void setClickListener() {
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

        TabLayout.Tab tab = tl_tabs.getTabAt(tabNum);
        tab.select();


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

    private void hitGetFollowingListApi(String user_id, String type) {
        Call<PojoUserSettingsResponse> call = apiService.getUserSettings(user_id, type);
        call.enqueue(new Callback<PojoUserSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pojoUserSettingsResponseData = response.body().data;

                        followingPagerAdapter = new FollowingPagerAdapter(FollowingListActivity.this,
                                pojoUserSettingsResponseData, getSupportFragmentManager());
                        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
                        vp_pager.setAdapter(followingPagerAdapter);

                        tl_tabs.getTabAt(0).setText(getString(R.string.interests) + " ("
                                + pojoUserSettingsResponseData.relatedInterestArr.size() + ")");
                        tl_tabs.getTabAt(1).setText(getString(R.string.people) + " ("
                                + pojoUserSettingsResponseData.followers.size() + ")");

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

                        vp_pager.setCurrentItem(tabNum);

                    } else {
                        commonFunctions.setToastMessage(FollowingListActivity.this, "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    ll_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(FollowingListActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //ll_parent.setVisibility(View.VISIBLE);
                openNoConnectionDialog(getString(R.string.following));

            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog .setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetFollowingListApi(targetUserId, "manage-feed");
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
}
