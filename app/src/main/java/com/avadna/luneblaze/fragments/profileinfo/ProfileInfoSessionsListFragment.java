package com.avadna.luneblaze.fragments.profileinfo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.sessionCreation.SessionCreationPart1;
import com.avadna.luneblaze.adapters.profileInfo.sessions.ProfileInfoAttendedSessionListAdapter;
import com.avadna.luneblaze.adapters.profileInfo.sessions.ProfileInfoConductedSessionListAdapter;
import com.avadna.luneblaze.adapters.profileInfo.sessions.ProfileInfoInitiatedSessionListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionConducted;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionInitiated;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionsAttended;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileInfoSessionsListFragment extends Fragment {

    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String targetUserId;

    private int offset = 0;
    private boolean isGetMoreApiCalled = false;
    private ProgressBar pb_loading_content;
    private LinearLayout ll_data_wrapper;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_conducted_session_list;
    private ProfileInfoConductedSessionListAdapter conductedSessionAdapter;
    private List<PojoSessionConducted> conductedSessionList = new ArrayList<>();
    private LinearLayoutManager conductedLayoutManager;

    private RecyclerView rv_attended_session_list;
    private ProfileInfoAttendedSessionListAdapter attendedSessionListAdapter;
    private List<PojoSessionsAttended> attendedSessionList = new ArrayList<>();
    private LinearLayoutManager attendedLayoutManager;


    private RecyclerView rv_initiated_session_list;
    private ProfileInfoInitiatedSessionListAdapter initiatedSessionAdapter;
    private List<PojoSessionInitiated> initiatedSessionList = new ArrayList<>();
    private LinearLayoutManager initiatedLayoutManager;

    private TextView tv_engaged_sessions, tv_conducted_sessions, tv_attended_sessions;
    private TextView tv_no_sessions, tv_create_session;

    private View.OnClickListener mOnClickListener;
    private boolean apiResponseReceived = false;
    private int currentTab = 0;


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
        View baseLayout = inflater.inflate(R.layout.fragment_profile_info_sessions_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();

        initViews(baseLayout);
        initSessionLists(baseLayout);
        initClickListener();
        setClickListeners();

        offset = 0;
        hitGetUserProfileApi(user_id, targetUserId, "sessions", offset);
        return baseLayout;
    }

    private void setClickListeners() {
        tv_engaged_sessions.setOnClickListener(mOnClickListener);
        tv_conducted_sessions.setOnClickListener(mOnClickListener);
        tv_attended_sessions.setOnClickListener(mOnClickListener);
        tv_create_session.setOnClickListener(mOnClickListener);
    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitGetUserProfileApi(user_id, targetUserId, "sessions", offset);
            }
        });

        tv_engaged_sessions = (TextView) baseLayout.findViewById(R.id.tv_engaged_sessions);
        tv_conducted_sessions = (TextView) baseLayout.findViewById(R.id.tv_conducted_sessions);
        tv_attended_sessions = (TextView) baseLayout.findViewById(R.id.tv_attended_sessions);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        ll_data_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_data_wrapper);
        tv_no_sessions = (TextView) baseLayout.findViewById(R.id.tv_no_sessions);
        tv_no_sessions.setVisibility(View.GONE);
        tv_create_session = (TextView) baseLayout.findViewById(R.id.tv_create_session);
        tv_create_session.setVisibility(View.GONE);

        ll_data_wrapper.setVisibility(View.GONE);
    }

    private void initSessionLists(View baseLayout) {
        initiatedSessionList = new ArrayList<>();
        rv_initiated_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_initiated_session_list);
        rv_initiated_session_list.setNestedScrollingEnabled(true);
        initiatedSessionAdapter = new ProfileInfoInitiatedSessionListAdapter(getActivity(), initiatedSessionList);
        initiatedLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_initiated_session_list.setLayoutManager(initiatedLayoutManager);
        rv_initiated_session_list.setAdapter(initiatedSessionAdapter);

        rv_initiated_session_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4 && !isGetMoreApiCalled && initiatedLayoutManager
                        .findLastCompletelyVisibleItemPosition() > initiatedSessionList.size() - 3) {
                    hitGetUserProfileApi(user_id, targetUserId, "sessions", ++offset);
                    initiatedSessionAdapter.setShowProgress(true);
                }
            }
        });

        conductedSessionList = new ArrayList<>();
        rv_conducted_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_conducted_session_list);
        rv_conducted_session_list.setNestedScrollingEnabled(true);
        conductedSessionAdapter = new ProfileInfoConductedSessionListAdapter(getActivity(), conductedSessionList);
        conductedLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_conducted_session_list.setLayoutManager(conductedLayoutManager);
        rv_conducted_session_list.setAdapter(conductedSessionAdapter);

        rv_conducted_session_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4 && !isGetMoreApiCalled && conductedLayoutManager
                        .findLastCompletelyVisibleItemPosition() > conductedSessionList.size() - 3) {
                    hitGetUserProfileApi(user_id, targetUserId, "sessions", ++offset);
                    conductedSessionAdapter.setShowProgress(true);

                }
            }
        });

        attendedSessionList = new ArrayList<>();
        rv_attended_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_attended_session_list);
        rv_attended_session_list.setNestedScrollingEnabled(true);
        attendedSessionListAdapter = new ProfileInfoAttendedSessionListAdapter(getActivity(), attendedSessionList);
        attendedLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_attended_session_list.setLayoutManager(attendedLayoutManager);
        rv_attended_session_list.setAdapter(attendedSessionListAdapter);

        rv_attended_session_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4 && !isGetMoreApiCalled && attendedLayoutManager
                        .findLastCompletelyVisibleItemPosition() > attendedSessionList.size() - 3) {
                    hitGetUserProfileApi(user_id, targetUserId, "sessions", ++offset);
                    attendedSessionListAdapter.setShowProgress(true);

                }
            }
        });

    }

    private void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_attended_sessions:
                        tv_no_sessions.setVisibility(View.GONE);
                        tv_create_session.setVisibility(View.GONE);

                        currentTab = 0;
                        tv_attended_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_attended_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                        tv_engaged_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_engaged_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_conducted_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_conducted_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_initiated_session_list.setVisibility(View.GONE);
                        rv_conducted_session_list.setVisibility(View.GONE);
                        rv_attended_session_list.setVisibility(View.VISIBLE);

                        if (apiResponseReceived && attendedSessionList.isEmpty()) {
                            tv_no_sessions.setText(getString(R.string.no_sessions_attended));
                            tv_no_sessions.setVisibility(View.VISIBLE);
                            if (user_id.equals(targetUserId)) {
                                tv_create_session.setVisibility(View.VISIBLE);
                                tv_create_session.setText(getString(R.string.browse_sessions));
                                tv_create_session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled,
                                        0, 0, 0);
                            }
                        } else {
                            tv_no_sessions.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.tv_conducted_sessions:
                        tv_no_sessions.setVisibility(View.GONE);
                        tv_create_session.setVisibility(View.GONE);

                        currentTab = 1;
                        tv_conducted_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_conducted_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                        tv_engaged_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_engaged_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_attended_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_attended_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_initiated_session_list.setVisibility(View.GONE);
                        rv_conducted_session_list.setVisibility(View.VISIBLE);
                        rv_attended_session_list.setVisibility(View.GONE);

                        if (apiResponseReceived && conductedSessionList.isEmpty()) {
                            tv_no_sessions.setText(getString(R.string.no_sessions_shared));
                            tv_no_sessions.setVisibility(View.VISIBLE);
                            if (user_id.equals(targetUserId)) {
                                tv_create_session.setVisibility(View.VISIBLE);
                                tv_create_session.setText(getString(R.string.conduct_a_session));
                                tv_create_session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_session_gray,
                                        0, 0, 0);
                            }
                        } else {
                            tv_no_sessions.setVisibility(View.GONE);
                        }

                        break;


                    case R.id.tv_engaged_sessions:
                        tv_no_sessions.setVisibility(View.GONE);
                        tv_create_session.setVisibility(View.GONE);

                        currentTab = 2;
                        tv_engaged_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_engaged_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                        tv_conducted_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_conducted_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_attended_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_attended_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_initiated_session_list.setVisibility(View.VISIBLE);
                        rv_conducted_session_list.setVisibility(View.GONE);
                        rv_attended_session_list.setVisibility(View.GONE);

                        if (apiResponseReceived && initiatedSessionList.isEmpty()) {
                            tv_no_sessions.setText(getString(R.string.no_sessions_initiated));
                            tv_no_sessions.setVisibility(View.VISIBLE);
                            if (user_id.equals(targetUserId)) {
                                tv_create_session.setVisibility(View.VISIBLE);
                                tv_create_session.setText(getString(R.string.initiate_a_session));
                                tv_create_session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_learn_gray,
                                        0, 0, 0);

                            }
                        } else {
                            tv_no_sessions.setVisibility(View.GONE);
                        }


                        break;

                    case R.id.tv_create_session:
                        if (currentTab == 0) {
                            Intent sessionConductIntent = new Intent(getActivity(), MainActivity.class);
                            sessionConductIntent.putExtra("tab", AppKeys.SESSION);
                            startActivity(sessionConductIntent);
                        } else if (currentTab == 1) {
                            Intent sessionConductIntent = new Intent(getActivity(), SessionCreationPart1.class);
                            sessionConductIntent.putExtra("type", AppKeys.SESSION_CONDUCT);
                            startActivity(sessionConductIntent);
                        } else {
                            Intent sessionInitiateIntent = new Intent(getActivity(), SessionCreationPart1.class);
                            sessionInitiateIntent.putExtra("type", AppKeys.SESSION_INITIATE);
                            startActivity(sessionInitiateIntent);
                        }
                        break;

                }
            }
        };
    }


    private void hitGetUserProfileApi(final String user_id, final String targetUserId, String type, final int offset) {
        Call<PojoProfileTabResponse> call = apiService.getUserProfileTabData(user_id, targetUserId, type,
                String.valueOf(offset));
        ll_no_connection_wrapper.setVisibility(View.GONE);
        if (!isGetMoreApiCalled) {
            isGetMoreApiCalled = true;
            call.enqueue(new Callback<PojoProfileTabResponse>() {
                @Override
                public void onResponse(Call<PojoProfileTabResponse> call, Response<PojoProfileTabResponse> response) {
                    if (response != null && response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null && getActivity() != null) {
                            apiResponseReceived = true;
                            if (offset == 0) {
                                initiatedSessionList.clear();
                                attendedSessionList.clear();
                                conductedSessionList.clear();
                            }
                            /*if (response.body().data.initiatedSession.size() == 0) {
                                initiatedSessionAdapter.setShowProgress(false);
                            }
                            if (response.body().data.attendedSession.size() == 0) {
                                attendedSessionListAdapter.setShowProgress(false);
                            }
                            if (response.body().data.conductedSession.size() == 0) {
                                conductedSessionAdapter.setShowProgress(false);
                            }*/

                            initiatedSessionAdapter.setShowProgress(false);

                            attendedSessionListAdapter.setShowProgress(false);

                            conductedSessionAdapter.setShowProgress(false);


                            initiatedSessionList.addAll(response.body().data.initiatedSession);
                            attendedSessionList.addAll(response.body().data.attendedSession);
                            conductedSessionList.addAll(response.body().data.conductedSession);

                            if (offset == 0) {
                                switch (currentTab) {
                                    case 0:
                                        if (apiResponseReceived && attendedSessionList.isEmpty()) {
                                            tv_no_sessions.setText(R.string.no_sessions_attended);
                                            tv_no_sessions.setVisibility(View.VISIBLE);
                                            if (user_id.equals(targetUserId)) {
                                                tv_create_session.setVisibility(View.VISIBLE);
                                                tv_create_session.setText(getString(R.string.attend_a_session));
                                            }
                                        } else {
                                            tv_no_sessions.setVisibility(View.GONE);
                                            rv_attended_session_list.setVisibility(View.VISIBLE);
                                        }
                                        break;

                                    case 1:
                                        if (apiResponseReceived && conductedSessionList.isEmpty()) {
                                            tv_no_sessions.setText(R.string.no_sessions_conducted);
                                            tv_no_sessions.setVisibility(View.VISIBLE);
                                            if (user_id.equals(targetUserId)) {
                                                tv_create_session.setVisibility(View.VISIBLE);
                                                tv_create_session.setText(getString(R.string.conduct_a_session));
                                            }
                                        } else {
                                            tv_no_sessions.setVisibility(View.GONE);
                                        }
                                        break;

                                    case 2:
                                        if (apiResponseReceived && initiatedSessionList.isEmpty()) {
                                            tv_no_sessions.setText(R.string.no_sessions_initiated);
                                            tv_no_sessions.setVisibility(View.VISIBLE);
                                            if (user_id.equals(targetUserId)) {
                                                tv_create_session.setVisibility(View.VISIBLE);
                                                tv_create_session.setText(getString(R.string.initiate_a_session));
                                            }
                                        } else {
                                            tv_no_sessions.setVisibility(View.GONE);
                                        }
                                        break;
                                }
                            }
                            initiatedSessionAdapter.notifyDataSetChanged();
                            attendedSessionListAdapter.notifyDataSetChanged();
                            conductedSessionAdapter.notifyDataSetChanged();

                        } else {
                            //if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    pb_loading_content.setVisibility(View.GONE);
                    ll_data_wrapper.setVisibility(View.VISIBLE);
                    isGetMoreApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoProfileTabResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled = false;

                    if (attendedSessionList.isEmpty() && conductedSessionList.isEmpty() && initiatedSessionList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }


}
