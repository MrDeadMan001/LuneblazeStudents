package com.avadna.luneblaze.fragments.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.adapters.search.OrganisationSearchResultAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchOrganisation;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganisationSearchResultFragment extends Fragment implements
        OrganisationSearchResultAdapter.OrganisationSearchResultAdapterCallback {


    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_list;
    List<PojoSearchOrganisation> oraganisationList;
    OrganisationSearchResultAdapter organisationSearchResultAdapter;
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

        if (position == 8) {
            offset = 0;
            hitSearchOrganisationApi(user_id, query, offset, "organization");
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
                        hitSearchOrganisationApi(user_id, query, 0, "venues");
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
            hitSearchOrganisationApi(user_id, query, 0, "organization");
        } else if (!query.equals(str) || oraganisationList.isEmpty()) {
            query = str;
            offset = 0;
            hitSearchOrganisationApi(user_id, query, 0, "organization");
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
                hitSearchOrganisationApi(user_id, query, 0, "organization");
            }
        });
        initPeopleList(baseLayout);
    }

    private void initPeopleList(View baseLayout) {
        oraganisationList = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_people_list);
        organisationSearchResultAdapter = new OrganisationSearchResultAdapter(this, oraganisationList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(organisationSearchResultAdapter);
        organisationSearchResultAdapter.setShowProgressBar(false);
        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (linearLayoutManager.findLastVisibleItemPosition() > oraganisationList.size() - 2) {
                        hitSearchOrganisationApi(user_id, query, ++offset, "organization");
                    }
                    ((SearchActivity) getActivity()).closeKeyboard();
                }
            }
        });
    }

    private void hitSearchOrganisationApi(final String user_id, final String query, final int localOffset, String type) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        if (localOffset == 0) {
            organisationSearchResultAdapter.setShowNoMoreResults(false);
            oraganisationList.clear();
            organisationSearchResultAdapter.notifyDataSetChanged();
        }
        if (!isSearchApiCalled) {
            organisationSearchResultAdapter.setShowProgressBar(true);
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
                            oraganisationList.clear();
                        }
                        if (response.body().data.organization != null) {
                            if (response.body().data.organization.isEmpty()) {
                                organisationSearchResultAdapter.setShowNoMoreResults(true);
                            }
                            oraganisationList.addAll(response.body().data.organization);
                        } else {
                            organisationSearchResultAdapter.setShowNoMoreResults(true);
                        }
                        organisationSearchResultAdapter.notifyDataSetChanged();
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    organisationSearchResultAdapter.setShowProgressBar(false);
                    isSearchApiCalled = false;


                    SearchActivity searchActivity = ((SearchActivity) getActivity());
                    if (searchActivity != null) {
                        String latestQuery = searchActivity.getSearchQuery();
                        if (!latestQuery.equals(query)) {
                            offset = 0;
                            hitSearchOrganisationApi(user_id, OrganisationSearchResultFragment.this.query,
                                    offset, "organization");
                        }
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    isSearchApiCalled = false;
                    organisationSearchResultAdapter.setShowProgressBar(false);
                    if (oraganisationList.isEmpty()) {
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
    public void organisationSearchClickMethod(int position, PojoSearchOrganisation pojoSearchOrganisation, String type) {
        if (oraganisationList.get(position).isFollowing == 1) {
            oraganisationList.get(position).isFollowing = 0;
            oraganisationList.get(position).totalfollower--;
            hitFollowOrganisationApi(user_id, oraganisationList.get(position).id, "organizer_unfollow");

        } else {
            oraganisationList.get(position).isFollowing = 1;
            oraganisationList.get(position).totalfollower++;
            hitFollowOrganisationApi(user_id, oraganisationList.get(position).id, "organizer_follow");

        }
        organisationSearchResultAdapter.notifyDataSetChanged();
    }

    private void hitFollowOrganisationApi(String user_id, String venue_id, String connection_type) {
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
