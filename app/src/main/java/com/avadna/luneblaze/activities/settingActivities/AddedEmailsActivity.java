package com.avadna.luneblaze.activities.settingActivities;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

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
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoExtraInfo;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddedEmailsActivity extends AppBaseActivity implements
        EmailListAdapter.EmailListAdapterCallback {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    RecyclerView rv_email_list;
    EmailListAdapter emailListAdapter;
    List<PojoExtraInfo> phoneEmailList;
    TextView tv_add_email;
    View.OnClickListener onClickListener;

    RelativeLayout rl_content_wrapper;
    ProgressBar pb_loading_content;

    TextView tv_dialog_description;
    EditText et_password, et_confirmation_code, et_email;
    TextInputLayout til_email, til_confirmation_code;

    TextView tv_done, tv_cancel;
    boolean OtpSent = false;
    int PHONE_VERIFIED = 1;
    Dialog addEmailDialog, confirmOTPDialog;
    boolean newEmailAdded = false;

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
        hitGetUserPhoneEmailApi(user_id, "");

    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.emails));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AddedEmailsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AddedEmailsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_add_email = (TextView) findViewById(R.id.tv_add_email);
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        initEmailList();
    }

    private void initClickListener() {

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_add_email:
                        openAddEmailDialog("");
                        break;
                }
            }
        };
    }


    private void setClickListener() {
        tv_add_email.setOnClickListener(onClickListener);
    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initEmailList() {
        phoneEmailList = new ArrayList<>();
        rv_email_list = (RecyclerView) findViewById(R.id.rv_email_list);
        emailListAdapter = new EmailListAdapter(AddedEmailsActivity.this, phoneEmailList, AppKeys.TYPE_EMAIL);
        rv_email_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_email_list.setAdapter(emailListAdapter);
    }


    public void openAddEmailDialog(final String email) {
        addEmailDialog = new MyCustomThemeDialog(AddedEmailsActivity.this);
        addEmailDialog.setContentView(R.layout.add_email_dialog);
        et_confirmation_code = (EditText) addEmailDialog.findViewById(R.id.et_confirmation_code);
        et_confirmation_code.setVisibility(View.GONE);

        til_email = (TextInputLayout) addEmailDialog.findViewById(R.id.til_email);
        til_confirmation_code = (TextInputLayout) addEmailDialog.findViewById(R.id.til_confirmation_code);
        til_confirmation_code.setVisibility(View.GONE);

        et_email = (EditText) addEmailDialog.findViewById(R.id.et_email);
        tv_done = (TextView) addEmailDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) addEmailDialog.findViewById(R.id.tv_cancel);

        et_email.setHint(getString(R.string.email));

        tv_dialog_description = (TextView) addEmailDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(R.string.add_email_description);

        tv_done.setText(R.string.send_otp);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
                    boolean alrPresent = false;
                    for (int i = 0; i < phoneEmailList.size(); i++) {
                        if (et_email.getText().toString().trim().equals(phoneEmailList.get(i).data)) {
                            alrPresent = true;
                        }
                    }
                    if (alrPresent) {
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.already_added),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    } else {
                        hitAddEmailApi(user_id, "add_email", et_email.getText().toString().trim());
                        addEmailDialog.dismiss();
                    }

                }
            }
        });
        addEmailDialog.setCancelable(true);
        addEmailDialog.show();
    }


    public void openConfirmOtpDialog(String email) {
        // OtpSent=false;
        confirmOTPDialog = new MyCustomThemeDialog(AddedEmailsActivity.this);
        confirmOTPDialog.setContentView(R.layout.add_email_dialog);
        et_confirmation_code = (EditText) confirmOTPDialog.findViewById(R.id.et_confirmation_code);

        til_email = (TextInputLayout) confirmOTPDialog.findViewById(R.id.til_email);
        til_confirmation_code = (TextInputLayout) confirmOTPDialog.findViewById(R.id.til_confirmation_code);

        et_email = (EditText) confirmOTPDialog.findViewById(R.id.et_email);
        tv_done = (TextView) confirmOTPDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) confirmOTPDialog.findViewById(R.id.tv_cancel);
        tv_dialog_description = (TextView) confirmOTPDialog.findViewById(R.id.tv_dialog_description);

        et_email.setEnabled(false);
        et_email.setText(email);
        tv_dialog_description.setText(R.string.add_email_enter_otp_description);
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
                if (Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
                    hitConfirmEmailOtpApi(user_id, et_email.getText().toString().trim()
                            , et_confirmation_code.getText().toString().trim());

                }
            }

        });

        confirmOTPDialog.setCancelable(true);
        confirmOTPDialog.show();
    }


    private void hitGetUserPhoneEmailApi(final String user_id, final String email) {
        Call<PojoUserEmailPhoneResponse> call = apiService.getUserPhoneEmail(user_id);

        if (!newEmailAdded) {
            pb_loading_content.setVisibility(View.VISIBLE);
            rl_content_wrapper.setVisibility(View.GONE);
        }

        call.enqueue(new Callback<PojoUserEmailPhoneResponse>() {
            @Override
            public void onResponse(Call<PojoUserEmailPhoneResponse> call, Response<PojoUserEmailPhoneResponse> response) {
                if (response != null && response.body() != null) {
                    phoneEmailList.clear();
                    phoneEmailList.addAll(response.body().data.email);
                    emailListAdapter.notifyDataSetChanged();
                }

                if (newEmailAdded) {
                    commonFunctions.closeProgressDialog();

                    newEmailAdded = false;
                    for (int i = 0; i < phoneEmailList.size(); i++) {
                        if (phoneEmailList.get(i).data.equals(email)) {
                            hitSendOtpToEmailApi(email, user_id, phoneEmailList.get(i).usersExtraInfoId);

                            break;
                        }
                    }
                }


                pb_loading_content.setVisibility(View.GONE);
                rl_content_wrapper.setVisibility(View.VISIBLE);
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoUserEmailPhoneResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.emails));
                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetUserPhoneEmailApi(user_id, "");
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
        }
        catch (Exception e){

        }

    }

    private void hitConfirmEmailOtpApi(final String user_id, String email, String OTP) {
        Call<PojoNoDataResponse> call = apiService.verifyEmailWithOtp(user_id, email, OTP);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    hitGetUserPhoneEmailApi(user_id, "");
                    if (confirmOTPDialog != null) {
                        confirmOTPDialog.dismiss();

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitAddEmailApi(final String user_id, String type, final String email) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.addEmail(user_id, type, email);
        commonFunctions.openProgressDialog(getString(R.string.adding_email));
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().status == 1) {
                        String message = response.body().message;
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        newEmailAdded = true;
                        hitGetUserPhoneEmailApi(user_id, email);
                        addEmailDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
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

    @Override
    public void emailListItemClickCallback(int position, PojoExtraInfo currentItem, String type) {
        switch (type) {
            case AppKeys.VERIFY:
                OtpSent = true;
                hitSendOtpToEmailApi(currentItem.data, user_id, currentItem.usersExtraInfoId);
                break;

            case AppKeys.MAKE_PRIMARY:
                hitMakeEmailPrimaryApi(user_id, currentItem.id);
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
        confirmationDialog = new MyCustomThemeDialog(AddedEmailsActivity.this);
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
        final Dialog confirmationDialog;
        TextView tv_dialog_description, tv_yes, tv_no;
        confirmationDialog = new MyCustomThemeDialog(AddedEmailsActivity.this);
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
                hitDeletePhoneEmailApi(user_id, "user_email", phoneEmailList.get(position).data);
                phoneEmailList.remove(position);
                emailListAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }

    private void hitSendOtpToEmailApi(String email, final String user_id, String info_id) {
        Call<PojoNoDataResponse> call = apiService.sendOtpToEmail(user_id, info_id);
        commonFunctions.openProgressDialog();
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    if (response.body().status == 1) {
                        openConfirmOtpDialog(email);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitMakeEmailPrimaryApi(final String user_id, String info_id) {
        Call<PojoNoDataResponse> call = apiService.makeEmailPrimary(user_id, info_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    hitGetUserPhoneEmailApi(user_id, "");

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitDeletePhoneEmailApi(final String user_id, String type, String phone) {
        Call<PojoNoDataResponse> call = apiService.deletePhoneOrEmail(user_id, type, phone);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserPhoneEmailApi(user_id, "");
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


}
