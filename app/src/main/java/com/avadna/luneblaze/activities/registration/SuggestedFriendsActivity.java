package com.avadna.luneblaze.activities.registration;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestedFriendsActivity extends AppBaseActivity {

    ActionBar actionBar;

    RecyclerView rv_suggested_friends_list;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    ApiInterface apiService;
    List<PojoUserData> receivedUserList;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    TextView tv_next;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_friends);
        setUpActionBar();
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initApis();
        initViews();
        initClickListener();
        setClickListener();
        setTextWatcher();
        hitSuggestFriendsListApi(user_id);
    }



    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            //   actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.suggested_fellows));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SuggestedFriendsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SuggestedFriendsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_next = (TextView) findViewById(R.id.tv_next);

        initSuggestedFriendsList();
    }

    private void initClickListener() {

    }

    private void setTextWatcher() {

    }

    private void setClickListener() {
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(SuggestedFriendsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initSuggestedFriendsList() {
        receivedUserList = new ArrayList<>();
        rv_suggested_friends_list = (RecyclerView) findViewById(R.id.rv_suggested_friends_list);
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(SuggestedFriendsActivity.this, receivedUserList, "ver");
        rv_suggested_friends_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_suggested_friends_list.setAdapter(suggestedFriendsAdapter);
    }

    private void hitSuggestFriendsListApi(String user_id) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id, "0");
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {

                String message = response.body().message;
                if (response.body().data != null) {
                    receivedUserList.addAll(response.body().data);
                    suggestedFriendsAdapter.notifyDataSetChanged();
                } else {
                    commonFunctions.setToastMessage(SuggestedFriendsActivity.this, "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(SuggestedFriendsActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.suggested_fellows));
            }
        });

    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitSuggestFriendsListApi(user_id);
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

    public void hitAddFriendApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.addFriendApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

}
