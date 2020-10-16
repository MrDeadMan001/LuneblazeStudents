package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.settingActivities.ManageFeedActivity;
import com.avadna.luneblaze.adapters.SuggestPersonFollowListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SuggestFollowPersonFragment extends Fragment implements
        SuggestPersonFollowListAdapter.SuggestedPersonFollowCallback {

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;
    RecyclerView rv_suggest_follow_list;
    SuggestPersonFollowListAdapter suggestPersonFollowListAdapter;
    LinearLayoutManager linearLayoutManager;
    List<PojoUserData> receivedUserList;

    //ProgressBar pb_loading_content;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    boolean isApiCalled = false;
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_suggest_follow_person, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        return baseLayout;
    }

    private void initViews(View baseLayout) {
       // pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 0;
                hitSuggestFriendsListApi(user_id, page);

            }
        });

        initSuggestedFeedList(baseLayout);
    }

    private void initSuggestedFeedList(View baseLayout) {
        receivedUserList = new ArrayList<>();
        rv_suggest_follow_list = (RecyclerView) baseLayout.findViewById(R.id.rv_suggest_follow_list);
        suggestPersonFollowListAdapter = new SuggestPersonFollowListAdapter(this, receivedUserList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_suggest_follow_list.setLayoutManager(linearLayoutManager);
        rv_suggest_follow_list.setAdapter(suggestPersonFollowListAdapter);

        rv_suggest_follow_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5 && linearLayoutManager.findLastVisibleItemPosition() > receivedUserList.size() - 2) {
                    hitSuggestFriendsListApi(user_id, ++page);
                }
            }
        });
        hitSuggestFriendsListApi(user_id, page);

    }

    private void hitSuggestFriendsListApi(String user_id, final int page) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id,
                String.valueOf(page));
        suggestPersonFollowListAdapter.setShowNoMoreResults(false);

        if (!isApiCalled) {
            isApiCalled = true;
            suggestPersonFollowListAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
                @Override
                public void onResponse(Call<PojoGetSuggestedFriendListResponse> call,
                                       Response<PojoGetSuggestedFriendListResponse> response) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                     //   pb_loading_content.setVisibility(View.GONE);
                        if (page == 0) {
                            receivedUserList.clear();
                        }
                        if(response.body().data.isEmpty()){
                            suggestPersonFollowListAdapter.setShowNoMoreResults(true);
                        }
                        receivedUserList.addAll(response.body().data);
                        suggestPersonFollowListAdapter.notifyDataSetChanged();
                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,
                                AppKeys.TOAST_DEBUG);
                    }
                    isApiCalled = false;
                    suggestPersonFollowListAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                    isApiCalled = false;
                    suggestPersonFollowListAdapter.setShowProgressBar(false);
                    if(receivedUserList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public void suggestedPersonMethod(int position, String type) {
        switch (type){
            case AppKeys.USER_FOLLOW:
                if(receivedUserList.get(position).connection==null
                        ||receivedUserList.get(position).connection.equals("follow")
                        ||receivedUserList.get(position).connection.equals("add")) {
                    hitFollowApi(user_id, receivedUserList.get(position).userId, "follow", position);
                    ((ManageFeedActivity)getActivity()).increaseCount(AppKeys.USER);
                    receivedUserList.get(position).connection="unfollow";

                }
                else {
                    hitFollowApi(user_id, receivedUserList.get(position).userId, "unfollow", position);
                    ((ManageFeedActivity)getActivity()).decreaseCount(AppKeys.USER);
                    receivedUserList.get(position).connection="follow";
                }
                suggestPersonFollowListAdapter.notifyDataSetChanged();
                break;
        }
    }

    public void hitFollowApi(String user_id, String friend_id, String connection_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
