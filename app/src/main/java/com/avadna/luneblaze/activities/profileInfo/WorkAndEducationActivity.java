package com.avadna.luneblaze.activities.profileInfo;

import android.app.DatePickerDialog;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.profileInfo.EducationLargeListItemAdapter;
import com.avadna.luneblaze.adapters.registration.VenueSuggestPopupAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserWorkListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoWorkHistory;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkAndEducationActivity extends AppBaseActivity
        implements EducationLargeListItemAdapter.WorkAndEducationAdapterCallback {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String target_user_id;

    RecyclerView rv_education_list;
    EducationLargeListItemAdapter educationLargeListItemAdapter;
    List<PojoWorkHistory> workHistoryList;
    String startDate;
    String endDate;
    TextView tv_add_more;
    NestedScrollView nsv_content_wrapper;
    ProgressBar pb_loading_content;

    ListPopupWindow venueListPopUp;
    String selectedVenue = "";
    String selectedVenueId = "";

    EditText et_type, et_field, et_workplace;
    TextInputLayout til_type, til_field, til_workplace, til_start_date, til_end_date;
    PopupWindow popupWindow;
    String dataType;
    boolean isSearchApiCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_and_education);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        target_user_id = getIntent().getStringExtra(AppKeys.TARGET_USER_ID);
        if (target_user_id == null) {
            target_user_id = user_id;
        }
        setUpActionBar();

        initViews();
        initEducationList();
        hitGetUserWorkListApi(user_id, target_user_id, "work_history");
        venueListPopUp = new ListPopupWindow(WorkAndEducationActivity.this);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViews() {
        tv_add_more = (TextView) findViewById(R.id.tv_add_more);
        nsv_content_wrapper = (NestedScrollView) findViewById(R.id.nsv_content_wrapper);
        nsv_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);

        if (!target_user_id.equals(user_id)) {
            tv_add_more.setVisibility(View.GONE);
        } else {
            tv_add_more.setVisibility(View.VISIBLE);
        }
        tv_add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditEducationDialog(-1);
            }
        });
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.work_and_education));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(WorkAndEducationActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(WorkAndEducationActivity.this, R.color.status_bar_color));
            }
        }
    }

    public void initEducationList() {
        workHistoryList = new ArrayList<>();
        rv_education_list = (RecyclerView) findViewById(R.id.rv_education_list);
        rv_education_list.setLayoutManager(new LinearLayoutManager(WorkAndEducationActivity.this,
                RecyclerView.VERTICAL, true));
        educationLargeListItemAdapter = new EducationLargeListItemAdapter(WorkAndEducationActivity.this,
                workHistoryList, target_user_id);
        rv_education_list.setAdapter(educationLargeListItemAdapter);
        rv_education_list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }


    @Override
    public void workAndEducationListItemClickCallback(int position, PojoWorkHistory item, String type) {
        if (type.equals(AppKeys.EDIT)) {
            openEditEducationDialog(position);
        } else if (type.equals(AppKeys.DELETE)) {
            hitDeleteWorkApi(user_id, "delete_work_history", item.id);
            workHistoryList.remove(position);
            educationLargeListItemAdapter.notifyDataSetChanged();
        }
    }


    private void openEditEducationDialog(final int position) {

        TextView tv_done, tv_cancel;
        final CheckBox cb_current;
        final Dialog editEducationDialog = new MyCustomThemeDialog(WorkAndEducationActivity.this);
        editEducationDialog.setContentView(R.layout.edit_education_dialog);
        editEducationDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editEducationDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editEducationDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final EditText et_start_date = (EditText) editEducationDialog.findViewById(R.id.et_start_date);
        final EditText et_end_date = (EditText) editEducationDialog.findViewById(R.id.et_end_date);

        RadioButton rb_work = (RadioButton) editEducationDialog.findViewById(R.id.rb_work);
        RadioButton rb_education = (RadioButton) editEducationDialog.findViewById(R.id.rb_education);
        RadioButton rb_other = (RadioButton) editEducationDialog.findViewById(R.id.rb_other);

        et_type = (EditText) editEducationDialog.findViewById(R.id.et_type);

        et_field = (EditText) editEducationDialog.findViewById(R.id.et_field);
        et_workplace = (EditText) editEducationDialog.findViewById(R.id.et_workplace);

        til_type = (TextInputLayout) editEducationDialog.findViewById(R.id.til_type);
        til_type.setVisibility(View.GONE);
        til_field = (TextInputLayout) editEducationDialog.findViewById(R.id.til_field);
        til_workplace = (TextInputLayout) editEducationDialog.findViewById(R.id.til_workplace);
        til_start_date = (TextInputLayout) editEducationDialog.findViewById(R.id.til_start_date);
        til_end_date = (TextInputLayout) editEducationDialog.findViewById(R.id.til_end_date);

        rb_work.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rb_work.isChecked()) {
                    dataType = getString(R.string.work);
                    til_type.setVisibility(View.GONE);
                    til_workplace.setHint(getString(R.string.place_of_work));
                    til_field.setHint(getString(R.string.designation));
                }

            }
        });

        rb_education.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rb_education.isChecked()) {
                    dataType = getString(R.string.education);
                    til_type.setVisibility(View.GONE);
                    til_workplace.setHint(getString(R.string.institution));
                    til_field.setHint(getString(R.string.field_of_study));
                }
            }
        });

        rb_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rb_other.isChecked()) {
                    dataType = "";
                    til_type.setVisibility(View.VISIBLE);
                    til_workplace.setHint(getString(R.string.institution));
                    til_field.setHint(getString(R.string.field));
                }
            }
        });

        et_workplace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!selectedVenue.equals(s.toString().trim())) {
                    hitSearchVenueApi(user_id, s.toString(), 0, "venues");
                    selectedVenueId = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_workplace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    til_workplace.setError(null);
                }
            }
        });

        et_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    til_field.setError(null);
                }
            }
        });

        cb_current = (CheckBox) editEducationDialog.findViewById(R.id.cb_current);
        cb_current.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cb_current.isChecked()) {
                    et_end_date.setText(R.string.present);
                } else {
                    et_end_date.getText().clear();
                }
            }
        });
        tv_done = (TextView) editEducationDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) editEducationDialog.findViewById(R.id.tv_cancel);

        et_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.work_education_popup, null);
                //instantiate popup window
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                //display the popup window
                popupWindow.showAsDropDown(til_type, 0, 0);

                TextView tv_work = (TextView) customView.findViewById(R.id.tv_work);
                TextView tv_education = (TextView) customView.findViewById(R.id.tv_education);


                tv_work.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        et_type.setText(getString(R.string.work));
                    }
                });

                tv_education.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        et_type.setText(getString(R.string.education));
                    }
                });
            }
        });

        final Calendar cal = Calendar.getInstance();
        final int currYear = cal.get(Calendar.YEAR);
        final int currMonth = cal.get(Calendar.MONTH);
        final int currDay = cal.get(Calendar.DAY_OF_MONTH);

        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(WorkAndEducationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                startDate = year + "-" + (month + 1) + "-" + day;
                                et_start_date.setText(commonFunctions.parseDateToName(startDate));

                            }
                        }, currYear, currMonth, currDay);
                mDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                mDatePickerDialog.show();
            }
        });

        et_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(WorkAndEducationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                endDate = year + "-" + (month + 1) + "-" + day;
                                et_end_date.setText(commonFunctions.parseDateToName(endDate));
                            }
                        }, currYear, currMonth, currDay);
                mDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                mDatePickerDialog.show();
            }
        });

        if (position >= 0) {
            if (workHistoryList.get(position).type.equalsIgnoreCase("work")) {
                rb_work.setChecked(true);
            } else if (workHistoryList.get(position).type.equalsIgnoreCase("education")) {
                rb_education.setChecked(true);
            } else {
                rb_work.setChecked(true);
            }
            et_type.setText(workHistoryList.get(position).type);
            et_field.setText(workHistoryList.get(position).subject);
            et_workplace.setText(workHistoryList.get(position).institution);
            et_start_date.setText(commonFunctions.parseDateToName(workHistoryList.get(position).startDate));
            startDate = workHistoryList.get(position).startDate;
            endDate = workHistoryList.get(position).endDate;
            if (endDate.equals("0")) {
                endDate = getString(R.string.present);
                et_end_date.setText(endDate);
                cb_current.setChecked(true);
            } else {
                et_end_date.setText(commonFunctions.parseDateToName(workHistoryList.get(position).endDate));
            }
        }

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_other.isChecked()) {
                    dataType = et_type.getText().toString();
                }
                String work_type = dataType;
                String field = et_field.getText().toString().trim();
                String workplace = et_workplace.getText().toString().trim();

                if (work_type.isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_select_type), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else if (field.isEmpty()) {
                    til_field.setError(getString(R.string.please_enter_field));
                   /* commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_field), Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                } else if (workplace.isEmpty()) {
                    til_workplace.setError(getString(R.string.please_enter_work_place));
                    /*commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_work_place), Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                } else {
                    if (position >= 0) {
                        if (cb_current.isChecked()) {
                            endDate = "0";
                            //hitUpdateWorkInfo(user_id, "work", field, workplace);
                        }
                        hitEditUserEducationApi(user_id, "edit_work_history", work_type, field,
                                workplace, startDate, endDate, workHistoryList.get(position).id,
                                selectedVenueId, position);
                        workHistoryList.get(position).type = work_type;
                        workHistoryList.get(position).subject = field;
                        workHistoryList.get(position).institution = workplace;
                        workHistoryList.get(position).startDate = startDate;
                        workHistoryList.get(position).endDate = endDate;
                        educationLargeListItemAdapter.notifyDataSetChanged();

                    } else {
                        if (cb_current.isChecked()) {
                            endDate = "0";
                            //  hitUpdateWorkInfo(user_id, "work", field, workplace);
                        }
                        hitAddUserEducationApi(user_id, "add_work_history", work_type, field,
                                workplace, startDate, endDate, selectedVenueId);
                        PojoWorkHistory pojoWorkHistory = new PojoWorkHistory("-1", user_id,
                                work_type, field, workplace, startDate, endDate);
                        workHistoryList.add(pojoWorkHistory);
                        educationLargeListItemAdapter.notifyDataSetChanged();
                    }
                    editEducationDialog.dismiss();
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEducationDialog.dismiss();
            }
        });
        editEducationDialog.show();
    }


    private void hitSearchVenueApi(final String user_id, final String query,
                                   final int localOffset, String type) {
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
                                new VenueSuggestPopupAdapter(WorkAndEducationActivity.this,
                                        response.body().data.venues);
                        venueListPopUp.setAnchorView(et_workplace);
                        venueListPopUp.setAdapter(venueSuggestPopupAdapter);

                        venueListPopUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedVenue = response.body().data.venues.get(i).venueName;
                                et_workplace.setText(selectedVenue);
                                selectedVenueId = response.body().data.venues.get(i).venueId;
                                venueListPopUp.dismiss();
                            }
                        });
                        venueListPopUp.show();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_workplace.getText().toString().trim();
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


    private void hitUpdateWorkInfo(final String user_id, String type, String
            designation, String intitution) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateWorkInfo(user_id, type, designation,
                intitution, null);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    Intent groupUpdate = new Intent(Config.PROFILE_UPDATED);
                    LocalBroadcastManager.getInstance(WorkAndEducationActivity.this).sendBroadcast(groupUpdate);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitAddUserEducationApi(final String user_id, String type, String work_type,
                                        String subject, String institution, String start_date,
                                        String end_date, String selectedVenueId) {
        if (selectedVenueId.isEmpty()) {
            selectedVenueId = "0";
        }
        Call<PojoNoDataResponse> call = apiService.addWorkHistory(user_id, type, work_type,
                subject, institution, start_date, end_date, selectedVenueId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserWorkListApi(user_id, target_user_id, "work_history");
                    Intent groupUpdate = new Intent(Config.PROFILE_UPDATED);
                    LocalBroadcastManager.getInstance(WorkAndEducationActivity.this).sendBroadcast(groupUpdate);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitEditUserEducationApi(final String user_id, String type, String work_type,
                                         String subject, String institution, String start_date,
                                         String end_date, String id, String selectedVenueId, int position) {
        if (selectedVenueId.isEmpty()) {
            selectedVenueId = workHistoryList.get(position).oraganisationId;
        }
        if (selectedVenueId.isEmpty() || selectedVenueId.equals("0")) {
            selectedVenueId = "1";
        }
        Call<PojoNoDataResponse> call = apiService.editWorkHistory(user_id, type, work_type,
                subject, institution, start_date, end_date, id, selectedVenueId);

        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserWorkListApi(user_id, target_user_id, "work_history");
                    Intent groupUpdate = new Intent(Config.PROFILE_UPDATED);
                    LocalBroadcastManager.getInstance(WorkAndEducationActivity.this).sendBroadcast(groupUpdate);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitDeleteWorkApi(final String user_id, String type, String id) {
        Call<PojoNoDataResponse> call = apiService.deleteWorkHistory(user_id, type, id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetUserWorkListApi(user_id, target_user_id, "work_history");
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitGetUserWorkListApi(String user_id, String target_user_id, String type) {
        nsv_content_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);

        Call<PojoUserWorkListResponse> call = apiService.getUserWorkList(target_user_id, target_user_id, type);
        call.enqueue(new Callback<PojoUserWorkListResponse>() {
            @Override
            public void onResponse(Call<PojoUserWorkListResponse> call, Response<PojoUserWorkListResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    workHistoryList.clear();
                    workHistoryList.addAll(response.body().data);

                    educationLargeListItemAdapter.notifyDataSetChanged();
                    nsv_content_wrapper.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoUserWorkListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

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

}
