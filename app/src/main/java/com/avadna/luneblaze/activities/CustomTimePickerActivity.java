package com.avadna.luneblaze.activities;

import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.view.View;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.calender.HoursListAdapter;
import com.avadna.luneblaze.adapters.calender.MinutesListAdapter;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;

import java.util.ArrayList;
import java.util.List;

public class CustomTimePickerActivity extends AppBaseActivity implements
        MinutesListAdapter.MinutesListAdapterCallback,
        HoursListAdapter.HoursListAdapterCallback {

    Dialog timeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_time_picker);
        openCustomDatePickerDialog(8);
    }

    private void openCustomDatePickerDialog(int venueId) {
        timeDialog = new MyCustomThemeDialog(this,R.style.NoTitleDialogTheme);
        timeDialog.setContentView(R.layout.custom_time_picker_dialog);

        RecyclerView rv_hours = (RecyclerView) timeDialog.findViewById(R.id.rv_hours);
        LinearLayoutManager hourLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_hours.setLayoutManager(hourLayoutManager);

        int startHour = 10;
        int endHour = 21;
        List<Integer> hoursList = new ArrayList<>();
        for (int i = startHour; i <= endHour; i++) {
            hoursList.add(i);
        }
        HoursListAdapter hoursListAdapter = new HoursListAdapter(this, hoursList);
        rv_hours.setAdapter(hoursListAdapter);
        SnapHelper hourSnapHelper = new LinearSnapHelper();
        hourSnapHelper.attachToRecyclerView(rv_hours);


        RecyclerView rv_minutes = (RecyclerView) timeDialog.findViewById(R.id.rv_minutes);
        LinearLayoutManager minuteLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_minutes.setLayoutManager(minuteLayoutManager);

        List<Integer> minutesList = new ArrayList<>();

        for (int i = 1; i <= 60; i++) {
            minutesList.add(i);
        }
        MinutesListAdapter minutesListAdapter = new MinutesListAdapter(this, minutesList);
        rv_minutes.setAdapter(minutesListAdapter);
        SnapHelper minuteSnapHelper = new LinearSnapHelper();
        minuteSnapHelper.attachToRecyclerView(rv_minutes);

        TextView tv_done_button = (TextView) timeDialog.findViewById(R.id.tv_done_button);
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View hourSnapView = hourSnapHelper.findSnapView(hourLayoutManager);
                int hourPos = hourLayoutManager.getPosition(hourSnapView);

                View minuteSnapView = minuteSnapHelper.findSnapView(minuteLayoutManager);
                int minutePos = minuteLayoutManager.getPosition(minuteSnapView);

                int pickedHour = hoursList.get(hourPos - 1);
                int pickedMinute = minutesList.get(minutePos - 1);

                String pickedTime = pickedHour + ":" + pickedMinute;
            }
        });

        timeDialog.show();

    }

    @Override
    public void hoursListItemClickCallback(int position, PojoUserData currentItem, String type) {

    }

    @Override
    public void minutesListItemClickCallback(int position, PojoUserData currentItem, String type) {

    }
}
