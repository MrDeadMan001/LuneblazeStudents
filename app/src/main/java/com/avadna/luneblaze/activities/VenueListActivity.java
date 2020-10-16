package com.avadna.luneblaze.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
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


public class VenueListActivity extends AppBaseActivity {


    RecyclerView rv_venue_list;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    ApiInterface apiService;
    List<PojoUserData> receivedUserList;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_venue_list);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initApis();
        initViews();
        initClickListener();
        setClickListener();
    }

    private void initViews() {
        initSuggestedFriendsList();
    }

    private void initClickListener() {

    }

    private void setClickListener() {
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initSuggestedFriendsList() {
        receivedUserList = new ArrayList<>();
        hitSuggestFriendsListApi(user_id);
        rv_venue_list = (RecyclerView) findViewById(R.id.rv_venue_list);
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(VenueListActivity.this,  receivedUserList, "ver");
        rv_venue_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_venue_list.setAdapter(suggestedFriendsAdapter);
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
                    commonFunctions.setToastMessage(VenueListActivity.this, "" + message,
                            Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(VenueListActivity.this, t.toString(),
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });

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
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void hitCancelFriendApi(String user_id, String friend_id, String connection_type) {

        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
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
