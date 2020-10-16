package com.avadna.luneblaze.adapters.session;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.sessions.SessionDiscussionFragment;
import com.avadna.luneblaze.fragments.sessions.SessionPollFragment;
import com.avadna.luneblaze.fragments.sessions.VerifiedSessionDetailsFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;

public class AssignedSessionPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    Bundle bundle;
    String id;

    Fragment currentFragment;
    String discussion_id;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();



    public AssignedSessionPagerAdapter(Context context, FragmentManager fm, String id, String discussion_id) {
        super(fm);
        mContext = context;
        bundle = new Bundle();
        this.discussion_id=discussion_id;
        this.id = id;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        bundle.putString("id", id);

        if (position == 0) {
            VerifiedSessionDetailsFragment verifiedSessionDetailsFragment = new VerifiedSessionDetailsFragment();
            verifiedSessionDetailsFragment.setArguments(bundle);
            return verifiedSessionDetailsFragment;
        } else if (position == 1) {
            SessionPollFragment sessionPollFragment = new SessionPollFragment();
            bundle.putString("type", AppKeys.VERIFIED);
            sessionPollFragment.setArguments(bundle);
            return sessionPollFragment;
        } else {
            SessionDiscussionFragment sessionDiscussionFragment = new SessionDiscussionFragment();
            bundle.putString("type", AppKeys.VERIFIED);
            bundle.putString("discussion_id",discussion_id);

            sessionDiscussionFragment.setArguments(bundle);
            return sessionDiscussionFragment;
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.details);
            case 1:
                return mContext.getString(R.string.poll);
            case 2:
                return mContext.getString(R.string.discussion);
            default:
                return null;
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

