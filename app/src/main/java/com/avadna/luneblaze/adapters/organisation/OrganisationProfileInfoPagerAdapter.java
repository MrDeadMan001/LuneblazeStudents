package com.avadna.luneblaze.adapters.organisation;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.organisation.OrganisationAdsListFragment;
import com.avadna.luneblaze.fragments.organisation.OrganisationCampusDriveFragment;
import com.avadna.luneblaze.fragments.organisation.OrganisationPostListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class OrganisationProfileInfoPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    String organisation_id = "";

    boolean canSeeAdsTab = false;

    public OrganisationProfileInfoPagerAdapter(Context context, FragmentManager fm, String organisation_id,boolean canSeeAdsTab) {
        super(fm);
        mContext = context;
        this.canSeeAdsTab=canSeeAdsTab;
        this.organisation_id = organisation_id;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(AppKeys.ORGANISATION_ID, organisation_id);
        if (canSeeAdsTab) {
            if (position == 0) {
                OrganisationPostListFragment organisationPostListFragment = new OrganisationPostListFragment();
                organisationPostListFragment.setArguments(bundle);
                return organisationPostListFragment;
            } else if (position == 1) {
                OrganisationCampusDriveFragment organisationCampusDriveFragment = new OrganisationCampusDriveFragment();
                organisationCampusDriveFragment.setArguments(bundle);
                return organisationCampusDriveFragment;

            } else {
                OrganisationAdsListFragment organisationAdsListFragment = new OrganisationAdsListFragment();
                organisationAdsListFragment.setArguments(bundle);
                return organisationAdsListFragment;
            }
        } else {
            if (position == 0) {
                OrganisationPostListFragment organisationPostListFragment = new OrganisationPostListFragment();
                organisationPostListFragment.setArguments(bundle);
                return organisationPostListFragment;
            } else {
                OrganisationCampusDriveFragment organisationCampusDriveFragment = new OrganisationCampusDriveFragment();
                organisationCampusDriveFragment.setArguments(bundle);
                return organisationCampusDriveFragment;
            }
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        if (canSeeAdsTab) {
            return 3;
        } else {
            return 2;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = mContext.getString(R.string.posts);
        } else if (position == 1) {
            title = mContext.getString(R.string.job_posts);
        } else if (position == 2) {
            title = mContext.getString(R.string.boosted);
        }
        return title;
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
