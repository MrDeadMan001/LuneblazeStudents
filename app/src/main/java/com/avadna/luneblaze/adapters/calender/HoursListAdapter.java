package com.avadna.luneblaze.adapters.calender;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;

import java.util.List;

public class HoursListAdapter extends RecyclerView.Adapter<HoursListAdapter.MyViewHolder> {


    Activity activity;
    Fragment fragment;
    List<Integer> hoursList;
    HoursListAdapterCallback blockedListAdapterCallback;
    int listHeight;


    public HoursListAdapter(Activity activity, List<Integer> hours) {
        try {
            this.blockedListAdapterCallback = ((HoursListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  HoursListAdapterCallback.");
        }
        this.activity = activity;
        this.hoursList = hours;
        listHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, activity.getResources().getDisplayMetrics());

        //  this.hierarchyList = hierarchyList;
    }

    public HoursListAdapter(Fragment fragment, List<Integer> hours) {
        try {
            this.blockedListAdapterCallback = ((HoursListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement  HoursListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.hoursList = hours;
        listHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, activity.getResources().getDisplayMetrics());

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;

        MyViewHolder(View view) {
            super(view);
            tv_item = (TextView) view.findViewById(R.id.tv_item);
            tv_item.setGravity(Gravity.CENTER);

        }
    }


    @Override
    public HoursListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hour_list_item, parent, false);

        return new HoursListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HoursListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (position == 0) {
            holder.tv_item.setVisibility(View.INVISIBLE);

        } else if (position == hoursList.size() + 1) {
            holder.tv_item.setVisibility(View.INVISIBLE);

        } else {
            holder.tv_item.setVisibility(View.VISIBLE);
            holder.tv_item.setText(String.valueOf(hoursList.get(position - 1)));
        }

    }


    @Override
    public int getItemCount() {
        return hoursList.size() + 2;
        //  return hierarchyList.size();
    }

    public static interface HoursListAdapterCallback {
        void hoursListItemClickCallback(int position, PojoUserData currentItem, String type);
    }

}

