package com.avadna.luneblaze.activities.assessment;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.assessment.AssessmentTypeListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentListResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessmentListActivity extends AppBaseActivity {

    private static final int NEW_PAYMENT_CODE = 1234;
   private ActionBar actionBar;
   private ApiInterface apiService;
   private PreferenceUtils preferenceUtils;
   private CommonFunctions commonFunctions;
   private String user_id = "";
   private RecyclerView rv_assessment_list;
   private AssessmentTypeListAdapter assessmentTypeListAdapter;
   private List<PojoAssessmentListResponseData> assessmentList;
   private ProgressBar pb_loading_content;
   private TextView tv_no_assessment;
   private RelativeLayout rl_content_wrapper;
   private TextView tv_new_assessment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(AssessmentListActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();

    }

    @Override
    protected void onResume(){
        super.onResume();
        hitGetAssessmentListApi(user_id);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.my_assessments));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AssessmentListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AssessmentListActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        tv_new_assessment = (TextView) findViewById(R.id.tv_new_assessment);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        tv_no_assessment= (TextView) findViewById(R.id.tv_no_assessment);
        tv_no_assessment.setVisibility(View.GONE);
        rv_assessment_list = (RecyclerView) findViewById(R.id.rv_assessment_list);
        assessmentList = new ArrayList<>();
        assessmentTypeListAdapter = new AssessmentTypeListAdapter(AssessmentListActivity.this, assessmentList);
        //using horizontal linearlayout as we want horizontal list
        rv_assessment_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_assessment_list.setAdapter(assessmentTypeListAdapter);

        tv_new_assessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assessmentIntent = new Intent(AssessmentListActivity.this, AssessmentStatusActivity.class);
                startActivityForResult(assessmentIntent, NEW_PAYMENT_CODE);
            }
        });
    }


    private void hitGetAssessmentListApi(String user_id) {
        pb_loading_content.setVisibility(View.VISIBLE);
        rl_content_wrapper.setVisibility(View.GONE);
        tv_no_assessment.setVisibility(View.GONE);
        Call<PojoAssessmentListResponse> call = apiService.getAssessmentList(user_id);
        call.enqueue(new Callback<PojoAssessmentListResponse>() {
            @Override
            public void onResponse(Call<PojoAssessmentListResponse> call, Response<PojoAssessmentListResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        assessmentList.clear();
                        assessmentList.addAll(response.body().data);
                        assessmentTypeListAdapter.notifyDataSetChanged();
                    } else {
                        tv_no_assessment.setVisibility(View.VISIBLE);
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    pb_loading_content.setVisibility(View.GONE);
                    rl_content_wrapper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoAssessmentListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.my_assessments));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PAYMENT_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                hitGetAssessmentListApi(user_id);
            }
        }
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetAssessmentListApi(user_id);
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
