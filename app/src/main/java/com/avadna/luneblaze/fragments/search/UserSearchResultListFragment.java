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
import com.avadna.luneblaze.adapters.search.UserSearchResultListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserSearchResultListFragment extends Fragment implements
        UserSearchResultListAdapter.UserSearchResultAdapterCallback {

    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id = "";

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_people_list;
    private List<PojoUserData> userList;
    private UserSearchResultListAdapter userSearchResultListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int offset = 0;
    private String query = "";
    private boolean isSearchApiCalled = false;


    private int position;

    private boolean loadedForEmptyString = false;


    public UserSearchResultListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            Type type = new TypeToken< List<PojoUserData>>() {}.getType();
            String str=getArguments().getString("user");
            if(str!=null&&str.isEmpty()){
                oraganisationList=new ArrayList<>();
            }
            else {
                oraganisationList= new Gson().fromJson(str, type);
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_people_list, container, false);
        preferenceUtils = new PreferenceUtils(this.getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        initViews(baseLayout);

        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SEARCH_STRING_UPDATE));

        query = getArguments().getString("query", "");
        position = getArguments().getInt("position");

        if (position == 0) {
            offset = 0;
            hitSearchUserApi(user_id, query, offset, "users");
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
                hitSearchUserApi(user_id, query, offset, "users");
            }
        });
        initPeopleList(baseLayout);
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.SEARCH_STRING_UPDATE)) {
                 /*   // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    int pos=((SearchActivity)getActivity()).getSelectedTab();
                    if(pos==0){
                        query = intent.getStringExtra("query");
                        score=0;
                        hitSearchUserApi(user_id, query, score, "users");
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
            hitSearchUserApi(user_id, query, offset, "users");
        } else if (!query.equals(str) || userList.isEmpty()) {
            query = str;
            offset = 0;
            hitSearchUserApi(user_id, query, offset, "users");
        }
    }

    private void initPeopleList(View baseLayout) {
        userList = new ArrayList<>();
        rv_people_list = (RecyclerView) baseLayout.findViewById(R.id.rv_people_list);
        userSearchResultListAdapter =
                new UserSearchResultListAdapter(UserSearchResultListFragment.this, userList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false);
        rv_people_list.setLayoutManager(linearLayoutManager);
        rv_people_list.setAdapter(userSearchResultListAdapter);
        userSearchResultListAdapter.setShowProgressBar(false);
        rv_people_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (linearLayoutManager.findLastVisibleItemPosition() > userList.size() - 2) {
                        hitSearchUserApi(user_id, query, ++offset, "users");
                    }
                    ((SearchActivity) getActivity()).closeKeyboard();
                }
            }
        });

    }

    private void hitSearchUserApi(final String user_id, final String query, final int localOffset, String type) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        if (localOffset == 0) {
            userSearchResultListAdapter.setShowNoMoreResults(false);
            userList.clear();
            userSearchResultListAdapter.notifyDataSetChanged();
        }
        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            userSearchResultListAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null) {
                        PojoSearchResultResponseData data = response.body().data;
                        SearchActivity searchActivity = (SearchActivity) getActivity();
                        if (searchActivity != null) {
                            searchActivity.updateTabCount(data.usersCount, data.sessionsCount,
                                    data.postsCount, data.articlesCount, data.pollCount, data.qaCount, data.interestCount,
                                    data.venuesCount, 0);
                        }

                        message = response.body().message;
                        if (localOffset == 0) {
                            userList.clear();
                        }
                        if (response.body().data.users != null) {
                            if (response.body().data.users.isEmpty()) {
                                userSearchResultListAdapter.setShowNoMoreResults(true);
                            }
                            userList.addAll(response.body().data.users);
                        }
                        userSearchResultListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    userSearchResultListAdapter.setShowProgressBar(false);
                    isSearchApiCalled = false;
                    SearchActivity searchActivity = ((SearchActivity) getActivity());
                    if (searchActivity != null) {
                        String latestQuery = searchActivity.getSearchQuery();
                        if (!latestQuery.equals(query)) {
                            offset = 0;
                            hitSearchUserApi(user_id, UserSearchResultListFragment.this.query, offset,
                                    "users");
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isSearchApiCalled = false;
                    userSearchResultListAdapter.setShowProgressBar(false);
                    if (userList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                    //   pb_loading_content.setVisibility(View.GONE);

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
    public void inviteListClickMethod(int position, PojoUserData pojoUserData, String type) {
        if (userList.get(position).connection.equals("add")) {
            hitUpdateRequestStatusApi(user_id, pojoUserData.userId, "follow");
            userList.get(position).connection = "following";
        } else {
            hitUpdateRequestStatusApi(user_id, pojoUserData.userId, "unfollow");
            userList.get(position).connection = "add";
        }
        userSearchResultListAdapter.notifyDataSetChanged();
    }


    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // oraganisationList.remove(position);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
