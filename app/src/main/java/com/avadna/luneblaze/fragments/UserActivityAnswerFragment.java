package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.UserActivityAnswerAdapter;


public class UserActivityAnswerFragment extends Fragment {

    RecyclerView rv_item_list;
    UserActivityAnswerAdapter userActivityAnswerAdapter;
    int listSize=5;

    public UserActivityAnswerFragment() {
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
        View baseLayout=inflater.inflate(R.layout.fragment_user_activity_answer, container, false);
        rv_item_list=(RecyclerView)baseLayout.findViewById(R.id.rv_item_list);
        userActivityAnswerAdapter=new UserActivityAnswerAdapter(listSize);
        rv_item_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_item_list.setAdapter(userActivityAnswerAdapter);
        return baseLayout;
    }

}
