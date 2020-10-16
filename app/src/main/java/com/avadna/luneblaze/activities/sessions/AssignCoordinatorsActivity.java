package com.avadna.luneblaze.activities.sessions;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.ConversationAddedUserListAdapter;
import com.avadna.luneblaze.adapters.session.CoodinatorListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueTeam;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignCoordinatorsActivity extends AppBaseActivity
        implements CoodinatorListAdapter.CoordiantorListAdapterCallback,
        ConversationAddedUserListAdapter.AddedRecipientListAdapterCallback {


    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;
    String session_id;
    String venue_id;

    ProgressBar pb_api_status;

    View.OnClickListener onClickListener;
    Toolbar tl_top_bar;
    TextView tv_done;

    /*RecyclerView rv_my_friends_list;
    RecipientSearchResultListAdapter recipientSearchResultListAdapter;
    List<PojoUserData> friendList;*/

    RecyclerView rv_my_friends_list;
    PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    CoodinatorListAdapter coodinatorListAdapter;
    List<PojoVenueTeam> coordinatorList;

    RecyclerView rv_added_userList;
    ConversationAddedUserListAdapter conversationAddedUserListAdapter;
    List<PojoUserData> addedUserList;

    String coor1 = "";
    String coor2 = "";

    RelativeLayout rl_parent_wrapper;



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

        setContentView(R.layout.activity_assign_coordiantors);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        session_id = getIntent().getStringExtra("session_id");
        venue_id = getIntent().getStringExtra("venue_id");
        coor1 = getIntent().getStringExtra("coor1");
        coor2 = getIntent().getStringExtra("coor2");
        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();
        hitGetVenueByIdApi(user_id, venue_id);
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.coordinators));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AssignCoordinatorsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AssignCoordinatorsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        pb_api_status = (ProgressBar) findViewById(R.id.pb_api_status);
        tv_done = (TextView) findViewById(R.id.tv_done);
        rl_parent_wrapper = (RelativeLayout) findViewById(R.id.rl_parent_wrapper);
        rl_parent_wrapper.setVisibility(View.GONE);

        setUpCoordinatorList();
        setUpAddedUserList();
    }

    private void setUpAddedUserList() {
        addedUserList = new ArrayList<>();
        rv_added_userList = (RecyclerView) findViewById(R.id.rv_added_userList);
        conversationAddedUserListAdapter = new ConversationAddedUserListAdapter(AssignCoordinatorsActivity.this, addedUserList);
        rv_added_userList.setLayoutManager(new LinearLayoutManager(AssignCoordinatorsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        rv_added_userList.setAdapter(conversationAddedUserListAdapter);
    }


    private void setUpCoordinatorList() {
        coordinatorList = new ArrayList<>();
        rv_my_friends_list = (RecyclerView) findViewById(R.id.rv_my_friends_list);
        coodinatorListAdapter = new CoodinatorListAdapter(AssignCoordinatorsActivity.this, coordinatorList);
        rv_my_friends_list.setLayoutManager(new LinearLayoutManager(AssignCoordinatorsActivity.this,
                LinearLayoutManager.VERTICAL, false));
        rv_my_friends_list.setAdapter(coodinatorListAdapter);
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_done:
                        assignCoordinators(addedUserList);
                        break;
                    case R.id.iv_group_photo:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(AssignCoordinatorsActivity.this);
                        break;
                }
            }
        };
    }

    private void assignCoordinators(List<PojoUserData> addedUserList) {
        Map<String, String> coordinatorMap = new HashMap<>();
        for (int i = 0; i < addedUserList.size(); i++) {
            String key = "coordinators" + "[" + i + "]";
            coordinatorMap.put(key, addedUserList.get(i).userId);
        }
        hitAddCoordinatorsApi(session_id, coordinatorMap);
    }

    private void setClickListeners() {
        tv_done.setOnClickListener(onClickListener);
        tv_done.setOnClickListener(onClickListener);

    }

    private void hitGetVenueByIdApi(String user_id, String venue_id) {

        boolean allFieldsOk = !venue_id.isEmpty() && !user_id.isEmpty();

        if (allFieldsOk) {
            Call<PojoGetVenueDetailsResponse> call = apiService.getVenueDetailsFromIdApi(user_id, venue_id, "upcoming");
            call.enqueue(new Callback<PojoGetVenueDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoGetVenueDetailsResponse> call, Response<PojoGetVenueDetailsResponse> response) {
                    pb_api_status.setVisibility(View.GONE);
                    String message = "";
                    if (response.body().data != null) {
                        rl_parent_wrapper.setVisibility(View.VISIBLE);
                        message = response.body().message;
                        pojoGetVenueDetailsResponse = response.body();
                        coordinatorList.clear();
                        coordinatorList.addAll(response.body().data.teams);

                        for (int i = 0; i < coordinatorList.size(); i++) {
                            if (coordinatorList.get(i).userId.equals(coor1)) {
                                coordinatorList.get(i).userDetail.selected = true;
                                addedUserList.add(coordinatorList.get(i).userDetail);
                            }
                            if (coordinatorList.get(i).userId.equals(coor2)) {
                                coordinatorList.get(i).userDetail.selected = true;
                                addedUserList.add(coordinatorList.get(i).userDetail);
                            }
                        }

                        conversationAddedUserListAdapter.notifyDataSetChanged();
                        coodinatorListAdapter.notifyDataSetChanged();

                    } else {
                        commonFunctions.setToastMessage(AssignCoordinatorsActivity.this, "" + message,
                                Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoGetVenueDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    openNoConnectionDialog(getString(R.string.coordinators));
                    commonFunctions.setToastMessage(AssignCoordinatorsActivity.this,
                            "" + t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            });
        }
    }

    private void openNoConnectionDialog(String title) {

        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetVenueByIdApi(user_id, venue_id);
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

    /*private void hitGetFriendListApi(String user_id) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id);
        call.enqueue(new Callback<PojoGetFriendsListResponse>() {
            @Override
            public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                String message = response.body().message;
                if (response.body().data != null) {
                    friendList.addAll(response.body().data);
                    for (int i = 0; i < friendList.size(); i++) {
                        //set all new as false as this field is not received from api and is null
                        friendList.get(i).selected = false;
                        for (int j = 0; j < addedUserList.size(); j++) {
                            //set the previously added users as selected
                            if (addedUserList.get(j).userId.equals(friendList.get(i).userId)) {
                                friendList.get(i).selected = addedUserList.get(j).selected;
                            }
                        }
                    }

                    recipientSearchResultListAdapter.notifyDataSetChanged();
                } else {
                    commonFunctions.setToastMessage(AssignCoordinatorsActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(AssignCoordinatorsActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }*/


    private void hitAddCoordinatorsApi(String user_id, Map<String, String> users) {
        Call<PojoNoDataResponse> call = apiService.assignSessionCoordinators(session_id, users);
        rl_parent_wrapper.setVisibility(View.GONE);
        pb_api_status.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call,
                                   Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                commonFunctions.setToastMessage(AssignCoordinatorsActivity.this, message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
              /*  Intent sessionIntent=new Intent(AssignCoordinatorsActivity.this,SessionLoaderActivity.class);
                sessionIntent.putExtra("id",session_id);
                sessionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(sessionIntent);*/


                Intent pushNotification = new Intent(Config.SESSION_RELOAD);
                LocalBroadcastManager.getInstance(AssignCoordinatorsActivity.this)
                        .sendBroadcast(pushNotification);

                Intent updateSessionDetailsIntent = new Intent(Config.SESSION_DETAILS_UPDATED);
                LocalBroadcastManager.getInstance(AssignCoordinatorsActivity.this)
                        .sendBroadcast(updateSessionDetailsIntent);

                finish();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                rl_parent_wrapper.setVisibility(View.VISIBLE);
                pb_api_status.setVisibility(View.GONE);
                commonFunctions.setToastMessage(AssignCoordinatorsActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }


    @Override
    public void onAddedRecipientListMethodCallback(int position) {
        for (int i = 0; i < coordinatorList.size(); i++) {
            for (int j = 0; j < addedUserList.size(); j++) {
                if (coordinatorList.get(i).userDetail.userId.equals(addedUserList.get(j).userId)) {
                    coordinatorList.get(i).userDetail.selected = false;
                    break;
                }
            }
        }

        addedUserList.remove(position);
        coodinatorListAdapter.notifyDataSetChanged();
        conversationAddedUserListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCoordinatorListMethodCallback(int position, PojoVenueTeam item) {
        if (!addedUserList.contains(coordinatorList.get(position).userDetail)) {
            if (addedUserList.size() > 1) {
                commonFunctions.setToastMessage(AssignCoordinatorsActivity.this,
                        getString(R.string.only_two_coordiantor_can_be_added), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            } else {
                coordinatorList.get(position).userDetail.selected = true;
                addedUserList.add(coordinatorList.get(position).userDetail);
            }
        } else {
            coordinatorList.get(position).userDetail.selected = false;
            addedUserList.remove(coordinatorList.get(position).userDetail);
        }
        coodinatorListAdapter.notifyDataSetChanged();
        conversationAddedUserListAdapter.notifyDataSetChanged();
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
