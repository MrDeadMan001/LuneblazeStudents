package com.avadna.luneblaze.activities.settingActivities;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.EmailListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoExtraInfo;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddedPhoneNumbersActivity extends AppBaseActivity implements
        EmailListAdapter.EmailListAdapterCallback {

    private static final int REQUEST_CODE_READ_SMS = 123;

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    RecyclerView rv_phone_list;
    EmailListAdapter phoneListAdapter;
    List<PojoExtraInfo> phoneEmailList;
    TextView tv_add_phone;
    View.OnClickListener onClickListener;

    RelativeLayout rl_content_wrapper;
    ProgressBar pb_loading_content;

    TextView tv_dialog_description;
    EditText et_password, et_confirmation_code, et_email;
    TextInputLayout til_email, til_confirmation_code;
    TextView tv_done, tv_cancel;
    // boolean OtpSent = false;
    int PHONE_VERIFIED = 1;

    Dialog addPhoneDialog, confirmOTPDialog;
    String currentPhoneForOTP = "";

    Dialog confirmationDialog;
    MySMSBroadcastReceiver mySMSBroadcastReceiver;
    String otpSmsPrefix = "<#> Your OTP for mobile verification is ";

    String newNumberAdded = "";
    boolean newMadePrimary = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_emails);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initApis();
        initViews();
        initClickListener();
        setClickListener();
        hitGetUserPhoneEmailApi(user_id);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.phones_numbers));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AddedPhoneNumbersActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AddedPhoneNumbersActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_add_phone = (TextView) findViewById(R.id.tv_add_email);
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        initSuggestedFriendsList();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_add_email:
                        openAddPhoneDialog();
                        break;
                }
            }
        };
    }


    private void setClickListener() {
        tv_add_phone.setOnClickListener(onClickListener);
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initSuggestedFriendsList() {
        phoneEmailList = new ArrayList<>();
        rv_phone_list = (RecyclerView) findViewById(R.id.rv_email_list);
        phoneListAdapter = new EmailListAdapter(AddedPhoneNumbersActivity.this, phoneEmailList, AppKeys.TYPE_PHONE);
        rv_phone_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_phone_list.setAdapter(phoneListAdapter);
    }


    public void openAddPhoneDialog() {
        // OtpSent=false;
        addPhoneDialog = new MyCustomThemeDialog(AddedPhoneNumbersActivity.this);
        addPhoneDialog.setContentView(R.layout.add_email_dialog);
        til_email = (TextInputLayout) addPhoneDialog.findViewById(R.id.til_email);
        til_email.setHint(getString(R.string.phone));

        til_confirmation_code = (TextInputLayout) addPhoneDialog.findViewById(R.id.til_confirmation_code);
        til_confirmation_code.setVisibility(View.GONE);

        et_confirmation_code = (EditText) addPhoneDialog.findViewById(R.id.et_confirmation_code);
        et_confirmation_code.setVisibility(View.GONE);
        et_email = (EditText) addPhoneDialog.findViewById(R.id.et_email);
        tv_done = (TextView) addPhoneDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) addPhoneDialog.findViewById(R.id.tv_cancel);

        et_email.setHint("Phone Number");

        tv_dialog_description = (TextView) addPhoneDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(R.string.add_phone_otp_description);

        tv_done.setText(R.string.send_otp);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = et_email.getText().toString().trim();
                if (Patterns.PHONE.matcher(phone).matches() && phone.length() == 10) {
                    boolean alrPresent = false;
                    for (int i = 0; i < phoneEmailList.size(); i++) {
                        if (et_email.getText().toString().trim().equals(phoneEmailList.get(i).data)) {
                            alrPresent = true;
                        }
                    }
                    if (alrPresent) {
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.already_added),
                                AppKeys.TOAST_USER, Toast.LENGTH_LONG);
                    } else {
                        hitAddPhoneApi(user_id, "add_mobile", et_email.getText().toString().trim());
                    }
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_valid_phone),
                            AppKeys.TOAST_USER, Toast.LENGTH_LONG);
                }
            }

        });

        addPhoneDialog.setCancelable(true);
        addPhoneDialog.show();
    }


    public void openConfirmOtpDialog(final String mobile) {
        // OtpSent=false;
        confirmOTPDialog = new MyCustomThemeDialog(AddedPhoneNumbersActivity.this);
        confirmOTPDialog.setContentView(R.layout.add_email_dialog);
        et_confirmation_code = (EditText) confirmOTPDialog.findViewById(R.id.et_confirmation_code);

        til_email = (TextInputLayout) confirmOTPDialog.findViewById(R.id.til_email);
        til_email.setHint(getString(R.string.phone));

        til_confirmation_code = (TextInputLayout) confirmOTPDialog.findViewById(R.id.til_confirmation_code);

        et_email = (EditText) confirmOTPDialog.findViewById(R.id.et_email);
        tv_done = (TextView) confirmOTPDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) confirmOTPDialog.findViewById(R.id.tv_cancel);
        tv_dialog_description = (TextView) confirmOTPDialog.findViewById(R.id.tv_dialog_description);

        et_email.setEnabled(false);
        et_email.setText(mobile);
        tv_dialog_description.setText(R.string.add_phone_enter_otp_description);
        tv_done.setText(R.string.done);


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOTPDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_email.getText().toString().trim();
                if (Patterns.PHONE.matcher(s).matches() && s.length() == 10) {
                    hitConfirmOtpApi(user_id, "verify_mobile", mobile
                            , et_confirmation_code.getText().toString().trim());
                }
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
                        if (et_confirmation_code != null) {
                            et_confirmation_code.setText(otp);
                        }
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


        confirmOTPDialog.setCancelable(true);
        confirmOTPDialog.show();
    }


    private void hitGetUserPhoneEmailApi(final String user_id) {
        Call<PojoUserEmailPhoneResponse> call = apiService.getUserPhoneEmail(user_id);
        call.enqueue(new Callback<PojoUserEmailPhoneResponse>() {
            @Override
            public void onResponse(Call<PojoUserEmailPhoneResponse> call, Response<PojoUserEmailPhoneResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().data.mobile.size() == 1) {
                        if (newNumberAdded.equals(response.body().data.mobile.get(0).data)) {
                            hitMakePhonePrimaryApi(user_id, response.body().data.mobile.get(0).usersExtraInfoId,
                                    "make_primary_phone", response.body().data.mobile.get(0).data);
                        }
                    }
                    phoneEmailList.clear();
                    phoneEmailList.addAll(response.body().data.mobile);
                    phoneListAdapter.notifyDataSetChanged();
                }


                pb_loading_content.setVisibility(View.GONE);
                rl_content_wrapper.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<PojoUserEmailPhoneResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.phones_numbers));
                //rl_content_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetUserPhoneEmailApi(user_id);
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.setTitle(title);

            connectionErrorDialog.show();
        } catch (Exception e) {

        }

    }


    private void hitSendOtpToPhoneApi(String user_id, String type, final String mobile) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.sendOtpToPhone(user_id, type, mobile);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                commonFunctions.closeProgressDialog();

                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    if (addPhoneDialog != null) {
                        addPhoneDialog.dismiss();
                    }
                    openConfirmOtpDialog(mobile);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void hitConfirmOtpApi(final String user_id, String type, final String mobile, String otp) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.verifyOtp(user_id, type, mobile, otp);
        commonFunctions.openProgressDialog();
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    newNumberAdded = mobile;
                    confirmOTPDialog.dismiss();
                    for (int i = 0; i < phoneEmailList.size(); i++) {
                        if (phoneEmailList.get(i).data.equals(mobile)) {
                            phoneEmailList.get(i).status = "1";
                        }
                    }
                    phoneListAdapter.notifyDataSetChanged();
                    hitGetUserPhoneEmailApi(user_id);

                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void hitAddPhoneApi(final String user_id, String type, String mobile) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.addPhone(user_id, type, mobile);
        commonFunctions.openProgressDialog(getString(R.string.adding_phone));
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {

                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (!message.contains("existing")) {
                        currentPhoneForOTP = et_email.getText().toString().trim();
                        hitSendOtpToPhoneApi(user_id, "send_otp", currentPhoneForOTP);

                        // hitSendOtpToPhoneApi(user_id, "send_otp", et_email.getText().toString().trim());
                    }
                    if (response.body().status != 1) {
                        commonFunctions.closeProgressDialog();
                    }
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                for (int i = 0; i < phoneEmailList.size(); i++) {
                    if (phoneEmailList.get(i).type.equals("user_mobile")
                            && phoneEmailList.get(i).status.equals("1")
                            && phoneEmailList.get(i).primary.equals("1")) {
                        Intent intent = new Intent();
                        intent.putExtra("otp_needed", !newMadePrimary);
                        setResult(PHONE_VERIFIED, intent);
                        break;
                    }
                }
                finish();
                // onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void emailListItemClickCallback(int position, PojoExtraInfo currentItem, String type) {
        switch (type) {
            case AppKeys.VERIFY:
       /*         OtpSent=true;
                openConfirmOtpDialog(currentItem.data);*/
                currentPhoneForOTP = currentItem.data;
                hitSendOtpToPhoneApi(user_id, "send_otp", currentPhoneForOTP);
                //  hitSendOtpToPhoneApi(user_id,"send_otp",currentItem.data);
                break;

            case AppKeys.MAKE_PRIMARY:
                hitMakePhonePrimaryApi(user_id, currentItem.id, "make_primary_phone", currentItem.data);
                break;

            case AppKeys.REMOVE:
                if (currentItem.primary.equals("1")) {
                    openMessageDialog();
                } else {
                    openConfirmationDialog(position);
                }
                break;
        }
    }

    private void openMessageDialog() {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(AddedPhoneNumbersActivity.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.you_cannot_delete_primary_phone);
        confirmationDialog.show();
    }

    private void openConfirmationDialog(final int position) {
        TextView tv_dialog_description, tv_yes, tv_no;
        confirmationDialog = new MyCustomThemeDialog(AddedPhoneNumbersActivity.this);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

        tv_dialog_description.setText(getString(R.string.remove_added_phone_confirmation));

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitDeletePhoneEmailApi(user_id, "user_mobile", phoneEmailList.get(position).data);
                phoneEmailList.remove(position);
                phoneListAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }


    private void hitDeletePhoneEmailApi(final String user_id, String type, String phone) {
        Call<PojoNoDataResponse> call = apiService.deletePhoneOrEmail(user_id, type, phone);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserPhoneEmailApi(user_id);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitMakePhonePrimaryApi(final String user_id, String info_id, String type, String data) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, info_id, type);
        commonFunctions.openProgressDialog();

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserPhoneEmailApi(user_id);

                    for (int i = 0; i < phoneEmailList.size(); i++) {
                        if (phoneEmailList.get(i).data.equals(data)) {
                            phoneEmailList.get(i).primary = "1";
                        }
                    }
                    phoneListAdapter.notifyDataSetChanged();

                    if (newNumberAdded.equals(data)) {
                        newMadePrimary = true;
                        newNumberAdded = "";
                    }
                }
                commonFunctions.closeProgressDialog();

            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), "" + t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
