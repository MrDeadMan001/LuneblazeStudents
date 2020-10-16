package com.avadna.luneblaze.fragments.profileinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.CreateArticleActivity;
import com.avadna.luneblaze.adapters.profileInfo.ProfileInfoArticleListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoArticlesWritten;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileInfoArticlesListFragment extends Fragment {
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String targetUserId;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_list;
    List<PojoArticlesWritten> articlesWritten;
    ProfileInfoArticleListAdapter profileInfoArticleListAdapter;
    StaggeredGridLayoutManager gridLayoutManager;
    ProgressBar pb_loading_content;
    TextView tv_no_questions;
    TextView tv_add_content;
    boolean listEndReached = false;

    int offset = 0;
    boolean isGetMoreApiCalled = false;
    static final int CREATE_ARTICLE_REQUEST_CODE = 41271;

    private BroadcastReceiver mBroadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetUserId = getArguments().getString(AppKeys.TARGET_USER_ID);
        }
        initBroadCastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_profile_info_questions_list, container, false);
        initViews(baseLayout);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        offset = 0;
        hitGetUserProfileApi(user_id, targetUserId, "articles", offset);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AppKeys.ARTICLE_DELETED));
        return baseLayout;
    }

    private void initBroadCastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(AppKeys.ARTICLE_DELETED)) {
                    if (articlesWritten != null && profileInfoArticleListAdapter != null) {
                        pb_loading_content.setVisibility(View.VISIBLE);
                        articlesWritten.clear();
                        profileInfoArticleListAdapter.notifyDataSetChanged();
                        offset=0;
                        hitGetUserProfileApi(user_id, targetUserId, "articles", offset);
                    }
                }
            }
        };
    }

    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitGetUserProfileApi(user_id, targetUserId, "articles", offset);
            }
        });

        initArticleList(baseLayout);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        tv_no_questions = (TextView) baseLayout.findViewById(R.id.tv_no_questions);
        tv_no_questions.setText(getString(R.string.no_articles_published));
        tv_no_questions.setVisibility(View.GONE);

        tv_add_content = (TextView) baseLayout.findViewById(R.id.tv_add_content);
        tv_add_content.setText(getString(R.string.create_article));
        tv_add_content.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_article_gray, 0, 0, 0);
        tv_add_content.setVisibility(View.GONE);
    }

    private void initArticleList(View baseLayout) {
        articlesWritten = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_list);
        profileInfoArticleListAdapter = new ProfileInfoArticleListAdapter(getActivity(), articlesWritten);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
 /*       gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                return 0;
            }
        });*/

        rv_list.setLayoutManager(gridLayoutManager);
        rv_list.setAdapter(profileInfoArticleListAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] into = new int[2];
                into = gridLayoutManager.findLastVisibleItemPositions(into);
                if (dy > 4 && !isGetMoreApiCalled && into[0] > articlesWritten.size() - 2) {
                    hitGetUserProfileApi(user_id, targetUserId, "articles", ++offset);
                }
            }
        });

    }

    private void hitGetUserProfileApi(final String user_id, final String targetUserId, String type, final int localOffset) {
        Call<PojoProfileTabResponse> call = apiService.getUserProfileTabData(user_id, targetUserId, type,
                String.valueOf(localOffset));
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isGetMoreApiCalled && !listEndReached) {
            isGetMoreApiCalled = true;
            profileInfoArticleListAdapter.showProgress(true);
            call.enqueue(new Callback<PojoProfileTabResponse>() {
                @Override
                public void onResponse(Call<PojoProfileTabResponse> call, Response<PojoProfileTabResponse> response) {
                    isGetMoreApiCalled = false;
                    if (response != null && response.body() != null && getActivity() != null) {
                        profileInfoArticleListAdapter.showProgress(false);
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (localOffset == 0) {
                                articlesWritten.clear();
                            }
                            if (response.body().data.articles != null && !response.body().data.articles.isEmpty()) {
                                articlesWritten.addAll(response.body().data.articles);
                                profileInfoArticleListAdapter.showProgress(true);
                                profileInfoArticleListAdapter.notifyDataSetChanged();
                                if (articlesWritten.size() < 15) {
                                    hitGetUserProfileApi(user_id, targetUserId, "articles", ++offset);
                                }
                            } else {
                                profileInfoArticleListAdapter.showProgress(false);
                                if (articlesWritten.size() > 8) {
                                    profileInfoArticleListAdapter.showNoMoreResults(true);
                                }
                                profileInfoArticleListAdapter.notifyDataSetChanged();
                                listEndReached = true;
                            }


                            if (articlesWritten.isEmpty()) {
                                profileInfoArticleListAdapter.showNoMoreResults(false);
                                profileInfoArticleListAdapter.showProgress(false);
                                profileInfoArticleListAdapter.notifyDataSetChanged();
                                tv_no_questions.setVisibility(View.VISIBLE);
                                if (user_id.equals(targetUserId)) {
                                    tv_add_content.setVisibility(View.VISIBLE);
                                    tv_add_content.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent articleIntent = new Intent(getActivity(), CreateArticleActivity.class);
                                            startActivityForResult(articleIntent, CREATE_ARTICLE_REQUEST_CODE);
                                        }
                                    });
                                }
                            } else {
                                tv_no_questions.setVisibility(View.GONE);
                                tv_add_content.setVisibility(View.GONE);
                            }

                        } else {
                            //if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    pb_loading_content.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<PojoProfileTabResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled = false;
                    profileInfoArticleListAdapter.showProgress(false);

                    if (articlesWritten.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ARTICLE_REQUEST_CODE) {
            offset = 0;
            hitGetUserProfileApi(user_id, targetUserId, "articles", offset);
            tv_no_questions.setVisibility(View.GONE);
            tv_add_content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }

}
