package com.avadna.luneblaze.activities.organisation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessionCreation.SessionCreationPart4;
import com.avadna.luneblaze.adapters.organisation.OrganisationTypeListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoRegisterOrganisationResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterOrganisationActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private TextInputLayout til_company_name, til_entity_name, til_type, til_website, til_email, til_phone;
    private EditText et_company_name, et_entity_name, et_type, et_website, et_email, et_phone;
    private TextView tv_done_button;

    private ProgressBar pb_loading_type;
    private ListPopupWindow organisationListPopupWindow;
    private boolean isSearchApiCalled = false;
    private String selectedType = "";
    private String selectedTypeId = "";

    private View.OnClickListener onClickListener;

    private Dialog accountVerificationDialog;
    private MySMSBroadcastReceiver mySMSBroadcastReceiver;
    private String otpSmsPrefix = "<#> Your OTP for account verification is ";
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_organisation);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        setTextChangedListener();

        organisationListPopupWindow = new ListPopupWindow(RegisterOrganisationActivity.this);
        organisationListPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        organisationListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            //   actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");
            actionBar.setTitle(getString(R.string.register_organisation));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(RegisterOrganisationActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(RegisterOrganisationActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        til_company_name = (TextInputLayout) findViewById(R.id.til_company_name);
        til_entity_name = (TextInputLayout) findViewById(R.id.til_entity_name);
        til_type = (TextInputLayout) findViewById(R.id.til_type);
        til_website = (TextInputLayout) findViewById(R.id.til_website);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_phone = (TextInputLayout) findViewById(R.id.til_phone);
        et_company_name = (EditText) findViewById(R.id.et_company_name);
        et_entity_name = (EditText) findViewById(R.id.et_entity_name);
        et_type = (EditText) findViewById(R.id.et_type);
        et_website = (EditText) findViewById(R.id.et_website);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_done_button = (TextView) findViewById(R.id.tv_done_button);
        pb_loading_type = (ProgressBar) findViewById(R.id.pb_loading_type);
        pb_loading_type.setVisibility(View.GONE);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_done_button:
                        if (validateAllFields()) {
                            if (et_phone.requestFocus()) {
                                imm.hideSoftInputFromWindow(et_phone.getWindowToken(), 0);
                            }
                            hitRegisterOrganisationApi(user_id, et_company_name.getText().toString().trim(),
                                    et_entity_name.getText().toString().trim(), selectedTypeId,
                                    et_website.getText().toString().trim(),
                                    et_email.getText().toString().trim(), et_phone.getText().toString().trim());
                        }
                        break;
                }
            }
        };
    }

    private void hitGetOrganisationTypeApi(String user_id, String query) {
        Call<PojoGetOrganisationTypeResponse> call = apiService.getOrganisationType(user_id, query.toLowerCase());
        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            pb_loading_type.setVisibility(View.VISIBLE);
            organisationListPopupWindow.dismiss();
            call.enqueue(new Callback<PojoGetOrganisationTypeResponse>() {
                @Override
                public void onResponse(Call<PojoGetOrganisationTypeResponse> call,
                                       Response<PojoGetOrganisationTypeResponse> response) {
                    pb_loading_type.setVisibility(View.GONE);
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        organisationListPopupWindow.dismiss();
                        organisationListPopupWindow.setAnchorView(et_type);
                        List<PojoGetOrganisationTypeResponseData> filteredList = new ArrayList<>();
                        for (int i = 0; i < response.body().data.size(); i++) {
                            if (response.body().data.get(i).name.toLowerCase().contains(query.toLowerCase())) {
                                filteredList.add(response.body().data.get(i));
                            }
                        }
                        Collections.sort(filteredList, new Comparator<PojoGetOrganisationTypeResponseData>() {
                            public int compare(PojoGetOrganisationTypeResponseData d1, PojoGetOrganisationTypeResponseData d2) {
                                return d1.getName().compareTo(d2.getName());
                            }
                        });
                        OrganisationTypeListAdapter organisationTypeListAdapter = new OrganisationTypeListAdapter(
                                RegisterOrganisationActivity.this, filteredList);
                        organisationListPopupWindow.setAdapter(organisationTypeListAdapter);

                        organisationListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedType = filteredList.get(i).name;
                                et_type.setText(selectedType);
                                selectedTypeId = filteredList.get(i).id;
                                organisationListPopupWindow.dismiss();
                            }
                        });
                        organisationListPopupWindow.show();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_type.getText().toString().trim();
                    if (!latestQuery.equals(query)) {
                        hitGetOrganisationTypeApi(user_id, latestQuery);
                    }
                }

                @Override
                public void onFailure(Call<PojoGetOrganisationTypeResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    isSearchApiCalled = false;
                    pb_loading_type.setVisibility(View.GONE);

                }
            });
        }
    }


    private boolean validateAllFields() {
        boolean valid = true;
        String website = et_website.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        if (et_company_name.getText().toString().isEmpty()) {
            valid = false;
            til_company_name.setError(getString(R.string.please_enter_company_name));
        }
        if (et_entity_name.getText().toString().isEmpty()) {
            valid = false;
            til_entity_name.setError(getString(R.string.please_enter_the_legal_entity_name));
        }
        if (website.isEmpty()) {
            valid = false;
            til_website.setError(getString(R.string.please_enter_company_website));
        }
        if (!isValidWebsite(website)) {
            valid = false;
            til_website.setError(getString(R.string.please_enter_valid_website));
        }
        if (selectedTypeId == null || selectedTypeId.isEmpty()) {
            til_type.setError(getString(R.string.please_choose_company_type));
            valid = false;
        }
        if (email.isEmpty()) {
            valid = false;
            til_email.setError(getString(R.string.please_enter_contact_email));
        }
        if (!isValidEmail(email)) {
            valid = false;
            til_email.setError(getString(R.string.please_enter_valid_email));
        }
       /* if (!doesEmailMatchWebsite(email, website)) {
            valid = false;
            til_phone.setError(getString(R.string.please_enter_the_official_email));
        }*/
        if (!isValidPhone(phone)) {
            valid = false;
            til_phone.setError(getString(R.string.please_enter_valid_phone));
        }
        return valid;
    }


    public boolean isValidPhone(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.PHONE.matcher(target).matches();
        }
    }

    public boolean isValidWebsite(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.WEB_URL.matcher(target).matches();
        }
    }


    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean doesEmailMatchWebsite(CharSequence email, CharSequence website) {
        String host = getHostName("http://" + website);
        if (host.contains(".")) {
            host = host.substring(0, host.indexOf("."));
        }
        if (email.toString().contains(host)) {
            return true;
        }
        return false;
    }


    public String getHostName(String url) {
        String hostname = "";
        URI uri = null;
        try {
            uri = new URI(url);
            hostname = uri.getHost();
            // to provide faultproof result, check if not null then return only hostname, without www.
            if (hostname != null) {
                return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return hostname;
    }

    private void setClickListener() {
        tv_done_button.setOnClickListener(onClickListener);

    }

    private void setTextChangedListener() {
        et_company_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    til_company_name.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_entity_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    til_entity_name.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!selectedType.equals(charSequence.toString().trim())) {
                    String query = charSequence.toString();
                    hitGetOrganisationTypeApi(user_id, query);
                }
                if (!charSequence.toString().isEmpty()) {
                    til_type.setError(null);
                } else {
                    til_type.setError(getString(R.string.please_choose_company_type));
                }
                selectedTypeId = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_website.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidWebsite(s)) {
                    til_website.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidEmail(s)) {
                    til_email.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidPhone(s)) {
                    til_phone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void hitRegisterOrganisationApi(String user_id, String name, String legal_name, String type_id,
                                            String website, String email, String phone) {
        //  pb_loading.setVisibility(View.VISIBLE);
        tv_done_button.setVisibility(View.GONE);
        commonFunctions.openProgressDialog(getString(R.string.registering_organisation));

        Call<PojoRegisterOrganisationResponse> call = apiService.registerOrganisation(user_id,
                name, legal_name, type_id, website, email, phone);
        call.enqueue(new Callback<PojoRegisterOrganisationResponse>() {
            @Override
            public void onResponse(Call<PojoRegisterOrganisationResponse> call, Response<PojoRegisterOrganisationResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    tv_done_button.setVisibility(View.VISIBLE);
                    String message = response.body().message;
                    if (response.body().status == 1) {
                        openOtpDialog(response.body().data.id.toString());
                    } else {
                        if (message.contains("exist"))
                            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.this_website_already_registered),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoRegisterOrganisationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                tv_done_button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openOtpDialog(String organisation_id) {
        accountVerificationDialog = new MyCustomThemeDialog(this);
        commonFunctions.closeProgressDialog();
        accountVerificationDialog.setContentView(R.layout.otp_dialog);
        final EditText et_content = (EditText) accountVerificationDialog.findViewById(R.id.et_content);
        et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_dialog_description = (TextView) accountVerificationDialog.findViewById(R.id.tv_dialog_description);
        TextView tv_done = (TextView) accountVerificationDialog.findViewById(R.id.tv_done);
        final TextView tv_resend = (TextView) accountVerificationDialog.findViewById(R.id.tv_resend);
        tv_resend.setVisibility(View.GONE);

        final CountDownTimer countDownTimer;
        final TextView tv_timer = (TextView) accountVerificationDialog.findViewById(R.id.tv_timer);

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
                if (validateAllFields()) {
                    countDownTimer.start();
                    hitRegisterOrganisationApi(user_id, et_company_name.getText().toString().trim(),
                            et_entity_name.getText().toString().trim(), selectedTypeId, et_website.getText().toString().trim(),
                            et_email.getText().toString().trim(), et_phone.getText().toString().trim());
                    accountVerificationDialog.dismiss();
                }
            }
        });

        tv_dialog_description.setText(getString(R.string.otp_sent_to_email));

        TextView tv_cancel = (TextView) accountVerificationDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountVerificationDialog.dismiss();
            }
        });


        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_otp),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    if (et_content.requestFocus()) {
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    }
                    hitVerifyOrganisation(user_id, organisation_id, et_content.getText().toString().trim());
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
        accountVerificationDialog.setCancelable(false);
        accountVerificationDialog.show();
    }

    private void hitVerifyOrganisation(String user_id, String organisation_id, String otp) {
        //  pb_loading.setVisibility(View.VISIBLE);
        tv_done_button.setVisibility(View.GONE);
        commonFunctions.openProgressDialog(getString(R.string.creating_company_page));

        Call<PojoNoDataResponse> call = apiService.verifyOrganisation(user_id, organisation_id, otp);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    tv_done_button.setVisibility(View.VISIBLE);
                    if (response.body().status == 1) {
                        if (accountVerificationDialog != null) {
                            accountVerificationDialog.dismiss();
                        }
                        final Dialog confirmationDialog;
                        confirmationDialog = new MyCustomThemeDialog(RegisterOrganisationActivity.this);
                        confirmationDialog.setContentView(R.layout.message_dialog);

                        TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                        tv_dialog_description.setText(getString(R.string.organisation_page_live_after_verify));

                        Button bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                         /*   commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                                Intent completeIntent = new Intent(RegisterOrganisationActivity.this,
                                        CompleteOrganisationRegistrationActivity.class);
                                completeIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                                startActivity(completeIntent);
                                confirmationDialog.dismiss();
                                finish();
                            }
                        });

                        Button bt_cancel = (Button) confirmationDialog.findViewById(R.id.bt_cancel);
                        bt_cancel.setVisibility(View.VISIBLE);
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                         /*   commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                                confirmationDialog.dismiss();
                                finish();
                            }
                        });

                        confirmationDialog.setCancelable(false);
                        confirmationDialog.show();
                    } else {
                        String message = response.body().message;
                        commonFunctions.setToastMessage(getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                tv_done_button.setVisibility(View.VISIBLE);
            }
        });
    }
}
