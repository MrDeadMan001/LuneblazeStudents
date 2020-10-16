package com.avadna.luneblaze.fragments.profileinfo;

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
import com.avadna.luneblaze.adapters.profileInfo.ProfileInfoAnswerListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileAnswers;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileInfoAnswersListFragment extends Fragment {
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String targetUserId;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_list;
    List<PojoProfileAnswers> answersPostedList;
    ProfileInfoAnswerListAdapter profileInfoAnswerListAdapter;
    LinearLayoutManager linearLayoutManager;
    TextView tv_no_questions;

    int offset = 0;
    boolean isGetMoreApiCalled = false;

    ProgressBar pb_loading_content;
    private boolean listBottomReached = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetUserId = getArguments().getString(AppKeys.TARGET_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_profile_info_questions_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);

        offset = 0;
        hitGetUserProfileApi(user_id, targetUserId, "answers", offset);
        return baseLayout;
    }

    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset=0;
                hitGetUserProfileApi(user_id, targetUserId, "answers", offset);
            }
        });

        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        tv_no_questions = (TextView) baseLayout.findViewById(R.id.tv_no_questions);
        tv_no_questions.setText(getString(R.string.no_answers_posted));
        tv_no_questions.setVisibility(View.GONE);
        initAnswerList(baseLayout);
    }

    private void initAnswerList(View baseLayout) {
        answersPostedList = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_list);
        profileInfoAnswerListAdapter = new ProfileInfoAnswerListAdapter(getActivity(), answersPostedList);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(profileInfoAnswerListAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4 && !isGetMoreApiCalled && linearLayoutManager.findLastCompletelyVisibleItemPosition() > answersPostedList.size() - 3) {
                    hitGetUserProfileApi(user_id, targetUserId, "answers", ++offset);

                }
            }
        });

    }

    private void hitGetUserProfileApi(String user_id, String targetUserId, String type, final int offset) {
        Call<PojoProfileTabResponse> call = apiService.getUserProfileTabData(user_id, targetUserId, type,
                String.valueOf(offset));
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isGetMoreApiCalled && !listBottomReached) {
            isGetMoreApiCalled = true;
            profileInfoAnswerListAdapter.showProgressBar(true);

            call.enqueue(new Callback<PojoProfileTabResponse>() {
                @Override
                public void onResponse(Call<PojoProfileTabResponse> call, Response<PojoProfileTabResponse> response) {
                    if (response != null && response.body() != null&&getActivity()!=null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                answersPostedList.clear();
                            }
                            answersPostedList.addAll(response.body().data.answers);
                            if (response.body().data.answers.size() == 0) {
                                listBottomReached = true;
                                profileInfoAnswerListAdapter.showProgressBar(false);

                            }

                            if (answersPostedList.isEmpty()) {
                                tv_no_questions.setVisibility(View.VISIBLE);
                            }
                            profileInfoAnswerListAdapter.notifyDataSetChanged();

                        } else {
                            //if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    profileInfoAnswerListAdapter.showProgressBar(false);

                    pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoProfileTabResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetMoreApiCalled = false;
                    pb_loading_content.setVisibility(View.GONE);
                    profileInfoAnswerListAdapter.showProgressBar(false);

                    if(answersPostedList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }
}
