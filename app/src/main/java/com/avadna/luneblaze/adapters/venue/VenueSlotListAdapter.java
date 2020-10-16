package com.avadna.luneblaze.adapters.venue;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;

import java.util.ArrayList;
import java.util.List;

public class VenueSlotListAdapter extends RecyclerView.Adapter<VenueSlotListAdapter.MyViewHolder> {
    Context context;
    int[] daysOfWeek;
    List<String> days;

    Fragment fragment;

    int venueId;

    List<PojoVenueDay> daysList;
    

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_day_name;
        TextView tv_slot1;
        TextView tv_slot2;
        View view_divider;

        MyViewHolder(View view) {
            super(view);
            tv_day_name = (TextView) view.findViewById(R.id.tv_day_name);
            tv_slot1 = (TextView) view.findViewById(R.id.tv_slot1);
            tv_slot2 = (TextView) view.findViewById(R.id.tv_slot2);
            view_divider = (View) view.findViewById(R.id.view_divider);
        }
    }

    public VenueSlotListAdapter(Context context,int venueId,int[] daysOfWeek, List<PojoVenueDay> daysList) {
        this.context = context;
        this.daysOfWeek = daysOfWeek;
        this.venueId = venueId;
        this.daysList=daysList;

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

    public VenueSlotListAdapter(Fragment fragment, int venueId,int[] daysOfWeek, List<PojoVenueDay> daysList) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.daysOfWeek = daysOfWeek;
        this.venueId = venueId;
        this.daysList=daysList;


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
    public VenueSlotListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_list_item, parent, false);
        return new VenueSlotListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenueSlotListAdapter.MyViewHolder holder, final int position) {
        holder.tv_day_name.setText(days.get(daysOfWeek[position]));

        if (daysList.get(daysOfWeek[position]).slots.isEmpty()) {
            holder.tv_slot1.setVisibility(View.GONE);
            holder.tv_slot2.setVisibility(View.GONE);
        } else if (daysList.get(daysOfWeek[position]).slots.size() == 1) {
            String start = daysList.get(daysOfWeek[position]).slots.get(0).start;
            String end = daysList.get(daysOfWeek[position]).slots.get(0).end;
            holder.tv_slot1.setText(start + "-" + end);

            holder.tv_slot1.setVisibility(View.VISIBLE);
            holder.tv_slot2.setVisibility(View.GONE);
        } else if (daysList.get(daysOfWeek[position]).slots.size() == 2) {
            String start = daysList.get(daysOfWeek[position]).slots.get(0).start;
            String end = daysList.get(daysOfWeek[position]).slots.get(0).end;
            holder.tv_slot1.setText(start + "-" + end);

            start = daysList.get(daysOfWeek[position]).slots.get(1).start;
            end = daysList.get(daysOfWeek[position]).slots.get(1).end;
            holder.tv_slot2.setText(start + "-" + end);

            holder.tv_slot1.setVisibility(View.VISIBLE);
            holder.tv_slot2.setVisibility(View.VISIBLE);
        }

        if (position == daysOfWeek.length - 1) {
            holder.view_divider.setVisibility(View.GONE);
        } else {
            holder.view_divider.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return daysOfWeek.length;
        //  return hierarchyList.size();
    }
}