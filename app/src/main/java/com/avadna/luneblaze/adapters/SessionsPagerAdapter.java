package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.AssignedSessionsListFragment;
import com.avadna.luneblaze.fragments.OrganisedSessionsListFragment;
import com.avadna.luneblaze.fragments.UpcomingSessionsListFragment;
import com.avadna.luneblaze.fragments.VerifiedSessionsListFragment;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.google.gson.Gson;

public class SessionsPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;
    PojoSessions sessions;


    public SessionsPagerAdapter(Context context, FragmentManager fm, PojoSessions sessions) {
        super(fm);
        mContext = context;
        this.sessions=sessions;
        gson = new Gson();
        jsonString = gson.toJson(sessions);
        bundle = new Bundle();
        bundle.putString("sessions", jsonString);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            UpcomingSessionsListFragment upcomingSessionsListFragment = new UpcomingSessionsListFragment();
            upcomingSessionsListFragment.setArguments(bundle);
            return upcomingSessionsListFragment;
        } else if (position == 1) {
            AssignedSessionsListFragment assignedSessionsListFragment = new AssignedSessionsListFragment();
            assignedSessionsListFragment.setArguments(bundle);
            return assignedSessionsListFragment;
        }
        else if(position==2){
            VerifiedSessionsListFragment verifiedSessionsListFragment = new VerifiedSessionsListFragment();
            verifiedSessionsListFragment.setArguments(bundle);
            return verifiedSessionsListFragment;
        }
        else {
            OrganisedSessionsListFragment organisedSessionsListFragment = new OrganisedSessionsListFragment();
            organisedSessionsListFragment.setArguments(bundle);
            return organisedSessionsListFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }
}
