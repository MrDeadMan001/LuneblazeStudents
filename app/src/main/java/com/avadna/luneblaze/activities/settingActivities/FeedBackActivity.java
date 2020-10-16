package com.avadna.luneblaze.activities.settingActivities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackActivity extends AppBaseActivity {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    Spinner sp_feedback_list;
    ArrayAdapter<String> feedbackSpinnerAdapter;
    List<String> feedbackListOptions;

    EditText et_description;
    TextView tv_submit;

    RelativeLayout rl_parent_wrapper;
    ProgressBar pb_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed_back);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initFeedbackListSpinner();
    }

    private void initViews() {
        et_description = (EditText) findViewById(R.id.et_description);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        rl_parent_wrapper= (RelativeLayout) findViewById(R.id.rl_parent_wrapper);
        pb_loading= (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitSubmitFeedbackApi(user_id, "feedback",
                        et_description.getText().toString().trim(),
                        feedbackListOptions.get(sp_feedback_list.getSelectedItemPosition()));
            }
        });
    }


    private void hitSubmitFeedbackApi(String user_id, String type, String feedback, String section) {

        pb_loading.setVisibility(View.VISIBLE);
        rl_parent_wrapper.setVisibility(View.GONE);
        tv_submit.setVisibility(View.GONE);
        section=section.toLowerCase();

        Call<PojoUpdateAppSettingsResponse> call = apiService.submitFeedback(user_id, type, feedback, section);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),getString(R.string.thanks_for_feedback),
                            Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                    finish();
                }
                else {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                    pb_loading.setVisibility(View.GONE);
                    rl_parent_wrapper.setVisibility(View.VISIBLE);
                    tv_submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                pb_loading.setVisibility(View.GONE);
                rl_parent_wrapper.setVisibility(View.VISIBLE);
                tv_submit.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.feedback));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(FeedBackActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(FeedBackActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initFeedbackListSpinner() {
        sp_feedback_list = (Spinner) findViewById(R.id.sp_feedback_list);
        feedbackListOptions = new ArrayList<String>();
        feedbackListOptions.add(getString(R.string.interest));
        feedbackListOptions.add(getString(R.string.question));
        feedbackListOptions.add(getString(R.string.session));
        feedbackListOptions.add(getString(R.string.post));
        feedbackListOptions.add(getString(R.string.venue));
        feedbackListOptions.add(getString(R.string.article));
        feedbackListOptions.add(getString(R.string.poll));

        feedbackSpinnerAdapter = new ArrayAdapter<String>(FeedBackActivity.this, R.layout.custom_spinner_dropdown_item, feedbackListOptions);
        feedbackSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_feedback_list.setAdapter(feedbackSpinnerAdapter);
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
