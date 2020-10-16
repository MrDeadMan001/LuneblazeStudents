package com.avadna.luneblaze.activities.organisation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.organisation.SponsorSessionListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorSessionListResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorSessionListItem;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SponsorSessionsListActivity extends AppBaseActivity implements
        SponsorSessionListAdapter.SessionSponsorAdapterCallback {
    ActionBar actionBar;

    SwipeRefreshLayout srl_refresh;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ProgressBar pb_loading_content;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_session_list;
    SponsorSessionListAdapter sponsorSessionListAdapter;
    LinearLayoutManager sessionListLayoutManager;
    boolean isSessionApiCalled = false;
    List<PojoSponsorSessionListItem> assignedSessions = new ArrayList<>();


    String organisation_id = "2";
    int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_sessions_list);

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initSessionList();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        offset = 0;
        hitGetAllSessionsApi(user_id, offset);
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.available_sessions));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SponsorSessionsListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SponsorSessionsListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void hitGetAllSessionsApi(final String user_id, int offset) {
        ll_no_connection_wrapper.setVisibility(View.GONE);

        Call<PojoGetSponsorSessionListResponse> call = apiService.getSponsorSessionList(user_id,
                String.valueOf(offset));
        sponsorSessionListAdapter.setShowMoreProgressBar(true);
        sponsorSessionListAdapter.setShowNoMorePresent(false);

        call.enqueue(new Callback<PojoGetSponsorSessionListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSponsorSessionListResponse> call,
                                   Response<PojoGetSponsorSessionListResponse> response) {
                String message = "";
                if (response.body() != null && response.body().data != null) {
                    if (offset == 0) {
                        assignedSessions.clear();
                        if (response.body().data.isEmpty()) {
                            sponsorSessionListAdapter.setShowNoMorePresent(true);
                        }
                    } else if (response.body().data.size() < 10) {
                        sponsorSessionListAdapter.setShowNoMorePresent(true);
                    }
                    isSessionApiCalled = false;
                    for (int i = 0; i < response.body().data.size(); i++) {
                        response.body().data.get(i).sponsored = false;
                    }
                    assignedSessions.addAll(response.body().data);
                    sponsorSessionListAdapter.notifyDataSetChanged();

                }
                srl_refresh.setRefreshing(false);
                pb_loading_content.setVisibility(View.GONE);
                sponsorSessionListAdapter.setShowMoreProgressBar(false);
            }

            @Override
            public void onFailure(Call<PojoGetSponsorSessionListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                srl_refresh.setRefreshing(false);
                isSessionApiCalled = false;
                if (assignedSessions.isEmpty()) {
                    pb_loading_content.setVisibility(View.GONE);
                    ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    sponsorSessionListAdapter.setShowMoreProgressBar(false);
                }
            }
        });


    }


    private void initViews() {

        ll_no_connection_wrapper = (LinearLayout) findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitGetAllSessionsApi(user_id, offset);
            }
        });

        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                hitGetAllSessionsApi(user_id, offset);
            }
        });

    }

    private void initSessionList() {

        assignedSessions = new ArrayList<>();
        rv_session_list = (RecyclerView) findViewById(R.id.rv_session_list);
        rv_session_list.setNestedScrollingEnabled(true);
        sponsorSessionListAdapter = new SponsorSessionListAdapter(this, assignedSessions);
        sessionListLayoutManager = new LinearLayoutManager(SponsorSessionsListActivity.this,
                RecyclerView.VERTICAL, false);
        rv_session_list.setLayoutManager(sessionListLayoutManager);

        rv_session_list.setAdapter(sponsorSessionListAdapter);

        rv_session_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 5) {
                    if (!isSessionApiCalled && sessionListLayoutManager.findLastCompletelyVisibleItemPosition()
                            > assignedSessions.size() - 2) {
                        hitGetAllSessionsApi(user_id, ++offset);

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
    public void onSessionSponsorAdapterMethod(int position, String type) {
        switch (type) {
            case AppKeys.SPONSOR:
                if (!assignedSessions.get(position).sponsored) {
                   openConfirmationDialog(position);
                }
                break;
        }
    }

    public void openConfirmationDialog(int position) {
        TextView tv_dialog_description, tv_yes, tv_no;
        final Dialog confirmationDialog = new MyCustomThemeDialog(SponsorSessionsListActivity.this,
                R.style.NoTitleDialogTheme);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);


        String sessionName="";
        sessionName=assignedSessions.get(position).title;
        tv_dialog_description.setText(getString(R.string.are_sure_to_sponsor_session)+" "+sessionName);
        confirmationDialog.setCancelable(false);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitSponsorSessionApi(organisation_id, assignedSessions.get(position).sessionsId, position);
                commonFunctions.openProgressDialog();
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }

    private void hitSponsorSessionApi(String sponsor_id, String session_id, int position) {
        Call<PojoNoDataResponse> call = apiService.sponsorSession(sponsor_id, session_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                String message = "";
                if (response != null && response.body() != null) {
                    if (response.body().status == 0) {
                        if (response.body().message.contains("No current plan")) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_sponsorship_plan),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                            Intent planListIntent = new Intent(SponsorSessionsListActivity.this,
                                    SponsorPlanListActivity.class);
                            planListIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                            startActivity(planListIntent);
                        }
                        if (response.body().message.contains("sufficient balance")) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.not_enough_balance),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                            Intent planListIntent = new Intent(SponsorSessionsListActivity.this,
                                    SponsorPlanListActivity.class);
                            planListIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                            startActivity(planListIntent);
                        }
                    }
                    if (response.body().status == 1) {
                        assignedSessions.get(position).sponsored = true;
                        sponsorSessionListAdapter.notifyDataSetChanged();
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.sponsored_successfully),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
                message = response.body().message;
                commonFunctions.setToastMessage(getApplicationContext(), message,
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }


            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

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
}
