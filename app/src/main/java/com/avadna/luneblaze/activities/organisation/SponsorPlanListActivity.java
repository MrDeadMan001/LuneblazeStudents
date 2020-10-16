package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.organisation.SponsorPlanListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorPlansResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanListData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SponsorPlanListActivity extends AppBaseActivity {

    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ApiInterface apiService;
    RecyclerView rv_plan_list;
    ProgressBar pb_loading;
    SponsorPlanListAdapter sponsorPlanListAdapter;
    List<PojoSponsorPlanListData> planList;
    int offset = 0;
    String organisation_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_plan_list);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        if (organisation_id == null) {
            organisation_id = "0";
        }
        initSessionList();
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        offset = 0;
        hitGetSponsorPlanListApi(offset);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.sponsorship_plans));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SponsorPlanListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SponsorPlanListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initSessionList() {
        planList = new ArrayList<>();
        rv_plan_list = (RecyclerView) findViewById(R.id.rv_plan_list);
        sponsorPlanListAdapter = new SponsorPlanListAdapter(SponsorPlanListActivity.this,
                planList, organisation_id);
        //using horizontal linearlayout as we want horizontal list
        rv_plan_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,
                false));
        rv_plan_list.setAdapter(sponsorPlanListAdapter);
    }

    private void hitGetSponsorPlanListApi(int offset) {
        Call<PojoGetSponsorPlansResponse> call = apiService.getSponsorPlanList(String.valueOf(offset));
        call.enqueue(new Callback<PojoGetSponsorPlansResponse>() {
            @Override
            public void onResponse(Call<PojoGetSponsorPlansResponse> call, Response<PojoGetSponsorPlansResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (offset == 0) {
                            planList.clear();
                        }
                        planList.addAll(response.body().data);
                        sponsorPlanListAdapter.notifyDataSetChanged();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    pb_loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoGetSponsorPlansResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.active_sessions));

            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    offset = 0;
                    hitGetSponsorPlanListApi(offset);
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKeys.SPONSOR_PLAN_SET) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}


