package com.avadna.luneblaze.activities.profileInfo;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.profileInfo.UserVenueListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetUserVenueResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserVenue;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserVenueListActivity extends AppBaseActivity {
    ActionBar actionBar;


    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    String targetUserId;
    ProgressBar pb_loading_content;
    TextView tv_no_venues;

    RecyclerView rv_list;
    List<PojoUserVenue> venueList;
    UserVenueListAdapter userVenueListAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_venue_list);
        setUpActionBar();
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        targetUserId = getIntent().getStringExtra("target_user_id");
        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        initViews();
        hitGetUserProfileApi(user_id, targetUserId, "user_venue");


    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.venues));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(UserVenueListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(UserVenueListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        initVenueList();
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        tv_no_venues = (TextView) findViewById(R.id.tv_no_venues);
        tv_no_venues.setVisibility(View.GONE);
    }

    private void initVenueList() {
        venueList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        userVenueListAdapter = new UserVenueListAdapter(this, venueList);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(userVenueListAdapter);
    }

    private void hitGetUserProfileApi(String userId, String targetUserId, String type) {
        Call<PojoGetUserVenueResponse> call = apiService.getUserProfileVenue(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoGetUserVenueResponse>() {
            @Override
            public void onResponse(Call<PojoGetUserVenueResponse> call, Response<PojoGetUserVenueResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        venueList.clear();
                        venueList.addAll(response.body().data.profile.userVenues);
                        if (venueList.isEmpty()) {
                            tv_no_venues.setVisibility(View.VISIBLE);
                        }
                        actionBar.setTitle(getString(R.string.venues) + " (" + venueList.size() + ")");
                        userVenueListAdapter.notifyDataSetChanged();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                //  ll_content_wrapper.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoGetUserVenueResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //  ll_content_wrapper.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
