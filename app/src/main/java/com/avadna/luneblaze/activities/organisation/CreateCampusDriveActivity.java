package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.payments.RazorPayPaymentActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.assessment.TaggedAssessmentCategoriesAdapter;
import com.avadna.luneblaze.adapters.calender.DateListAdapter;
import com.avadna.luneblaze.adapters.calender.DayGroupAdapter;
import com.avadna.luneblaze.adapters.calender.MonthListAdapter;
import com.avadna.luneblaze.adapters.calender.YearListAdapter;
import com.avadna.luneblaze.adapters.organisation.EligibleCollegeListAdapter;
import com.avadna.luneblaze.adapters.organisation.VacancyListAdapter;
import com.avadna.luneblaze.adapters.registration.VenueSuggestPopupAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoEligibilityFilters;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDrivePriceResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCampusBookedDatesResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCollegeListForDriveResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoJobListItem;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//todo fix the confusion between both and any session role type

public class CreateCampusDriveActivity extends AppBaseActivity implements
        VacancyListAdapter.VacancyListAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback,
        DayGroupAdapter.DayGroupItemCallback,
        TaggedAssessmentCategoriesAdapter.TaggedAssessmentCategoriesAdapterCallback {


    private ActionBar actionBar;
    private RecyclerView rv_vacancy_list;
    private VacancyListAdapter vacancyListAdapter;
    private List<PojoJobListItem> vacancyList;

    private CommonFunctions commonFunctions;
    private PreferenceUtils preferenceUtils;
    private ApiInterface apiService;
    private String user_id;
    private String organisation_id = "1";

    private TextView tv_add_more_job_bottom, tv_add_more_job_top;

    private TextInputLayout til_venue;
    private EditText et_venue;
    private ProgressBar pb_loading_venues;

    private TextInputLayout til_date;
    private EditText et_date;

    private TextInputLayout til_max_allowed;
    private EditText et_max_allowed;

    private TextView tv_done;
    private TextView tv_eligible;


    private RelativeLayout rl_num_session_wrapper, rl_roles_wrapper, rl_tagged_categories_wrapper;
    private TextView tv_num_sessions, tv_session_role;
    private ImageButton ib_clear_num_sessions, ib_clear_session_role, ib_clear_tagged_categories;
    private RadioButton rb_on_campus, rb_off_campus;

    private String venueType = AppKeys.DRIVE_COLLEGE;


    private RecyclerView rv_tagged_categories_list;
    ArrayList<PojoAssessmentCategory> selectedCategoryList;
    private TaggedAssessmentCategoriesAdapter taggedAssessmentCategoriesAdapter;

    private GridLayoutManager gridLayoutManager;
    private TextView tv_length_getter;
    private EditText et_number_of_sessions;
    private DisplayMetrics displayMetrics;
    private int screenHeight, screenWidth;

    private static final int SELECT_INTEREST_CODE = 2;
    private static final int ELIGIBILITY_FILTERS = 1002;
    private final static int PAYMENT_REQUEST_CODE = 2345;


    private String selectedVenue = "";
    private String selectedVenueId = "";
    private ImageButton ib_clear;
    private boolean allowVenueSuggestion = false;
    private boolean isSearchApiCalled = false;
    private ListPopupWindow venueListPopUp;

    private View.OnClickListener onClickListener;
    private int currDay, currMonth, currYear, chosenDay = 0, chosenMonth = 0, chosenYear = 0;
    private String chosendate = "";


    private boolean isGetCollegeListApiCalled = false;

    private RecyclerView rv_upvoters;
    private EligibleCollegeListAdapter eligibleCollegeListAdapter;
    private List<PojoGetCollegeListForDriveResponseData> collegeList;
    private RadioButton rb_both, rb_attend, rb_conduct;
    private String selectedSessionRole = "Any";
    private String selectedInterests;


    private ProgressBar pb_loading_users;

    private int MIN_SESSION_COUNT = 0;
    private int sessionCount = MIN_SESSION_COUNT;

    private TextView tv_total_count;

    private Dialog collegeEligibilityDialog;
    private Dialog filterDialog;

    private PojoCampusDriveListResponseData pojoCampusDriveListResponseData;

    private Dialog calenderDialog;
    private int yearPos;
    private int monthPos;
    private List<Date> selectedDates = new ArrayList<>();
    private List<Date> preSelectedDates = new ArrayList<>();
    private DateListAdapter dateListAdapter;


    private static final int PAYMENT_FOR_DRIVE = 123;
    private float drive_price;
    private float discount;
    private int drive_count;

    private PojoEligibilityFilters pojoEligibilityFilters;
    private String company_address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_drive);
        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);

        if (organisation_id == null || organisation_id.isEmpty()) {
            organisation_id = "7";
        }
        initDispMetrics();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListeners();
        venueListPopUp = new ListPopupWindow(CreateCampusDriveActivity.this);

        Type type = new TypeToken<PojoCampusDriveListResponseData>() {
        }.getType();
        String dataStr = getIntent().getStringExtra(AppKeys.CAMPUS_DRIVE_DATA);
        pojoCampusDriveListResponseData = new Gson().fromJson(dataStr, type);

        if (pojoCampusDriveListResponseData != null) {
            setDataOnViews();
        }

        hitGetCampusBookedDates(selectedVenueId);
        getCampusDrivePrice(user_id, organisation_id);
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.hire));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CreateCampusDriveActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CreateCampusDriveActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.et_date:
                        if (selectedVenueId == null || selectedVenueId.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_choose_venue_first), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            openCalenderDialog();
                        }
                        // openDatePickerDialogBox();
                        break;

                    case R.id.tv_add_more_job_bottom:
                    case R.id.tv_add_more_job_top:
                        openAddPositionDialog();
                        break;

                    case R.id.tv_eligible:
                        Intent filterIntent = new Intent(CreateCampusDriveActivity.this,
                                FilterStudentsActivity.class);
                        if (pojoEligibilityFilters != null) {
                            Type type = new TypeToken<PojoEligibilityFilters>() {
                            }.getType();
                            String filterStr = new Gson().toJson(pojoEligibilityFilters, type);
                            filterIntent.putExtra(AppKeys.ELIGIBILITY_FILTERS, filterStr);
                        }
                        startActivityForResult(filterIntent, ELIGIBILITY_FILTERS);
                        //openFilterDialog();
                        break;

                    case R.id.ib_clear_num_sessions:
                        sessionCount = 0;
                        rl_num_session_wrapper.setVisibility(View.GONE);
                        break;

                    case R.id.ib_clear_session_role:
                        selectedSessionRole = "";
                        rl_roles_wrapper.setVisibility(View.GONE);
                        break;

                    case R.id.ib_clear_tagged_categories:
                        selectedCategoryList.clear();
                        taggedAssessmentCategoriesAdapter.notifyDataSetChanged();
                        rl_tagged_categories_wrapper.setVisibility(View.GONE);
                        break;

                    case R.id.tv_done:
                        if (validateAllFields()) {
                            //todo maybe add discount for first drive
                            if (true) {
                                chosendate = getFreeDate();
                                drive_price = (int) (drive_price * (1 - (discount / 100)));
                                hitInitiateCampusDriveApi(user_id, organisation_id, venueType,
                                        selectedVenueId, company_address,
                                        chosendate, sessionCount, selectedSessionRole,
                                        String.valueOf(1));
                            } else {
                                chosendate = getFreeDate();
                                hitInitiateCampusDriveApi(user_id, organisation_id, venueType,
                                        selectedVenueId, company_address,
                                        chosendate, sessionCount, selectedSessionRole,
                                        String.valueOf(1));
                            }
                        }
                        break;
                }
            }
        };
    }


    private void updateSelectedInterestList() {
        gridLayoutManager = (new GridLayoutManager(CreateCampusDriveActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!selectedCategoryList.isEmpty()) {
                    tv_length_getter.setText(selectedCategoryList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();
                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth * 0.85f);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_tagged_categories_list.setLayoutManager(gridLayoutManager);
        taggedAssessmentCategoriesAdapter = new TaggedAssessmentCategoriesAdapter(this, selectedCategoryList, 1);
        rv_tagged_categories_list.setAdapter(taggedAssessmentCategoriesAdapter);
    }

    private boolean validateAllFields() {
        boolean isValid = true;
        if (vacancyList.isEmpty()) {
            commonFunctions.setToastMessage(CreateCampusDriveActivity.this, getString(R.string.please_add_job_positions),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            isValid = false;
        }

        if (pojoEligibilityFilters == null) {
            commonFunctions.setToastMessage(CreateCampusDriveActivity.this, getString(R.string.please_add_eligibility),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            isValid = false;
        }

        if (rb_on_campus.isChecked()) {
            if (selectedVenueId == null || selectedVenueId.isEmpty()) {
                til_venue.setError(getString(R.string.please_choose_a_valid_venue_from_the_list));
                isValid = false;
            }
        } else {
            company_address = et_venue.getText().toString().trim();
            if (company_address.length() < 5) {
                til_venue.setError(getString(R.string.please_enter_valid_company_address));
                isValid = false;
            }
        }

        /*if (chosendate == null || chosendate.isEmpty()) {
            commonFunctions.setToastMessage(CreateCampusDriveActivity.this, getString(R.string.please_choose_a_date),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            isValid = false;
        }*/

        return isValid;
    }

    private void openAddPositionDialog() {
        /*if (!et_position.getText().toString().isEmpty()) {
            vacancyList.add(new PojoJobListItem(et_position.getText().toString(), 1));
            vacancyListAdapter.notifyDataSetChanged();
            et_position.getText().clear();
        }*/
        Button bt_ok;
        final Dialog dialog;
        dialog = new MyCustomThemeDialog(CreateCampusDriveActivity.this);
        dialog.setContentView(R.layout.add_job_position_dialog);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout til_position, til_description, til_position_count;
        EditText et_position, et_description, et_position_count;

        til_position = (TextInputLayout) dialog.findViewById(R.id.til_position);
        til_description = (TextInputLayout) dialog.findViewById(R.id.til_description);
        til_position_count = (TextInputLayout) dialog.findViewById(R.id.til_position_count);

        et_position = (EditText) dialog.findViewById(R.id.et_position);
        et_description = (EditText) dialog.findViewById(R.id.et_description);
        et_position_count = (EditText) dialog.findViewById(R.id.et_position_count);

        bt_ok = (Button) dialog.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allValid = true;
                if (et_position.getText().toString().trim().isEmpty()) {
                    til_position.setError(getString(R.string.please_enter_job_position));
                    allValid = false;
                }
                if (et_description.getText().toString().trim().isEmpty()) {
                    til_description.setError(getString(R.string.please_enter_job_description));
                    allValid = false;
                }
                if (et_position_count.getText().toString().trim().isEmpty()) {
                    til_position_count.setError(getString(R.string.please_enter_number_of_positions));
                    allValid = false;
                }
                if (allValid) {
                    vacancyList.add(new PojoJobListItem(et_position.getText().toString(),
                            et_description.getText().toString().trim(),
                            et_position_count.getText().toString().trim()));
                    vacancyListAdapter.notifyDataSetChanged();
                    showOrHideAddMoreButtons();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    private void showOrHideAddMoreButtons() {
        if (vacancyList == null || vacancyList.isEmpty()) {
            tv_add_more_job_bottom.setVisibility(View.GONE);
            tv_add_more_job_top.setVisibility(View.VISIBLE);
        } else {
            tv_add_more_job_bottom.setVisibility(View.VISIBLE);
            tv_add_more_job_top.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        tv_add_more_job_bottom.setOnClickListener(onClickListener);
        tv_add_more_job_top.setOnClickListener(onClickListener);
        et_date.setOnClickListener(onClickListener);
        tv_done.setOnClickListener(onClickListener);
        tv_eligible.setOnClickListener(onClickListener);
        ib_clear_num_sessions.setOnClickListener(onClickListener);
        ib_clear_session_role.setOnClickListener(onClickListener);
        ib_clear_tagged_categories.setOnClickListener(onClickListener);
    }

    private void initViews() {
        tv_add_more_job_bottom = (TextView) findViewById(R.id.tv_add_more_job_bottom);
        tv_add_more_job_bottom.setVisibility(View.GONE);
        tv_add_more_job_top = (TextView) findViewById(R.id.tv_add_more_job_top);
        rb_on_campus = (RadioButton) findViewById(R.id.rb_on_campus);
        rb_off_campus = (RadioButton) findViewById(R.id.rb_off_campus);
        til_venue = (TextInputLayout) findViewById(R.id.til_venue);
        et_venue = (EditText) findViewById(R.id.et_venue);
        ib_clear = (ImageButton) findViewById(R.id.ib_clear);
        ib_clear.setVisibility(View.GONE);
        ib_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_clear.setVisibility(View.INVISIBLE);
                et_venue.setEnabled(true);
                selectedVenueId = "";
                et_venue.getText().clear();
            }
        });
        pb_loading_venues = (ProgressBar) findViewById(R.id.pb_loading_venues);
        pb_loading_venues.setVisibility(View.GONE);
        til_max_allowed = (TextInputLayout) findViewById(R.id.til_max_allowed);
        et_max_allowed = (EditText) findViewById(R.id.et_max_allowed);

        et_venue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!selectedVenue.equals(s.toString().trim()) && s.length() != 0) {
                    hitSearchVenueApi(user_id, s.toString(), 0, "venues");
                }
                if (s.length() == 0) {
                    //   til_institution.setError(getString(R.string.field_cant_be_empty));
                    ib_clear.setVisibility(View.GONE);

                } else {
                    til_venue.setError(null);
                    ib_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_venue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && selectedVenueId.isEmpty()) {
                    et_venue.getText().clear();
                }
            }
        });

        rb_on_campus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    company_address = null;
                    venueType = AppKeys.DRIVE_COLLEGE;
                    til_venue.setHint(getString(R.string.pick_college));
                    et_venue.getText().clear();
                    et_venue.setEnabled(true);

                }
            }
        });

        rb_off_campus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedVenueId = null;
                    venueType = AppKeys.DRIVE_ORGANIZATION;
                    et_venue.getText().clear();
                    til_venue.setHint(getString(R.string.enter_company_address));
                    et_venue.setEnabled(true);

                }
            }
        });

        til_date = (TextInputLayout) findViewById(R.id.til_date);
        et_date = (EditText) findViewById(R.id.et_date);
        tv_done = (TextView) findViewById(R.id.tv_done);

        tv_eligible = (TextView) findViewById(R.id.tv_eligible);

        rl_num_session_wrapper = (RelativeLayout) findViewById(R.id.rl_num_session_wrapper);
        rl_num_session_wrapper.setVisibility(View.GONE);
        rl_roles_wrapper = (RelativeLayout) findViewById(R.id.rl_roles_wrapper);
        rl_roles_wrapper.setVisibility(View.GONE);
        rl_tagged_categories_wrapper = (RelativeLayout) findViewById(R.id.rl_tagged_categories_wrapper);
        rl_tagged_categories_wrapper.setVisibility(View.GONE);
        tv_num_sessions = (TextView) findViewById(R.id.tv_num_sessions);
        tv_session_role = (TextView) findViewById(R.id.tv_session_role);
        ib_clear_num_sessions = (ImageButton) findViewById(R.id.ib_clear_num_sessions);
        ib_clear_session_role = (ImageButton) findViewById(R.id.ib_clear_session_role);
        ib_clear_tagged_categories = (ImageButton) findViewById(R.id.ib_clear_tagged_categories);

        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);

        selectedCategoryList = new ArrayList<>();
        rv_tagged_categories_list = (RecyclerView) findViewById(R.id.rv_tagged_categories_list);
        rv_tagged_categories_list.setLayoutManager(new LinearLayoutManager(CreateCampusDriveActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        taggedAssessmentCategoriesAdapter = new TaggedAssessmentCategoriesAdapter(this, selectedCategoryList, 1);
        rv_tagged_categories_list.setAdapter(taggedAssessmentCategoriesAdapter);
        initVacancyList();

    }


    private void setDataOnViews() {
        if (pojoCampusDriveListResponseData != null) {
            selectedCategoryList.clear();
            //selectedCategoryList.addAll(pojoCampusDriveListResponseData.filterCriteria.taggedInterestList);
            taggedAssessmentCategoriesAdapter.notifyDataSetChanged();


            vacancyList.clear();
            vacancyList.addAll(pojoCampusDriveListResponseData.jobs);


            vacancyListAdapter.notifyDataSetChanged();
            showOrHideAddMoreButtons();

            selectedVenue = pojoCampusDriveListResponseData.collegename;
            selectedVenueId = pojoCampusDriveListResponseData.college;

            et_venue.setText(selectedVenue);

            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = source.parse(pojoCampusDriveListResponseData.date);
                selectedDates.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            et_date.setText(commonFunctions.parseDateToName(pojoCampusDriveListResponseData.date));

            sessionCount = pojoCampusDriveListResponseData.filterCriteria.numSessions;
            tv_num_sessions.setText(String.valueOf(sessionCount));
            selectedSessionRole = pojoCampusDriveListResponseData.filterCriteria.role;
            tv_session_role.setText(selectedSessionRole);

            rl_num_session_wrapper.setVisibility(View.VISIBLE);
            rl_tagged_categories_wrapper.setVisibility(View.VISIBLE);
            rl_roles_wrapper.setVisibility(View.VISIBLE);

        }
    }

    private void getCampusDrivePrice(String user_id, String organisation_id) {
        Call<PojoCampusDrivePriceResponse> call = apiService.getCampusDrivePrice(user_id, organisation_id);
        call.enqueue(new Callback<PojoCampusDrivePriceResponse>() {
            @Override
            public void onResponse(Call<PojoCampusDrivePriceResponse> call,
                                   Response<PojoCampusDrivePriceResponse> response) {

                if (response != null && response.body() != null && response.body().data != null) {
                    commonFunctions.closeProgressDialog();
                    drive_price = Float.parseFloat(response.body().data.campusDrivePrice);
                    discount = Float.parseFloat(response.body().data.discountPercentage);
                    drive_count = response.body().data.campusDriveCreated;
                    //dateListAdapter.notifyDataSetChanged();
                    //calenderDialog.show();
                }
            }

            @Override
            public void onFailure(Call<PojoCampusDrivePriceResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }


    private void hitSearchVenueApi(final String user_id, final String query, final int localOffset, String type) {
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);

        if (rb_on_campus.isChecked() && !isSearchApiCalled) {
            isSearchApiCalled = true;
            venueListPopUp.dismiss();
            pb_loading_venues.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    pb_loading_venues.setVisibility(View.GONE);

                    if (rb_on_campus.isChecked() && response != null && response.body() != null && response.body().data != null) {
                        VenueSuggestPopupAdapter venueSuggestPopupAdapter =
                                new VenueSuggestPopupAdapter(CreateCampusDriveActivity.this,
                                        response.body().data.venues);
                        venueListPopUp.setAnchorView(et_venue);
                        venueListPopUp.setAdapter(venueSuggestPopupAdapter);

                        venueListPopUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedVenue = response.body().data.venues.get(i).venueName;
                                et_venue.setText(selectedVenue);
                                selectedVenueId = response.body().data.venues.get(i).venueId;
                                venueListPopUp.dismiss();
                                ib_clear.setVisibility(View.VISIBLE);
                                et_venue.setEnabled(false);
                            }
                        });
                        venueListPopUp.show();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_venue.getText().toString().trim();
                    if (!latestQuery.equals(query)) {
                        hitSearchVenueApi(user_id, latestQuery, 0, "venues");
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    pb_loading_venues.setVisibility(View.GONE);
                    isSearchApiCalled = false;

                }
            });
        }
    }

    private void initVacancyList() {
        vacancyList = new ArrayList<>();

        /*vacancyList.add(new PojoJobListItem("Java Dev",
                "A java developer for associate software engineer role", "5"));
        vacancyList.add(new PojoJobListItem("Business Dev",
                "A business developer who will look for new opportunities to expand business", "2"));
        vacancyList.add(new PojoJobListItem("Tester",
                "Associate software tester that ca work with selenium", "2"));*/

        rv_vacancy_list = (RecyclerView) findViewById(R.id.rv_vacancy_list);
        rv_vacancy_list.setLayoutManager
                (new LinearLayoutManager(CreateCampusDriveActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_vacancy_list.setNestedScrollingEnabled(false);
        vacancyListAdapter = new VacancyListAdapter(CreateCampusDriveActivity.this,
                vacancyList, true);
        rv_vacancy_list.setAdapter(vacancyListAdapter);
        showOrHideAddMoreButtons();

    }


    private void openCalenderDialog() {
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
        int arraySize = 7;

        final int[] daysOfWeek = new int[arraySize];
        int counter = 0;
        for (int i = 0; i < 7; i++) {
            //need to adjust by +1 as calender.day_of_week gives sunday 1 but in api sunday is at index 0
            daysOfWeek[counter++] = i + 1;
        }

        calendar = Calendar.getInstance();
        final int years[] = new int[2];
        years[0] = calendar.get(Calendar.YEAR);
        years[1] = calendar.get(Calendar.YEAR) + 1;

        final ListPopupWindow yearPopupWindow = new ListPopupWindow(CreateCampusDriveActivity.this);
        YearListAdapter yearListAdapter;

        final ListPopupWindow monthPopupWindow = new ListPopupWindow(CreateCampusDriveActivity.this);
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

        monthListAdapter = new MonthListAdapter(CreateCampusDriveActivity.this, months);
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

        yearListAdapter = new YearListAdapter(CreateCampusDriveActivity.this, years);
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

        LinearLayoutManager dateListLayoutManager = new LinearLayoutManager(CreateCampusDriveActivity.this,
                RecyclerView.VERTICAL, false);
        rv_date_list.setLayoutManager(dateListLayoutManager);
        List<PojoVenueDay> venueDays = null;
        dateListAdapter = new DateListAdapter(CreateCampusDriveActivity.this, years[yearPos],
                monthPos, daysOfWeek, selectedDates, preSelectedDates, 0, true, venueDays);
        rv_date_list.setAdapter(dateListAdapter);
        preSelectedDates.clear();
        commonFunctions.openProgressDialog();
        hitGetCampusBookedDates(selectedVenueId);
    }

    private void hitGetCampusBookedDates(String venue_id) {
        Call<PojoGetCampusBookedDatesResponse> call = apiService.getCampusBookedDate(venue_id);
        call.enqueue(new Callback<PojoGetCampusBookedDatesResponse>() {
            @Override
            public void onResponse(Call<PojoGetCampusBookedDatesResponse> call,
                                   Response<PojoGetCampusBookedDatesResponse> response) {

                if (response != null && response.body() != null && response.body().data != null) {
                    commonFunctions.closeProgressDialog();
                    for (int i = 0; i < response.body().data.size(); i++) {
                        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = null;
                        try {
                            date = source.parse(response.body().data.get(i));
                            preSelectedDates.add(date);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //dateListAdapter.notifyDataSetChanged();
                    //calenderDialog.show();
                }
            }

            @Override
            public void onFailure(Call<PojoGetCampusBookedDatesResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }


    private void openDatePickerDialogBox() {
        final Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        chosenYear = currYear;
        chosenDay = currDay;
        chosenMonth = currMonth;

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(CreateCampusDriveActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        chosenDay = day;
                        chosenMonth = month;
                        chosenYear = year;
                        chosendate = year + "-" + (month + 1) + "-" + day;
                        et_date.setText(commonFunctions.parseDateToName(chosendate));
                    }
                }, currYear, currMonth, currDay);
        mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        mDatePickerDialog.show();
    }

    @Override
    public void vacancyListAdapterClick(String action, int position) {
        switch (action) {
            case AppKeys.EDIT:
                openEditJobItemDialog(position);
                break;

            case AppKeys.DELETE:
                vacancyList.remove(position);
                vacancyListAdapter.notifyDataSetChanged();
                showOrHideAddMoreButtons();
                break;
        }
    }

    private void openEditJobItemDialog(final int position) {
        Button bt_ok;
        final Dialog dialog;
        dialog = new MyCustomThemeDialog(CreateCampusDriveActivity.this);
        dialog.setContentView(R.layout.add_job_position_dialog);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout til_position, til_description, til_position_count;
        EditText et_position, et_description, et_position_count;

        til_position = (TextInputLayout) dialog.findViewById(R.id.til_position);
        til_description = (TextInputLayout) dialog.findViewById(R.id.til_description);
        til_position_count = (TextInputLayout) dialog.findViewById(R.id.til_position_count);

        et_position = (EditText) dialog.findViewById(R.id.et_position);
        et_description = (EditText) dialog.findViewById(R.id.et_description);
        et_position_count = (EditText) dialog.findViewById(R.id.et_position_count);

        et_position.setText(vacancyList.get(position).jobname);
        et_description.setText(vacancyList.get(position).description);
        et_position_count.setText(String.valueOf(vacancyList.get(position).noofvacancies));

        bt_ok = (Button) dialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allValid = true;
                if (et_position.getText().toString().trim().isEmpty()) {
                    til_position.setError(getString(R.string.please_enter_job_position));
                    allValid = false;
                }
                if (et_description.getText().toString().trim().isEmpty()) {
                    til_description.setError(getString(R.string.please_enter_job_description));
                    allValid = false;
                }
                if (et_position_count.getText().toString().trim().isEmpty()) {
                    til_position_count.setError(getString(R.string.please_enter_number_of_positions));
                    allValid = false;
                }
                if (allValid) {
                    vacancyList.get(position).jobname = et_position.getText().toString();
                    vacancyList.get(position).description = et_description.getText().toString().trim();
                    vacancyList.get(position).noofvacancies = et_position_count.getText().toString().trim();

                    vacancyListAdapter.notifyDataSetChanged();
                    showOrHideAddMoreButtons();

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    private String getFreeDate() {
        String freeDate = "";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        while (isDateBooked(date)) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            date = cal.getTime();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        freeDate = dateFormat.format(date);
        return freeDate;
    }

    private boolean isDateBooked(Date date) {
        if (preSelectedDates == null || preSelectedDates.isEmpty()) {
            return false;
        }
        for (int i = 0; i < preSelectedDates.size(); i++) {
            if (date.compareTo(preSelectedDates.get(i)) == 0) {
                return true;
            }
        }
        return false;
    }

    private void hitInitiateCampusDriveApi(String user_id, String organisation_id, String venuetype,
                                           String venue_id, String address,
                                           String campusdate, int no_of_session,
                                           String session_role, String amount) {
        HashMap<String, String> descriptionMap = new HashMap<>();
        HashMap<String, String> countMap = new HashMap<>();
        HashMap<String, String> positionMap = new HashMap<>();
        for (int i = 0; i < vacancyList.size(); i++) {
            positionMap.put("job_position[" + i + "]", vacancyList.get(i).jobname);
            descriptionMap.put("job_description[" + i + "]", vacancyList.get(i).description);
            countMap.put("noofvacancies[" + i + "]", String.valueOf(vacancyList.get(i).noofvacancies));
        }

        HashMap<String, String> categoryMap = new HashMap<>();
        HashMap<String, String> dependencyMap = new HashMap<>();
        HashMap<String, String> andOrMap = new HashMap<>();

        for (int i = 0; i < selectedCategoryList.size(); i++) {
            categoryMap.put("categories[" + i + "]", selectedCategoryList.get(i).categoryId);

            if (selectedCategoryList.get(i).marksDependency) {
                dependencyMap.put("dependency[" + i + "]", "1");
            } else {
                dependencyMap.put("dependency[" + i + "]", "0");
            }
            andOrMap.put("and_or[" + i + "]", selectedCategoryList.get(i).and_or + "");
        }

        if (venuetype.equals(AppKeys.DRIVE_ORGANIZATION)) {
            venue_id = organisation_id;
        }


        Call<PojoInitiatePaymentResponse> call = apiService.initiateCampusDrivePayment(positionMap,
                descriptionMap, countMap, user_id, amount, organisation_id, venuetype, venue_id, address,
                campusdate, categoryMap, dependencyMap, andOrMap, String.valueOf(no_of_session),
                session_role, String.valueOf(sessionCount), selectedInterests);

        commonFunctions.openProgressDialog(getString(R.string.please_wait));
        call.enqueue(new Callback<PojoInitiatePaymentResponse>() {
            @Override
            public void onResponse(Call<PojoInitiatePaymentResponse> call,
                                   Response<PojoInitiatePaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    startPaymentActivity(response, amount);
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.some_error_occured_contact_support), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoInitiatePaymentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                commonFunctions.closeProgressDialog();
            }
        });
    }

    private void startPaymentActivity(Response<PojoInitiatePaymentResponse> response, String amount) {
        Intent paymentIntent = new Intent(CreateCampusDriveActivity.this,
                RazorPayPaymentActivity.class);
        if (response.body() != null) {
            paymentIntent.putExtra(AppKeys.PAYMENT_REMARKS, getString(R.string.campus_drive_payment));
            paymentIntent.putExtra(AppKeys.ID, response.body().data);
            paymentIntent.putExtra(AppKeys.PRICE, amount);
            startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == SELECT_INTEREST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                }.getType();
                String dataStr = data.getStringExtra("data");
                ArrayList<PojoGetInterestListResponseDataListItem> tempList;
                Gson gson = new Gson();
                tempList = gson.fromJson(dataStr, type);
                selectedInterestList.clear();
                selectedInterestList.addAll(tempList);
                filteredInterestListAdapter.notifyDataSetChanged();
            }
        }*/
        if (requestCode == PAYMENT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                hitScheduleDriveApi(user_id, organisation_id, venueType, selectedVenueId,
                        company_address, chosendate, sessionCount, selectedSessionRole, data);
            }
        }
        if (requestCode == ELIGIBILITY_FILTERS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Type type = new TypeToken<PojoEligibilityFilters>() {
                }.getType();
                String dataStr = data.getStringExtra(AppKeys.ELIGIBILITY_FILTERS);
                pojoEligibilityFilters = new Gson().fromJson(dataStr, type);
                if (pojoEligibilityFilters != null) {
                    sessionCount = Integer.parseInt(pojoEligibilityFilters.no_of_sessions);
                    tv_num_sessions.setText(String.valueOf(sessionCount));
                    selectedSessionRole = pojoEligibilityFilters.session_role;
                    tv_session_role.setText(selectedSessionRole);

                    rl_num_session_wrapper.setVisibility(View.VISIBLE);
                    rl_tagged_categories_wrapper.setVisibility(View.VISIBLE);
                    rl_roles_wrapper.setVisibility(View.VISIBLE);
                    selectedCategoryList.clear();
                    selectedCategoryList.addAll(pojoEligibilityFilters.selectedCategoryList);
                    taggedAssessmentCategoriesAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void hitScheduleDriveApi(String user_id, String organisation_id, String venuetype,
                                     String venue_id, String address, String campusdate, int no_of_session,
                                     String session_role, Intent data) {
        HashMap<String, String> descriptionMap = new HashMap<>();
        HashMap<String, String> countMap = new HashMap<>();
        HashMap<String, String> positionMap = new HashMap<>();
        for (int i = 0; i < vacancyList.size(); i++) {
            positionMap.put("job_position[" + i + "]", vacancyList.get(i).jobname);
            descriptionMap.put("job_description[" + i + "]", vacancyList.get(i).description);
            countMap.put("noofvacancies[" + i + "]", String.valueOf(vacancyList.get(i).noofvacancies));
        }

        HashMap<String, String> categoryMap = new HashMap<>();
        HashMap<String, String> dependencyMap = new HashMap<>();
        HashMap<String, String> andOrMap = new HashMap<>();

        for (int i = 0; i < selectedCategoryList.size(); i++) {
            categoryMap.put("categories[" + i + "]", selectedCategoryList.get(i).categoryId);

            if (selectedCategoryList.get(i).marksDependency) {
                dependencyMap.put("dependency[" + i + "]", "1");
            } else {
                dependencyMap.put("dependency[" + i + "]", "0");
            }
            andOrMap.put("and_or[" + i + "]", selectedCategoryList.get(i).and_or + "");
        }

        if (venuetype.equals(AppKeys.DRIVE_ORGANIZATION)) {
            venue_id = organisation_id;
        }

        Call<PojoNoDataResponse> call = apiService.updateCampusDrivePayment(positionMap, descriptionMap,
                countMap, user_id, data.getStringExtra(AppKeys.PRICE), organisation_id, venuetype, venue_id, address,
                campusdate, categoryMap, dependencyMap, andOrMap, String.valueOf(no_of_session),
                session_role, data.getStringExtra(AppKeys.ID), data.getStringExtra(AppKeys.TRANSACTION_ID),
                data.getStringExtra(AppKeys.PAYMENT_ID), data.getStringExtra(AppKeys.PAYMENT_STATUS),
                session_role, String.valueOf(sessionCount), selectedInterests);

        commonFunctions.openProgressDialog(getString(R.string.posting_campus_drive));
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().status == 1) {
                    if (data.getStringExtra(AppKeys.PAYMENT_STATUS).equals(AppKeys.PAYMENT_COMPLETED)) {
                        Intent campusDriveIntent = new Intent(Config.NEW_CAMPUS_DRIVE_ADDED);
                        LocalBroadcastManager.getInstance(CreateCampusDriveActivity.this)
                                .sendBroadcast(campusDriveIntent);
                        finish();
                    }
                } else {
                    if (CreateCampusDriveActivity.this != null)
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getApplicationContext().getString(R.string.some_error_occured_contact_support),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                openNoConnectionDialog(data, getString(R.string.hire));
            }
        });
    }

    private void openNoConnectionDialog(Intent data, String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitScheduleDriveApi(user_id, organisation_id, venueType, selectedVenueId,
                            company_address, chosendate, sessionCount, selectedSessionRole, data);
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
        } catch (Exception e) {

        }
    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {
    }

    @Override
    public void onDateClicked(Date date) {
        selectedDates.clear();
        selectedDates.add(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        chosendate = formatter.format(date);
        et_date.setText(commonFunctions.parseDateToName(chosendate));
        dateListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaggedAssessmentCategoryItemClick(int position, PojoAssessmentCategory pojoAssessmentCategory) {

    }
}
