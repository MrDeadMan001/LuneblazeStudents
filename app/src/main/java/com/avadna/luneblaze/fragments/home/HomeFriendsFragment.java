package com.avadna.luneblaze.fragments.home;

import android.content.Intent;
import android.os.Bundle;

import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.FriendsPagerAdapter;


public class HomeFriendsFragment extends Fragment {

    private ActionBar actionBar;
    private TabLayout tl_tabs;
    private ViewPager vp_pager;
    private FriendsPagerAdapter friendsPagerAdapter;
    private PreferenceUtils preferenceUtils;
    private String user_id;
    private LinearLayout ll_content_wrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_home_friends, container, false);
        preferenceUtils = new PreferenceUtils(getActivity());
        user_id = preferenceUtils.get_user_id();
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            showGuestLogin(baseLayout);
        } else {
            initViews(baseLayout);
        }
        return baseLayout;
    }

    private void showGuestLogin(View baseLayout) {
        RelativeLayout rl_guest_login_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_guest_login_wrapper);
        rl_guest_login_wrapper.setVisibility(View.VISIBLE);
        TextView tv_login_button = (TextView) baseLayout.findViewById(R.id.tv_login_button);
        tv_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }

    private void initViews(View baseLayout) {
        setUpTabLayout(baseLayout);
    }

    private void setUpTabLayout(View baseLayout) {
        ll_content_wrapper=(LinearLayout)baseLayout.findViewById(R.id.ll_content_wrapper);
        ll_content_wrapper.setVisibility(View.VISIBLE);
        tl_tabs = (TabLayout) baseLayout.findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.my_fellows)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.requests)));
        // tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.suggested)));
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

        friendsPagerAdapter = new FriendsPagerAdapter(getActivity(), getChildFragmentManager());
        vp_pager = (ViewPager) baseLayout.findViewById(R.id.vp_pager);
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

        vp_pager.setCurrentItem(1);
    }

    public void setTabCount(int tabNum, int counter) {
        if (tabNum == 0) {
            tl_tabs.getTabAt(tabNum).setText(getString(R.string.my_fellows) + " (" + counter + ")");
        } else if (tabNum == 1) {
            tl_tabs.getTabAt(tabNum).setText(getString(R.string.requests) + " (" + counter + ")");

        }
    }
}
