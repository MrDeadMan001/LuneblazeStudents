package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.AssignedSessionAdapter;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class AssignedSessionsListFragment extends Fragment {

    RecyclerView rv_assigned_session_list;
    AssignedSessionAdapter assignedSessionAdapter;
    List<PojoAssignedSessions> assignedSessions;
    PojoSessions sessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_assigned_sessions_list, container, false);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("sessions");
            if (!mParam1.equals("null")) {
                Gson gson = new Gson();
                sessions = gson.fromJson(mParam1, PojoSessions.class);
                assignedSessions=sessions.assigned;
            }
        }
        initAssignedSessionList(baseLayout);
        return baseLayout;
    }

    private void initAssignedSessionList(View baseLayout) {
        assignedSessions=new ArrayList<>();
        rv_assigned_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_assigned_session_list);
        rv_assigned_session_list.setNestedScrollingEnabled(true);
        assignedSessionAdapter = new AssignedSessionAdapter(getActivity(),assignedSessions, true);
        rv_assigned_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_assigned_session_list.setAdapter(assignedSessionAdapter);
    }
}
