package com.avadna.luneblaze.adapters.profileInfo;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoWorkHistory;

import java.util.List;

public class EducationSmallListAdapter extends RecyclerView.Adapter<EducationSmallListAdapter.MyViewHolder>{
    List<PojoWorkHistory> workHistoryList;
    Activity activity;
    String type;
    CommonFunctions commonFunctions;

    public EducationSmallListAdapter(Activity activity,
                                     List<PojoWorkHistory> workHistoryList, String type) {
        this.activity=activity;
        this.workHistoryList=workHistoryList;
        this.type=type;
        commonFunctions=new CommonFunctions(activity);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_description;
        ImageView iv_icon;

        MyViewHolder(View view) {
            super(view);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

        }
    }


    @Override
    public EducationSmallListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.education_list_item, parent, false);
        return new EducationSmallListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EducationSmallListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoWorkHistory currentItem=workHistoryList.get(position);
        String startDate=commonFunctions.parseDateToName(currentItem.startDate);
        String endDate=currentItem.endDate;
        if(endDate.equals("0")){
            endDate=activity.getString(R.string.present);
        }
        else {
            endDate=commonFunctions.parseDateToName(endDate);
        }

       /* holder.tv_description.setText(currentItem.type +" as "+currentItem.subject+" at "
                +currentItem.institution+" from "+startDate+" to "+endDate);*/

       if (currentItem.type.equalsIgnoreCase("studing")){
           holder.tv_description.setText("Studying" +currentItem.subject+" at "
                   +currentItem.institution);
       }
       else {
           holder.tv_description.setText(currentItem.type +" as "+currentItem.subject+" at "
                   +currentItem.institution);
       }


    }

    @Override
    public int getItemCount() {
        return workHistoryList.size();
        //  return hierarchyList.size();
    }
}
