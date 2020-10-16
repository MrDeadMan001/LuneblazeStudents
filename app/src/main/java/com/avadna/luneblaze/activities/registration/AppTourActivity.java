package com.avadna.luneblaze.activities.registration;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.adapters.registration.AppTourImageSliderPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AppTourActivity extends AppBaseActivity {

    ViewPager vp_pager;
    TabLayout tl_pager_dots;
    int[] caricatures = new int[2];
    TextView tv_login, tv_register;
    PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_tour);
        preferenceUtils = new PreferenceUtils(this);
        initViews();
    }

    private void initViews() {

        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceUtils.setAppTourStatus(false);
                Intent loginIntent = new Intent(AppTourActivity.this,
                        LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceUtils.setAppTourStatus(false);
                Intent RegisterIntent = new Intent(AppTourActivity.this,
                        RegisterActivity1.class);
                startActivity(RegisterIntent);
                finish();
            }
        });

        caricatures[0] = R.drawable.tour_caricature_1;
        caricatures[1] = R.drawable.tour_caricature_2;

        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
        Type type = new TypeToken<List<PojoVenuePhoto>>() {
        }.getType();
        vp_pager.setAdapter(new AppTourImageSliderPagerAdapter(this, caricatures));

        tl_pager_dots = (TabLayout) findViewById(R.id.tl_pager_dots);
        tl_pager_dots.setupWithViewPager(vp_pager, true);

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 3) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
