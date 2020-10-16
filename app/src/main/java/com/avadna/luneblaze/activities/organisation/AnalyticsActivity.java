package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.organisation.InterestGraphAdapter;
import com.avadna.luneblaze.adapters.profileInfo.sessions.ProfileInfoAttendedSessionListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGraphInterestItem;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanDetailsData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanDetailsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppBaseActivity {
    ProgressBar pb_loading_content;
    CoordinatorLayout cl_parent;
    AppBarLayout appBarLayout;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String owner_id = "1";
    String organisation_id;


    ActionBar actionBar;
    RecyclerView rv_interest_graph;
    InterestGraphAdapter interestGraphAdapter;
    List<PojoGraphInterestItem> dataList;
    LinearLayout ll_content_wrapper;
    TextView tv_plan_name;
    TextView tv_plan_price;
    TextView tv_per_student_cost;
    TextView tv_current_balance;
    TextView tv_spent_amount;
    TextView tv_total_attendee_count;
    TextView tv_sponsored_session_count;
    LinearLayout ll_session_count_wrapper;
    PojoSponsorPlanDetailsData pojoSponsorPlanDetailsData;
    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        hitGetSponsorAnalytics(organisation_id);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.analytics));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AnalyticsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AnalyticsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_session_count_wrapper:
                        if (pojoSponsorPlanDetailsData != null
                                && !pojoSponsorPlanDetailsData.sponsoredSession.isEmpty()) {
                            openSessionList();
                        }
                        break;
                }
            }
        };
    }


    private void openSessionList() {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.sponsored_sessions));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });

        RecyclerView rv_list = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        rv_list.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        ProfileInfoAttendedSessionListAdapter attendedSessionListAdapter;
        attendedSessionListAdapter = new ProfileInfoAttendedSessionListAdapter(this,
                pojoSponsorPlanDetailsData.sponsoredSession);
        attendedSessionListAdapter.setShowProgress(false);

        rv_list.setAdapter(attendedSessionListAdapter);
        userListDialog.show();
    }

    private void setClickListener() {
        ll_session_count_wrapper.setOnClickListener(onClickListener);
    }

    private void initGraph() {
        dataList = new ArrayList<>();
        rv_interest_graph = (RecyclerView) findViewById(R.id.rv_interest_graph);
        rv_interest_graph.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        interestGraphAdapter = new InterestGraphAdapter(this, dataList, "normal");
        interestGraphAdapter.setTotalVotes("123");
        rv_interest_graph.setAdapter(interestGraphAdapter);
        rv_interest_graph.setNestedScrollingEnabled(false);
    }


    private void hitGetSponsorAnalytics(final String organisation_id) {
        Call<PojoSponsorPlanDetailsResponse> call = apiService.getSponsorPlanDetails(organisation_id);
        call.enqueue(new Callback<PojoSponsorPlanDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSponsorPlanDetailsResponse> call,
                                   Response<PojoSponsorPlanDetailsResponse> response) {
                if (response != null && response.body() != null) {
                    pb_loading_content.setVisibility(View.GONE);
                    String message = response.body().message;
                    if (response.body().data != null) {
                        ll_content_wrapper.setVisibility(View.VISIBLE);
                        pb_loading_content.setVisibility(View.GONE);
                        pojoSponsorPlanDetailsData = response.body().data;
                        setDataOnViews();

                    } else {
                        openNoConnectionDialog(getString(R.string.analytics));
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoSponsorPlanDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.analytics));

                //  cl_parent.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
            }
        });
    }

    private void setDataOnViews() {
        if (pojoSponsorPlanDetailsData != null && pojoSponsorPlanDetailsData.currentplan != null) {
            tv_plan_name.setText("" + pojoSponsorPlanDetailsData.currentplan.planname);
            tv_plan_price.setText("₹" + pojoSponsorPlanDetailsData.currentplan.planprice);
            tv_current_balance.setText("₹" + pojoSponsorPlanDetailsData.currentplan.currentbalance);
            tv_per_student_cost.setText("₹" + pojoSponsorPlanDetailsData.currentplan.costperstudent);
            tv_spent_amount.setText("₹" + pojoSponsorPlanDetailsData.totalamountspent);
/*
            int attendeeCount = 0;
            for (int i = 0; i < pojoSponsorPlanDetailsData.sponsoredSession.size(); i++) {
                int count = Integer.parseInt(pojoSponsorPlanDetailsData.sponsoredSession.get(i).totalAttends);
                attendeeCount = attendeeCount + count;
            }*/

            tv_total_attendee_count.setText("" + pojoSponsorPlanDetailsData.totalattendee);
            if (pojoSponsorPlanDetailsData.sponsoredSession != null) {
                tv_sponsored_session_count.setText("" + pojoSponsorPlanDetailsData.sponsoredSession.size());

            }

            dataList.clear();
            int total = 0;

            if (pojoSponsorPlanDetailsData.interests != null) {
                for (int i = 0; i < pojoSponsorPlanDetailsData.interests.size() && i < 10; i++) {
                    dataList.add(pojoSponsorPlanDetailsData.interests.get(i));
                }
            }
            for (int i = 0; i < dataList.size(); i++) {
                int count = Integer.parseInt(dataList.get(i).cnt);
                total = total + count;
            }

            interestGraphAdapter.setTotalVotes(String.valueOf(total));
            interestGraphAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        ll_content_wrapper.setVisibility(View.GONE);
        tv_plan_name = (TextView) findViewById(R.id.tv_plan_name);
        tv_plan_price = (TextView) findViewById(R.id.tv_plan_price);
        tv_per_student_cost = (TextView) findViewById(R.id.tv_per_student_cost);
        tv_current_balance = (TextView) findViewById(R.id.tv_current_balance);
        tv_spent_amount = (TextView) findViewById(R.id.tv_spent_amount);
        tv_total_attendee_count = (TextView) findViewById(R.id.tv_total_attendee_count);
        tv_sponsored_session_count = (TextView) findViewById(R.id.tv_sponsored_session_count);
        ll_session_count_wrapper = (LinearLayout) findViewById(R.id.ll_session_count_wrapper);

        initGraph();
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog = new ConnectionErrorDialog(AnalyticsActivity.this, R.style.AppTheme,
                    title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSponsorAnalytics(organisation_id);
                    pb_loading_content.setVisibility(View.VISIBLE);
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
}
