package com.avadna.luneblaze.activities.profileInfo;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.registration.InterestHierarchyActivity;
import com.avadna.luneblaze.adapters.FollowingInterestListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetUserInterestsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoRelatedInterestArr;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInterestListActivity extends AppBaseActivity {

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String targetUserId;

    LinearLayout ll_content_wrapper;
    ProgressBar pb_loading_content;
    TextView tv_no_interests, tv_add_interests;

    RecyclerView rv_interest_list;
    FollowingInterestListAdapter followingInterestListAdapter;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;
    List<PojoRelatedInterestArr> relatedInterestArr;

    List<PojoGetInterestListResponseDataListItem> selectedInterestList = new ArrayList<>();
    private static final int SELECT_INTEREST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interest_list);

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        targetUserId = getIntent().getStringExtra("target_user_id");

        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        setUpActionBar();
        // setImagePicker();
        initViews();
        initInterestList();
        hitGetUserProfileApi(user_id, targetUserId, "interests");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViews() {
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        ll_content_wrapper.setVisibility(View.GONE);
        tv_no_interests = (TextView) findViewById(R.id.tv_no_interests);
        tv_add_interests = (TextView) findViewById(R.id.tv_add_interests);

        tv_no_interests.setVisibility(View.GONE);
        tv_add_interests.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
    }


    private void initInterestList() {
        relatedInterestArr = new ArrayList<>();
        rv_interest_list = (RecyclerView) findViewById(R.id.rv_interest_list);
        followingInterestListAdapter = new FollowingInterestListAdapter(1, relatedInterestArr, this);
        rv_interest_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_interest_list.setAdapter(followingInterestListAdapter);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.interests));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(UserInterestListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(UserInterestListActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void hitGetUserProfileApi(final String user_id, final String targetUserId, String type) {
        Call<PojoGetUserInterestsResponse> call = apiService.getUserInterests(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoGetUserInterestsResponse>() {
            @Override
            public void onResponse(Call<PojoGetUserInterestsResponse> call, Response<PojoGetUserInterestsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        relatedInterestArr.clear();
                        relatedInterestArr.addAll(response.body().data.sessionInterestArr);
                        followingInterestListAdapter.notifyDataSetChanged();
                        if (relatedInterestArr.isEmpty()) {
                            tv_no_interests.setVisibility(View.VISIBLE);
                            if (user_id.equals(targetUserId)) {
                                tv_add_interests.setVisibility(View.VISIBLE);
                                tv_add_interests.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent pickInterestIntent = new Intent(UserInterestListActivity.this,
                                                InterestHierarchyActivity.class);
                                        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                                        }.getType();
                                        Gson gson = new Gson();

                                        String dataStr = gson.toJson(selectedInterestList, type);
                                        pickInterestIntent.putExtra("data", dataStr);
                                        pickInterestIntent.putExtra("type", AppKeys.SELECT_INTEREST);
                                        startActivityForResult(pickInterestIntent, SELECT_INTEREST_CODE);
                                    }
                                });
                            }
                        }
                        else {
                            tv_no_interests.setVisibility(View.GONE);
                            tv_add_interests.setVisibility(View.GONE);
                        }

                        pb_loading_content.setVisibility(View.GONE);
                        ll_content_wrapper.setVisibility(View.VISIBLE);
                        // tv_num_interests.setText(""+relatedInterestArr.size()+" "+getString(R.string.interests));
                        actionBar.setTitle(getString(R.string.interests) + " (" + relatedInterestArr.size() + ")");
                    }
                }

            }

            @Override
            public void onFailure(Call<PojoGetUserInterestsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SELECT_INTEREST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                }.getType();
                String dataStr = data.getStringExtra("data");
                ArrayList<PojoGetInterestListResponseDataListItem> tempList;
                Gson gson = new Gson();
                tempList = gson.fromJson(dataStr, type);
                selectedInterestList.clear();
                selectedInterestList.addAll(tempList);

                if (user_id.equals(targetUserId)) {
                    hitUpdateUserInterest(user_id, selectedInterestList);
                }

            }
        }
    }

    private void hitUpdateUserInterest(final String user_id, List<PojoGetInterestListResponseDataListItem> selectedInterestList) {

        pb_loading_content.setVisibility(View.VISIBLE);
        ll_content_wrapper.setVisibility(View.GONE);

        HashMap<String, String> interestMap = new HashMap<>();
        for (int i = 0; i < selectedInterestList.size(); i++) {
            interestMap.put("interest_ids[" + i + "]", selectedInterestList.get(i).interestId);
        }
        Call<PojoNoDataResponse> call = apiService.updateUserInterest(user_id, interestMap);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                hitGetUserProfileApi(user_id, targetUserId, "interests");

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


}
