package com.avadna.luneblaze.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.home.AllMessageListFragment;
import com.avadna.luneblaze.fragments.home.HomeFriendsFragment;
import com.avadna.luneblaze.fragments.home.NewsFeedFragment;
import com.avadna.luneblaze.fragments.home.NotificationsListFragment;
import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;


public class MainActivityAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public MainActivityAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NotificationsListFragment notificationsListFragment = new NotificationsListFragment();
                return notificationsListFragment;
            case 1:
                AllMessageListFragment allMessageListFragment = new AllMessageListFragment();
                return allMessageListFragment;
            case 2:
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                return newsFeedFragment;
            case 3:
                AllSessionListFragmentNew allSessionsListFragmentOld = new AllSessionListFragmentNew();
                return allSessionsListFragmentOld;
            case 4:
                HomeFriendsFragment homeFriendsFragment = new HomeFriendsFragment();
                return homeFriendsFragment;
            default:
                HomeFriendsFragment homeFriendsFragment1 = new HomeFriendsFragment();
                return homeFriendsFragment1;

        }

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

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 5;
    }
}

