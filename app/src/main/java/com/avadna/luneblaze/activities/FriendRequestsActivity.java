package com.avadna.luneblaze.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.friendRequests.FriendRequestsPagerAdapter;

public class FriendRequestsActivity extends AppBaseActivity {

    TabLayout tl_tabs;
    ViewPager vp_pager;
    FriendRequestsPagerAdapter friendRequestsPagerAdapter;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        setUpActionBar();

        setUpTabLayout();
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.requests));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(FriendRequestsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(FriendRequestsActivity.this, R.color.status_bar_color));
            }
            actionBar.setElevation(0);
        }
    }

    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.sent)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.received)));

        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);


        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                vp_pager.setCurrentItem(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
        friendRequestsPagerAdapter = new FriendRequestsPagerAdapter(FriendRequestsActivity.this,
                getSupportFragmentManager());

        vp_pager.setAdapter(friendRequestsPagerAdapter);
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
