package com.avadna.luneblaze.activities.settingActivities;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.organisation.MyOrganisationsListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoMyOrganizationListItem;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoUserOrganisationListResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrganisationsListActivity extends AppBaseActivity implements
        MyOrganisationsListAdapter.MyOrganisationsListAdapterCallback {

    RecyclerView rv_organisations_list;
    List<PojoMyOrganizationListItem> oraganisationList;
    MyOrganisationsListAdapter myOrganisationsListAdapter;
    LinearLayoutManager linearLayoutManager;

    ProgressBar pb_loading_content;
    TextView tv_no_organisations;

    ActionBar actionBar;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_organisations_list);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        setUpActionBar();
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(this);
        initViews();
        hitGetUserOrganisationList(user_id, "my_organisations");

    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        tv_no_organisations = (TextView) findViewById(R.id.tv_no_organisations);
        initOrganisationList();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.my_organisations));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(MyOrganisationsListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(MyOrganisationsListActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initOrganisationList() {
        oraganisationList = new ArrayList<>();
        rv_organisations_list = (RecyclerView) findViewById(R.id.rv_organisations_list);
        myOrganisationsListAdapter = new MyOrganisationsListAdapter(this, oraganisationList);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_organisations_list.setLayoutManager(linearLayoutManager);
        rv_organisations_list.setAdapter(myOrganisationsListAdapter);
        myOrganisationsListAdapter.setShowProgressBar(false);

    }


    private void hitGetUserOrganisationList(final String user_id, String type) {
        Call<PojoUserOrganisationListResponse> call = apiService.getUserOrganisations(user_id, type);
        tv_no_organisations.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoUserOrganisationListResponse>() {
            @Override
            public void onResponse(Call<PojoUserOrganisationListResponse> call, Response<PojoUserOrganisationListResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pb_loading_content.setVisibility(View.GONE);

                        if (response.body().data.organization.isEmpty()) {
                            tv_no_organisations.setVisibility(View.VISIBLE);
                        } else {
                            oraganisationList.clear();
                            oraganisationList.addAll(response.body().data.organization);
                            myOrganisationsListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserOrganisationListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetUserOrganisationList(user_id, "my_organisations");
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

    @Override
    public void organisationSearchClickMethod(int position, PojoMyOrganizationListItem pojoSearchOrganisation, String type) {

    }
}
