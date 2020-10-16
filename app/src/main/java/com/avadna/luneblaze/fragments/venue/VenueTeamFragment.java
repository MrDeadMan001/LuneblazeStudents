package com.avadna.luneblaze.fragments.venue;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.avadna.luneblaze.adapters.venue.VenueTeamAdapter;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueTeam;

import java.util.ArrayList;
import java.util.List;


public class VenueTeamFragment extends Fragment {

    RecyclerView rv_team_list;
    VenueTeamAdapter venueTeamAdapter;
    int listSize=5;
    PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    List<PojoVenueTeam> venueTeam;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_venue_team, container, false);
        initTeamList(baseLayout);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("params");
            if(!mParam1.equals("null")){
                Gson gson=new Gson();
                pojoGetVenueDetailsResponse = gson.fromJson(mParam1, PojoGetVenueDetailsResponse.class);
                venueTeam.addAll(pojoGetVenueDetailsResponse.data.teams);
            }
        }
        return baseLayout;
    }

    private void initTeamList(View baseLayout) {
        venueTeam=new ArrayList<>();
        rv_team_list=(RecyclerView)baseLayout.findViewById(R.id.rv_team_list);
        venueTeamAdapter=new VenueTeamAdapter(getActivity(),venueTeam);
        rv_team_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rv_team_list.setAdapter(venueTeamAdapter);

    }
}
