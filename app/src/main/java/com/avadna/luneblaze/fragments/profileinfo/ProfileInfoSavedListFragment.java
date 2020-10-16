package com.avadna.luneblaze.fragments.profileinfo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.newsfeed.NewsFeedAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileInfoSavedListFragment extends Fragment implements NewsFeedAdapter.NewsFeedAdapterCallback {
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String targetUserId;


    RecyclerView rv_list;
    NewsFeedAdapter newsFeedAdapter;
    List<PojoGetNewsFeedResponseData> newsFeedList;
    LinearLayoutManager linearLayoutManager;

    int offset=0;
    boolean isGetMoreApiCalled=false;

    ProgressBar pb_loading_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetUserId=getArguments().getString(AppKeys.TARGET_USER_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_profile_info_questions_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions=new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        offset=0;
        hitGetUserProfileApi(user_id,targetUserId, "saved_post",offset);
        return baseLayout;
    }


    private void initViews(View baseLayout) {
        pb_loading_content=(ProgressBar)baseLayout.findViewById(R.id.pb_loading_content);

        initPinnedList(baseLayout);
    }

    private void initPinnedList(View baseLayout) {
        newsFeedList = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_list);
        newsFeedAdapter = new NewsFeedAdapter(this, newsFeedList,null,false,false);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(newsFeedAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>4&&!isGetMoreApiCalled&&linearLayoutManager.findLastCompletelyVisibleItemPosition()>newsFeedList.size()-3){
                    hitGetUserProfileApi(user_id,targetUserId, "saved_post",++offset);

                }
            }
        });

    }

    private void hitGetUserProfileApi(String user_id, String targetUserId, String type, final int offset) {
        Call<PojoProfileTabResponse> call = apiService.getUserProfileTabData(user_id,targetUserId, type,
                String.valueOf(offset));
        if(!isGetMoreApiCalled){
            isGetMoreApiCalled=true;
            call.enqueue(new Callback<PojoProfileTabResponse>() {
                @Override
                public void onResponse(Call<PojoProfileTabResponse> call, Response<PojoProfileTabResponse> response) {
                    if (response != null && response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if(offset==0){
                                newsFeedList.clear();
                            }
                            for (int i = 0; i < response.body().data.profile.savedPosts.size(); i++) {
                                PojoGetNewsFeedResponseData currentItem = response.body().data.profile.savedPosts.get(i);
                                if(currentItem.postId!=null&&!currentItem.postId.isEmpty()){
                                    switch (currentItem.postType) {
                                        case "session_shared":
                                        case "session_attend":
                                        case "session":
                                            if(currentItem.origin.sessionsId==null
                                                    ||currentItem.origin.sessionsId.isEmpty()){
                                                continue;
                                            }
                                            break;


                                        case "article":
                                        case "article_shared":
                                        case "article_like":
                                        case "article_comment":
                                            if (currentItem.origin.articlesId == null
                                                    || currentItem.origin.articlesId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "question":
                                        case "question_shared":
                                            if (currentItem.origin.sessionsQaId == null
                                                    || currentItem.origin.sessionsQaId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "answer_shared":
                                            if (currentItem.origin.sessionsQaId == null
                                                    || currentItem.origin.sessionsQaId.isEmpty()
                                                    || currentItem.answer.sessionsQaId == null) {
                                                continue;
                                            }
                                            break;

                                        /*case "photos":
                                        case "":
                                            if (currentItem.postId == null
                                                    || currentItem.postId.isEmpty()) {
                                                continue;
                                            }
                                            break;*/

                                        case "post_like":
                                        case "post_comment":
                                        case "post_shared":
                                            if (currentItem.origin.postId == null
                                                    || currentItem.origin.postId.isEmpty()) {
                                                continue;
                                            }
                                            break;


                                      /*  case "poll":
                                            if(currentItem.postId==null||currentItem.postId.isEmpty()){
                                                continue;
                                            }
                                            break;*/

                                        case "interest_shared":
                                            if (currentItem.origin.interestId == null
                                                    || currentItem.origin.interestId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "venue_shared":
                                            if (currentItem.origin.venueId == null
                                                    || currentItem.origin.venueId.isEmpty()) {
                                                continue;
                                            }
                                            break;


                                        case "shared":
                                            if (currentItem.origin.postType.equals("poll")
                                                    || currentItem.origin.postType.equals("photos")
                                                    || currentItem.origin.postType.equals("")) {
                                                if(currentItem.origin.postId==null
                                                        ||currentItem.origin.postId.isEmpty()){
                                                    continue;
                                                }
                                            }
                                            break;

                                    }

                                    newsFeedList.add(currentItem);
                                }
                            }                            newsFeedAdapter.notifyDataSetChanged();

                        } else {
                            //if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled=false;
                }

                @Override
                public void onFailure(Call<PojoProfileTabResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled=false;
                }
            });
        }

    }


    @Override
    public void newsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type, String data, String meta_data) {

    }
}
