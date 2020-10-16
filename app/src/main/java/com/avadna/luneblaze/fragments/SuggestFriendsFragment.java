package com.avadna.luneblaze.fragments;


import android.os.Bundle;
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
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SuggestFriendsFragment extends Fragment {
    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;
    RecyclerView rv_suggested_friends_list;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;
    List<PojoUserData> receivedUserList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_suggest_friends, container, false);
        commonFunctions = new CommonFunctions(getActivity());
        preferenceUtils = new PreferenceUtils(getActivity());
        initApis();
        initViews(baseLayout);
        initClickListener();
        setClickListener();
        return baseLayout;

    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitSuggestFriendsListApi(preferenceUtils.get_user_id(), "0");
            }
        });
        initSuggestedFriendsList(baseLayout);
    }

    private void initClickListener() {

    }

    private void setTextWatcher() {

    }

    private void setClickListener() {
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initSuggestedFriendsList(View baseLayout) {
        receivedUserList = new ArrayList<>();
        hitSuggestFriendsListApi(preferenceUtils.get_user_id(), "0");
        rv_suggested_friends_list = (RecyclerView) baseLayout.findViewById(R.id.rv_suggested_friends_list);
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(getActivity(),  receivedUserList, "ver");
        rv_suggested_friends_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_suggested_friends_list.setAdapter(suggestedFriendsAdapter);
    }

    private void hitSuggestFriendsListApi(String user_id, String page) {

        ll_no_connection_wrapper.setVisibility(View.GONE);
        boolean allFiledsOk = !user_id.isEmpty() && !page.isEmpty();

        if (allFiledsOk) {
            Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id, page);
            call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
                @Override
                public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        receivedUserList.addAll(response.body().data);
                        suggestedFriendsAdapter.notifyDataSetChanged();
                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);

                    if(receivedUserList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
    }

}
