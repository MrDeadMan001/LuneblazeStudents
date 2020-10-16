package com.avadna.luneblaze.activities;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.settingActivities.AddedPhoneNumbersActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.rest.SMSApiClient;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.security.SecureRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionAcceptFormActivity extends AppBaseActivity {
    private static final int PHONE_VERIFIED = 1;
    ActionBar actionBar;
    ApiInterface apiService;
    ApiInterface SMSapiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String session_id;

    EditText et_accepting_reason;
    EditText et_session_highlights;
    EditText et_num_attendees;
    TextView tv_done_button;
    String phone = "";
    ProgressBar pb_loading;
    ScrollView sv_parent_wrapper;

    private static final int REQUEST_CODE_READ_SMS = 123;

    String numAttendees;

    Dialog otpDialog;
    boolean otpNeeded = true;

    MySMSBroadcastReceiver mySMSBroadcastReceiver;

    String otpSmsPrefix = "<#> Your OTP for session acceptance is ";


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

        setContentView(R.layout.activity_session_accept);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        SMSapiService = SMSApiClient.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        session_id = getIntent().getStringExtra("id");
        initViews();
    }

    private void initViews() {
        et_accepting_reason = (EditText) findViewById(R.id.et_accepting_reason);
        et_session_highlights = (EditText) findViewById(R.id.et_session_highlights);
        et_num_attendees = (EditText) findViewById(R.id.et_num_attendees);
        tv_done_button = (TextView) findViewById(R.id.tv_done_button);
        sv_parent_wrapper = (ScrollView) findViewById(R.id.sv_parent_wrapper);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);

        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
    }

    private void checkData() {
        if (et_accepting_reason.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_accept_reason), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        } else if (et_session_highlights.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_session_highlights), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        } else if (et_num_attendees.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_num_attendees), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        } else if (Math.abs(Integer.parseInt(et_num_attendees.getText().toString())) <= 20) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.min_20_attendees), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        } else {
            int attendees = Integer.parseInt(et_num_attendees.getText().toString());
            attendees = Math.abs(attendees);
            if (attendees >= 20) {
                numAttendees = String.valueOf(attendees);
                commonFunctions.openProgressDialog();
                hitGetUserPhoneEmailApi(user_id);
            }

        }
    }

    private void hitGetUserPhoneEmailApi(final String user_id) {
        Call<PojoUserEmailPhoneResponse> call = apiService.getUserPhoneEmail(user_id);
        call.enqueue(new Callback<PojoUserEmailPhoneResponse>() {
            @Override
            public void onResponse(Call<PojoUserEmailPhoneResponse> call, Response<PojoUserEmailPhoneResponse> response) {
                if (response != null && response.body() != null) {

                    commonFunctions.closeProgressDialog();
                    boolean verfiedPresent = false;
                    boolean primaryPresent = false;

                    for (int i = 0; i < response.body().data.mobile.size(); i++) {

                        if (response.body().data.mobile.get(i).status.equals("1")) {
                            verfiedPresent = true;
                        }
                        if (response.body().data.mobile.get(i).primary.equals("1")) {
                            phone = response.body().data.mobile.get(i).data;
                            primaryPresent = true;
                            verfiedPresent = true;
                            break;
                        }
                    }
                    if (!verfiedPresent) {
                        openMessageDialog(getString(R.string.you_dont_have_a_verified_phone));
                    } else if (!primaryPresent) {
                        openMessageDialog(getString(R.string.you_dont_have_a_primary_phone));
                    } else if (otpNeeded) {
                        String OTP = "" + generateRandomNumber();
                        hitSendOtpApi(phone, OTP);
                        openOtpDialog(phone, OTP);
                        //  requestSmsPermission();
                    } else {
                        hitSessionAcceptApi("session_accept", user_id, session_id,
                                et_accepting_reason.getText().toString().trim(),
                                et_session_highlights.getText().toString().trim(), numAttendees);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserEmailPhoneResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.cannot_connect_server), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                commonFunctions.closeProgressDialog();

            }
        });
    }


    private void hitSendOtpApi(final String phone, final String OTP) {
        String SMS = otpSmsPrefix + OTP + AppKeys.otpSmsSuffix;
        sv_parent_wrapper.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
        Call<String> call = SMSapiService.sendSMS(AES.decrypt(AppKeys.SMS_UNAME, AppKeys.enKey),
                AES.decrypt(AppKeys.SMS_PASS, AppKeys.enKey), "1", AppKeys.SMS_COMPANY_NAME,
                phone, SMS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                sv_parent_wrapper.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.cannot_connect_server), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                sv_parent_wrapper.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                commonFunctions.closeProgressDialog();

            }
        });


    }

    private void openOtpDialog(final String phone, final String otp) {
        otpDialog = new MyCustomThemeDialog(SessionAcceptFormActivity.this, R.style.NoTitleDialogTheme);

        otpDialog.setContentView(R.layout.otp_dialog);
        final EditText et_content = (EditText) otpDialog.findViewById(R.id.et_content);
        et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_dialog_description = (TextView) otpDialog.findViewById(R.id.tv_dialog_description);
        TextView tv_done = (TextView) otpDialog.findViewById(R.id.tv_done);
        tv_dialog_description.setText(getString(R.string.otp_sent_to_phone));
        TextView tv_cancel = (TextView) otpDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_otp),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else if (!et_content.getText().toString().equals(otp)) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.otp_does_not_match),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitSessionAcceptApi("session_accept", user_id, session_id,
                            et_accepting_reason.getText().toString().trim(),
                            et_session_highlights.getText().toString().trim(), numAttendees);
                    otpDialog.dismiss();
                }
            }
        });


        final TextView tv_resend = (TextView) otpDialog.findViewById(R.id.tv_resend);
        tv_resend.setVisibility(View.GONE);

        final CountDownTimer countDownTimer;
        final TextView tv_timer = (TextView) otpDialog.findViewById(R.id.tv_timer);

        countDownTimer = new CountDownTimer(AppKeys.OTP_WAITING_TIME, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_timer.setText(getString(R.string.waiting_for_otp) + " \n" + (millisUntilFinished / 1000) + " sec");
                //  tv_resend.setEnabled(false);
                tv_resend.setVisibility(View.GONE);
            }

            public void onFinish() {
                if (et_content.getText().toString().isEmpty()) {
                    tv_timer.setText(getString(R.string.didnt_receive_otp));
                    tv_resend.setVisibility(View.VISIBLE);
                    // tv_resend.setEnabled(true);
                }
            }
        };

        countDownTimer.start();
        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.start();
                hitSendOtpApi(phone, otp);
            }
        });

        mySMSBroadcastReceiver = new MySMSBroadcastReceiver();
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
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
                        et_content.setText(otp);
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

        otpDialog.show();
    }

    public int generateRandomNumber() {
        int randomNumber;
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < 6; i++) {
            int number = secureRandom.nextInt(9);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }

        randomNumber = Integer.parseInt(s);

        return randomNumber;
    }


    private void openMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(SessionAcceptFormActivity.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(message);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                Intent addPhoneIntent = new Intent(SessionAcceptFormActivity.this, AddedPhoneNumbersActivity.class);
                startActivityForResult(addPhoneIntent, PHONE_VERIFIED);
            }
        });
        confirmationDialog.show();
    }

    private void hitSessionAcceptApi(String handle, String user_id, final String session_id, String ans1, String ans2, String numAttendees) {
        Call<PojoNoDataResponse> call = apiService.acceptSession(handle, user_id, session_id, ans1, ans2, numAttendees);
        sv_parent_wrapper.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                  /*  Intent sessionIntent = new Intent(SessionAcceptFormActivity.this, SessionLoaderActivity.class);
                    sessionIntent.putExtra("id", session_id);
                    startActivity(sessionIntent);*/
                    otpDialog.dismiss();

                    message = getString(R.string.accept_dialog_message);
                    Button bt_ok;
                    final Dialog confirmationDialog;
                    confirmationDialog = new MyCustomThemeDialog(SessionAcceptFormActivity.this);
                    confirmationDialog.setContentView(R.layout.message_dialog);

                    TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                    tv_dialog_description.setText(Html.fromHtml(message));

                    bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         /*   commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                        }
                    });
                    confirmationDialog.setCancelable(false);
                    confirmationDialog.show();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.cannot_connect_server), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                sv_parent_wrapper.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                commonFunctions.closeProgressDialog();
            }
        });
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.request_sharing));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionAcceptFormActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionAcceptFormActivity.this, R.color.status_bar_color));
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_VERIFIED) {
            if (data != null) {
                otpNeeded = data.getBooleanExtra("otp_needed", false);
            }
        }
    }
}
