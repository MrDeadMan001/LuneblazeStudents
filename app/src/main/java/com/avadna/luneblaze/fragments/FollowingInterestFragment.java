package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.FollowingInterestListAdapter;
import com.avadna.luneblaze.pojo.pojoSettings.PojoRelatedInterestArr;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class FollowingInterestFragment extends Fragment {

    RecyclerView rv_following_list;
    FollowingInterestListAdapter followingInterestListAdapter;
    ApiInterface apiService;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;
    List<PojoRelatedInterestArr> relatedInterestArr ;
    Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_following_interest, container, false);
        gson=new Gson();
        String data=getArguments().getString("data");
        if(data!=null){
            pojoUserSettingsResponseData=
                    gson.fromJson(data, new TypeToken<PojoUserSettingsResponseData>() {}.getType());
            relatedInterestArr=pojoUserSettingsResponseData.relatedInterestArr;
        }
        initApis();
        initViews(baseLayout);
        initClickListener();
        setClickListener();
        return baseLayout;
    }

    private void initViews(View baseLayout) {
        initSuggestedFeedList(baseLayout);
    }

    private void initClickListener() {

    }


    private void setClickListener() {
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initSuggestedFeedList(View baseLayout) {
        rv_following_list = (RecyclerView) baseLayout.findViewById(R.id.rv_following_list);
        followingInterestListAdapter = new FollowingInterestListAdapter(1,relatedInterestArr,getActivity());
        rv_following_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_following_list.setAdapter(followingInterestListAdapter);
    }

}
