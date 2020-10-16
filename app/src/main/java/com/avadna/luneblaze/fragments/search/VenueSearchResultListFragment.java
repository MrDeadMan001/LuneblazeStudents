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
import com.avadna.luneblaze.adapters.search.VenueSearchResultListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponseData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchVenue;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenueSearchResultListFragment extends Fragment implements
        VenueSearchResultListAdapter.VenueSearchResultAdapterCallback {


    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_list;
    List<PojoSearchVenue> venueList;
    VenueSearchResultListAdapter venueSearchResultListAdapter;
    LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    int offset = 0;
    String query = "";
    int position;
    boolean isSearchApiCalled = false;
    private boolean loadedForEmptyString = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.OPEN_VENUE_FILTER));

        query = getArguments().getString("query", "");
        position = getArguments().getInt("position");

        if (position == 7) {
            offset = 0;
            hitSearchVenueApi(user_id, query, offset, "venues");
        }

        return baseLayout;
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.SEARCH_STRING_UPDATE)) {
                  /*  int pos = ((SearchActivity) getActivity()).getSelectedTab();
                    if (pos == 6) {
                        query = intent.getStringExtra("query");
                        score = 0;
                        hitSearchVenueApi(user_id, query, 0, "venues");
                    }*/
                }
                if (intent.getAction().equals(Config.OPEN_ARTICLE_FILTER)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    openFilterDilaog();
                }
            }
        };
    }

    public void openFilterDilaog() {

    }


    public void updateSearchString(String str) {
        if (str.isEmpty() && !loadedForEmptyString) {
            loadedForEmptyString = true;
            query = str;
            offset = 0;
            hitSearchVenueApi(user_id, query, 0, "venues");
        } else if (!query.equals(str) || venueList.isEmpty()) {
            query = str;
            offset = 0;
            hitSearchVenueApi(user_id, query, 0, "venues");
        }
    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitSearchVenueApi(user_id, query, 0, "venues");
            }
        });
        initPeopleList(baseLayout);
    }

    private void initPeopleList(View baseLayout) {
        venueList = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_people_list);
        venueSearchResultListAdapter = new VenueSearchResultListAdapter(this, venueList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(venueSearchResultListAdapter);
        venueSearchResultListAdapter.setShowProgressBar(false);
        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (linearLayoutManager.findLastVisibleItemPosition() > venueList.size() - 2) {
                        hitSearchVenueApi(user_id, query, ++offset, "venues");
                    }
                    ((SearchActivity) getActivity()).closeKeyboard();
                }
            }
        });
    }

    private void hitSearchVenueApi(final String user_id, final String query, final int localOffset, String type) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        if (localOffset == 0) {
            venueSearchResultListAdapter.setShowNoMoreResults(false);
            venueList.clear();
            venueSearchResultListAdapter.notifyDataSetChanged();
        }
        if (!isSearchApiCalled) {
            venueSearchResultListAdapter.setShowProgressBar(true);
            isSearchApiCalled = true;
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        PojoSearchResultResponseData data=response.body().data;
                        SearchActivity searchActivity = (SearchActivity) getActivity();
                        if (searchActivity != null) {
                            searchActivity.updateTabCount(data.usersCount, data.sessionsCount,
                                    data.postsCount, data.articlesCount, data.pollCount, data.qaCount, data.interestCount,
                                    data.venuesCount, 0);
                        }
                        if (localOffset == 0) {
                            venueList.clear();
                        }
                        if (response.body().data.venues != null) {
                            if (response.body().data.venues.isEmpty()) {
                                venueSearchResultListAdapter.setShowNoMoreResults(true);
                            }
                            venueList.addAll(response.body().data.venues);
                        } else {
                            venueSearchResultListAdapter.setShowNoMoreResults(true);
                        }
                        venueSearchResultListAdapter.notifyDataSetChanged();
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    venueSearchResultListAdapter.setShowProgressBar(false);
                    isSearchApiCalled = false;


                    SearchActivity searchActivity = ((SearchActivity) getActivity());
                    if (searchActivity != null) {
                        String latestQuery = searchActivity.getSearchQuery();
                        if (!latestQuery.equals(query)) {
                            offset = 0;
                            hitSearchVenueApi(user_id, VenueSearchResultListFragment.this.query, offset, "venues");
                        }
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    isSearchApiCalled = false;
                    venueSearchResultListAdapter.setShowProgressBar(false);
                    if (venueList.isEmpty()) {
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
    public void venueSearchClickMethod(int position, PojoSearchVenue pojoSearchVenue, String type) {

        if (venueList.get(position).iFollow == 1) {
            venueList.get(position).iFollow = 0;
            venueList.get(position).followersCount--;
            hitFollowVenueApi(user_id, venueList.get(position).venueId, "remove_notify_me_venue", position);

        } else {
            venueList.get(position).iFollow = 1;
            venueList.get(position).followersCount++;
            hitFollowVenueApi(user_id, venueList.get(position).venueId, "notify_me_venue", position);

        }

        venueSearchResultListAdapter.notifyDataSetChanged();
    }


    private void hitFollowVenueApi(String user_id, String venue_id, String connection_type, final int position) {
        {
            Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, venue_id, connection_type);
            call.enqueue(new Callback<PojoUserConnectResponse>() {
                @Override
                public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
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
}
