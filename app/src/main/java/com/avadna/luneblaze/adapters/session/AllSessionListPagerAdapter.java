package com.avadna.luneblaze.adapters.session;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.SearchAllResultsFragment;
import com.avadna.luneblaze.fragments.home.sessionslist.AllAssignedSessionListFragment;
import com.avadna.luneblaze.fragments.home.sessionslist.AllOrganisedSessionListFragment;
import com.avadna.luneblaze.fragments.home.sessionslist.AllVerifiedSessionsListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;

public class AllSessionListPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    AllVerifiedSessionsListFragment allVerifiedSessionsListFragment;
    AllAssignedSessionListFragment allAssignedSessionListFragment;
    AllOrganisedSessionListFragment allOrganisedSessionListFragment;
    String fixed_interest_id;
    Bundle sessionBundle;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public AllSessionListPagerAdapter(Context context, FragmentManager fm, String fixed_interest_id,
                                      String fixed_venue_id) {
        super(fm);
        mContext = context;
        this.fixed_interest_id = fixed_interest_id;
        sessionBundle = new Bundle();
        sessionBundle.putString(AppKeys.INTEREST_ID, fixed_interest_id);
        sessionBundle.putString(AppKeys.VENUE_ID, fixed_venue_id);

    }


    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                allVerifiedSessionsListFragment = new AllVerifiedSessionsListFragment();
                allVerifiedSessionsListFragment.setArguments(sessionBundle);
                return allVerifiedSessionsListFragment;

            case 1:
                allAssignedSessionListFragment = new AllAssignedSessionListFragment();
                allAssignedSessionListFragment.setArguments(sessionBundle);
                return allAssignedSessionListFragment;

            case 2:
                allOrganisedSessionListFragment = new AllOrganisedSessionListFragment();
                allOrganisedSessionListFragment.setArguments(sessionBundle);
                return allOrganisedSessionListFragment;

            default:
                return new SearchAllResultsFragment();
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;

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
