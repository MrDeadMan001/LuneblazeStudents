package com.avadna.luneblaze.activities.registration;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.registration.VenueSuggestPopupAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSignUp2Response;
import com.avadna.luneblaze.pojo.PojoSignUp2ResponseData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity2 extends AppBaseActivity {

    ApiInterface apiService;
    RadioButton rb_working, rb_studying, rb_other;
    EditText et_current_status;
    EditText et_institution, et_designation;
    EditText et_start_date;
    TextInputLayout til_current_status, til_institution, til_start_date, til_designation;
    TextView tv_next, tv_skip;
    View.OnClickListener mOnClickListener;
    PreferenceUtils preferenceUtils;
    String user_id;
    String startDate;

    String user_work_status = "Working";
    CommonFunctions commonFunctions;

    boolean enableApiCall = true;
    ProgressDialog progressDialog;
    boolean institutionActivated = false, designationActivated = false, currentStatusActivated = false;

    boolean isSearchApiCalled = false;

    ListPopupWindow venueListPopUp;
    String selectedVenue = "";
    String selectedVenueId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        if (user_id == null || user_id.isEmpty()) {
            user_id = getIntent().getStringExtra("user_id");
        }
        commonFunctions = new CommonFunctions(this);
        initViews();
        initClickListener();
        setClickListener();
        setTextWatcher();
        setFocusListener();
        initApis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(RegisterActivity2.this, R.color.status_bar_color));
        }

        venueListPopUp=new ListPopupWindow(RegisterActivity2.this);
        venueListPopUp.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        venueListPopUp.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }


    private void setFocusListener() {
        et_institution.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    institutionActivated = true;
                }
                if (institutionActivated && !hasFocus) {
                    if (et_institution.getText().toString().length() == 0) {
                        til_institution.setError(getString(R.string.field_cant_be_empty));
                    } else {
                        til_institution.setError(null);
                    }
                }
            }
        });


        et_designation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    designationActivated = true;
                }
                if (designationActivated && !hasFocus) {
                    if (et_designation.getText().toString().length() == 0) {
                        til_designation.setError(getString(R.string.field_cant_be_empty));

                    } else {
                        til_designation.setError(null);
                    }
                }
            }
        });

        et_current_status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentStatusActivated = true;
                }
                if (currentStatusActivated && !hasFocus) {
                    if (et_current_status.getText().toString().length() == 0) {
                        til_current_status.setError(getString(R.string.field_cant_be_empty));

                    } else {
                        til_current_status.setError(null);
                    }
                }
            }
        });

    }

    private void setTextWatcher() {

        et_institution.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!selectedVenue.equals(s.toString().trim())) {
                    hitSearchVenueApi(user_id, s.toString(), 0, "venues");
                    selectedVenueId="";
                }

                if (s.length() == 0) {

                    //   til_institution.setError(getString(R.string.field_cant_be_empty));

                } else {
                    til_institution.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    //  til_designation.setError(getString(R.string.field_cant_be_empty));
                } else {
                    til_designation.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_current_status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    //  til_designation.setError(getString(R.string.field_cant_be_empty));
                } else {
                    til_current_status.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_next:
                        String work_place = et_institution.getText().toString().trim();
                        String designation = et_designation.getText().toString().trim();

                        if (rb_other.isChecked()) {
                            user_work_status = et_current_status.getText().toString().trim();
                        }

                        //commonFunctions.setToastMessage(getApplicationContext(), "register clicked", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        if (user_work_status.isEmpty()) {
                            til_current_status.setError(getString(R.string.field_cant_be_empty));
                         /*   commonFunctions.setToastMessage(getApplicationContext(),
                                    "Please choose your current status", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                        } else if (work_place.isEmpty()) {
                            til_institution.setError(getString(R.string.field_cant_be_empty));
                           /* commonFunctions.setToastMessage(getApplicationContext(),
                                    "Please enter your institution name", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                        } else if (designation.isEmpty()) {
                            til_designation.setError(getString(R.string.field_cant_be_empty));
                         /*   commonFunctions.setToastMessage(getApplicationContext(),
                                    "Please enter your designation", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                        } else {
                            hitRegistrationCurrentStatusApi(user_id, user_work_status, work_place,
                                    designation,selectedVenueId);
                            til_current_status.setError(null);
                            til_institution.setError(null);
                            til_designation.setError(null);
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        break;


                    case R.id.rb_working:
                        if (rb_working.isChecked()) {
                            user_work_status = "Working";
                            til_institution.setHint(getString(R.string.place_of_work));
                            til_designation.setHint(getString(R.string.designation));
                            til_current_status.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.rb_studying:
                        if (rb_studying.isChecked()) {
                            //typo in api field value for studying
                            user_work_status = "Studing";
                            til_institution.setHint(getString(R.string.institution));
                            til_designation.setHint(getString(R.string.field_of_study));
                            til_current_status.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.rb_other:
                        if (rb_other.isChecked()) {
                            user_work_status = "";
                            til_institution.setHint(getString(R.string.place));
                            til_designation.setHint(getString(R.string.designation));
                            til_current_status.setVisibility(View.VISIBLE);
                        }
                        break;


                    case R.id.tv_skip:
                        //todo might need to change flow in future
                        /*Intent interestListActivityIntent = new Intent(RegisterActivity2.this, InterestHierarchyActivity.class);
                        startActivity(interestListActivityIntent);*/

                        Intent suggestedFriendsIntent = new Intent(RegisterActivity2.this,
                                SuggestedFriendsActivity.class);
                        startActivity(suggestedFriendsIntent);

                        RegisterActivity2.this.finish();
                        break;

                    case R.id.et_start_date:
                        final Calendar cal = Calendar.getInstance();
                        final int currYear = cal.get(Calendar.YEAR);
                        final int currMonth = cal.get(Calendar.MONTH);
                        final int currDay = cal.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog mDatePickerDialog = new DatePickerDialog(RegisterActivity2.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        startDate = year + "-" + (month + 1) + "-" + day;
                                        et_start_date.setText(commonFunctions.parseDateToName(startDate));

                                    }
                                }, currYear, currDay, currMonth);
                        mDatePickerDialog.show();
                        break;

                    default:
                        break;
                }
            }
        };

    }


    private void hitSearchVenueApi(final String user_id, final String query, final int localOffset, String type) {
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);

        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            venueListPopUp.dismiss();
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        VenueSuggestPopupAdapter venueSuggestPopupAdapter =
                                new VenueSuggestPopupAdapter(RegisterActivity2.this,
                                        response.body().data.venues);
                        venueListPopUp.setAnchorView(et_institution);
                        venueListPopUp.setAdapter(venueSuggestPopupAdapter);

                        venueListPopUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedVenue = response.body().data.venues.get(i).venueName;
                                et_institution.setText(selectedVenue);
                                selectedVenueId=response.body().data.venues.get(i).venueId;
                                venueListPopUp.dismiss();
                            }
                        });
                        venueListPopUp.show();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_institution.getText().toString().trim();
                    if (!latestQuery.equals(query)) {
                        hitSearchVenueApi(user_id, latestQuery, 0, "venues");
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    isSearchApiCalled = false;

                }
            });
        }

    }


    private void hitRegistrationCurrentStatusApi(String user_id, String user_work, String work_place,
                                                 String work_title,String selectedVenueId) {

        enableApiCall = false;
        showProgressDialog();
        Call<PojoSignUp2Response> call = apiService.registrationCurrentStatusApi(user_id, user_work,
                work_place, work_title,selectedVenueId);
        call.enqueue(new Callback<PojoSignUp2Response>() {
            @Override
            public void onResponse(Call<PojoSignUp2Response> call, Response<PojoSignUp2Response> response) {
                enableApiCall = true;
                progressDialog.dismiss();
                String userId = "0";
                int status = 0;
                if (response.body().data != null) {
                    PojoSignUp2ResponseData pojoSignUp2ResponseData = response.body().data;
                    if (response.body().data.userId != null) {
                        userId = response.body().data.userId;
                    }
                }
                if (response.body().status != null) {
                    status = response.body().status;
                }
                if (status == 1) {
                    //todo might need to change flow in future
                    /*Intent interestListActivityIntent = new Intent(RegisterActivity2.this,
                            InterestHierarchyActivity.class);
                    startActivity(interestListActivityIntent);*/
                    Intent suggestedFriendsIntent = new Intent(RegisterActivity2.this,
                            SuggestedFriendsActivity.class);
                    startActivity(suggestedFriendsIntent);
                    RegisterActivity2.this.finish();
                } else {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }

            }

            @Override
            public void onFailure(Call<PojoSignUp2Response> call, Throwable t) {
                enableApiCall = true;
                progressDialog.dismiss();
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });


        Call<PojoNoDataResponse> call2 = apiService.addWorkHistory(user_id, "add_work_history"
                , user_work, work_title, work_place, startDate, "0",selectedVenueId);
        call2.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void showProgressDialog() {
        progressDialog.setTitle("Adding Info");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void initViews() {
        progressDialog = new ProgressDialog(RegisterActivity2.this);
        rb_working = (RadioButton) findViewById(R.id.rb_working);
        rb_working.setChecked(true);
        rb_studying = (RadioButton) findViewById(R.id.rb_studying);
        rb_other = (RadioButton) findViewById(R.id.rb_other);
        et_start_date = (EditText) findViewById(R.id.et_start_date);
        et_current_status = (EditText) findViewById(R.id.et_current_status);
        et_institution = (EditText) findViewById(R.id.et_institution);
        et_designation = (EditText) findViewById(R.id.et_designation);
        til_current_status = (TextInputLayout) findViewById(R.id.til_current_status);
        til_current_status.setVisibility(View.GONE);

        til_institution = (TextInputLayout) findViewById(R.id.til_institution);
        til_start_date = (TextInputLayout) findViewById(R.id.til_start_date);
        til_start_date.setVisibility(View.GONE);
        til_designation = (TextInputLayout) findViewById(R.id.til_designation);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
    }


    private void setClickListener() {
        tv_next.setOnClickListener(mOnClickListener);
        et_start_date.setOnClickListener(mOnClickListener);
        rb_working.setOnClickListener(mOnClickListener);
        rb_studying.setOnClickListener(mOnClickListener);
        rb_other.setOnClickListener(mOnClickListener);
        tv_skip.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
