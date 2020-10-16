package com.avadna.luneblaze.activities;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.KeyValuePair;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.activities.registration.RegisterActivity1;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;

import com.avadna.luneblaze.pojo.PojoLoggedInDevicesResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoLogoutResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSignUp1Response;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import com.avadna.luneblaze.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppBaseActivity {
    private static final int REQUEST_CODE_READ_SMS = 123;

    private TextInputLayout til_password, til_email;
    private TextInputEditText et_email, et_password;
    private TextView tv_forgot_password, tv_login_button, tv_create_account;
    private TextView tv_browse_app;
    private TextView tv_tnc_message_link;
    private View.OnClickListener mOnClickListener;
    private String email = "Email";
    private ApiInterface apiService;
    private CommonFunctions commonFunctions;
    private PreferenceUtils preferenceUtils;
    private PojoLoginResponseData pojoLoginResponseData;
    private Dialog twoStepOtpDialog, accountVerificationDialog;

    private RelativeLayout rl_google_signup_wrapper;
    private SignInButton sign_in_button;
    private GoogleSignInClient mGoogleSignInClient;


    private BroadcastReceiver broadcastReceiver;
    private final int OTP_LENGTH = 6;
    private MySMSBroadcastReceiver mySMSBroadcastReceiver;
    private String otpSmsPrefix = "<#> Your OTP for account activation is ";

    private static final int RC_SIGN_IN = 32;


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

        setContentView(R.layout.activity_login);
        //  initFacebookLogin();
        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);
        preferenceUtils.setAppTourStatus(false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        initViews();
        initClickListener(this);
        setClickListener();
        setTextWatchers();
        setFocusChangeListeners();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        FirebaseApp.initializeApp(getApplicationContext());
        String token = FirebaseInstanceId.getInstance().getToken();
        editor.putString("regId", token);
        editor.commit();
       /* AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        List<String> signatures;
        signatures = appSignatureHelper.getAppSignatures();
        Log.d("signature_sha", signatures.get(0));
        preferenceUtils.saveSmsHash(signatures.get(0));*/

        initBroadCastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.FINISH_LOGIN_ACTIVITY));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //String encryptedapiUname = AES.encrypt("AIzaSyDz5R8xq3arIj0QOpyAAV6upybAtq2Lpdo", AppKeys.enKey);


       /* String encryptedapiUname = AES.encrypt("admin", AppKeys.enKey);
        String encryptedapiPass = AES.encrypt("sb~|ay9a6&", AppKeys.enKey);
        String encryptedSMSUname = AES.encrypt("20180428", AppKeys.enKey);
        String encryptedSMSPass = AES.encrypt("EL99rtrG", AppKeys.enKey);
       String placesKey=AES.encrypt("AIzaSyDz5R8xq3arIj0QOpyAAV6upybAtq2Lpdo",AppKeys.enKey);
       */
        int b = 5;

    }

    private void logAppLoginEvent(String email) {
        String method;
        if(Patterns.PHONE.matcher(email).matches()){
            method="phone";
        }
        else {
            method="email";
        }
        ArrayList<KeyValuePair> pairs=new ArrayList<>();
        KeyValuePair loginMethod=new KeyValuePair(FirebaseAnalytics.Param.METHOD,method);
        pairs.add(loginMethod);
        commonFunctions.logFirebaseEvent(FirebaseAnalytics.Event.LOGIN,pairs);
    }

    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.FINISH_LOGIN_ACTIVITY)) {
                    finish();
                }
            }
        };
    }

    private void setClickListener() {
        tv_forgot_password.setOnClickListener(mOnClickListener);
        tv_login_button.setOnClickListener(mOnClickListener);
        tv_create_account.setOnClickListener(mOnClickListener);
        tv_tnc_message_link.setOnClickListener(mOnClickListener);
        rl_google_signup_wrapper.setOnClickListener(mOnClickListener);
        tv_browse_app.setOnClickListener(mOnClickListener);
        // tv_fb_login.setOnClickListener(mOnClickListener);

    }

    private void initClickListener(final Context c) {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.rl_google_signup_wrapper:
                        commonFunctions.openProgressDialog();
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;


                    case R.id.tv_forgot_password:
                        Intent resetIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                        resetIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT/* | Intent.FLAG_ACTIVITY_NO_HISTORY*/);

                        startActivity(resetIntent);
                        break;
                    case R.id.tv_login_button:
                        if (validateAllFields()) {
                            hitLoginApi(et_email.getText().toString().trim(), et_password.getText().toString().trim(),
                                    Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
                                    , getLocalIpAddress());
                            commonFunctions.openProgressDialog(getString(R.string.logging_in));

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (et_password.requestFocus()) {
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                        break;

                    case R.id.tv_create_account:
                        // commonFunctions.setToastMessage(c, "create account clicked", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        Intent registerActivityIntent = new Intent(c, RegisterActivity1.class);
                        registerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT/* | Intent.FLAG_ACTIVITY_NO_HISTORY*/);
                        startActivity(registerActivityIntent);
                        break;

                    case R.id.tv_browse_app:
                        PojoLoginResponseData guestLoginData=new PojoLoginResponseData();
                        guestLoginData.userId=AppKeys.GUEST_USER_ID;
                        guestLoginData.userFullname=getString(R.string.guest);
                        preferenceUtils.savePref(AppKeys.USER_ID_KEY,AppKeys.GUEST_USER_ID);
                        preferenceUtils.savePref(AppKeys.USER_NAME_KEY, getString(R.string.guest));
                        preferenceUtils.saveUserLoginData(guestLoginData);
                        Intent newsFeedIntent = new Intent(LoginActivity.this, SplashLoaderActivity.class);
                        startActivity(newsFeedIntent);
                        finish();
                        break;

                    /*case R.id.tv_fb_login:
                        LoginManager.getInstance().logInWithReadPermissions((Activity) c, Arrays.asList( "public_profile", "email", "user_friends"));
                        break;*/

                    case R.id.tv_tnc_message_link:
                        commonFunctions.urlLoader(AppKeys.terms_of_use_link);
                        /*Intent webIntent = new Intent(LoginActivity.this, WebViewActivity.class);
                        webIntent.putExtra("url", AppKeys.terms_of_use_link);
                        startActivity(webIntent);*/
                        break;

                    default:
                        break;
                }
            }
        };
    }


    private void initViews() {
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_password = (TextInputEditText) findViewById(R.id.et_password);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_login_button = (TextView) findViewById(R.id.tv_login_button);
        tv_create_account = (TextView) findViewById(R.id.tv_create_account);
        tv_browse_app = (TextView) findViewById(R.id.tv_browse_app);
        tv_tnc_message_link = (TextView) findViewById(R.id.tv_tnc_message_link);

        rl_google_signup_wrapper = (RelativeLayout) findViewById(R.id.rl_google_signup_wrapper);
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        //  tv_fb_login=(TextView)findViewById(R.id.tv_fb_login);
    }

    private void setFocusChangeListeners() {
        final Pattern p = Pattern.compile("[a-z]");

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String s = et_email.getText().toString().trim();
                if (!s.isEmpty() && !hasFocus) {
                    //if it contains letters
                    if (p.matcher(s).find()) {
                        //check for valid email address
                        if (isValidEmail(s) || s.length() == 0) {
                            til_email.setError(null);
                        } else {
                            til_email.setError(getString(R.string.please_enter_valid_email));
                        }
                    }
                    //if it only contains numbers
                    else {
                        if (Patterns.PHONE.matcher(s).matches() || s.length() == 0 || s.length() == 10) {
                            til_email.setError(null);
                        } else {
                            til_email.setError(getString(R.string.please_enter_valid_phone));
                        }
                    }
                }
            }
        });

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String s = et_password.getText().toString().trim();
                if (!s.isEmpty() && !hasFocus) {
                    if (isPasswordValid(s.toString()) || s.length() == 0) {
                        til_password.setError(null);
                    } else {
                        til_password.setError(getString(R.string.password_must_be_8));
                    }
                }
            }
        });
    }

    private void setTextWatchers() {
   /*     et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(isEmailValid(et_email.getText().toString().trim())){
                    et_email.setBackgroundResource(R.drawable.app_theme_border);
                }
                else{
                    et_email.setBackgroundResource(R.drawable.red_border);
                    et_email.setError("Please enter a valid Email");
                }
            }
        });
*/
        et_email.addTextChangedListener(new TextWatcher() {
            Pattern p = Pattern.compile("[a-z]");

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //if it contains letters
                if (p.matcher(s).find()) {
                    //check for valid email address
                    if (isValidEmail(s) || s.length() == 0) {
                        til_email.setError(null);
                    } else {
                        //  til_email.setError(getString(R.string.enter_valid_email));
                    }
                }
                //if it only contains numbers
                else {
                    if (Patterns.PHONE.matcher(s).matches() || s.length() == 0 || s.length() == 10) {
                        til_email.setError(null);
                    } else {
                        // til_email.setError(getString(R.string.enter_valid_email));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPasswordValid(s.toString()) || s.length() == 0) {
                    til_password.setError(null);
                } else {
                    //  til_password.setError(getString(R.string.password_must_be_8));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public boolean validateAllFields() {
        Pattern p = Pattern.compile("[a-z]");
        String password = et_password.getText().toString().trim();
        String enteredString = "";
        enteredString = et_email.getText().toString().trim();
        boolean allValuesOk = true;

        if (enteredString.length() == 0) {
            et_email.setError(getString(R.string.please_enter_email_or_phone));
            allValuesOk = false;
        }
        //if it contains letters
        else if (p.matcher(enteredString).find()) {
            //check for valid email address
            if (!isValidEmail(enteredString)) {
                til_email.setError(getString(R.string.please_enter_valid_email));
                allValuesOk = false;
            } else {
                til_email.setError(null);
            }
        }
        //if it only contains numbers
        else {
            if (!Patterns.PHONE.matcher(enteredString).matches() || enteredString.length() != 10) {
                til_email.setError(getString(R.string.please_enter_valid_phone));
                allValuesOk = false;
            } else {
                til_email.setError(null);
            }
        }

        if (isPasswordValid(password)) {
            til_password.setError(null);
        } else {
            til_password.setError(getString(R.string.pass_must_8_long_no_spaces));
            allValuesOk = false;
        }

        return allValuesOk;
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 8 && !password.contains(" ");
    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        //Log.i(TAG, "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            // Log.e(TAG, ex.toString());
        }
        return null;
    }

    private void hitLoginApi(final String email, final String password, String os, String ip_address) {
        FirebaseInstanceId.getInstance().getInstanceId();
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        if (regId == null) {
            regId = "";
        }
        Call<PojoLoginResponse> call = apiService.loginApi(email, password, os, ip_address, regId, android_id);
        call.enqueue(new Callback<PojoLoginResponse>() {
            @Override
            public void onResponse(Call<PojoLoginResponse> call, Response<PojoLoginResponse> response) {
                String msg = "";
                if (response.body() != null) {
                    msg = response.body().message;
                    if (response.body().data != null && response.body().status == 1) {
                        pojoLoginResponseData = response.body().data;
                        if (response.body().data.twoStep != null && response.body().data.twoStep) {
                            commonFunctions.closeProgressDialog();
                            if (twoStepOtpDialog == null || !twoStepOtpDialog.isShowing()) {
                                openTwoStepOtpDialog(pojoLoginResponseData.userId, email, "login");
                            }

                        } else if (pojoLoginResponseData.message != null && pojoLoginResponseData.message.contains("activate")) {
                            commonFunctions.closeProgressDialog();
                            openAccountActivationDialog(pojoLoginResponseData.userId);
                        } else {
                            preferenceUtils.savePref(AppKeys.USER_ID_KEY, pojoLoginResponseData.userId);
                            //preferenceUtils.savePref(AppKeys.USER_ID_KEY, "2436");
                            preferenceUtils.savePref(AppKeys.USER_NAME_KEY, pojoLoginResponseData.userFullname);
                            preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                            Intent newsFeedIntent = new Intent(LoginActivity.this, SplashLoaderActivity.class);
                            startActivity(newsFeedIntent);
                            newsFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            logAppLoginEvent(email);
                            finish();
                        }
                    } else {
                        til_email.setError(msg);
                        commonFunctions.setToastMessage(getApplicationContext(), msg,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();
                        til_password.setPasswordVisibilityToggleEnabled(true);
                    }
                } else {
                    commonFunctions.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<PojoLoginResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(LoginActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                // Log error here since request failed

            }
        });
    }


    private void openTwoStepOtpDialog(final String userId, final String email, final String type) {
        twoStepOtpDialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);

        twoStepOtpDialog.setContentView(R.layout.otp_dialog);
        final EditText et_content = (EditText) twoStepOtpDialog.findViewById(R.id.et_content);
        et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_dialog_description = (TextView) twoStepOtpDialog.findViewById(R.id.tv_dialog_description);
        TextView tv_done = (TextView) twoStepOtpDialog.findViewById(R.id.tv_done);
        final TextView tv_resend = (TextView) twoStepOtpDialog.findViewById(R.id.tv_resend);
        tv_resend.setVisibility(View.GONE);

        final CountDownTimer countDownTimer;
        final TextView tv_timer = (TextView) twoStepOtpDialog.findViewById(R.id.tv_timer);


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
                hitLoginApi(et_email.getText().toString().trim(), et_password.getText().toString().trim(),
                        Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
                        , getLocalIpAddress());
            }
        });

        tv_dialog_description.setText(getString(R.string.otp_sent_to_phone));

        TextView tv_cancel = (TextView) twoStepOtpDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoStepOtpDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_otp),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    commonFunctions.openProgressDialog();
                    hitLoginTwoStepApi(userId, et_content.getText().toString(), email);
                    twoStepOtpDialog.dismiss();
                }
            }
        });

       /* ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_SMS},
                REQUEST_CODE_READ_SMS);*/

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

        twoStepOtpDialog.show();
    }


    private void openAccountActivationDialog(final String userId) {
        accountVerificationDialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);
        accountVerificationDialog.setContentView(R.layout.otp_dialog);
        final EditText et_content = (EditText) accountVerificationDialog.findViewById(R.id.et_content);
        et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_dialog_description = (TextView) accountVerificationDialog.findViewById(R.id.tv_dialog_description);
        TextView tv_done = (TextView) accountVerificationDialog.findViewById(R.id.tv_done);
        final TextView tv_resend = (TextView) accountVerificationDialog.findViewById(R.id.tv_resend);
        // tv_resend.setVisibility(View.GONE);

        final CountDownTimer countDownTimer;
        final TextView tv_timer = (TextView) accountVerificationDialog.findViewById(R.id.tv_timer);
        tv_timer.setText(getString(R.string.didnt_receive_otp));

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

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.start();
                registerAgain(userId);
            }
        });

        tv_dialog_description.setText(getString(R.string.account_unverified_description));
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
                    commonFunctions.openProgressDialog();
                    hitSignupVerifyApi(userId, et_content.getText().toString());
                    accountVerificationDialog.dismiss();
                }
            }
        });

       /* ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_SMS},
                REQUEST_CODE_READ_SMS);*/

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
        registerAgain(userId);
        accountVerificationDialog.show();
    }

    public void registerAgain(String userId) {
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = source.parse(pojoLoginResponseData.dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String firstName = pojoLoginResponseData.userFullname;
        String lastName = "";

        if (firstName.contains(" ")) {
            lastName = firstName.substring(firstName.indexOf(" ") + 1, firstName.length());
            firstName = firstName.substring(0, firstName.indexOf(" "));
        }

        String phone = et_email.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        Pattern p = Pattern.compile("[a-z]");
        if (p.matcher(email).find()) {
            phone = null;
        } else {
            email = null;
        }

        hitRegistrationApi(firstName, lastName, email, et_password.getText().toString().trim(),
                pojoLoginResponseData.userGender, cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), phone);
    }

    private void hitRegistrationApi(String first_name, String last_name, final String email, final String password,
                                    String gender, int day, int month, int year, String phone) {
        commonFunctions.openProgressDialog();

        if (last_name.isEmpty()) {
            last_name = null;
        }


        Call<PojoSignUp1Response> call = apiService.registrationApi(first_name, last_name, email, password,
                gender, String.valueOf(day), String.valueOf(month), String.valueOf(year), phone, "");

        call.enqueue(new Callback<PojoSignUp1Response>() {
            @Override
            public void onResponse(Call<PojoSignUp1Response> call, Response<PojoSignUp1Response> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null && response.body().status == 1) {
                    } else {
                        commonFunctions.setToastMessage(LoginActivity.this, "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();
                    }
                }

            }

            @Override
            public void onFailure(Call<PojoSignUp1Response> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed

                commonFunctions.setToastMessage(LoginActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void hitSignupVerifyApi(String user_id, String otp) {
        Call<PojoNoDataResponse> call = apiService.verifySignup(user_id, otp);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String msg = "";
                if (response.body() != null) {
                    msg = response.body().message;
                    if (response.body().status == 1) {
                        hitLoginApi(et_email.getText().toString().trim(), et_password.getText().toString().trim(),
                                Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(),
                                getLocalIpAddress());
                        accountVerificationDialog.dismiss();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), msg,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed

            }
        });
    }


    private void hitLoginTwoStepApi(final String user_id, final String otp, final String email) {
        Call<PojoNoDataResponse> call = apiService.loginTwoStepApi(user_id, otp);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String msg = "";
                if (response.body() != null) {
                    msg = response.body().message;
                    if (response.body().status == 1) {
                        preferenceUtils.savePref(AppKeys.USER_ID_KEY, pojoLoginResponseData.userId);
                        preferenceUtils.savePref(AppKeys.USER_NAME_KEY, pojoLoginResponseData.userFullname);
                        preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                        Intent newsFeedIntent = new Intent(LoginActivity.this, SplashLoaderActivity.class);
                        startActivity(newsFeedIntent);
                        finish();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), msg,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();
                        openTwoStepOtpDialog(user_id, email, "two_step");
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(LoginActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                // Log error here since request failed
            }
        });
    }


    private void hitGetLoginDevicesApi(final String user_id, String email) {
        Call<PojoLoggedInDevicesResponse> call = apiService.getLoggedInDevices(email);
        call.enqueue(new Callback<PojoLoggedInDevicesResponse>() {
            @Override
            public void onResponse(Call<PojoLoggedInDevicesResponse> call, Response<PojoLoggedInDevicesResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    commonFunctions.closeProgressDialog();
                    for (int i = 1; i < response.body().devices.size(); i++) {
                        hitLogoutApi(user_id, response.body().devices.get(i));
                    }

                    Intent newsFeedIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(newsFeedIntent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<PojoLoggedInDevicesResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }


    private void hitLogoutApi(String user_id, String device_id) {
        Call<PojoLogoutResponse> call = apiService.logout(user_id, device_id, pojoLoginResponseData.sessionId.toString());
        call.enqueue(new Callback<PojoLogoutResponse>() {
            @Override
            public void onResponse(Call<PojoLogoutResponse> call, Response<PojoLogoutResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoLogoutResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startRegisterActivity(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Registration", "signInResult:failed code=" + e.getStatusCode());
            startRegisterActivity(null);
        }
    }


    public void startRegisterActivity(GoogleSignInAccount account) {
        commonFunctions.closeProgressDialog();
        if (account != null) {
            Uri uri = account.getPhotoUrl();
            if (uri != null) {
                String imagePath = uri.getPath();
            }
            String name = account.getDisplayName();
            String[] words = name.split(" ");


            String firstname = "";
            for (int i = 0; i < words.length - 1; i++) {
                firstname = firstname + " " + words[i];
            }

            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity1.class);
            registerIntent.putExtra(AppKeys.FIRST_NAME, firstname);
            registerIntent.putExtra(AppKeys.LAST_NAME, words[words.length - 1]);
            registerIntent.putExtra(AppKeys.EMAIL, account.getEmail());
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT/* | Intent.FLAG_ACTIVITY_NO_HISTORY*/);
            startActivity(registerIntent);

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);
    }


}
