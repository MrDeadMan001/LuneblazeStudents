package com.avadna.luneblaze.fragments.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.adapters.search.InterestSearchResultListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoInterestSession;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InterestSearchListFragment extends Fragment implements
        InterestSearchResultListAdapter.InterestSearchResultAdapterCallback {
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_interest_list;
    List<PojoInterestSession> interestList;
    InterestSearchResultListAdapter interestSearchResultListAdapter;
    LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    int offset = 0;
    String query = "";
    int position;
    boolean isSearchApiCalled = false;
    private boolean loadedForEmptyString = false;

    public InterestSearchListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (getArguments() != null) {
            Type type = new TypeToken< List<PojoInterestSession>>() {}.getType();
            String str=getArguments().getString("interest");
            if(str!=null&&str.isEmpty()){
                interestList=new ArrayList<>();
            }
            else {
                interestList= new Gson().fromJson(str, type);
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View baseLayout = inflater.inflate(R.layout.fragment_interest_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this.getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SEARCH_STRING_UPDATE));
        query = getArguments().getString("query", "");
        position = getArguments().getInt("position");

        if (position == 6) {
            offset = 0;
            hitSearchInterestApi(user_id, query, offset, "interests");
        }

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
                hitSearchInterestApi(user_id, query, offset, "interests");

            }
        });
        initInterestList(baseLayout);
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.SEARCH_STRING_UPDATE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    /*int pos = ((SearchActivity) getActivity()).getSelectedTab();
                    if (pos == 5) {
                        query = intent.getStringExtra("query");
                        score = 0;
                        hitSearchInterestApi(user_id, query, score, "interests");
                    }*/
                }
            }
        };

    }

    public void updateSearchString(String str) {
        if (str.isEmpty() && !loadedForEmptyString) {
            loadedForEmptyString = true;
            query = str;
            offset = 0;
            hitSearchInterestApi(user_id, query, offset, "interests");
        } else if (!query.equals(str)|| interestList.isEmpty()) {
            query = str;
            offset = 0;
            hitSearchInterestApi(user_id, query, offset, "interests");
        }
    }

    private void initInterestList(View baseLayout) {
        interestList = new ArrayList<>();
        rv_interest_list = (RecyclerView) baseLayout.findViewById(R.id.rv_interest_list);
        interestSearchResultListAdapter = new InterestSearchResultListAdapter(this, interestList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_interest_list.setLayoutManager(linearLayoutManager);
        rv_interest_list.setAdapter(interestSearchResultListAdapter);
        interestSearchResultListAdapter.setShowProgressBar(false);
        rv_interest_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (linearLayoutManager.findLastVisibleItemPosition() > interestList.size() - 2) {
                        hitSearchInterestApi(user_id, query, ++offset, "interests");
                    }
                    ((SearchActivity) getActivity()).closeKeyboard();

                }
            }
        });
    }

    private void hitSearchInterestApi(final String user_id, final String query, final int localOffset, String type) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        if (localOffset == 0) {
            interestSearchResultListAdapter.setShowNoMoreResults(false);
            interestList.clear();
            interestSearchResultListAdapter.notifyDataSetChanged();
        }
        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            interestSearchResultListAdapter.setShowProgressBar(true);

            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null) {
                        PojoSearchResultResponseData data=response.body().data;
                        SearchActivity searchActivity = (SearchActivity) getActivity();
                        if (searchActivity != null) {
                            searchActivity.updateTabCount(data.usersCount, data.sessionsCount,
                                    data.postsCount, data.articlesCount, data.pollCount, data.qaCount, data.interestCount,
                                    data.venuesCount, 0);
                        }
                        message = response.body().message;
                        if (localOffset == 0) {
                            interestList.clear();
                        }
                        if (response.body().data.interestSessions != null) {
                            if (response.body().data.interestSessions.isEmpty()) {
                                interestSearchResultListAdapter.setShowNoMoreResults(true);
                            }
                            interestList.addAll(response.body().data.interestSessions);
                        }
                        interestSearchResultListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                    interestSearchResultListAdapter.setShowProgressBar(false);
                    isSearchApiCalled = false;
                    SearchActivity searchActivity = ((SearchActivity) getActivity());
                    if (searchActivity != null) {
                        String latestQuery = searchActivity.getSearchQuery();
                        if (!latestQuery.equals(query)) {
                            offset = 0;
                            hitSearchInterestApi(user_id, InterestSearchListFragment.this.query,
                                    offset, "interests");

                        }
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.GONE);
                    isSearchApiCalled = false;
                    interestSearchResultListAdapter.setShowProgressBar(false);
                    if (interestList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void interestSearchClickMethod(int position, PojoInterestSession pojoInterestSession,
                                          String type) {
        if (interestList.get(position).iFollow == 1) {
            hitFollowInterestApi(user_id, interestList.get(position).interestId,
                    "interest_unfollow", position);
            interestList.get(position).iFollow = 0;
        } else {
            hitFollowInterestApi(user_id, interestList.get(position).interestId,
                    "interest_follow", position);
            interestList.get(position).iFollow = 1;
        }
        interestSearchResultListAdapter.notifyDataSetChanged();


    }


    public void hitFollowInterestApi(String user_id, String friend_id, String connection_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG,
                                AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
            }
        });

    }
}
