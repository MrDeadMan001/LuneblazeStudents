package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.avadna.luneblaze.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 11-02-2018.
 */

public class DatePollAdapter extends RecyclerView.Adapter<DatePollAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    List<String> more_options_list;
    ArrayAdapter<String> spinnerAdapter;
    View.OnClickListener mOnClickListener;
    boolean[] expandedStatus = new boolean[7];
    private boolean checkedAll;

    public void setCheckedAll(boolean checkedAll) {
        this.checkedAll = checkedAll;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_selected;
        Spinner sp_time_options;
        View graph_bar;
        RadioGroup rg_time;
        RadioButton rb_morning, rb_evening, rb_any;
        TextView tv_date;
        LinearLayout ll_time_votes_container;


        MyViewHolder(View view) {
            super(view);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
            //    sp_time_options = (Spinner) view.findViewById(R.id.sp_time_options);
            //    graph_bar=(View)view.findViewById(R.id.graph_bar);
            rg_time = (RadioGroup) view.findViewById(R.id.rg_time);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            rb_morning = (RadioButton) view.findViewById(R.id.rb_morning);
            rb_evening = (RadioButton) view.findViewById(R.id.rb_evening);
            rb_any = (RadioButton) view.findViewById(R.id.rb_any);
            ll_time_votes_container = (LinearLayout) view.findViewById(R.id.ll_time_votes_container);
        }
    }


    public DatePollAdapter(Activity activity, int listSize) {
        this.listSize = listSize;
        this.activity = activity;
        more_options_list = new ArrayList<String>();
        more_options_list.add("Morning (20 Votes)");
        more_options_list.add("Evening (37 Votes)");
        more_options_list.add("Any");
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public DatePollAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_poll_list_item, parent, false);

        return new DatePollAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DatePollAdapter.MyViewHolder holder, final int position) {

        if(checkedAll){
            holder.cb_selected.setChecked(true);
        }
        else {
            holder.cb_selected.setChecked(false);
        }

        if (expandedStatus[position]) {
            holder.rg_time.setVisibility(View.VISIBLE);
            holder.ll_time_votes_container.setVisibility(View.VISIBLE);
        } else {
            holder.rg_time.setVisibility(View.GONE);
            holder.ll_time_votes_container.setVisibility(View.GONE);
        }

        if ((!holder.rb_morning.isChecked()) && (!holder.rb_evening.isChecked())) {
            holder.rb_any.setChecked(true);
        } else {
            holder.rb_any.setChecked(false);
        }

       /* spinnerAdapter = new ArrayAdapter<String>(activity, R.layout.custom_spinner_item, user_options_list);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        holder.sp_time_options.setAdapter(spinnerAdapter);



        holder.sp_time_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });*/

        holder.cb_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_date.callOnClick();
                //had to call toggle to counter the toggling in tv_date's click listener
                holder.cb_selected.toggle();
            }
        });

        holder.tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_selected.toggle();

                if (holder.rg_time.getVisibility() == View.GONE) {
                    holder.rg_time.setVisibility(View.VISIBLE);
                    holder.ll_time_votes_container.setVisibility(View.VISIBLE);
                    expandedStatus = new boolean[7];
                    expandedStatus[position] = true;
                    notifyDataSetChanged();
                } else if (holder.rg_time.getVisibility() == View.VISIBLE) {
                    holder.rg_time.setVisibility(View.GONE);
                    holder.ll_time_votes_container.setVisibility(View.GONE);
                    expandedStatus = new boolean[7];
                    expandedStatus[position] = false;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSize;
        //  return hierarchyList.size();
    }
}


