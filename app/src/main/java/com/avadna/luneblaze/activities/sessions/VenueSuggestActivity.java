package com.avadna.luneblaze.activities.sessions;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.VenueSearchResultAdapter;
import com.avadna.luneblaze.adapters.calender.DateListAdapter;
import com.avadna.luneblaze.adapters.calender.DayGroupAdapter;
import com.avadna.luneblaze.adapters.calender.MonthListAdapter;
import com.avadna.luneblaze.adapters.calender.YearListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueSuggestActivity extends AppBaseActivity implements
        VenueSearchResultAdapter.VenueSearchResultAdapterCallback,
        DayGroupAdapter.DayGroupItemCallback {

    Toolbar tl_top_bar;
    ActionBar actionBar;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    EditText et_search_bar;
    ImageButton ib_back_gray, ib_search_gray;

    RelativeLayout rl_data_wrapper;
    ProgressBar pb_loading;

    LinearLayout ll_venue_data_wrapper;
    TextView tv_chosen_venue;
    TextView tv_date1, tv_date2;
    TextView tv_done;

    RecyclerView rv_list;
    VenueSearchResultAdapter venueResultsListAdapter;
    List<PojoGetVenueListResponseData> venueSearchResultList;

    String session_id;
    //  Dialog confirmationDialog;
    Dialog venueDateDialog;

    View.OnClickListener onClickListener;
    PojoGetVenueListResponseData chosenVenue;

    Dialog calenderDialog;
    int yearPos;
    int monthPos;
    List<Date> selectedDates;
    List<Date> preSelectedDates;
    DateListAdapter dateListAdapter;

    Date chosenDate1;
    Date chosenDate2;
    int pickDatePosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_suggest);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        session_id = getIntent().getStringExtra("id");
        initViews();
        initClickListener();
        setClickListener();
        initVenueResultList();
        hitVenueSearchWithTextApi(user_id, session_id, "");
        selectedDates = new ArrayList<>();
        preSelectedDates = new ArrayList<>();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            // actionBar.setDisplayHomeAsUpEnabled(true);
            //   actionBar.setHomeButtonEnabled(true);
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VenueSuggestActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VenueSuggestActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_back_gray:
                        finish();
                        break;

                    case R.id.ib_search_gray:
                        String query = et_search_bar.getText().toString().trim();
                        hitVenueSearchWithTextApi(user_id, session_id, query);
                        break;

                    case R.id.tv_done:
                        if (chosenVenue != null && preSelectedDates.size() == 2) {

                            hitSuggestVenueApi(user_id, session_id, chosenVenue.venueId);
                            venueDateDialog.dismiss();
                            //confirmationDialog.dismiss();
                        }
                        break;

                    case R.id.tv_date1:
                        openCalenderDialog(0);
                        break;

                    case R.id.tv_date2:
                        openCalenderDialog(1);
                        break;
                }
            }
        };
    }


    private void openCalenderDialog(final int datePosition) {
        pickDatePosition = datePosition;
        selectedDates = new ArrayList<>();
        calenderDialog = new MyCustomThemeDialog(this, android.R.style.Theme_Material_Dialog_NoActionBar);
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

        final int venueId = Integer.parseInt(chosenVenue.venueId);

        int arraySize = 0;

        for (int i = 0; i < chosenVenue.days.size(); i++) {
            if (!chosenVenue.days.get(i).slots.isEmpty()) {
                arraySize++;
            }
        }

        final int[] daysOfWeek = new int[arraySize];
        int counter = 0;
        for (int i = 0; i < chosenVenue.days.size(); i++) {
            if (!chosenVenue.days.get(i).slots.isEmpty()) {
                //need to adjust by +1 as calender.day_of_week gives sunday 1 but in api sunday is at index 0
                daysOfWeek[counter++] = i + 1;
            }
        }

        calendar = Calendar.getInstance();
        final int years[] = new int[2];
        years[0] = calendar.get(Calendar.YEAR);
        years[1] = calendar.get(Calendar.YEAR) + 1;

        final ListPopupWindow yearPopupWindow = new ListPopupWindow(VenueSuggestActivity.this);
        YearListAdapter yearListAdapter;

        final ListPopupWindow monthPopupWindow = new ListPopupWindow(VenueSuggestActivity.this);
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

        monthListAdapter = new MonthListAdapter(VenueSuggestActivity.this, months);
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

        yearListAdapter = new YearListAdapter(VenueSuggestActivity.this, years);
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

        LinearLayoutManager dateListLayoutManager = new LinearLayoutManager(VenueSuggestActivity.this,
                LinearLayoutManager.VERTICAL, false);
        rv_date_list.setLayoutManager(dateListLayoutManager);


        dateListAdapter = new DateListAdapter(VenueSuggestActivity.this, years[yearPos],
                monthPos, daysOfWeek, selectedDates, preSelectedDates, venueId, true, chosenVenue.days);
        rv_date_list.setAdapter(dateListAdapter);

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
        confirmationDialog = new MyCustomThemeDialog(VenueSuggestActivity.this);
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


    private void setDatesOnViews() {
        preSelectedDates.clear();
        if (chosenDate1 != null) {
            String date = "";
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                date = outputFormat.format(chosenDate1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_date1.setText(date);
            preSelectedDates.add(chosenDate1);
        }
        if (chosenDate2 != null) {
            String date = "";
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                date = outputFormat.format(chosenDate2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_date2.setText(date);
            preSelectedDates.add(chosenDate2);
        }

    }

    private void initViews() {
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        ib_search_gray = (ImageButton) findViewById(R.id.ib_search_gray);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        rl_data_wrapper = (RelativeLayout) findViewById(R.id.rl_data_wrapper);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);

        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hitVenueSearchWithTextApi(user_id, session_id, s.toString().trim());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_search_bar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    hitVenueSearchWithTextApi(user_id, session_id, et_search_bar.getText().toString().trim());
                }
            }
        });

        et_search_bar.post(new Runnable() {
            @Override
            public void run() {
                if (et_search_bar.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
                }
            }
        });
    }

    private void setClickListener() {
        ib_back_gray.setOnClickListener(onClickListener);
        ib_search_gray.setOnClickListener(onClickListener);

    }

    private void initVenueResultList() {
        venueSearchResultList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        rv_list.setLayoutManager
                (new LinearLayoutManager(VenueSuggestActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_list.setNestedScrollingEnabled(false);
        venueResultsListAdapter = new VenueSearchResultAdapter(VenueSuggestActivity.this, venueSearchResultList);
        rv_list.setAdapter(venueResultsListAdapter);
    }

    private void hitVenueSearchWithTextApi(String user_id, String session_id, String text) {

        Call<PojoGetVenueListResponse> call = apiService.searchVenueForPoll(user_id, session_id, text);
        pb_loading.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<PojoGetVenueListResponse>() {
            @Override
            public void onResponse(Call<PojoGetVenueListResponse> call, Response<PojoGetVenueListResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    String message = response.body().message;
                    venueSearchResultList.clear();
                    pb_loading.setVisibility(View.GONE);
                    //only show 4 results in list
                    venueSearchResultList.addAll(response.body().data);
                    // interestSearchResultList.addAll(response.body().data);
                    venueResultsListAdapter.notifyDataSetChanged();
                    commonFunctions.setToastMessage(VenueSuggestActivity.this, "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetVenueListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(VenueSuggestActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    @Override
    public void venueResultItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData) {
      /*  venueSearchResultList.clear();
        venueResultsListAdapter.notifyDataSetChanged();*/
        selectedDates.clear();
        preSelectedDates.clear();

        chosenVenue = pojoGetVenueListResponseData;

        openVenueAndDateDialog();

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openVenueAndDateDialog() {

        venueDateDialog = new MyCustomThemeDialog(VenueSuggestActivity.this);
        venueDateDialog.setContentView(R.layout.choose_venue_and_date_dialog);
        ll_venue_data_wrapper = (LinearLayout) venueDateDialog.findViewById(R.id.ll_venue_data_wrapper);
        tv_chosen_venue = (TextView) venueDateDialog.findViewById(R.id.tv_chosen_venue);
        tv_date1 = (TextView) venueDateDialog.findViewById(R.id.tv_date1);
        tv_date2 = (TextView) venueDateDialog.findViewById(R.id.tv_date2);

        tv_date1.setOnClickListener(onClickListener);
        tv_date2.setOnClickListener(onClickListener);

        venueDateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        tv_done = (TextView) venueDateDialog.findViewById(R.id.tv_done);
        tv_done.setOnClickListener(onClickListener);
        // tv_done.setVisibility(View.GONE);

        tv_chosen_venue.setText(chosenVenue.venueName);
        venueDateDialog.show();
    }

  /*  private void openConfirmationDialog(final PojoGetVenueListResponseData venueData) {
        TextView tv_yes, tv_no;
        confirmationDialog = new MyCustomThemeDialog(VenueSuggestActivity.this);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        confirmationDialog.show();
    }*/


    private void hitSuggestVenueApi(String user_id, String session_id, String venueId) {
        HashMap<String, String> dateMap = new HashMap<>();

        for (int i = 0; i < preSelectedDates.size(); i++) {
            String date = "";
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = outputFormat.format(preSelectedDates.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            dateMap.put("dates[" + i + "]", date);
        }

        Call<PojoNoDataResponse> call = apiService.suggestVenue(user_id, session_id, venueId, dateMap);

        pb_loading.setVisibility(View.VISIBLE);
        rl_data_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                commonFunctions.setToastMessage(VenueSuggestActivity.this, "" + message,
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                Intent newVenueAdded = new Intent(Config.NEW_VENUE_ADDED);
                LocalBroadcastManager.getInstance(VenueSuggestActivity.this).sendBroadcast(newVenueAdded);
                VenueSuggestActivity.this.onBackPressed();
                if (et_search_bar.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                }
                finish();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(VenueSuggestActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                rl_data_wrapper.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);

            }
        });
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

    @Override
    public void onDateClicked(Date date) {
        selectedDates.clear();
        selectedDates.add(date);
        if (pickDatePosition == 0) {
            chosenDate1 = date;
        } else if (pickDatePosition == 1) {
            chosenDate2 = date;
        }
        setDatesOnViews();
        dateListAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
}
