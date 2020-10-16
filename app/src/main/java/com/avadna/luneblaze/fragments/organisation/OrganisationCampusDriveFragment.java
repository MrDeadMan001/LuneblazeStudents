package com.avadna.luneblaze.fragments.organisation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.assessment.TaggedAssessmentCategoriesAdapter;
import com.avadna.luneblaze.adapters.organisation.CampusDriveListAdapter;
import com.avadna.luneblaze.adapters.organisation.VacancyListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrganisationCampusDriveFragment extends Fragment implements
        VacancyListAdapter.VacancyListAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback,
        CampusDriveListAdapter.CampusDriveListCallback,
        TaggedAssessmentCategoriesAdapter.TaggedAssessmentCategoriesAdapterCallback {

    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String organization_id = "2";
    private String targetUserId;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_drive_list;
    private List<PojoCampusDriveListResponseData> driveList;
    private CampusDriveListAdapter campusDriveListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView tv_no_questions;

    private int offset = 0;
    private boolean isGetMoreApiCalled = false;

   // private ProgressBar pb_loading_content;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            organization_id = getArguments().getString(AppKeys.ORGANISATION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View baseLayout = inflater.inflate(R.layout.fragment_organisation_campus_drive, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);

        offset = 0;
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_CAMPUS_DRIVE_ADDED));

        hitGetCampusDriveList(organization_id, offset);
        return baseLayout;
    }


    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.NEW_CAMPUS_DRIVE_ADDED)) {
                    offset = 0;
                    // hitGetNewsFeedApi(user_id, "posts_profile", currentFilter, offset, targetUserId);
                    hitGetCampusDriveList(organization_id, offset);
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
                hitGetCampusDriveList(organization_id, offset);
            }
        });

      //  pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
      //  pb_loading_content.setVisibility(View.GONE);
        tv_no_questions = (TextView) baseLayout.findViewById(R.id.tv_no_questions);
        tv_no_questions.setText(getString(R.string.no_drives_created));
        tv_no_questions.setVisibility(View.GONE);
        initDriveList(baseLayout);
    }


    private void initDriveList(View baseLayout) {
        driveList = new ArrayList<>();
        rv_drive_list = (RecyclerView) baseLayout.findViewById(R.id.rv_drive_list);
        campusDriveListAdapter = new CampusDriveListAdapter(OrganisationCampusDriveFragment.this, driveList, organization_id);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_drive_list.setLayoutManager(linearLayoutManager);
        rv_drive_list.setAdapter(campusDriveListAdapter);

        rv_drive_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4 && !isGetMoreApiCalled && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        > driveList.size() - 3) {
                    hitGetCampusDriveList(user_id, ++offset);
                    try {
                        ((OrganisationProfileInfoActivity) getActivity()).setFlotingButtonVisible(false);
                    } catch (Exception e) {

                    }
                }

                if (dy < -4) {
                    try {
                        ((OrganisationProfileInfoActivity) getActivity()).setFlotingButtonVisible(true);
                    } catch (Exception e) {

                    }
                }
            }
        });

    }


    private void hitGetCampusDriveList(String organization_id, final int offset) {
        Call<PojoCampusDriveListResponse> call = apiService.getCampusDriveList(organization_id,
                String.valueOf(offset));
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isGetMoreApiCalled) {
            isGetMoreApiCalled = true;
            campusDriveListAdapter.showProgressBar(true);
            call.enqueue(new Callback<PojoCampusDriveListResponse>() {
                @Override
                public void onResponse(Call<PojoCampusDriveListResponse> call, Response<PojoCampusDriveListResponse> response) {
                    if (response != null && response.body() != null && getActivity() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                driveList.clear();
                            }
                            driveList.addAll(response.body().data);
                            if (driveList.isEmpty()) {
                                tv_no_questions.setVisibility(View.VISIBLE);
                                tv_no_questions.setText(R.string.no_campus_drives);
                            } else {
                                tv_no_questions.setVisibility(View.GONE);
                            }
                            campusDriveListAdapter.notifyDataSetChanged();

                        } else {
                            //if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    campusDriveListAdapter.showProgressBar(false);
                  //  pb_loading_content.setVisibility(View.GONE);
                    isGetMoreApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoCampusDriveListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetMoreApiCalled = false;
                   // pb_loading_content.setVisibility(View.GONE);
                    campusDriveListAdapter.showProgressBar(false);

                    if (driveList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    @Override
    public void onDriveItemClick(int position, String action) {
        switch (action) {
            case AppKeys.APPLY_DRIVE:
                applyCampusDrive(user_id, driveList.get(position).scheduleid, "1");
                driveList.get(position).eligible = 2;
                campusDriveListAdapter.notifyItemChanged(position);
                break;

            case AppKeys.CANCEL_DRIVE_APPLY:
                applyCampusDrive(user_id, driveList.get(position).scheduleid, "0");
                driveList.get(position).eligible = 1;
                campusDriveListAdapter.notifyItemChanged(position);
                break;
        }
    }

    private void applyCampusDrive(String user_id, String campusschedule_id, String status) {
        Call<PojoNoDataResponse> call = apiService.applyCampusDrive(user_id, campusschedule_id, status);
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isGetMoreApiCalled) {
            isGetMoreApiCalled = true;
            campusDriveListAdapter.showProgressBar(true);
            call.enqueue(new Callback<PojoNoDataResponse>() {
                @Override
                public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                    if (response != null && response.body() != null && getActivity() != null) {
                        if (OrganisationCampusDriveFragment.this != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                    getString(R.string.applied_for_drive_successfully), Toast.LENGTH_LONG,
                                    AppKeys.TOAST_USER);
                    }
                }

                @Override
                public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });
        }
    }

    @Override
    public void onTaggedAssessmentCategoryItemClick(int position, PojoAssessmentCategory pojoAssessmentCategory) {

    }

    @Override
    public void vacancyListAdapterClick(String action, int position) {

    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {

    }
}
