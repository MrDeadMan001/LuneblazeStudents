package com.avadna.luneblaze.activities;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.DatePollAdapter;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.Calendar;
import java.util.Date;

public class DatePollActivity extends AppBaseActivity {
    ActionBar actionBar;
    ScrollView sv_parent_wrapper;

    ApiInterface apiService;

    CheckBox cb_select_all;
    LinearLayout ll_date_data_wrapper;
    RecyclerView rv_date_poll_list;
    DatePollAdapter datePollAdapter;
    RecyclerView.LayoutManager dateLayoutManager;
    TextView tv_add_date;

    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;


    String dateToShow;
    String[] monthNames = {"", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String currentdate="";
    private int c_day;
    private int c_month;
    private int c_year;



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

        setContentView(R.layout.activity_date_poll);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        initViews();
        initDatePollList();
        initClickListener();
        setClickListeners();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.select_venue));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(DatePollActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(DatePollActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        ll_date_data_wrapper=(LinearLayout)findViewById(R.id.ll_date_data_wrapper);
        tv_add_date=(TextView)findViewById(R.id.tv_add_date);
        cb_select_all = (CheckBox) findViewById(R.id.cb_select_all);

        cb_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                datePollAdapter.setCheckedAll(isChecked);

            }
        });

    }

    private void initClickListener() {
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_add_date:
                        openDatePicker();
                        break;
                }
            }
        };
    }

    private void setClickListeners() {
        tv_add_date.setOnClickListener(onClickListener);
    }

    private void initDatePollList() {
        rv_date_poll_list = (RecyclerView) findViewById(R.id.rv_date_poll_list);
        datePollAdapter = new DatePollAdapter(DatePollActivity.this,5);
        //using horizontal linearlayout as we want horizontal list
        rv_date_poll_list.setNestedScrollingEnabled(false);
        dateLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_date_poll_list.setLayoutManager(dateLayoutManager);
        rv_date_poll_list.setAdapter(datePollAdapter);
    }

    private void openDatePicker() {
        final Calendar cal = Calendar.getInstance();
        c_day = cal.get(Calendar.DAY_OF_MONTH);
        c_month = cal.get(Calendar.MONTH);
        c_year = cal.get(Calendar.YEAR);
        Date date = new Date();
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(DatePollActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                c_day = day;
                c_month = month;
                c_year  = year;
                //setText
            }
        }, c_year, c_month, c_day);
        cal.add(Calendar.DAY_OF_MONTH,10);
        mDatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        mDatePickerDialog.show();
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
