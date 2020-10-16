package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.UpcomingSessionAdapter;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoUpcomingSession;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class UpcomingSessionsListFragment extends Fragment {
    RecyclerView rv_upcoming_session_list;
    UpcomingSessionAdapter upcomingSessionAdapter;
    List<PojoUpcomingSession> upcomingSessions;
    PojoSessions sessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_upcoming_sessions_list, container, false);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("sessions");
            if (!mParam1.equals("null")) {
                Gson gson = new Gson();
                sessions = gson.fromJson(mParam1, PojoSessions.class);
                upcomingSessions =sessions.initiation;
            }
        }
        initViews(baseLayout);
        return baseLayout;
    }

    private void initViews(View baseLayout) {
        initUpcomingSessionList(baseLayout);
    }

    private void initUpcomingSessionList(View baseLayout) {
        upcomingSessions =new ArrayList<>();
        rv_upcoming_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_upcoming_session_list);
        rv_upcoming_session_list.setNestedScrollingEnabled(true);
        upcomingSessionAdapter=new UpcomingSessionAdapter(getActivity(), upcomingSessions, true);
        rv_upcoming_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_upcoming_session_list.setAdapter(upcomingSessionAdapter);
    }
}
