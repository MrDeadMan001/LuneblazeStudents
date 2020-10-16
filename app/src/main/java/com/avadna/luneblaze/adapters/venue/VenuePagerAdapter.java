package com.avadna.luneblaze.adapters.venue;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.gson.Gson;
import com.avadna.luneblaze.fragments.venue.VenueDetailsFragment;
import com.avadna.luneblaze.fragments.venue.VenueTeamFragment;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;

/**
 * Created by Sunny on 17-12-2017.
 */

public class VenuePagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public VenuePagerAdapter(Context context, FragmentManager fm, PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse) {
        super(fm);
        mContext = context;
        this.pojoGetVenueDetailsResponse=pojoGetVenueDetailsResponse;
        gson = new Gson();
        jsonString = gson.toJson(pojoGetVenueDetailsResponse);
        bundle = new Bundle();
        bundle.putString("params", jsonString);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            VenueDetailsFragment venueDetailsFragment = new VenueDetailsFragment();
            venueDetailsFragment.setArguments(bundle);
            return venueDetailsFragment;
        } else if (position == 1){
            AllSessionListFragmentNew allSessionListFragmentNew = new AllSessionListFragmentNew();
            bundle.putString(AppKeys.VENUE_ID,pojoGetVenueDetailsResponse.data.venueData.venueId);
            allSessionListFragmentNew.setArguments(bundle);
            return allSessionListFragmentNew;

        } else {
            VenueTeamFragment venueTeamFragment = new VenueTeamFragment();
            venueTeamFragment.setArguments(bundle);
            return venueTeamFragment;
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
