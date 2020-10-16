package com.avadna.luneblaze.adapters.calender;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayGroupAdapter extends RecyclerView.Adapter<DayGroupAdapter.MyViewHolder> {
    Context context;
    int dayOfWeek;
    List<Integer> daysInMonth;
    List<Date> selectedDates;
    List<Date> datesToBeShown;
    List<Date> preSelectedDates;
    boolean disablePreviousDates;


    DayGroupItemCallback dayGroupItemCallback;

    int yearNumber;
    int monthNumber;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;

        MyViewHolder(View view) {
            super(view);
            tv_item = (TextView) view.findViewById(R.id.tv_item);

        }
    }


    public DayGroupAdapter(Context context, int yearNumber, int monthNumber, int dayOfWeek,
                           List<Date> selectedDates, List<Date> preSelectedDates, boolean disablePreviousDates) {

        try {
            this.dayGroupItemCallback = (DayGroupItemCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement articleCommentListAdapterCallback.");
        }
        this.context = context;
        this.dayOfWeek = dayOfWeek;
        this.yearNumber = yearNumber;
        this.monthNumber = monthNumber;
        this.selectedDates = selectedDates;
        this.preSelectedDates = preSelectedDates;
        this.disablePreviousDates = disablePreviousDates;

        daysInMonth = new ArrayList<>();
        datesToBeShown = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearNumber, monthNumber, 1);

        for (int i = 0; i < 31; i++) {
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int month=calendar.get(Calendar.MONTH);
            if (day == dayOfWeek&&month==monthNumber) {
                daysInMonth.add(calendar.get(Calendar.DAY_OF_MONTH));
                datesToBeShown.add(calendar.getTime());
            }
            calendar.add(Calendar.DATE, 1);
        }

        //  this.hierarchyList = hierarchyList;
    }

    public DayGroupAdapter(Fragment fragment, int yearNumber, int monthNumber, int dayOfWeek,
                           List<Date> selectedDates, List<Date> preSelectedDates, boolean disablePreviousDates) {

        try {
            this.dayGroupItemCallback = (DayGroupItemCallback) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment Must implement articleCommentListAdapterCallback.");
        }
        this.context = fragment.getContext();
        this.dayOfWeek = dayOfWeek;
        this.yearNumber = yearNumber;
        this.monthNumber = monthNumber;
        this.selectedDates = selectedDates;
        this.preSelectedDates = preSelectedDates;
        this.disablePreviousDates = disablePreviousDates;


        daysInMonth = new ArrayList<>();
        datesToBeShown = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearNumber, monthNumber, 1);

        for (int i = 0; i < 31; i++) {
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int month=calendar.get(Calendar.MONTH);
            if (day == dayOfWeek&&month==monthNumber) {
                daysInMonth.add(calendar.get(Calendar.DAY_OF_MONTH));
                datesToBeShown.add(calendar.getTime());
            }
            calendar.add(Calendar.DATE, 1);
        }

        //  this.hierarchyList = hierarchyList;
    }


    @Override
    public DayGroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_calender_date_item, parent, false);
        return new DayGroupAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DayGroupAdapter.MyViewHolder holder, final int position) {
        holder.tv_item.setText("" + daysInMonth.get(position));

        int DP4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                context.getResources().getDisplayMetrics());
        holder.tv_item.setPadding(DP4, 0, DP4, 0);
        if (isDateSelected(datesToBeShown.get(position))) {
            holder.tv_item.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tv_item.setBackgroundResource(R.drawable.selected_date_bg);
        } else if (isDatePreSelected(datesToBeShown.get(position))) {
            holder.tv_item.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tv_item.setBackgroundResource(R.drawable.disabled_date_bg);
        } else if (isDateDisabled(datesToBeShown.get(position))) {
            holder.tv_item.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
            holder.tv_item.setBackgroundResource(R.color.transparent);
        } else {
            holder.tv_item.setTextColor(ContextCompat.getColor(context, R.color.med_grey));
            holder.tv_item.setBackgroundResource(R.color.transparent);
            holder.tv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(yearNumber, monthNumber, daysInMonth.get(position));
                    dayGroupItemCallback.onDateClicked(cal.getTime());
                }
            });
        }

    }

    private boolean isDateDisabled(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date curr=calendar.getTime();
        if(curr.after(date)){
            return true;
        }
        else {
            return false;
        }

    }

    private boolean isDateSelected(Date date) {
        for (int i = 0; i < selectedDates.size(); i++) {
            if (areDatesSame(selectedDates.get(i), date)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDatePreSelected(Date date) {
        for (int i = 0; i < preSelectedDates.size(); i++) {
            if (areDatesSame(preSelectedDates.get(i), date)) {
                return true;
            }
        }
        return false;
    }


    public boolean areDatesSame(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return daysInMonth.size();
        //  return hierarchyList.size();
    }


    public static interface DayGroupItemCallback {
        void onDateClicked(Date date);
    }
}