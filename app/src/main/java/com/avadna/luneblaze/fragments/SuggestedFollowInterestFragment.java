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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.settingActivities.ManageFeedActivity;
import com.avadna.luneblaze.adapters.SuggestedInterestsAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponse;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SuggestedFollowInterestFragment extends Fragment implements SuggestedInterestsAdapter.SuggestedInterestAdapterCallback {

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_suggest_follow_list;
    SuggestedInterestsAdapter suggestedInterestsAdapter;
    LinearLayoutManager interestListLayoutManager;
    List<PojoGetInterestListResponseDataListItem> interestList;

    ProgressBar pb_loading_content;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    boolean isGetMoreApiCalled = false;
    int offset = 0;

    List<Integer> randomList=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_suggested_follow_interest, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        offset = 0;
        hitSuggestedInterestApi(user_id, "interest", "0", "1", offset);
        return baseLayout;

    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                pb_loading_content.setVisibility(View.VISIBLE);
                hitSuggestedInterestApi(user_id, "interest", "0", "1", offset);
            }
        });

        pb_loading_content=(ProgressBar)baseLayout.findViewById(R.id.pb_loading_content);
       // pb_loading_content.setVisibility(View.GONE);
        initSuggestedFeedList(baseLayout);
    }

    private void initSuggestedFeedList(View baseLayout) {
        interestList = new ArrayList<>();
        rv_suggest_follow_list = (RecyclerView) baseLayout.findViewById(R.id.rv_suggest_follow_list);
        rv_suggest_follow_list.setVisibility(View.GONE);
        suggestedInterestsAdapter = new SuggestedInterestsAdapter(this, interestList);
        interestListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_suggest_follow_list.setLayoutManager(interestListLayoutManager);
        rv_suggest_follow_list.setAdapter(suggestedInterestsAdapter);
        suggestedInterestsAdapter.setShowNoMoreResults(false);
        rv_suggest_follow_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5 && interestListLayoutManager.findLastVisibleItemPosition() > interestList.size() - 2) {
                    int ranNum=generateRandomNumber();
                    while(isRandomPresent(ranNum)){
                        ranNum=generateRandomNumber();
                    }
                    randomList.add(ranNum);
                    hitSuggestedInterestApi(user_id, "interest", String.valueOf(ranNum), "1", ++offset);
                }
            }
        });
    }

    public int generateRandomNumber() {
        int randomNumber;
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < 2; i++) {
            int number = secureRandom.nextInt(9);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }

        randomNumber = Integer.parseInt(s);

        return randomNumber;
    }

    public boolean isRandomPresent(int num){
        boolean isPresent=false;
        for(int i=0;i<randomList.size();i++){
            if(randomList.get(i)==num){
                isPresent=true;
                break;
            }
        }
        return isPresent;
    }

    private void hitSuggestedInterestApi(final String user_id, String type, String parent_id, String interest_id,
                                         final int offset) {

        ll_no_connection_wrapper.setVisibility(View.GONE);
        if (!isGetMoreApiCalled) {
            isGetMoreApiCalled = true;
            suggestedInterestsAdapter.setShowProgressBar(true);
            Call<PojoGetInterestListResponse> call = apiService.getInterestListApi(user_id, parent_id);
            call.enqueue(new Callback<PojoGetInterestListResponse>() {
                @Override
                public void onResponse(Call<PojoGetInterestListResponse> call, Response<PojoGetInterestListResponse> response) {
                    isGetMoreApiCalled = false;
                    String message = response.body().message;
                    if (response.body().data != null) {
                     //   pb_loading_content.setVisibility(View.GONE);
                       /* if (offset == 0) {
                            interestList.clear();
                        }*/
                        response.body().data.subinterest=removeAlreadyFollowed(response.body().data.subinterest);
                        interestList.addAll(response.body().data.subinterest);
                        suggestedInterestsAdapter.notifyDataSetChanged();

                        if(interestList.size()>6){
                            pb_loading_content.setVisibility(View.GONE);
                            rv_suggest_follow_list.setVisibility(View.VISIBLE);
                        }

                        if(response.body().data.subinterest.isEmpty()||interestList.size()<10){
                            int ranNum=generateRandomNumber();
                            while(isRandomPresent(ranNum)){
                                ranNum=generateRandomNumber();
                            }
                            randomList.add(ranNum);
                            hitSuggestedInterestApi(user_id, "interest", String.valueOf(ranNum), "1", offset);
                        }
                    } else {
                        if(getActivity()!=null)if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                    suggestedInterestsAdapter.setShowProgressBar(false);

                }

                @Override
                public void onFailure(Call<PojoGetInterestListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetMoreApiCalled = false;
                    pb_loading_content.setVisibility(View.GONE);
                    suggestedInterestsAdapter.setShowProgressBar(false);
                    if(interestList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
    }

    private List<PojoGetInterestListResponseDataListItem> removeAlreadyFollowed(List<PojoGetInterestListResponseDataListItem> data) {
        List<PojoGetInterestListResponseDataListItem> temp=new ArrayList<>();
        if(data!=null){
            for(int i=0;i<data.size();i++){
                if(data.get(i).iFollowRelated!=null&&data.get(i).iFollowRelated==0){
                    temp.add(data.get(i));
                }
            }
        }
        return temp;
    }


    @Override
    public void onSuggestedInterestItemClick(int position, String type) {
        switch (type) {
            case AppKeys.INTEREST_FOLLOW:
                if(interestList.get(position).iFollowRelated==null||interestList.get(position).iFollowRelated==0){
                    hitFollowInterestApi(user_id, interestList.get(position).interestId, "interest_follow", position);
                    suggestedInterestsAdapter.notifyDataSetChanged();
                    ((ManageFeedActivity)getActivity()).increaseCount(AppKeys.INTEREST);
                    interestList.get(position).iFollowRelated=1;
                }
                else {
                    hitFollowInterestApi(user_id, interestList.get(position).interestId, "interest_unfollow", position);
                    suggestedInterestsAdapter.notifyDataSetChanged();
                    ((ManageFeedActivity)getActivity()).decreaseCount(AppKeys.INTEREST);
                    interestList.get(position).iFollowRelated=0;
                }
                suggestedInterestsAdapter.notifyDataSetChanged();
                break;
        }
    }

    public void hitFollowInterestApi(String user_id, String friend_id, String connection_type, final int position) {
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
