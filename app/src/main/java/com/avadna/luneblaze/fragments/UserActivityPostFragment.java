package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.UserActivityPostAdapter;


public class UserActivityPostFragment extends Fragment {

    RecyclerView rv_item_list;
    UserActivityPostAdapter userActivityPostAdapter;
    int listSize=5;

    public UserActivityPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_user_activity_post, container, false);
        rv_item_list=(RecyclerView)baseLayout.findViewById(R.id.rv_item_list);
        userActivityPostAdapter=new UserActivityPostAdapter(listSize,1);
        rv_item_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_item_list.setAdapter(userActivityPostAdapter);
        return baseLayout;
    }

}
