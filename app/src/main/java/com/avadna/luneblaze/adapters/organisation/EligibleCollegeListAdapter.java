package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCollegeListForDriveResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;


public class EligibleCollegeListAdapter extends RecyclerView.Adapter<EligibleCollegeListAdapter.MyViewHolder> {
    List<PojoGetCollegeListForDriveResponseData> dataList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;


    public EligibleCollegeListAdapter(Activity activity, List<PojoGetCollegeListForDriveResponseData> dataList) {
        this.dataList = dataList;
        this.activity = activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_student_count;
        TextView tv_college_name;


        MyViewHolder(View view) {
            super(view);
            tv_student_count = (TextView) view.findViewById(R.id.tv_student_count);
            tv_college_name = (TextView) view.findViewById(R.id.tv_college_name);
        }
    }


    @Override
    public EligibleCollegeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_list_with_students_item, parent, false);

        return new EligibleCollegeListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EligibleCollegeListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoGetCollegeListForDriveResponseData currentItem = dataList.get(position);
        String users="";
        for(int i=0;i<currentItem.user_ids.size();i++){
            users=users+currentItem.user_ids.get(i)+", ";
        }
       // holder.tv_student_count.setText("" + currentItem.noofstudent+"("+users+")");
        holder.tv_student_count.setText("" + currentItem.noofstudent);
        holder.tv_college_name.setText(currentItem.venueName);

        holder.tv_college_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent venueIntent = new Intent(activity, VenueActivity.class);
                venueIntent.putExtra("id", currentItem.venueId);
                activity.startActivity(venueIntent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
        //  return hierarchyList.size();
    }


}

