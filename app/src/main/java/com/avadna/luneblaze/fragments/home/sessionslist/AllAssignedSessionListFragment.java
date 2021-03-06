package com.avadna.luneblaze.fragments.home.sessionslist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.adapters.AssignedSessionAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SessionFilterOptionState;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoGetAllSessionsResponse;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllAssignedSessionListFragment extends Fragment {
    private PojoSessions sessions;
    private SwipeRefreshLayout srl_refresh;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private String mParam1;

    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id = "";
    private ProgressBar pb_loading_content;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_session_list;
    private AssignedSessionAdapter assignedSessionAdapter;
    private LinearLayoutManager sessionListLayoutManager;
    private boolean isSessionApiCalled = false;
    private List<PojoAssignedSessions> assignedSessions = new ArrayList<>();


    private double latitude = 28;
    private double longitude = 77;
    private String oldSessionRole = "Any";
    private String oldIsMyVenuesSelected = "0";
    private String oldLocation = "Any";

    private List<String> selectedVenueIdList = new ArrayList<>();
    private List<String> selectedInterestIdList = new ArrayList<>();

    private String oldIsMyInterestSelected = "0";
    private String oldTime = "";

    private String oldStartDate = "";
    private String oldEndDate = "";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private String fixed_venue_id;
    private String fixed_interest_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fixed_interest_id = getArguments().getString(AppKeys.INTEREST_ID, null);
            fixed_venue_id = getArguments().getString(AppKeys.VENUE_ID, null);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_verified_sessions_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        initSessionList(baseLayout);
        initBroadCastReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_FILTER_UPDATED));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_ATTENDED_STATUS));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_ENDED));

        if (preferenceUtils.getUserCurrentLocation() != null) {
            latitude = preferenceUtils.getUserCurrentLocation().latitude;
            longitude = preferenceUtils.getUserCurrentLocation().longitude;
        }

       /* for(int i=0;i<600;i++){

            double LAT=latitude+((i%20)/400f);
            double LONG=longitude+((i%20)/400f);
            hitGetAllSessionsApi(String.valueOf(i), "assigned", "distance", "ASC", getLastSessionTime(),
                    LAT, LONG, oldSessionRole, oldLocation, selectedVenueIdList,
                    oldIsMyVenuesSelected, selectedInterestIdList, oldIsMyInterestSelected, oldTime,
                    oldStartDate, oldEndDate);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        hitGetAllSessionsApi(user_id, "assigned", "distance", "ASC", getLastSessionTime(),
                latitude, longitude, oldSessionRole, oldLocation, selectedVenueIdList,
                oldIsMyVenuesSelected, selectedInterestIdList, oldIsMyInterestSelected, oldTime,
                oldStartDate, oldEndDate);

        return baseLayout;
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.SESSION_FILTER_UPDATED)) {
                    String activityName = intent.getStringExtra(AppKeys.ACTIVITY_NAME);
                    if (getActivity().getClass().getSimpleName().equals(activityName)) {
                        Type type = new TypeToken<SessionFilterOptionState>() {
                        }.getType();
                        SessionFilterOptionState filterOptions = new Gson()
                                .fromJson(intent.getStringExtra("filterData"), type);

                        oldSessionRole = filterOptions.oldSessionRole;
                        oldStartDate = filterOptions.oldStartDate;
                        oldEndDate = filterOptions.oldEndDate;
                        oldLocation = filterOptions.oldLocation;
                        oldTime = filterOptions.oldTime;
                        oldIsMyVenuesSelected = filterOptions.oldIsMyVenuesSelected;
                        oldIsMyInterestSelected = filterOptions.oldIsMyInterestSelected;

                        selectedVenueIdList = filterOptions.selectedVenueIdList;
                        selectedInterestIdList = filterOptions.selectedInterestIdList;

                        /*if (oldTime.equals(TIME_DATE_RANGE)) {
                            oldTime = TIME_ANY;
                        }*/
                        // pb_loading_content.setVisibility(View.VISIBLE);
                        assignedSessions.clear();
                        assignedSessionAdapter.setShowMoreProgressBar(true);
                        assignedSessionAdapter.setShowNoMorePresent(false);
                        assignedSessionAdapter.notifyDataSetChanged();
                        hitGetAllSessionsApi(user_id, "assigned", "distance", "ASC",
                                getLastSessionTime(), latitude, longitude, oldSessionRole, oldLocation,
                                selectedVenueIdList, oldIsMyVenuesSelected, selectedInterestIdList,
                                oldIsMyInterestSelected, oldTime, oldStartDate, oldEndDate);
                    }
                }

                if (intent.getAction().equals(Config.SESSION_ATTENDED_STATUS)) {
                    String attendStatus = intent.getStringExtra(AppKeys.ATTEND_STATUS);
                    String sessionId = intent.getStringExtra(AppKeys.SESSION_ID);
                    for (int i = 0; i < assignedSessions.size(); i++) {
                        if (assignedSessions.get(i).sessionsId.equals(sessionId)) {
                            if (attendStatus.equals("0")) {
                                assignedSessions.get(i).iAttend = false;
                            } else {
                                assignedSessions.get(i).iAttend = true;
                            }
                        }
                    }
                    assignedSessionAdapter.notifyDataSetChanged();
                }

                if (intent.getAction().equals(Config.SESSION_ENDED)) {
                    String sessionId = intent.getStringExtra(AppKeys.SESSION_ID);
                    for (int i = 0; i < assignedSessions.size(); i++) {
                        if (assignedSessions.get(i).sessionsId.equals(sessionId)) {
                            assignedSessions.remove(i);
                            assignedSessionAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }
        };

    }


    private void hitGetAllSessionsApi(final String user_id, String session_type, String sort, String order,
                                      final String last_session_time, final double latitude, final double longitude,
                                      String session_role, String location_based, List<String> venues,
                                      String isMyVenuesSelected, List<String> interests,
                                      String isMyInterestSelected, String time_based,
                                      String start_date, String end_date) {

        ll_no_connection_wrapper.setVisibility(View.GONE);
        if (fixed_interest_id != null && !fixed_interest_id.isEmpty()) {
            interests.clear();
            interests.add(fixed_interest_id);
        }
        if (fixed_venue_id != null && !fixed_venue_id.isEmpty()) {
            venues.clear();
            venues.add(fixed_venue_id);
        }

        if (!isSessionApiCalled) {
            isSessionApiCalled = true;
            if (start_date.isEmpty()) {
                start_date = null;
            }
            if (end_date.isEmpty()) {
                end_date = null;
            }
            HashMap<String, String> interestMap = new HashMap<>();

            for (int i = 0; i < interests.size(); i++) {
                interestMap.put("interest_search[" + i + "]", interests.get(i));
            }

            HashMap<String, String> venueMap = new HashMap<>();
            for (int i = 0; i < venues.size(); i++) {
                venueMap.put("venue_id_search[" + i + "]", venues.get(i));
            }

            Call<PojoGetAllSessionsResponse> call = apiService.getAllSessionsApiNew(user_id, session_type,
                    /* sort,*/ order, last_session_time, String.valueOf(latitude), String.valueOf(longitude),
                    session_role, location_based, venueMap, isMyVenuesSelected, interestMap,
                    isMyInterestSelected, time_based, start_date, end_date);
            assignedSessionAdapter.setShowMoreProgressBar(true);
            assignedSessionAdapter.setShowNoMorePresent(false);
            Log.d("called lat long ", "lat " + latitude + "long " + longitude + "of user" + user_id);

            call.enqueue(new Callback<PojoGetAllSessionsResponse>() {
                @Override
                public void onResponse(Call<PojoGetAllSessionsResponse> call, Response<PojoGetAllSessionsResponse> response) {
                    Log.d("coordinates", "of user" + user_id + "updated");

                    String message = "";
                    if (response.body() != null && response.body().data != null) {
                        if (last_session_time == null || last_session_time.equals("0")) {
                            assignedSessions.clear();
                            if (response.body().data.sessions.assigned.isEmpty()) {
                                assignedSessionAdapter.setShowNoMorePresent(true);
                            }

                        } else if (response.body().data.sessions.assigned.size() < 10) {
                            assignedSessionAdapter.setShowNoMorePresent(true);
                        }
                        isSessionApiCalled = false;
                        assignedSessions.addAll(response.body().data.sessions.assigned);
                        assignedSessionAdapter.notifyDataSetChanged();

                    }
                    srl_refresh.setRefreshing(false);
                    pb_loading_content.setVisibility(View.GONE);
                    assignedSessionAdapter.setShowMoreProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoGetAllSessionsResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    srl_refresh.setRefreshing(false);
                    isSessionApiCalled = false;
                    if (assignedSessions.isEmpty()) {
                        pb_loading_content.setVisibility(View.GONE);
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                        assignedSessionAdapter.setShowMoreProgressBar(false);
                    }
                }
            });
        }

    }


    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetAllSessionsApi(user_id, "assigned", "distance", "ASC",
                        getLastSessionTime(), latitude, longitude, oldSessionRole, oldLocation,
                        selectedVenueIdList, oldIsMyVenuesSelected, selectedInterestIdList,
                        oldIsMyInterestSelected, oldTime, oldStartDate, oldEndDate);
            }
        });

        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitGetAllSessionsApi(user_id, "assigned", "distance", "ASC",
                        null, latitude, longitude, oldSessionRole, oldLocation, selectedVenueIdList,
                        oldIsMyVenuesSelected, selectedInterestIdList, oldIsMyInterestSelected,
                        oldTime, oldStartDate, oldEndDate);
            }
        });

    }

    private void initSessionList(View baseLayout) {

        assignedSessions = new ArrayList<>();
        rv_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_session_list);
        rv_session_list.setNestedScrollingEnabled(true);
        if (fixed_venue_id != null) {
            assignedSessionAdapter = new AssignedSessionAdapter(getActivity(), assignedSessions,
                    true);
        } else {
            assignedSessionAdapter = new AssignedSessionAdapter(getActivity(), assignedSessions,
                    false);
        }
        sessionListLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false);
        rv_session_list.setLayoutManager(sessionListLayoutManager);
        rv_session_list.setAdapter(assignedSessionAdapter);

        rv_session_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    String name = getActivity().getClass().getSimpleName();
                    if (dy > 4) {
                        if (name.equals("MainActivity")) {
                            ((MainActivity) getActivity()).hideBars();
                        } else if (name.equals("InterestActivity")) {
                            ((InterestActivity) getActivity()).hideFilter();
                        }
                    } else if (dy < -4) {
                        if (name.equals("MainActivity")) {
                            ((MainActivity) getActivity()).showBars();
                        } else if (name.equals("InterestActivity")) {
                            ((InterestActivity) getActivity()).showFilter();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (dy > 5) {
                    if (!isSessionApiCalled && sessionListLayoutManager.findLastCompletelyVisibleItemPosition() > assignedSessions.size() - 2) {
                        hitGetAllSessionsApi(user_id, "assigned", "distance", "ASC", getLastSessionTime(), latitude,
                                longitude, oldSessionRole, oldLocation, selectedVenueIdList,
                                oldIsMyVenuesSelected, selectedInterestIdList, oldIsMyInterestSelected,
                                oldTime, oldStartDate, oldEndDate);
                    }
                }
            }
        });

    }

    private String getLastSessionTime() {
        if (assignedSessions.isEmpty()) {
            return null;
        } else {
            return String.valueOf(assignedSessions.get(assignedSessions.size() - 1).assigningTime);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
