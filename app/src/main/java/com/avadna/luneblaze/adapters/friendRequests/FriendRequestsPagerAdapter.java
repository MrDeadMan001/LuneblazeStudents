package com.avadna.luneblaze.adapters.friendRequests;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.SearchAllResultsFragment;
import com.avadna.luneblaze.fragments.friendRequests.ReceivedFriendRequestsFragment;
import com.avadna.luneblaze.fragments.friendRequests.SentFriendRequestsFragment;

public class FriendRequestsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;


    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public FriendRequestsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        Bundle userBundle = new Bundle();


        switch (position) {

            case 0:
                SentFriendRequestsFragment sentFriendRequestsFragment = new SentFriendRequestsFragment();
                sentFriendRequestsFragment.setArguments(userBundle);
                return sentFriendRequestsFragment;

            case 1:
                ReceivedFriendRequestsFragment receivedFriendRequestsFragment = new ReceivedFriendRequestsFragment();
                receivedFriendRequestsFragment.setArguments(userBundle);
                return receivedFriendRequestsFragment;

            default:
                return new SearchAllResultsFragment();
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
