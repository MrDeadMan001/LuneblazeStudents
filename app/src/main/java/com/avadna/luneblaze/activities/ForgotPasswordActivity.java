package com.avadna.luneblaze.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppBaseActivity {

    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;

    private LinearLayout ll_data_wrapper;
    private RelativeLayout rl_parent_wrapper;

    private TextInputLayout til_email, til_reset_key, til_new_password, til_confirm_password;
    private EditText et_email, et_reset_key, et_new_password, et_confirm_password;
    private TextView tv_login;
    private TextView tv_save;

    private ProgressBar pb_loading;

    private String phone = "";

    private boolean keySent = false;
    private MySMSBroadcastReceiver mySMSBroadcastReceiver;
    private String otpSmsPrefix = "<#> Your OTP for password reset process is ";


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

        setContentView(R.layout.activity_forgot_password);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initViews();
        setUpActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(ForgotPasswordActivity.this, R.color.colorPrimaryDark));
        }
    }

    private void initViews() {
        rl_parent_wrapper = (RelativeLayout) findViewById(R.id.rl_parent_wrapper);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);
        et_email = (EditText) findViewById(R.id.et_email);
        et_reset_key = (EditText) findViewById(R.id.et_reset_key);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_reset_key = (TextInputLayout) findViewById(R.id.til_reset_key);
        til_new_password = (TextInputLayout) findViewById(R.id.til_new_password);
        til_confirm_password = (TextInputLayout) findViewById(R.id.til_confirm_password);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_save = (TextView) findViewById(R.id.tv_save);
        ll_data_wrapper = (LinearLayout) findViewById(R.id.ll_data_wrapper);
        ll_data_wrapper.setVisibility(View.GONE);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivityIntent = new Intent(ForgotPasswordActivity.this,
                        LoginActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(loginActivityIntent);
            }
        });

        tv_save.setText(getString(R.string.send_key));

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (keySent) {
                    if (et_reset_key.getText().toString().trim().isEmpty()) {
                        til_reset_key.setError(getString(R.string.please_enter_reset_key));
                        //   commonFunctions.setToastMessage(getApplicationContext(), R.string.please_enter_reset_key, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    } else if (et_new_password.getText().toString().trim().isEmpty()) {
                        til_new_password.setError(getString(R.string.please_enter_new_password));
                        //commonFunctions.setToastMessage(getApplicationContext(), R.string.please_enter_new_password, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    } else if (!et_new_password.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
                        til_new_password.setError(getString(R.string.new_confirm_not_same));
                        // commonFunctions.setToastMessage(getApplicationContext(), R.string.new_confirm_not_same, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    } else {
                        hitResetPasswordWithKeyApi(et_email.getText().toString().trim(),
                                et_reset_key.getText().toString().trim(),
                                et_new_password.getText().toString().trim(),
                                et_confirm_password.getText().toString().trim());
                        til_reset_key.setError(null);
                        til_new_password.setError(null);
                        til_new_password.setError(null);
                    }
                } else {
                    Pattern p = Pattern.compile("[a-z]");
                    String enteredString = "";
                    enteredString = et_email.getText().toString().trim();
                    if (enteredString.length() == 0) {
                        til_email.setError(getString(R.string.please_enter_email_or_phone));
                    }
                    //if its an email
                    else if (p.matcher(enteredString).find()) {
                        //check for valid email address
                        if (!Patterns.EMAIL_ADDRESS.matcher(enteredString).matches()) {
                            til_email.setError(getString(R.string.please_enter_valid_email));
                        } else {
                            til_email.setError(null);
                            hitSendResetKeyApi(enteredString, null);
                        }
                    }
                    //if its a phone number
                    else {
                        if (!Patterns.PHONE.matcher(enteredString).matches() || enteredString.length() != 10) {
                            til_email.setError(getString(R.string.please_enter_valid_phone));
                        } else {

                            til_email.setError(null);
                            hitSendResetKeyApi(null, enteredString);
                            phone = enteredString;

                            mySMSBroadcastReceiver = new MySMSBroadcastReceiver();
                            SmsRetrieverClient client = SmsRetriever.getClient(ForgotPasswordActivity.this.getApplicationContext() /* context */);
                            Task<Void> task = client.startSmsRetriever();
                            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mySMSBroadcastReceiver.bindListener(new MySMSBroadcastReceiver.SmsListener() {
                                        @Override
                                        public void messageReceived(String messageText) {
                                            String otp = messageText;
                                            otp = otp.replace(otpSmsPrefix, "");
                                            otp = otp.replace(AppKeys.otpSmsSuffix, "");
                                            et_reset_key.setText(otp);
                                        }
                                    });
                                    registerReceiver(mySMSBroadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
                                }
                            });

                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to start retriever, inspect Exception for more details
                                    // ...
                                }
                            });
                        }
                    }

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
            actionBar.setTitle(getString(R.string.reset_password));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ForgotPasswordActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ForgotPasswordActivity.this, R.color.colorPrimaryDark));
            }
        }
    }


    private void hitSendResetKeyApi(final String email, final String phone) {
        Call<PojoNoDataResponse> call = apiService.sendResetKeyToMail(email, phone);
        commonFunctions.openProgressDialog(getString(R.string.sending_reset_token));
       /* rl_parent_wrapper.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);*/

        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();

                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().status == 1) {
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.reset_key_sent_to_email),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        keySent = true;
                        et_email.setEnabled(false);
                        ll_data_wrapper.setVisibility(View.VISIBLE);
                     /*   rl_parent_wrapper.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);*/
                        tv_save.setText(getString(R.string.reset_password));
                    } else {
                        til_email.setError(message);
                        /*rl_parent_wrapper.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);*/
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_valid_email_or_phone),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
                ll_data_wrapper.setVisibility(View.GONE);
                commonFunctions.closeProgressDialog();
          /*      rl_parent_wrapper.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);*/
                tv_save.setText(getString(R.string.send_key));

            }
        });
    }

    private void hitResetPasswordWithKeyApi(String email, String key, String new_pass, String confirm) {
        Pattern p = Pattern.compile("[a-z]");

        Call<PojoNoDataResponse> call;
        if (p.matcher(email).find()) {
            call = apiService.resetPasswordWithKey(email, null, key, new_pass, confirm);
        } else {
            call = apiService.resetPasswordWithKey(null, email, key, new_pass, confirm);
        }
        commonFunctions.openProgressDialog(getString(R.string.resetting_password));
     /*   rl_parent_wrapper.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);*/
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().status == 1) {
                        commonFunctions.closeProgressDialog();
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    /*    rl_parent_wrapper.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);*/
                        finish();
                    } else {
                        commonFunctions.closeProgressDialog();
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                     /*   rl_parent_wrapper.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);*/
                    }

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

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
