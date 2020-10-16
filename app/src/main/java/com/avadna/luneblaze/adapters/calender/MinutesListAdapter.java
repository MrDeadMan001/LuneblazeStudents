package com.avadna.luneblaze.adapters.calender;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;

import java.util.List;

public class MinutesListAdapter extends RecyclerView.Adapter<MinutesListAdapter.MyViewHolder> {



    Activity activity;
    Fragment fragment;
    List<Integer> minutesList;

    MinutesListAdapterCallback blockedListAdapterCallback;


    public MinutesListAdapter(Activity activity,List<Integer> minutesList) {
        try {
            this.blockedListAdapterCallback = ((MinutesListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  MinutesListAdapterCallback.");
        }
        this.activity = activity;
        this.minutesList =minutesList;

        //  this.hierarchyList = hierarchyList;
    }

    public MinutesListAdapter(Fragment fragment,List<Integer> minutesList) {
        try {
            this.blockedListAdapterCallback = ((MinutesListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement  MinutesListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.minutesList =minutesList;

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
    public MinutesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hour_list_item, parent, false);

        return new MinutesListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MinutesListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == 0) {
            holder.tv_item.setVisibility(View.INVISIBLE);

        } else if (position == minutesList.size() + 1) {
            holder.tv_item.setVisibility(View.INVISIBLE);

        } else {
            holder.tv_item.setVisibility(View.VISIBLE);
            if(minutesList.get(position-1)<10){
                holder.tv_item.setText("0"+String.valueOf(minutesList.get(position-1)));
            }
            else {
                holder.tv_item.setText(String.valueOf(minutesList.get(position-1)));

            }
        }

    }



    @Override
    public int getItemCount() {
        return minutesList.size()+2;
        //  return hierarchyList.size();
    }

    public static interface MinutesListAdapterCallback {
        void minutesListItemClickCallback(int position, PojoUserData currentItem, String type);
    }

}

