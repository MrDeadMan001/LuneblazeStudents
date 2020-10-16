package com.avadna.luneblaze.activities.settingActivities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationRequestActivity extends AppBaseActivity {
    ActionBar actionBar;

    ApiInterface apiService;
    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;

    TextView tv_description;
    TextInputLayout til_adhaar;
    EditText et_adhaar;
    TextView tv_done_button;

    String user_id;

    PojoUserSettingsResponseData pojoUserSettingsResponseData;
    String status="request";

    ProgressBar pb_loading_content;
    ScrollView sv_parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verification_request);
        setUpActionBar();


        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        initViews();
        setFocusChangeListeners();
        setClickListeners();
        hitGetUserVerificationSettingApi(user_id, "verification");
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.verfication_request));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VerificationRequestActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VerificationRequestActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {


        tv_description = (TextView) findViewById(R.id.tv_description);
        til_adhaar = (TextInputLayout) findViewById(R.id.til_adhaar);
        et_adhaar = (EditText) findViewById(R.id.et_adhaar);
        tv_done_button = (TextView) findViewById(R.id.tv_done_button);
        tv_done_button.setVisibility(View.GONE);

        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        sv_parent = (ScrollView) findViewById(R.id.sv_parent);
        sv_parent.setVisibility(View.GONE);

    }

    private void setFocusChangeListeners() {
        et_adhaar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (et_adhaar.getText().toString().trim().isEmpty()) {
                    til_adhaar.setError(null);
                }
            }
        });
    }

    private void setClickListeners() {
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_adhaar.getText().toString().trim().isEmpty()) {
                    til_adhaar.setError(getString(R.string.please_enter_valid_adhaar_number));
                }
                else {
                    if(status.equals("request")){
                        hitSendVerificationRequestApi(user_id,"send_verification_request");
                    }
                }
            }
        });
    }


    private void hitGetUserVerificationSettingApi(String user_id, String type) {
        Call<PojoUserSettingsResponse> call = apiService.getUserSettings(user_id, type);
        call.enqueue(new Callback<PojoUserSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        tv_done_button.setVisibility(View.VISIBLE);
                        sv_parent.setVisibility(View.VISIBLE);
                        pb_loading_content.setVisibility(View.GONE);
                        status=response.body().data.kase;
                        setValueBasedOnStatus();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }



            @Override
            public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void setValueBasedOnStatus() {
        if (status.equals("request")) {

        } else if (status.equals("pending")) {
            til_adhaar.setVisibility(View.GONE);
            tv_description.setText(getString(R.string.your_verification_request_is_pending));
        }
    }

    private void hitSendVerificationRequestApi(String user_id, final String type) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserSettings(user_id, type, "", "");
        tv_done_button.setVisibility(View.GONE);
        sv_parent.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    tv_done_button.setVisibility(View.GONE);
                    sv_parent.setVisibility(View.GONE);
                    pb_loading_content.setVisibility(View.VISIBLE);

                    status="pending";
                    setValueBasedOnStatus();

                  /*  if (type.equals("send_verification_request")) {
                        pojoUserSettingsResponseData.kase = "pending";
                    } else if (type.equals("cancel_verification_request")) {
                        pojoUserSettingsResponseData.kase = "request";
                    }
                    preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);*/
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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


}
