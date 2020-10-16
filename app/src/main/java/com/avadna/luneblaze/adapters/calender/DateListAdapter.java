package com.avadna.luneblaze.adapters.calender;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.MyViewHolder> {
    Context context;
    int[] daysOfWeek;
    List<String> days;
    List<Date> selectedDates;
    List<Date> preSelectedDates;
    boolean disablePreviousDates = false;

    int yearNumber;
    int monthNumber;
    Fragment fragment;

    int venueId;

    List<PojoVenueDay> daysList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_day_name;
        TextView tv_slot1;
        TextView tv_slot2;
        RecyclerView rv_grouped_dates;
        View view_divider;

        MyViewHolder(View view) {
            super(view);
            tv_day_name = (TextView) view.findViewById(R.id.tv_day_name);
            tv_slot1 = (TextView) view.findViewById(R.id.tv_slot1);
            tv_slot2 = (TextView) view.findViewById(R.id.tv_slot2);
            rv_grouped_dates = (RecyclerView) view.findViewById(R.id.rv_grouped_dates);
            view_divider = (View) view.findViewById(R.id.view_divider);
        }
    }

    public void setYearNumber(int yearNumber) {
        this.yearNumber = yearNumber;
        notifyDataSetChanged();
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
        notifyDataSetChanged();

    }

    public DateListAdapter(Context context, int yearNumber, int monthNumber, int[] daysOfWeek,
                           List<Date> selectedDates, List<Date> preSelectedDates, int venueId,
                           boolean disablePreviousdates, List<PojoVenueDay> daysList) {
        this.context = context;
        this.daysOfWeek = daysOfWeek;
        this.yearNumber = yearNumber;
        this.monthNumber = monthNumber;
        this.selectedDates = selectedDates;
        this.preSelectedDates = preSelectedDates;
        this.venueId = venueId;
        this.disablePreviousDates = disablePreviousdates;
        this.daysList = daysList;

        days = new ArrayList<>();
        days.add("Sun");
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        days.add("Sat");

        //  this.hierarchyList = hierarchyList;
    }

    public DateListAdapter(Fragment fragment, int yearNumber, int monthNumber, int[] daysOfWeek,
                           List<Date> selectedDates, List<Date> preSelectedDates, int venueId,
                           boolean disablePreviousdates, List<PojoVenueDay> daysList) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.daysOfWeek = daysOfWeek;
        this.yearNumber = yearNumber;
        this.monthNumber = monthNumber;
        this.selectedDates = selectedDates;
        this.preSelectedDates = preSelectedDates;
        this.venueId = venueId;
        this.disablePreviousDates = disablePreviousdates;
        this.daysList = daysList;

        days = new ArrayList<>();
        days.add("Sun");
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        days.add("Sat");

        //  this.hierarchyList = hierarchyList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calender_date_item, parent, false);
        return new DateListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DateListAdapter.MyViewHolder holder, final int position) {
        holder.tv_day_name.setText(days.get(daysOfWeek[position] - 1));

        if(daysList!=null){
            if (daysList.get(daysOfWeek[position] - 1).slots.size() == 1
                    && !daysList.get(daysOfWeek[position] - 1).slots.get(0).start.isEmpty()) {
                String start = daysList.get(daysOfWeek[position] - 1).slots.get(0).start;
                String end = daysList.get(daysOfWeek[position] - 1).slots.get(0).end;
                holder.tv_slot1.setText(start + "-" + end);

                holder.tv_slot1.setVisibility(View.VISIBLE);
                holder.tv_slot2.setVisibility(View.GONE);
            } else if (daysList.get(daysOfWeek[position] - 1).slots.size() == 2
                    && !daysList.get(daysOfWeek[position] - 1).slots.get(0).start.isEmpty()
                    && !daysList.get(daysOfWeek[position] - 1).slots.get(1).start.isEmpty()) {
                String start = daysList.get(daysOfWeek[position] - 1).slots.get(0).start;
                String end = daysList.get(daysOfWeek[position] - 1).slots.get(0).end;
                holder.tv_slot1.setText(start + "-" + end);

                start = daysList.get(daysOfWeek[position] - 1).slots.get(1).start;
                end = daysList.get(daysOfWeek[position] - 1).slots.get(1).end;
                holder.tv_slot2.setText(start + "-" + end);

                holder.tv_slot1.setVisibility(View.VISIBLE);
                holder.tv_slot2.setVisibility(View.VISIBLE);
            }
            else {
                holder.tv_slot1.setVisibility(View.GONE);
                holder.tv_slot2.setVisibility(View.GONE);
            }
        }
        else {
            holder.tv_slot1.setVisibility(View.GONE);
            holder.tv_slot2.setVisibility(View.GONE);
        }

        if (position == daysOfWeek.length - 1) {
            holder.view_divider.setVisibility(View.GONE);
        } else {
            holder.view_divider.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager dateListLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        DayGroupAdapter dateListAdapter;
        if (fragment == null) {
            dateListAdapter = new DayGroupAdapter(context, yearNumber, monthNumber,
                    daysOfWeek[position], selectedDates, preSelectedDates, disablePreviousDates);
        } else {
            dateListAdapter = new DayGroupAdapter(fragment, yearNumber, monthNumber,
                    daysOfWeek[position], selectedDates, preSelectedDates, disablePreviousDates);
        }

        holder.rv_grouped_dates.setAdapter(dateListAdapter);
        holder.rv_grouped_dates.setLayoutManager(dateListLayoutManager);

    }

    @Override
    public int getItemCount() {
        return daysOfWeek.length;
        //  return hierarchyList.size();
    }
}