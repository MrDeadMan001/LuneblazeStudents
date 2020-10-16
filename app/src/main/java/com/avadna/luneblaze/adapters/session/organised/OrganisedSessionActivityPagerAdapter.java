package com.avadna.luneblaze.adapters.session.organised;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.sessions.OrganisedSessionDetailsFragment;
import com.avadna.luneblaze.fragments.sessions.OrganisedSessionGalleryFragment;
import com.avadna.luneblaze.fragments.sessions.OrganisedSessionQuestionListFragment;

/**
 * Created by Sunny on 05-01-2018.
 */

public class OrganisedSessionActivityPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    Bundle bundle;
    String id;

    Fragment currentFragment;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public OrganisedSessionActivityPagerAdapter(Context context, FragmentManager fm, String id) {
        super(fm);
        mContext = context;
        bundle = new Bundle();
        this.id = id;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        bundle.putString("id", id);


        if (position == 0) {
            OrganisedSessionDetailsFragment organisedSessionDetailsFragment = new OrganisedSessionDetailsFragment();
            organisedSessionDetailsFragment.setArguments(bundle);
            return organisedSessionDetailsFragment;
        } else if (position == 1) {
            OrganisedSessionGalleryFragment organisedSessionGalleryFragment = new OrganisedSessionGalleryFragment();
            organisedSessionGalleryFragment.setArguments(bundle);
            return organisedSessionGalleryFragment;
        } else {
            OrganisedSessionQuestionListFragment sessionQuestionListFragment = new OrganisedSessionQuestionListFragment();
            sessionQuestionListFragment.setArguments(bundle);
            return sessionQuestionListFragment;
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

        return 3;

    }
}

