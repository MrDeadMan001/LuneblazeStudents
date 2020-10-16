package com.avadna.luneblaze.activities;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.avadna.luneblaze.adapters.UserActivityPagerAdapter;
import com.avadna.luneblaze.R;

public class UserActivity extends AppBaseActivity {


    TabLayout tl_user_activities;
    ViewPager vp_activities;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        tl_user_activities=(TabLayout)findViewById(R.id.tl_user_activities);
        tl_user_activities.addTab(tl_user_activities.newTab().setText("All"));
        tl_user_activities.addTab(tl_user_activities.newTab().setText("Sessions"));
        tl_user_activities.addTab(tl_user_activities.newTab().setText("Questions"));
        tl_user_activities.addTab(tl_user_activities.newTab().setText("Posts"));
        tl_user_activities.addTab(tl_user_activities.newTab().setText("Articles"));

        tl_user_activities.setTabGravity(TabLayout.GRAVITY_FILL);
        tl_user_activities.setTabMode(TabLayout.MODE_SCROLLABLE);

        vp_activities=(ViewPager)findViewById(R.id.vp_activities);
        vp_activities.setAdapter(new UserActivityPagerAdapter(this,getSupportFragmentManager(),1));
        vp_activities.setOffscreenPageLimit(4);

        tl_user_activities.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_activities.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_activities.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    tl_user_activities.setScrollPosition(position,positionOffset,false);


            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tl_user_activities.getTabAt(position);
                if(tab!=null){
                    tab.select();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    
}
