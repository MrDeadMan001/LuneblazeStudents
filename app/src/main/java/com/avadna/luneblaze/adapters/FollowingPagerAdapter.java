package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.FollowingInterestFragment;
import com.avadna.luneblaze.fragments.FollowingPeopleFragment;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.google.gson.Gson;

public class FollowingPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;


    public FollowingPagerAdapter(Context context, PojoUserSettingsResponseData pojoUserSettingsResponseData, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.pojoUserSettingsResponseData=pojoUserSettingsResponseData;
        gson=new Gson();
        jsonString=gson.toJson(pojoUserSettingsResponseData);
        bundle=new Bundle();
        bundle.putString("data",jsonString);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {


        if (position == 0) {
            FollowingInterestFragment followingInterestFragment = new FollowingInterestFragment();
            followingInterestFragment.setArguments(bundle);
            return followingInterestFragment;
        } else {
            FollowingPeopleFragment followingPeopleFragment = new FollowingPeopleFragment();
            followingPeopleFragment.setArguments(bundle);
            return followingPeopleFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }
}
