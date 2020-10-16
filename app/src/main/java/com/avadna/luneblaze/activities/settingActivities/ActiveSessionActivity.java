package com.avadna.luneblaze.activities.settingActivities;

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
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.ActiveSessionsListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoActiveSessions.PojoActiveSession;
import com.avadna.luneblaze.pojo.pojoActiveSessions.PojoActiveSessionsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveSessionActivity extends AppBaseActivity {
    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ApiInterface apiService;
    RecyclerView rv_list;
    ProgressBar pb_loading;
    ActiveSessionsListAdapter activeSessionsListAdapter;
    List<PojoActiveSession> activeSessions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils=new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id=preferenceUtils.get_user_id();
        initSessionList();
        pb_loading=(ProgressBar)findViewById(R.id.pb_loading);
        hitGetUserActiveSessionsApi(user_id,"active-session");

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
            actionBar.setTitle(getString(R.string.active_sessions));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ActiveSessionActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ActiveSessionActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initSessionList() {
        activeSessions=new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        activeSessionsListAdapter = new ActiveSessionsListAdapter(ActiveSessionActivity.this, activeSessions);
        //using horizontal linearlayout as we want horizontal list
        rv_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_list.setAdapter(activeSessionsListAdapter);
    }

    private void hitGetUserActiveSessionsApi(String user_id, String type) {
        Call<PojoActiveSessionsResponse> call = apiService.getActiveSessions(user_id, type);
        call.enqueue(new Callback<PojoActiveSessionsResponse>() {
            @Override
            public void onResponse(Call<PojoActiveSessionsResponse> call, Response<PojoActiveSessionsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        activeSessions.clear();
                        activeSessions.addAll(response.body().data.sessions);
                        activeSessionsListAdapter.notifyDataSetChanged();

                    }
                    else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }
                    pb_loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoActiveSessionsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.active_sessions));

            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog .setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetUserActiveSessionsApi(user_id,"active-session");
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();connectionErrorDialog.setTitle(title);
        }
        catch (Exception e){

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
    protected void onPause() {
        super.onPause();

    }
}
