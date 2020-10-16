package com.avadna.luneblaze.adapters.venue;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.DatePollActivity;

import java.util.List;

/**
 * Created by Sunny on 24-03-2018.
 */

public class VenuePollAdapter extends RecyclerView.Adapter<VenuePollAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    List<String> more_options_list;
    ArrayAdapter<String> spinnerAdapter;
    View.OnClickListener mOnClickListener;
    boolean allSelected = false;
    //boolean[] expandedStatus=new boolean[7];

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_selected;
        Spinner sp_time_options;
        /*  RadioGroup rg_time;
          RadioButton rb_morning,rb_evening,rb_any;*/
        TextView tv_date, graph_bar;


        MyViewHolder(View view) {
            super(view);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
            //    sp_time_options = (Spinner) view.findViewById(R.id.sp_time_options);
            //    graph_bar=(View)view.findViewById(R.id.graph_bar);
            //  rg_time=(RadioGroup)view.findViewById(R.id.rg_time);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            graph_bar = (TextView) view.findViewById(R.id.graph_bar);

            // rb_morning=(RadioButton)view.findViewById(R.id.rb_morning);
            // rb_evening=(RadioButton)view.findViewById(R.id.rb_evening);
            //  rb_any=(RadioButton)view.findViewById(R.id.rb_any);
        }
    }


    public VenuePollAdapter(Activity activity, int listSize) {
        this.listSize = listSize;
        this.activity = activity;
    /*    user_options_list = new ArrayList<String>();
        user_options_list.add("Morning (20 Votes)");
        user_options_list.add("Evening (37 Votes)");
        user_options_list.add("Any");*/
        //  this.hierarchyList = hierarchyList;
    }

    public void setCheckedAll(boolean allSelected){
        this.allSelected=allSelected;
        notifyDataSetChanged();
    }

    @Override
    public VenuePollAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_poll_list_item, parent, false);

        return new VenuePollAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenuePollAdapter.MyViewHolder holder, final int position) {

        if (allSelected) {
            holder.cb_selected.setChecked(true);
        } else {
            holder.cb_selected.setChecked(false);
        }


     /*   if(expandedStatus[position]){
            holder.rg_time.setVisibility(View.VISIBLE);
        }
        else {
            holder.rg_time.setVisibility(View.GONE);
        }

        if((!holder.rb_morning.isChecked())&&(!holder.rb_evening.isChecked())){
            holder.rb_any.setChecked(true);
        }
        else{
            holder.rb_any.setChecked(false);
        }*/

       /* spinnerAdapter = new ArrayAdapter<String>(activity, R.layout.custom_spinner_item, more_options_list);
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

        holder.tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dateIntent = new Intent(activity, DatePollActivity.class);
                activity.startActivity(dateIntent);
                if (!holder.cb_selected.isChecked()) {
                    holder.cb_selected.toggle();
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