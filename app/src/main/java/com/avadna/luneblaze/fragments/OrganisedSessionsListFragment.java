package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionListAdapter;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoOrganisedSession;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class OrganisedSessionsListFragment extends Fragment {
    RecyclerView rv_organised_session_list;
    OrganisedSessionListAdapter organisedSessionListAdapter;
    List<PojoOrganisedSession> organisedSessions;
    PojoSessions sessions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_organised_sessions_list, container, false);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("sessions");
            if (!mParam1.equals("null")) {
                Gson gson = new Gson();
                sessions = gson.fromJson(mParam1, PojoSessions.class);
                organisedSessions=sessions.organized;
            }
        }
        initOrganisedSessionList(baseLayout);
        return baseLayout;
    }

    private void initOrganisedSessionList(View baseLayout) {
        organisedSessions=new ArrayList<>();
        rv_organised_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_organised_session_list);
        rv_organised_session_list.setNestedScrollingEnabled(true);
        organisedSessionListAdapter =new OrganisedSessionListAdapter(getActivity(),organisedSessions, true);
        rv_organised_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_organised_session_list.setAdapter(organisedSessionListAdapter);
    }
}
