package com.avadna.luneblaze.activities.registration;

import android.app.DatePickerDialog;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.KeyValuePair;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSignUp1Response;
import com.avadna.luneblaze.pojo.PojoUpdateTokenResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity1 extends AppBaseActivity {
    private static final int RC_SIGN_IN = 32;
    private static final int REQUEST_CODE_READ_SMS = 123;
    private static final long OTP_WAITING_TIME = 40000;
    private ScrollView sv_content_wrapper;
    private DisplayMetrics displayMetrics;
    private int screenHeight, screenWidth;
    private EditText et_first_name;
    private EditText et_last_name, et_email, et_password;
    private EditText et_date_of_birth;

    private TextInputLayout til_first_name, til_last_name, til_email, til_password, til_date_of_birth;

    private TextView tv_next, tv_click_to_login;

    private TextView tnc_agreement;

    private SignInButton sign_in_button;
    private RadioButton rb_male, rb_female, rb_other;
    private View.OnClickListener mOnClickListener;
    private ApiInterface apiService;
    private String gender = "";
    private boolean enableApiCall = true;
    private GoogleSignInClient mGoogleSignInClient;
    private PreferenceUtils preferenceUtils;

    private CommonFunctions commonFunctions;
    private Dialog accountVerificationDialog;

    private int age;

    private String[] monthNames = {"", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String dob = "";
    private String startDate;

    private final int FIRST_NAME_MAX_LIMIT = 25;
    private final int LAST_NAME_MAX_LIMIT = 15;

    private int currDay, currMonth, currYear, chosenDay = 0, chosenMonth = 0, chosenYear = 0;
    private PojoLoginResponseData pojoLoginResponseData;

    private String userId;
    private String email;
    private String phone;
    private String password;
    private String firstName;
    private String lastName;

    private MySMSBroadcastReceiver mySMSBroadcastReceiver;
    private String otpSmsPrefix = "<#> Your OTP for account verification is ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        preferenceUtils.setAppTourStatus(false);

        initDispMetrics();
        initViews();
        initClickListener();
        setClickListener();
        setTextWatcher();
        setFocusListener();
        initApis();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       /* GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);*/
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.openProgressDialog();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(RegisterActivity1.this, R.color.status_bar_color));
        }*/

        checkIfIntentHasData();
    }

    private void checkIfIntentHasData() {
        String first_name = getIntent().getStringExtra(AppKeys.FIRST_NAME);
        if (first_name != null) {
            et_first_name.setText(first_name);
        }

        String last_name = getIntent().getStringExtra(AppKeys.LAST_NAME);
        if (last_name != null) {
            et_last_name.setText(last_name);
        }

        String email = getIntent().getStringExtra(AppKeys.EMAIL);
        if (email != null) {
            et_email.setText(email);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        commonFunctions.closeProgressDialog();
        if (account != null) {
            et_email.setText(account.getEmail());
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
            et_first_name.setText(firstname);
            et_last_name.setText(words[words.length - 1]);
        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setFocusListener() {

        et_first_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = et_first_name.getText().toString().trim();
                if (!s.isEmpty() && !hasFocus) {
                    if (isValidName(s)) {
                        til_first_name.setError(null);
                    } else {
                        til_first_name.setError(getString(R.string.first_name_only_letters));
                    }
                }
            }
        });

        et_last_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = et_last_name.getText().toString().trim();
                if (!s.isEmpty() && !hasFocus) {
                    if (isValidLastName(s)) {
                        til_last_name.setError(null);
                    } else {
                        til_last_name.setError(getString(R.string.last_name_only_letters_no_spaces));
                    }
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String s = et_email.getText().toString().trim();
                final Pattern p = Pattern.compile("[a-z]");

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

    /*    et_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String s = et_phone.getText().toString().trim();

                if (Patterns.PHONE.matcher(s).matches() || s.length() == 0) {
                    til_phone.setError(null);
                } else {
                    til_phone.setError(getString(R.string.enter_valid_phone));
                }
            }
        });*/


        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = et_password.getText().toString().trim();
                if (!s.isEmpty() && !hasFocus) {
                    if (isPasswordValid(s)) {
                        til_password.setError(null);
                    } else {
                        til_password.setError(getString(R.string.password_must_be_8));
                    }
                }
            }
        });
    }

    private void setTextWatcher() {
        et_first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String firstName = s.toString().trim();
                if (isValidName(firstName) || firstName.length() == 0) {
                    til_first_name.setError(null);
                } else {
                    // til_first_name.setError(getString(R.string.first_name_only_letters));
                }
                if (s.length() > FIRST_NAME_MAX_LIMIT) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.first_name_not_longer_than_25), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_first_name.setText(s.subSequence(0, FIRST_NAME_MAX_LIMIT));
                    et_first_name.setSelection(et_first_name.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lastName = s.toString().trim();
                if (isValidLastName(lastName) || lastName.length() == 0) {
                    til_last_name.setError(null);
                } else {
                    //  til_last_name.setError(getString(R.string.last_name_only_letters));
                }
                if (s.length() > LAST_NAME_MAX_LIMIT) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.last_name_not_longer_than_15), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_last_name.setText(s.subSequence(0, LAST_NAME_MAX_LIMIT));
                    et_last_name.setSelection(et_last_name.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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


/*        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Patterns.PHONE.matcher(s).matches() || s.length() == 0) {
                    til_phone.setError(null);
                } else {
                    // til_email.setError(getString(R.string.enter_valid_email));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPasswordValid(s.toString()) || s.length() == 0) {
                    til_password.setError(null);
                } else {
                    //    til_password.setError(getString(R.string.password_must_be_8));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_date_of_birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                age = getAge(chosenYear, chosenMonth, chosenDay);
                if (et_date_of_birth.getText().toString().isEmpty()) {
                    til_date_of_birth.setError(null);
                    //   commonFunctions.setToastMessage(RegisterActivity2.this, "Please enter your date of birth", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                } else if (age < 14) {
                    til_date_of_birth.setError(getString(R.string.register_age_limit_message));
                } else {
                    til_date_of_birth.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setFocusChangeListeners() {

    }


    public int getAge(int year, int month, int day) {
        final Calendar birthDay = Calendar.getInstance();
        birthDay.set(year, month, day);
        final Calendar current = Calendar.getInstance();
        if (current.getTimeInMillis() < birthDay.getTimeInMillis()) {
            til_date_of_birth.setError(getString(R.string.register_age_limit_message));
        }

        int age = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (birthDay.get(Calendar.MONTH) > current.get(Calendar.MONTH) ||
                (birthDay.get(Calendar.MONTH) == current.get(Calendar.MONTH) &&
                        birthDay.get(Calendar.DATE) > current.get(Calendar.DATE)))
            age--;
        return age;
    }


    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }


    public boolean validateAllFields() {

        boolean allValuesOk = true;


        email = et_email.getText().toString().trim();
        phone = et_email.getText().toString().trim();
        Pattern p = Pattern.compile("[a-z]");


        if (email.length() == 0) {
            til_email.setError(getString(R.string.please_enter_email_or_phone));
            allValuesOk = false;
        }

     /*   if (phone.length() == 0) {
            til_phone.setError(getString(R.string.please_enter_your_phone_number));
            allValuesOk = false;
        }*/

        //if it contains letters

        else if (p.matcher(email).find()) {
            email = et_email.getText().toString().trim();
            //check for valid email address
            if (!isValidEmail(email)) {
                til_email.setError(getString(R.string.please_enter_valid_email));
                allValuesOk = false;
            } else {
                til_email.setError(null);
                phone = null;
            }
        }
        //if it only contains numbers
        else if (!Patterns.PHONE.matcher(phone).matches() || phone.length() != 10) {
            til_email.setError(getString(R.string.please_enter_valid_phone));
            allValuesOk = false;
        } else {
            til_email.setError(null);
            email = null;
        }


        password = et_password.getText().toString().trim();
        if (isPasswordValid(password)) {
            til_password.setError(null);
        } else {
            til_password.setError(getString(R.string.pass_must_8_long_no_spaces));
            allValuesOk = false;
        }


        firstName = et_first_name.getText().toString().trim();
        if (isValidName(firstName)) {
            til_first_name.setError(null);
        } else {
            til_first_name.setError(getString(R.string.please_enter_a_valid_first_name));
            allValuesOk = false;
        }

        lastName = et_last_name.getText().toString().trim();
        if (isValidLastName(lastName)) {
            til_last_name.setError(null);
        } else {
            til_last_name.setError(getString(R.string.please_enter_a_valid_last_name));
            allValuesOk = false;
        }

        dob = "" + chosenYear + "-" + (chosenMonth + 1) + "-" + chosenDay;
        age = getAge(chosenYear, chosenMonth, chosenDay);


        if (dob.isEmpty() || chosenDay == 0 || chosenYear == 0) {
            til_date_of_birth.setError(getString(R.string.enter_dob));
            allValuesOk = false;

        } else if (age < 14) {
            til_date_of_birth.setError(getString(R.string.register_age_limit_message));
            allValuesOk = false;
        } else {
            til_date_of_birth.setError(null);
        }

        if (rb_male.isChecked()) {
            gender = "male";
        }
        if (rb_female.isChecked()) {
            gender = "female";
        }
        if (rb_other.isChecked()) {
            gender = "other";
        }

        if (gender.isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(),
                    getString(R.string.please_select_your_gender),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            allValuesOk = false;

        }

        return allValuesOk;
    }


    private void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_next:
                        if (validateAllFields()) {
                            hitRegistrationApi(firstName, lastName, email, password, gender, chosenDay,
                                    chosenMonth + 1, chosenYear, phone);
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        break;
                    case R.id.tv_click_to_login:
                        //  commonFunctions.setToastMessage(getApplicationContext(), "click to login clicked", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        Intent loginActivityIntent = new Intent(RegisterActivity1.this, LoginActivity.class);
                        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(loginActivityIntent);
                        break;

                    case R.id.et_date_of_birth:
                        openDatePickerDialogBox();

                        break;

                    case R.id.rb_male:
                        if (rb_male.isChecked()) {
                            gender = "male";
                        }
                        break;

                    case R.id.rb_female:
                        if (rb_female.isChecked()) {
                            gender = "female";
                        }
                        break;

                    case R.id.rb_other:
                        if (rb_other.isChecked()) {
                            gender = "other";
                        }
                        break;

                    default:
                        break;
                }
            }
        };

    }

    private boolean isValidPhone(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    private void openDatePickerDialogBox() {
        final Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currYear -= 14;
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);


        chosenYear = currYear;
        chosenDay = currDay;
        chosenMonth = currMonth;

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(RegisterActivity1.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                chosenDay = day;
                chosenMonth = month;
                chosenYear = year;
                et_date_of_birth.setText(day + " " + monthNames[month + 1] + " " + year);
            }
        }, currYear, currMonth, currDay);
        mDatePickerDialog.show();
    }


    private void hitRegistrationApi(String first_name, String last_name, final String email, final String password,
                                    String gender, int day, int month, int year, String phone) {
        commonFunctions.openProgressDialog();
        enableApiCall = false;

        if (last_name.isEmpty()) {
            last_name = null;
        }

        Call<PojoSignUp1Response> call = apiService.registrationApi(first_name, last_name, email, password,
                gender, String.valueOf(day), String.valueOf(month), String.valueOf(year), phone, "");

        call.enqueue(new Callback<PojoSignUp1Response>() {
            @Override
            public void onResponse(Call<PojoSignUp1Response> call, Response<PojoSignUp1Response> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null && response.body().status == 1) {
                        userId = String.valueOf(response.body().data.userId);
                        openOtpDialog(email, phone);

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();
                    }
                }
                enableApiCall = true;
            }

            @Override
            public void onFailure(Call<PojoSignUp1Response> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                enableApiCall = true;
                // Log error here since request failed

                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }


    private void openOtpDialog(final String email, String phone) {
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
                countDownTimer.start();
                hitRegistrationApi(firstName, lastName, email, password, gender, chosenDay,
                        chosenMonth + 1, chosenYear, phone);
                accountVerificationDialog.dismiss();
            }
        });

        tv_dialog_description.setText(getString(R.string.otp_sent_to_phone_email));
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
                    hitSignupVerifyApi(email,userId, et_content.getText().toString());
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
        accountVerificationDialog.show();
    }

    private void hitSignupVerifyApi(String email, String user_id, String otp) {
        Call<PojoNoDataResponse> call = apiService.verifySignup(user_id, otp);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String msg = "";
                if (response.body() != null) {
                    msg = response.body().message;
                    if (response.body().status == 1) {
                        logAppSignUpEvent(email);
                        hitLoginApi(et_email.getText().toString().trim(), et_password.getText().toString().trim(),
                                Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(),
                                getLocalIpAddress());
                        accountVerificationDialog.dismiss();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), msg,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();

                    }
                } else {
                    commonFunctions.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed

            }
        });
    }


    private void logAppSignUpEvent(String email) {
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
        commonFunctions.logFirebaseEvent(FirebaseAnalytics.Event.SIGN_UP,pairs);
    }


    private void hitLoginApi(String email, final String password, String os, String ip_address) {
        FirebaseInstanceId.getInstance().getInstanceId();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        commonFunctions.openProgressDialog();
        Call<PojoLoginResponse> call = apiService.loginApi(email, password, os, ip_address, regId, android_id);
        call.enqueue(new Callback<PojoLoginResponse>() {
            @Override
            public void onResponse(Call<PojoLoginResponse> call, Response<PojoLoginResponse> response) {
                String msg = "";
                if (response.body() != null) {
                    msg = response.body().message;
                    if (response.body().data != null && response.body().status == 1) {
                        pojoLoginResponseData = response.body().data;
                        preferenceUtils.savePref(AppKeys.USER_ID_KEY, pojoLoginResponseData.userId);
                        preferenceUtils.savePref(AppKeys.USER_NAME_KEY, pojoLoginResponseData.userFullname);
                        preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                        hitUpdateTokenApi();
                        Intent pushNotification = new Intent(Config.FINISH_LOGIN_ACTIVITY);
                        LocalBroadcastManager.getInstance(RegisterActivity1.this).sendBroadcast(pushNotification);
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), msg,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        commonFunctions.closeProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoLoginResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed

            }
        });
    }

    private void hitUpdateTokenApi() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        String user_id = pojoLoginResponseData.userId;
        Call<PojoUpdateTokenResponse> call = apiService.updateFcmToken(user_id, android_id, "A", regId);
        call.enqueue(new Callback<PojoUpdateTokenResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateTokenResponse> call, Response<PojoUpdateTokenResponse> response) {
                String message = "";
                commonFunctions.closeProgressDialog();
                if (response != null) {
                    Intent registerActivity2Intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                    registerActivity2Intent.putExtra("user_id", preferenceUtils.get_user_id());
                    startActivity(registerActivity2Intent);
                    RegisterActivity1.this.finish();
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateTokenResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed
                // commonFunctions.setToastMessage(ArticleActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
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

    public void initViews() {
        sv_content_wrapper = (ScrollView) findViewById(R.id.sv_content_wrapper);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        rb_other = (RadioButton) findViewById(R.id.rb_other);
        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        et_email = (EditText) findViewById(R.id.et_email);
        // et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        til_first_name = (TextInputLayout) findViewById(R.id.til_first_name);
        til_last_name = (TextInputLayout) findViewById(R.id.til_last_name);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        //  til_phone = (TextInputLayout) findViewById(R.id.til_phone);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_date_of_birth = (TextInputLayout) findViewById(R.id.til_date_of_birth);

        tnc_agreement = (TextView) findViewById(R.id.tnc_agreement);
        tv_next = (TextView) findViewById(R.id.tv_next);
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        et_date_of_birth = (EditText) findViewById(R.id.et_date_of_birth);
        tv_click_to_login = (TextView) findViewById(R.id.tv_click_to_login);

        for (int i = 0; i < sign_in_button.getChildCount(); i++) {
            View v = sign_in_button.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(getString(R.string.sign_up));
                return;
            }
        }
    }


    private void setClickListener() {
        tv_click_to_login.setOnClickListener(mOnClickListener);
        tv_next.setOnClickListener(mOnClickListener);
        rb_male.setOnClickListener(mOnClickListener);
        rb_female.setOnClickListener(mOnClickListener);
        rb_other.setOnClickListener(mOnClickListener);
        et_date_of_birth.setOnClickListener(mOnClickListener);


        SpannableString spannableString = new SpannableString(getString(R.string.tnc_agreement));

        ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                commonFunctions.urlLoader(AppKeys.privacy_policy_link);
               /* Intent webIntent = new Intent(RegisterActivity1.this, WebViewActivity.class);
                webIntent.putExtra(AppKeys.URL, AppKeys.privacy_policy_link);
                startActivity(webIntent);*/
            }
        };

        ClickableSpan clickableSpanSession = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                commonFunctions.urlLoader(AppKeys.session_policy_link);
                /*Intent webIntent = new Intent(RegisterActivity1.this, WebViewActivity.class);
                webIntent.putExtra(AppKeys.URL, AppKeys.session_policy_link);
                startActivity(webIntent);*/
            }
        };

        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                commonFunctions.urlLoader(AppKeys.terms_of_use_link);
                /*Intent webIntent = new Intent(RegisterActivity1.this, WebViewActivity.class);
                webIntent.putExtra(AppKeys.URL, AppKeys.terms_of_use_link);
                startActivity(webIntent);*/
            }
        };

        spannableString.setSpan(clickableSpanPrivacy, Integer.parseInt(getString(R.string.privacy_index_start)),
                Integer.parseInt(getString(R.string.privacy_index_end)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(clickableSpanSession, Integer.parseInt(getString(R.string.session_policy_index_start)),
                Integer.parseInt(getString(R.string.session_policy_index_end)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(clickableSpanTerms, Integer.parseInt(getString(R.string.terms_index_start)),
                Integer.parseInt(getString(R.string.terms_index_end)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tnc_agreement.setMovementMethod(LinkMovementMethod.getInstance());

        tnc_agreement.setText(spannableString);

    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public boolean isPasswordValid(String password) {
        return password.length() >= 8 && !password.contains(" ");
    }


    public boolean isValidName(String name) {
        return Pattern.compile("[a-zA-Z \\u0904-\\u097f]*+").matcher(name).matches();
    }

    public boolean isValidLastName(String name) {

        if (name.contains(" ")) {
            return false;
        } else {
            return Pattern.compile("[a-zA-Z \\u0904-\\u097f]*+").matcher(name).matches();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Registration", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
