package com.avadna.luneblaze.activities.sessionCreation;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoConductSessionResponse;
import com.avadna.luneblaze.pojo.PojoCreateSession;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.rest.SMSApiClient;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCreationPart4 extends AppBaseActivity {


    private static final int LOCAL_REQUEST_CODE_READ_SMS = 123;
    ApiInterface apiService;
    ApiInterface SMSapiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    String user_id;


    EditText et_accepting_reason;
    EditText et_highlights;
    EditText et_num_attendees;
    TextView tv_verify_otp;
    View.OnClickListener onClickListener;
    PojoCreateSession pojoCreateSession;
    String phone = "";

    TextView tv_dialog_description;
    EditText et_password, et_confirmation_code, et_phone;
    TextView tv_done, tv_cancel;
    boolean OtpSent = false;
    Dialog addPhoneDialog;

    int PHONE_VERIFIED = 1;
    boolean otpNeeded = true;

    ScrollView sv_content_wrapper;
    ProgressBar pb_calling_api;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    Dialog otpDialog;

    MySMSBroadcastReceiver mySMSBroadcastReceiver;
    String otpSmsPrefix = "<#> Your OTP for session creation is ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_part4);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        SMSapiService = SMSApiClient.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        initBroadCastReceiver();
        Type jsonType = new TypeToken<PojoCreateSession>() {
        }.getType();
        pojoCreateSession = new Gson().fromJson(getIntent().getStringExtra("data"), jsonType);


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_CREATION_FINISHED));


    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.SESSION_CREATION_FINISHED)) {
                    finish();
                }
            }
        };
    }

    private void initViews() {
        et_accepting_reason = (EditText) findViewById(R.id.et_accepting_reason);
        et_highlights = (EditText) findViewById(R.id.et_highlights);
        et_num_attendees = (EditText) findViewById(R.id.et_num_attendees);
        tv_verify_otp = (TextView) findViewById(R.id.tv_verify_otp);
        sv_content_wrapper = (ScrollView) findViewById(R.id.sv_content_wrapper);
        pb_calling_api = (ProgressBar) findViewById(R.id.pb_calling_api);
        pb_calling_api.setVisibility(View.GONE);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_verify_otp:
                        String ans1 = et_accepting_reason.getText().toString();
                        String ans2 = et_highlights.getText().toString();

                        String allowed = et_num_attendees.getText().toString();
                        if (ans1.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_reason), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else if (ans2.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_highlights), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else if (allowed.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext()
                                    , getString(R.string.please_enter_number_of_attendees), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            pojoCreateSession.ans1 = ans1;
                            pojoCreateSession.ans2 = ans2;
                            pojoCreateSession.people_allowed = allowed;
                            hitGetUserPhoneEmailApi(user_id);
                        }
                        break;
                }
            }


        };
    }

    private void setClickListener() {
        tv_verify_otp.setOnClickListener(onClickListener);
    }

    private void hitGetUserPhoneEmailApi(final String user_id) {
        Call<PojoUserEmailPhoneResponse> call = apiService.getUserPhoneEmail(user_id);

        pb_calling_api.setVisibility(View.VISIBLE);
        sv_content_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoUserEmailPhoneResponse>() {
            @Override
            public void onResponse(Call<PojoUserEmailPhoneResponse> call, Response<PojoUserEmailPhoneResponse> response) {
                if (response != null && response.body() != null) {
                    pb_calling_api.setVisibility(View.GONE);
                    sv_content_wrapper.setVisibility(View.VISIBLE);
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
                        hitConductSessionApi(pojoCreateSession);
                    }
                }

            }

            @Override
            public void onFailure(Call<PojoUserEmailPhoneResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_calling_api.setVisibility(View.GONE);
                sv_content_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(SessionCreationPart4.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(message);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                Intent addPhoneIntent = new Intent(SessionCreationPart4.this, AddedPhoneNumbersActivity.class);
                startActivityForResult(addPhoneIntent, PHONE_VERIFIED);
            }
        });
        confirmationDialog.show();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_session));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionCreationPart4.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionCreationPart4.this, R.color.status_bar_color));
            }
        }
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


    private void hitSendOtpApi(final String phone, final String OTP) {
        String SMS = otpSmsPrefix + OTP + AppKeys.otpSmsSuffix;

        Call<String> call = SMSapiService.sendSMS(AES.decrypt(AppKeys.SMS_UNAME, AppKeys.enKey),
                AES.decrypt(AppKeys.SMS_PASS, AppKeys.enKey), "1",
                AppKeys.SMS_COMPANY_NAME, phone, SMS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //openOtpDialog(phone, OTP);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        "Cannot connect to Server", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void openOtpDialog(final String phone, final String otp) {
        otpDialog = new MyCustomThemeDialog(SessionCreationPart4.this);
        otpDialog.setContentView(R.layout.otp_dialog);
        final EditText et_content = (EditText) otpDialog.findViewById(R.id.et_content);
        et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_dialog_description = (TextView) otpDialog.findViewById(R.id.tv_dialog_description);
        TextView tv_done = (TextView) otpDialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) otpDialog.findViewById(R.id.tv_cancel);
        tv_dialog_description.setText(getString(R.string.otp_sent_to_phone));
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
                    hitConductSessionApi(pojoCreateSession);
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
            }
        });
        registerReceiver(mySMSBroadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
            }
        });

        otpDialog.show();
    }


    private void hitConductSessionApi(final PojoCreateSession pojoCreateSession) {

        File file = commonFunctions.getScaledDownImage(pojoCreateSession.cover_photo_path);
        // file = getScaledDownImage(file);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), preferenceUtils.get_user_id());
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.description);


        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("cover_photo", file.getName(), mFile);

        RequestBody ans1Body = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.ans1);
        RequestBody ans2Body = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.ans2);
        RequestBody people_allowedBody = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.people_allowed);

        HashMap<String, RequestBody> topicMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.topics.size(); i++) {
            topicMap.put("topics[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.topics.get(i).value));
        }

        HashMap<String, RequestBody> prerequisiteMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.prerequisite.size(); i++) {
            prerequisiteMap.put("prerequisite[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.prerequisite.get(i).value));
        }

        HashMap<String, RequestBody> dateMap = new HashMap<>();

        for (int i = 0; i < pojoCreateSession.venue1Dates.size(); i++) {
            dateMap.put("event_date[" + pojoCreateSession.venues.get(0) + "][" + i + "]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue1Dates.get(i)));
        }

        for (int i = 0; i < pojoCreateSession.venue2Dates.size(); i++) {
            dateMap.put("event_date[" + pojoCreateSession.venues.get(1) + "][" + i + "]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue2Dates.get(i)));
        }
/*
        for (int i = 0; i < pojoCreateSession.venue3Dates.size(); i++) {
            dateMap.put("event_date[" + pojoCreateSession.venues.get(2) + "]["+i+"]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue3Dates.get(i)));
        }*/

        HashMap<String, RequestBody> interestIdsMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.interest_ids.size(); i++) {
            dateMap.put("interest_ids[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.interest_ids.get(i)));
        }

        HashMap<String, RequestBody> venueMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.venues.size(); i++) {
            dateMap.put("venues[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.venues.get(i)));
        }


        Call<PojoConductSessionResponse> call = apiService.conductSession(userBody, titleBody,
                topicMap, prerequisiteMap, descriptionBody, fileToUpload, dateMap, interestIdsMap,
                venueMap, ans1Body, ans2Body, people_allowedBody);
        pb_calling_api.setVisibility(View.VISIBLE);
        sv_content_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoConductSessionResponse>() {
            @Override
            public void onResponse(Call<PojoConductSessionResponse> call, Response<PojoConductSessionResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    String message = "Session <B>" + pojoCreateSession.title + "</B> is created successfully and will be live on the platform after admin verification.";

                    Button bt_ok;
                    final Dialog confirmationDialog;
                    confirmationDialog = new MyCustomThemeDialog(SessionCreationPart4.this);
                    confirmationDialog.setContentView(R.layout.message_dialog);

                    TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                    tv_dialog_description.setText(Html.fromHtml(message));

                    bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         /*   commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                            Intent pushNotification = new Intent(Config.SESSION_CREATION_FINISHED);
                            LocalBroadcastManager.getInstance(SessionCreationPart4.this).sendBroadcast(pushNotification);
                        }
                    });
                    confirmationDialog.setCancelable(false);
                    confirmationDialog.show();

                }
                pb_calling_api.setVisibility(View.GONE);
                sv_content_wrapper.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<PojoConductSessionResponse> call, Throwable t) {

                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_calling_api.setVisibility(View.GONE);
                sv_content_wrapper.setVisibility(View.VISIBLE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_VERIFIED) {
            if (data != null) {
                otpNeeded = data.getBooleanExtra("otp_needed", false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }
}
