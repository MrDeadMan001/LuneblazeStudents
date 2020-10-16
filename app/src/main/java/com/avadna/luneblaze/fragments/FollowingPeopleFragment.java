package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.FollowingPeopleListAdapter;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class FollowingPeopleFragment extends Fragment {

    RecyclerView rv_following_list;
    FollowingPeopleListAdapter followingPeopleListAdapter;
    ApiInterface apiService;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;
    List<PojoUserData> followingList ;
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
            followingList=pojoUserSettingsResponseData.followers;
        }
        initApis();
        initViews(baseLayout);
        initClickListener();
        setClickListener();
      //  hitGetUserInterestsApi("15");

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
     //   hitSuggestFriendsListApi("0");
        rv_following_list = (RecyclerView) baseLayout.findViewById(R.id.rv_following_list);
        followingPeopleListAdapter = new FollowingPeopleListAdapter(1,followingList,getActivity());
        rv_following_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_following_list.setAdapter(followingPeopleListAdapter);
    }

/*    private void hitGetUserInterestsApi(String user_id) {
        Call<PojoUserInterestsListResponse> call = apiService.getUserInterests(user_id);
        call.enqueue(new Callback<PojoUserInterestsListResponse>() {
            @Override
            public void onResponse(Call<PojoUserInterestsListResponse> call, Response<PojoUserInterestsListResponse> response) {
                int status = 0;
                String message = response.body().message;
                if (response.body().data != null) {
                    followingList.addAll(response.body().data);

                    followingPeopleListAdapter.notifyDataSetChanged();

                }
                if (response.body().status != null) {
                    status = response.body().status;
                }
                if (status == 1) {

                }

            }

            @Override
            public void onFailure(Call<PojoUserInterestsListResponse> call, Throwable t) {
                // Log error here since request failed


            }
        });
    }*/

    private void hitSuggestFriendsListApi(String page) {

     /*   String user_id = "51";

        boolean allFiledsOk = !user_id.isEmpty() && !page.isEmpty();

        if (allFiledsOk) {
            Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id,page);
            call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
                @Override
                public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        receivedList.addAll(response.body().data);
                        suggestedFriendsAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(SuggestedFriendsActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }
                @Override
                public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(SuggestedFriendsActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }*/
    }

}
