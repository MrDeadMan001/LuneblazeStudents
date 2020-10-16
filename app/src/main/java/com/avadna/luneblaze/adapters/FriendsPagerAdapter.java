package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.home.FriendRequestsFragment;
import com.avadna.luneblaze.fragments.MyFriendsFragment;

import com.avadna.luneblaze.fragments.SuggestFriendsFragment;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.google.gson.Gson;

/**
 * Created by Sunny on 18-03-2018.
 */

public class FriendsPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;


    public FriendsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        bundle=new Bundle();
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            MyFriendsFragment myFriendsFragment = new MyFriendsFragment();
            return myFriendsFragment;
        } else if (position == 1) {
            FriendRequestsFragment friendRequestsFragment = new FriendRequestsFragment();
            return friendRequestsFragment;
        } else {
            SuggestFriendsFragment suggestFriendsFragment = new SuggestFriendsFragment();
            return suggestFriendsFragment;

        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }
}
