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
import com.avadna.luneblaze.adapters.BlockedListAdapter;
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

public class BlockingListActivity extends AppBaseActivity
        implements BlockedListAdapter.BlockedListAdapterCallback {
    ActionBar actionBar;
    RecyclerView rv_blocked_list;
    LinearLayoutManager linearLayoutManager;
    ProgressBar pb_loading_content;
    BlockedListAdapter blockedListAdapter;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    List<PojoUserData> blockedList;
    int offset = 0;
    boolean isGetBlockApiCalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_blocking_list);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();

        hitGetBlockedListApi(user_id, "blocking", offset);

    }



    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.block_list));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(BlockingListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(BlockingListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        initBlockedList();
    }

    private void initClickListener() {

    }


    private void setClickListener() {
    }


    private void initBlockedList() {
        blockedList = new ArrayList<>();
        rv_blocked_list = (RecyclerView) findViewById(R.id.rv_blocked_list);
        blockedListAdapter = new BlockedListAdapter(blockedList, BlockingListActivity.this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_blocked_list.setLayoutManager(linearLayoutManager);
        rv_blocked_list.setAdapter(blockedListAdapter);

        rv_blocked_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (!isGetBlockApiCalled && linearLayoutManager.findLastVisibleItemPosition() > blockedList.size() - 3) {
                        hitGetBlockedListApi(user_id, "blocking", ++offset);

                    }
                }
            }
        });

    }


    private void hitGetBlockedListApi(String user_id, String type, final int offset) {
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
                                blockedList.clear();
                            }
                            blockedList.addAll(response.body().data.blocks);
                            blockedListAdapter.notifyDataSetChanged();

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                    Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                    pb_loading_content.setVisibility(View.GONE);
                    isGetBlockApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isGetBlockApiCalled = false;
                    if(blockedList.isEmpty()){
                        openNoConnectionDialog(getString(R.string.block_list));
                    }
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
                    hitGetBlockedListApi(user_id, "blocking", offset);
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
    public void blockedListItemClickCallback(int position, PojoUserData currentItem, String type) {
        hitUpdateUserConnectionApi(user_id, currentItem.userId, "unblock", position);
        blockedList.remove(position);
        blockedListAdapter.notifyDataSetChanged();

    }

    private void hitUpdateUserConnectionApi(String user_id, String id, String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

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
