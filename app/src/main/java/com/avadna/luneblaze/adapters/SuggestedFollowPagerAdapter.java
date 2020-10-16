package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.avadna.luneblaze.fragments.SuggestFollowPersonFragment;
import com.avadna.luneblaze.fragments.SuggestedFollowInterestFragment;
import com.google.gson.Gson;

public class SuggestedFollowPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;

    public SuggestedFollowPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            SuggestedFollowInterestFragment suggestedFollowInterestFragment = new SuggestedFollowInterestFragment();
            return suggestedFollowInterestFragment;
        } else {
            SuggestFollowPersonFragment suggestFollowPersonFragment = new SuggestFollowPersonFragment();
            return suggestFollowPersonFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }
}
