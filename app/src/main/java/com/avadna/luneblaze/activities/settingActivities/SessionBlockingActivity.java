package com.avadna.luneblaze.activities.settingActivities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
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
import com.avadna.luneblaze.adapters.SessionBlockingListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionBlockingActivity extends AppBaseActivity
        implements SessionBlockingListAdapter.SessionBlockedListAdapterCallback {
    ActionBar actionBar;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    RecyclerView rv_list;
    LinearLayoutManager linearLayoutManager;
    List<PojoUserData> sessionBlockingUserList;
    SessionBlockingListAdapter sessionBlockingListAdapter;
    ProgressBar pb_loading_users;
    int offset = 0;
    boolean isGetBlockApiCalled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session_blocking);
        setUpActionBar();
        initViews();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initList();
        hitGetsessionBlockingUserListApi(user_id, "session_block", offset);

    }

    private void initViews() {
        pb_loading_users = (ProgressBar) findViewById(R.id.pb_loading_users);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.session_blocking));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionBlockingActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionBlockingActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initList() {
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        rv_list.setPadding(16, 32, 16, 32);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        sessionBlockingUserList = new ArrayList<>();
        sessionBlockingListAdapter = new SessionBlockingListAdapter(SessionBlockingActivity.this,
                sessionBlockingUserList);
        rv_list.setAdapter(sessionBlockingListAdapter);
        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (!isGetBlockApiCalled && linearLayoutManager.findLastVisibleItemPosition() > sessionBlockingUserList.size() - 3) {
                        hitGetsessionBlockingUserListApi(user_id, "session_block", ++offset);

                    }
                }
            }
        });

        offset = 0;
    }

    private void hitGetsessionBlockingUserListApi(String user_id, String type, final int offset) {
        Call<PojoUserSettingsResponse> call = apiService.getBlockingList(user_id, type, String.valueOf(offset));
        if (!isGetBlockApiCalled) {
            isGetBlockApiCalled = true;
            call.enqueue(new Callback<PojoUserSettingsResponse>() {
                @Override
                public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                    if (response != null && response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                sessionBlockingUserList.clear();
                            }
                            sessionBlockingUserList.addAll(response.body().data.blocks);
                            sessionBlockingListAdapter.notifyDataSetChanged();

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                    Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    pb_loading_users.setVisibility(View.GONE);
                    isGetBlockApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isGetBlockApiCalled = false;
                    openNoConnectionDialog(getString(R.string.block_list));

                }
            });
        }

    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog .setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    offset=0;
                    hitGetsessionBlockingUserListApi(user_id, "session_block", offset);
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
    public void sessionBlockedListItemClickCallback(int position, PojoUserData currentItem, String type) {
        hitUpdateBlockingStatusApi(user_id, currentItem.userId, "session_unblock", position);
        sessionBlockingUserList.remove(position);
        sessionBlockingListAdapter.notifyDataSetChanged();

    }

    public void hitUpdateBlockingStatusApi(String user_id, String friend_id, final String connection_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
    }
}
