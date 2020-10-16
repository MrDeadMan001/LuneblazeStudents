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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.settingActivities.AddedPhoneNumbersActivity;
import com.avadna.luneblaze.adapters.SelectedVenuesListAdapter;
import com.avadna.luneblaze.adapters.VenueSearchResultAdapter;
import com.avadna.luneblaze.adapters.calender.DateListAdapter;
import com.avadna.luneblaze.adapters.calender.DayGroupAdapter;
import com.avadna.luneblaze.adapters.calender.MonthListAdapter;
import com.avadna.luneblaze.adapters.calender.YearListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoConductSessionResponse;
import com.avadna.luneblaze.pojo.PojoCreateSession;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCreationPart3 extends AppBaseActivity
        implements VenueSearchResultAdapter.VenueSearchResultAdapterCallback,
        SelectedVenuesListAdapter.SelectedVenuesListAdapterCallback,
        DayGroupAdapter.DayGroupItemCallback {

    private static final int LOCAL_REQUEST_CODE_READ_SMS = 123;
    String type;

    ApiInterface apiService;
    ApiInterface SMSapiService;

    ScrollView sv_content_wrapper;
    ProgressBar pb_calling_api;
    String user_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String phone = "";

    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;


    TextView tv_venue1;
    TextView tv_venue2;
    TextView tv_venue3;

    TextView tv_venue1_date1, tv_venue1_date2, tv_venue1_date3;
    TextView tv_venue2_date1, tv_venue2_date2, tv_venue2_date3;
    TextView tv_venue3_date1, tv_venue3_date2, tv_venue3_date3;

    Date dateArr[][] = new Date[2][2];

    String[] venue1Dates = new String[2];
    String[] venue2Dates = new String[2];
    String[] venue3Dates = new String[2];

    Dialog calenderDialog;
    int yearPos;
    int monthPos;
    List<Date> selectedDates;
    List<Date> preSelectedDates;
    DateListAdapter dateListAdapter;


    int pickDatePosition = 0;
    int pickVenuePosition = 0;

    Spinner sp_purpose_list;
    PojoCreateSession pojoCreateSession;

    TextView tv_verify_otp;
    int PHONE_VERIFIED = 1;
    boolean otpNeeded = true;

    PojoGetVenueListResponseData selectedVenue1;
    PojoGetVenueListResponseData selectedVenue2;
    PojoGetVenueListResponseData selectedVenue3;

  /*  RecyclerView rv_selected_venue_list;
    VenueSearchResultAdapter selectedVenuesListAdapter;
    RecyclerView rv_selected_venue_list_in_search;
    SelectedVenuesListAdapter selectedVenuesListInSearchAdapter;
    TextView tv_length_getter;*/

    //  ImageView iv_add_venue;

    Dialog venueSearchDialog;
    RecyclerView rv_venue_search_result;
    VenueSearchResultAdapter venueResultsListAdapter;
    List<PojoGetVenueListResponseData> venueSearchResultList = new ArrayList<>();
    EditText et_venue_name;
    TextView tv_venue_search_button;
    RelativeLayout rl_venue_search_wrapper;

    float lat = 40f;
    float lon = 74f;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    int venuePos = 0;
    int datePos = 0;

    Dialog otpDialog;

    MySMSBroadcastReceiver mySMSBroadcastReceiver;

    String otpSmsPrefix = "<#> Your OTP for session creation is ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_part3);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        SMSapiService = SMSApiClient.getClient().create(ApiInterface.class);

        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        type = getIntent().getStringExtra("type");
        setUpActionBar();

        initViews();
        //initLists();
        initClickListener();
        setClickListener();
        initBroadCastReceiver();
        Type jsonType = new TypeToken<PojoCreateSession>() {
        }.getType();
        pojoCreateSession = new Gson().fromJson(getIntent().getStringExtra("data"), jsonType);

        MyLocationObject myLocationObject = preferenceUtils.getUserCurrentLocation();
        if (myLocationObject != null) {
            lat = (float) myLocationObject.getLatitude();
            lon = (float) myLocationObject.getLongitude();
        }

        selectedDates = new ArrayList<>();
        preSelectedDates = new ArrayList<>();

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

    /*private void initLists() {
        rv_selected_venue_list = findViewById(R.id.rv_selected_venue_list);
        selectedVenuesListAdapter = new VenueSearchResultAdapter(this, selectedVenuesList);
        rv_selected_venue_list.setLayoutManager(new LinearLayoutManager(SessionCreationPart3.this,
                LinearLayoutManager.VERTICAL, false));
        rv_selected_venue_list.setAdapter(selectedVenuesListAdapter);

    }*/

    public void openVenueSearchDialog() {

        venueSearchDialog = new MyCustomThemeDialog(SessionCreationPart3.this);
        venueSearchDialog.setContentView(R.layout.venue_search_dialog);
        venueSearchDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        venueSearchDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        et_venue_name = (EditText) venueSearchDialog.findViewById(R.id.et_venue_name);
        final ImageButton ib_send;
        TextView tv_close_venue_search = (TextView) venueSearchDialog.findViewById(R.id.tv_close_venue_search);
        tv_close_venue_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueSearchDialog.dismiss();
            }
        });

        et_venue_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hitVenueSearchWithTextApi(lat, lon, charSequence.toString(), "0");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ib_send = (ImageButton) venueSearchDialog.findViewById(R.id.ib_send);
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitVenueSearchWithTextApi(lat, lon, et_venue_name.getText().toString().trim(), "0");
            }
        });

        venueSearchResultList = new ArrayList<>();
        rv_venue_search_result = (RecyclerView) venueSearchDialog.findViewById(R.id.rv_venue_search_result);
        rv_venue_search_result.setLayoutManager
                (new LinearLayoutManager(SessionCreationPart3.this,
                        RecyclerView.VERTICAL, false));
        rv_venue_search_result.setNestedScrollingEnabled(false);
        venueResultsListAdapter = new VenueSearchResultAdapter(this, venueSearchResultList);
        rv_venue_search_result.setAdapter(venueResultsListAdapter);

        if (!SessionCreationPart3.this.isFinishing()) {
            venueSearchDialog.show();
        }

        et_venue_name.post(new Runnable() {
            @Override
            public void run() {
                if (et_venue_name.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_venue_name, InputMethodManager.SHOW_FORCED);
                }
            }
        });


        hitVenueSearchWithTextApi(lat, lon, "", "0");

    }


    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_session));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionCreationPart3.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionCreationPart3.this, R.color.status_bar_color));
            }
        }
    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_venue1_date1:
                        if (selectedVenue1 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(0, 0);

                        }
                        break;

                    case R.id.tv_venue1_date2:
                        if (selectedVenue1 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(0, 1);

                        }
                        break;

                    case R.id.tv_venue1_date3:
                        if (selectedVenue1 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(0, 2);

                        }
                        break;

                    case R.id.tv_venue2_date1:
                        if (selectedVenue2 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(1, 0);

                        }
                        break;

                    case R.id.tv_venue2_date2:
                        if (selectedVenue2 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(1, 1);

                        }
                        break;

                    case R.id.tv_venue2_date3:
                        if (selectedVenue2 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(1, 2);

                        }
                        break;

                    case R.id.tv_venue3_date1:
                        if (selectedVenue3 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(2, 0);

                        }
                        break;

                    case R.id.tv_venue3_date2:
                        if (selectedVenue3 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(2, 1);

                        }
                        break;

                    case R.id.tv_venue3_date3:
                        if (selectedVenue3 == null) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog(2, 2);

                        }
                        break;


                    case R.id.tv_venue_search_button:
                        hitVenueSearchWithTextApi(lat, lon, et_venue_name.getText().toString().trim(), "0");
                        break;

                    case R.id.tv_next:

                        break;

                   /* case R.id.iv_add_venue:
                        openVenueSearchDialog();
                        break;*/


                    case R.id.tv_venue1:
                        venuePos = 0;
                        dateArr[0][0] = null;
                        dateArr[0][1] = null;
                        tv_venue1_date1.setText("");
                        tv_venue1_date2.setText("");
                        openVenueSearchDialog();
                        break;


                    case R.id.tv_venue2:
                        venuePos = 1;
                        dateArr[1][0] = null;
                        dateArr[1][1] = null;
                        tv_venue2_date1.setText("");
                        tv_venue2_date2.setText("");
                        openVenueSearchDialog();
                        break;

                    case R.id.tv_venue3:
                        venuePos = 2;
                        openVenueSearchDialog();

                        break;

                    case R.id.tv_verify_otp:
                        if (type != null && type.equals(AppKeys.SESSION_CONDUCT)) {
                            goSessionCreate4Screen();
                        } else {
                            if (selectedVenue1 == null || selectedVenue2 == null/* || selectedVenue3 == null*/) {
                                commonFunctions.setToastMessage(getApplicationContext(),
                                        getString(R.string.please_choose_two_venues_for_session),
                                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                            } else if (!areAllDatesSet()) {
                                commonFunctions.setToastMessage(getApplicationContext(),
                                        getString(R.string.choose_three_dates_for_session),
                                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                            } else {
                                pojoCreateSession.venues.clear();
                                pojoCreateSession.venues.add(selectedVenue1.venueId);
                                pojoCreateSession.venues.add(selectedVenue2.venueId);
                                //  pojoCreateSession.venues.add(selectedVenue3.venueId);

                                pojoCreateSession.venue1Dates.clear();
                                pojoCreateSession.venue2Dates.clear();
                                //   pojoCreateSession.venue3Dates.clear();

                                pojoCreateSession.venue1Dates.add(venue1Dates[0]);
                                pojoCreateSession.venue1Dates.add(venue1Dates[1]);

                                pojoCreateSession.venue2Dates.add(venue2Dates[0]);
                                pojoCreateSession.venue2Dates.add(venue2Dates[1]);
/*
                            pojoCreateSession.venue3Dates.add(venue3Dates[0]);
                            pojoCreateSession.venue3Dates.add(venue3Dates[1]);*/


                                // pojoCreateSession.event_date.add(dates[3]);
                                hitGetUserPhoneEmailApi(user_id);
                            }
                        }
                        break;

                }
            }
        };
    }

    private boolean areAllDatesSet() {
        if (dateArr[0][0] != null && dateArr[0][1] != null
                && dateArr[1][0] != null && dateArr[1][1] != null) {
            return true;
        } else {
            return false;
        }
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
                            verfiedPresent = true;
                            primaryPresent = true;
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

                        //  startSmsRetriever();
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
        confirmationDialog = new MyCustomThemeDialog(SessionCreationPart3.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(message);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                Intent addPhoneIntent = new Intent(SessionCreationPart3.this, AddedPhoneNumbersActivity.class);
                startActivityForResult(addPhoneIntent, PHONE_VERIFIED);
            }
        });
        confirmationDialog.show();
    }

    private void hitSendOtpApi(final String phone, final String OTP) {
        String SMS = otpSmsPrefix + OTP + AppKeys.otpSmsSuffix;
        Call<String> call = SMSapiService.sendSMS(AES.decrypt(AppKeys.SMS_UNAME, AppKeys.enKey),
                AES.decrypt(AppKeys.SMS_PASS, AppKeys.enKey), "1",
                AppKeys.SMS_COMPANY_NAME, phone, SMS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int a = 5;
                // openOtpDialog(phone, OTP);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), "Cannot connect to Server", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
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

    private void openOtpDialog(final String phone, final String otp) {
        otpDialog = new MyCustomThemeDialog(SessionCreationPart3.this);
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
                tv_timer.setText(getString(R.string.didnt_receive_otp));
                tv_resend.setVisibility(View.VISIBLE);
                // tv_resend.setEnabled(true);
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


    private void setClickListener() {
        //iv_add_venue.setOnClickListener(onClickListener);

        tv_venue1_date1.setOnClickListener(onClickListener);
        tv_venue1_date2.setOnClickListener(onClickListener);
        tv_venue1_date3.setOnClickListener(onClickListener);
        tv_venue2_date1.setOnClickListener(onClickListener);
        tv_venue2_date2.setOnClickListener(onClickListener);
        tv_venue2_date3.setOnClickListener(onClickListener);
        tv_venue3_date1.setOnClickListener(onClickListener);
        tv_venue3_date2.setOnClickListener(onClickListener);
        tv_venue3_date3.setOnClickListener(onClickListener);

        tv_venue1.setOnClickListener(onClickListener);
        tv_venue2.setOnClickListener(onClickListener);
        tv_venue3.setOnClickListener(onClickListener);

        tv_verify_otp.setOnClickListener(onClickListener);
    }

    private void initViews() {
        sv_content_wrapper = (ScrollView) findViewById(R.id.sv_content_wrapper);
        pb_calling_api = (ProgressBar) findViewById(R.id.pb_calling_api);
        pb_calling_api.setVisibility(View.GONE);
        et_venue_name = (EditText) findViewById(R.id.et_venue_name);
        tv_venue_search_button = (TextView) findViewById(R.id.tv_venue_search_button);
        //   rl_venue_search_wrapper = (RelativeLayout) findViewById(R.id.rl_venue_search_wrapper);

        //  iv_add_venue = (ImageView) findViewById(R.id.iv_add_venue);

        tv_venue1 = (TextView) findViewById(R.id.tv_venue1);
        tv_venue2 = (TextView) findViewById(R.id.tv_venue2);
        tv_venue3 = (TextView) findViewById(R.id.tv_venue3);

        tv_venue1_date1 = (TextView) findViewById(R.id.tv_venue1_date1);
        tv_venue1_date2 = (TextView) findViewById(R.id.tv_venue1_date2);
        tv_venue1_date3 = (TextView) findViewById(R.id.tv_venue1_date3);
        tv_venue2_date1 = (TextView) findViewById(R.id.tv_venue2_date1);
        tv_venue2_date2 = (TextView) findViewById(R.id.tv_venue2_date2);
        tv_venue2_date3 = (TextView) findViewById(R.id.tv_venue2_date3);
        tv_venue3_date1 = (TextView) findViewById(R.id.tv_venue3_date1);
        tv_venue3_date2 = (TextView) findViewById(R.id.tv_venue3_date2);
        tv_venue3_date3 = (TextView) findViewById(R.id.tv_venue3_date3);

        tv_verify_otp = (TextView) findViewById(R.id.tv_verify_otp);

        if (type != null && type.equals(AppKeys.SESSION_CONDUCT)) {
            tv_verify_otp.setText(getString(R.string.next));
        }
    }

    private void openCalenderDialog(final int venuePosition, final int datePosition) {
        pickVenuePosition = venuePosition;
        pickDatePosition = datePosition;
        selectedDates = new ArrayList<>();
        calenderDialog = new MyCustomThemeDialog(this);
        calenderDialog.setContentView(R.layout.custom_calender_dialog);
        TextView tv_done_button = (TextView) calenderDialog.findViewById(R.id.tv_done_button);

        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calenderDialog.dismiss();
            }
        });

        Calendar calendar = Calendar.getInstance();
        monthPos = calendar.get(Calendar.MONTH);
        final RecyclerView rv_date_list = (RecyclerView) calenderDialog.findViewById(R.id.rv_date_list);
        final int venueId;

        if (pickVenuePosition == 0) {
            venueId = Integer.parseInt(selectedVenue1.venueId);
        } else {
            venueId = Integer.parseInt(selectedVenue2.venueId);
        }

        int arraySize = 0;


        List<PojoVenueDay> venueDays = null;

        if (venuePosition == 0) {
            venueDays = selectedVenue1.days;
        } else {
            venueDays = selectedVenue2.days;
        }

        for (int i = 0; i < venueDays.size(); i++) {
            if (!venueDays.get(i).slots.isEmpty()) {
                arraySize++;
            }
        }


        final int[] daysOfWeek = new int[arraySize];
        int counter = 0;
        for (int i = 0; i < venueDays.size(); i++) {
            if (!venueDays.get(i).slots.isEmpty()) {
                //need to adjust by +1 as calender.day_of_week gives sunday 1 but in api sunday is at index 0
                daysOfWeek[counter++] = i + 1;
            }
        }

        calendar = Calendar.getInstance();
        final int years[] = new int[2];
        years[0] = calendar.get(Calendar.YEAR);
        years[1] = calendar.get(Calendar.YEAR) + 1;

        final ListPopupWindow yearPopupWindow = new ListPopupWindow(SessionCreationPart3.this);
        YearListAdapter yearListAdapter;

        final ListPopupWindow monthPopupWindow = new ListPopupWindow(SessionCreationPart3.this);
        final MonthListAdapter monthListAdapter;

        final TextView tv_year = (TextView) calenderDialog.findViewById(R.id.tv_year);
        final TextView tv_month = (TextView) calenderDialog.findViewById(R.id.tv_month);

        tv_year.setText("" + calendar.get(Calendar.YEAR));
        tv_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int heightPopUpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55,
                        getResources().getDisplayMetrics());
                yearPopupWindow.setHeight(heightPopUpHeight);
                yearPopupWindow.setVerticalOffset(-(heightPopUpHeight + tv_year.getHeight()) / 2);
                yearPopupWindow.show();

            }
        });

        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int monthPopUpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220,
                        getResources().getDisplayMetrics());
                monthPopupWindow.setHeight(monthPopUpHeight);
                monthPopupWindow.setVerticalOffset(-(monthPopUpHeight + tv_month.getHeight()) / 2);
                monthPopupWindow.show();

            }
        });

        final List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        tv_month.setText(months.get(calendar.get(Calendar.MONTH)));

        monthListAdapter = new MonthListAdapter(SessionCreationPart3.this, months);
        monthPopupWindow.setAdapter(monthListAdapter);
        monthPopupWindow.setAnchorView(tv_month);

        monthPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tv_month.setText(months.get(position));
                monthPopupWindow.dismiss();
                monthPos = position;
                dateListAdapter.setMonthNumber(monthPos);

            }
        });

        yearListAdapter = new YearListAdapter(SessionCreationPart3.this, years);
        yearPopupWindow.setAdapter(yearListAdapter);
        yearPopupWindow.setAnchorView(tv_year);
        yearPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tv_year.setText(String.valueOf(years[position]));
                yearPopupWindow.dismiss();
                yearPos = position;
                dateListAdapter.setYearNumber(years[yearPos]);

            }
        });

        LinearLayoutManager dateListLayoutManager = new LinearLayoutManager(SessionCreationPart3.this,
                RecyclerView.VERTICAL, false);
        rv_date_list.setLayoutManager(dateListLayoutManager);

        dateListAdapter = new DateListAdapter(SessionCreationPart3.this, years[yearPos],
                monthPos, daysOfWeek, selectedDates, preSelectedDates, venueId, true, venueDays);
        rv_date_list.setAdapter(dateListAdapter);

        updateDisabledDates();
        if (arraySize == 0) {
            openNoSlotsDialog();
        } else {
            calenderDialog.show();
        }
    }

    private void openNoSlotsDialog() {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(SessionCreationPart3.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.no_time_slots_available);
        confirmationDialog.show();
    }


    @Override
    public void onDateClicked(Date date) {
        selectedDates.clear();
        selectedDates.add(date);
        dateArr[pickVenuePosition][pickDatePosition] = date;
        String dateStr = "";
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateStr = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (pickVenuePosition) {
            case 0:
                switch (pickDatePosition) {
                    case 0:
                        venue1Dates[0] = dateStr;
                        tv_venue1_date1.setText(commonFunctions.parseDateToName(venue1Dates[0]));
                        break;

                    case 1:
                        venue1Dates[1] = dateStr;
                        tv_venue1_date2.setText(commonFunctions.parseDateToName(venue1Dates[1]));
                        break;
                }
                break;

            case 1:
                switch (pickDatePosition) {
                    case 0:
                        venue2Dates[0] = dateStr;
                        tv_venue2_date1.setText(commonFunctions.parseDateToName(venue2Dates[0]));
                        break;
                    case 1:
                        venue2Dates[1] = dateStr;
                        tv_venue2_date2.setText(commonFunctions.parseDateToName(venue2Dates[1]));
                        break;
                }
                break;
        }
        updateDisabledDates();
    }

    private void updateDisabledDates() {
        preSelectedDates.clear();
        if (dateArr[pickVenuePosition][0] != null) {
            preSelectedDates.add(dateArr[pickVenuePosition][0]);
        }
        if (dateArr[pickVenuePosition][1] != null) {
            preSelectedDates.add(dateArr[pickVenuePosition][1]);
        }
        dateListAdapter.notifyDataSetChanged();
    }

    private void hitVenueSearchWithTextApi(float lat, float lon, String text, String offset) {
        String latitude = Float.toString(lat);
        String longitude = Float.toString(lon);

        Call<PojoGetVenueListResponse> call = apiService.searchVenueWithTextApi(latitude, longitude, text, offset);
        call.enqueue(new Callback<PojoGetVenueListResponse>() {
            @Override
            public void onResponse(Call<PojoGetVenueListResponse> call, Response<PojoGetVenueListResponse> response) {

                String message = response.body().message;
                if (response.body().data != null) {
                    venueSearchResultList.clear();
                    //only show 4 results in list
                    for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                        venueSearchResultList.add(response.body().data.get(i));
                    }
                    // interestSearchResultList.addAll(response.body().data);
                    venueResultsListAdapter.notifyDataSetChanged();
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetVenueListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    private void hitConductSessionApi(final PojoCreateSession pojoCreateSession) {
        File file = commonFunctions.getScaledDownImage(pojoCreateSession.cover_photo_path);
        // file = getScaledDownImage(file);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), preferenceUtils.get_user_id());
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.description);

        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("cover_photo", file.getName(), mFile);

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
            dateMap.put("event_date[" + selectedVenue1.venueId + "][" + i + "]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue1Dates.get(i)));
        }

        for (int i = 0; i < pojoCreateSession.venue2Dates.size(); i++) {
            dateMap.put("event_date[" + selectedVenue2.venueId + "][" + i + "]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue2Dates.get(i)));
        }

       /* for (int i = 0; i < pojoCreateSession.venue3Dates.size(); i++) {
            dateMap.put("event_date[" + selectedVenue3.venueId + "][" + i + "]",
                    RequestBody.create(MediaType.parse("text/plain"), pojoCreateSession.venue3Dates.get(i)));
        }*/

        HashMap<String, RequestBody> interestIdsMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.interest_ids.size(); i++) {
            interestIdsMap.put("interest_ids[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.interest_ids.get(i)));
        }

        HashMap<String, RequestBody> venueMap = new HashMap<>();
        for (int i = 0; i < pojoCreateSession.venues.size(); i++) {
            venueMap.put("venues[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    pojoCreateSession.venues.get(i)));
        }

        Call<PojoConductSessionResponse> call = apiService.initiateSession(userBody, titleBody,
                topicMap, prerequisiteMap, descriptionBody, fileToUpload, dateMap, interestIdsMap,
                venueMap);
        pb_calling_api.setVisibility(View.VISIBLE);
        sv_content_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoConductSessionResponse>() {
            @Override
            public void onResponse(Call<PojoConductSessionResponse> call, Response<PojoConductSessionResponse> response) {
                if (response.body() != null) {
                    if (response.body().status == 1) {
                        String message = "Session <B>" + pojoCreateSession.title
                                + "</B> is created successfully and will be live on the platform after admin verification.";

                        Button bt_ok;
                        final Dialog confirmationDialog;
                        confirmationDialog = new MyCustomThemeDialog(SessionCreationPart3.this);
                        confirmationDialog.setContentView(R.layout.message_dialog);

                        TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                        tv_dialog_description.setText(Html.fromHtml(message));

                        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                      /*          commonFunctions.setToastMessage(getApplicationContext(), message,
                                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                                Intent pushNotification = new Intent(Config.SESSION_CREATION_FINISHED);
                                LocalBroadcastManager.getInstance(SessionCreationPart3.this).sendBroadcast(pushNotification);
                            }
                        });
                        confirmationDialog.setCancelable(false);
                        confirmationDialog.show();


                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), response.body().message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

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
    public void venueResultItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData) {
        boolean present = false;
        if (selectedVenue1 != null && selectedVenue1.venueId.equals(pojoGetVenueListResponseData.venueId)
                || selectedVenue2 != null && selectedVenue2.venueId.equals(pojoGetVenueListResponseData.venueId)
            /*|| selectedVenue3 != null && selectedVenue3.venueId.equals(pojoGetVenueListResponseData.venueId)*/) {
            commonFunctions.setToastMessage(getApplicationContext(),
                    getString(R.string.venue_already_addded), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            if (et_venue_name != null && et_venue_name.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_venue_name.getWindowToken(), 0);
            }

            switch (venuePos) {
                case 0:
                    selectedVenue1 = pojoGetVenueListResponseData;
                    tv_venue1.setText(selectedVenue1.venueName);
                    break;

                case 1:
                    selectedVenue2 = pojoGetVenueListResponseData;
                    tv_venue2.setText(selectedVenue2.venueName);
                    break;

                case 2:
                    selectedVenue3 = pojoGetVenueListResponseData;
                    tv_venue3.setText(selectedVenue3.venueName);
                    break;
            }
            venueSearchDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* if (type != null && type.equals(AppKeys.SESSION_CONDUCT)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.next_arrow, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_next:
                goSessionCreate4Screen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goSessionCreate4Screen() {
        if (selectedVenue1 == null || selectedVenue2 == null /*|| selectedVenue3 == null*/) {
            commonFunctions.setToastMessage(getApplicationContext(),
                    getString(R.string.please_choose_two_venues_for_session),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else if (!areAllDatesSet()) {
            commonFunctions.setToastMessage(getApplicationContext(),
                    getString(R.string.choose_three_dates_for_session),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            pojoCreateSession.venues.clear();
            pojoCreateSession.venues.add(selectedVenue1.venueId);
            pojoCreateSession.venues.add(selectedVenue2.venueId);
            //  pojoCreateSession.venues.add(selectedVenue3.venueId);

            pojoCreateSession.venue1Dates.clear();
            pojoCreateSession.venue2Dates.clear();
            // pojoCreateSession.venue3Dates.clear();

            pojoCreateSession.venue1Dates.add(venue1Dates[0]);
            pojoCreateSession.venue1Dates.add(venue1Dates[1]);

            pojoCreateSession.venue2Dates.add(venue2Dates[0]);
            pojoCreateSession.venue2Dates.add(venue2Dates[1]);

   /*                 pojoCreateSession.venue3Dates.add(venue3Dates[0]);
                    pojoCreateSession.venue3Dates.add(venue3Dates[1]);*/

            //    pojoCreateSession.event_date.add(dates[3]);
            Intent part4Intent = new Intent(SessionCreationPart3.this, SessionCreationPart4.class);
            Type jsonType = new TypeToken<PojoCreateSession>() {
            }.getType();
            part4Intent.putExtra("data", new Gson().toJson(pojoCreateSession, jsonType));
            startActivity(part4Intent);
        }
    }

    @Override
    public void selectedVenuesItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHONE_VERIFIED) {
            if (data != null) {
                otpNeeded = data.getBooleanExtra("otp_needed", true);
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
