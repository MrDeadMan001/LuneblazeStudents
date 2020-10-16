package com.avadna.luneblaze.activities.settingActivities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.ForgotPasswordActivity;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppBaseActivity {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;

    EditText et_current_password, et_new_password, et_confirm_new_password;
    TextView tv_save,tv_forgot_password;
    CommonFunctions commonFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initViews();
        setUpActionBar();

    }

    private void initViews() {
        et_current_password = (EditText) findViewById(R.id.et_current_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_new_password = (EditText) findViewById(R.id.et_confirm_new_password);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetIntent=new Intent(ChangePasswordActivity.this,ForgotPasswordActivity.class);
                startActivity(resetIntent);
            }
        });

        tv_save = (TextView) findViewById(R.id.tv_save);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_current_password.getText().toString().trim().isEmpty()) {
                    commonFunctions.setToastMessage(ChangePasswordActivity.this,
                            getString(R.string.please_enter_current_password), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }
                else if (et_new_password.getText().toString().trim().isEmpty()) {
                    commonFunctions.setToastMessage(ChangePasswordActivity.this,
                            getString(R.string.please_enter_new_password), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }

               else if(!et_new_password.getText().toString().trim().equals(et_confirm_new_password.getText().toString().trim())){
                    commonFunctions.setToastMessage(ChangePasswordActivity.this,
                            getString(R.string.new_confirm_not_same), Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                }
                else {
                    hitUpdatePasswordApi(user_id,"password",
                            et_current_password.getText().toString().trim(),
                            et_new_password.getText().toString().trim(),
                            et_confirm_new_password.getText().toString().trim());
                }
            }


        });
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.change_password));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ChangePasswordActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ChangePasswordActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void hitUpdatePasswordApi(String user_id, String type, String current, String new_pass, String confirm) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.changePassword(user_id, type, current,new_pass, confirm);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
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
