package com.avadna.luneblaze.adapters.profileInfo;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoWorkHistory;

import java.util.List;

public class EducationLargeListItemAdapter extends RecyclerView.Adapter<EducationLargeListItemAdapter.MyViewHolder> {
    List<PojoWorkHistory> workHistoryList;
    Activity activity;
    WorkAndEducationAdapterCallback workAndEducationAdapterCallback;
    CommonFunctions commonFunctions;
    String targetUserId;

    PreferenceUtils preferenceUtils;
    String user_id;

    public EducationLargeListItemAdapter(Activity activity, List<PojoWorkHistory> workHistoryList, String target_user_id) {
        try {
            this.workAndEducationAdapterCallback = ((WorkAndEducationAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement WorkAndEducationAdapterCallback.");
        }
        this.activity = activity;
        commonFunctions = new CommonFunctions(activity);
        targetUserId = target_user_id;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        this.workHistoryList = workHistoryList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;
        TextView tv_field;
        TextView tv_workplace;
        TextView tv_start_date;
        TextView tv_end_date;
        ImageView iv_edit;
        ImageView iv_delete;

        MyViewHolder(View view) {
            super(view);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tv_field = (TextView) view.findViewById(R.id.tv_field);
            tv_workplace = (TextView) view.findViewById(R.id.tv_workplace);
            tv_start_date = (TextView) view.findViewById(R.id.tv_start_date);
            tv_end_date = (TextView) view.findViewById(R.id.tv_end_date);
            iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }


    @Override
    public EducationLargeListItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.education_list_item_expanded, parent, false);
        return new EducationLargeListItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EducationLargeListItemAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoWorkHistory currentItem = workHistoryList.get(position);
        holder.tv_type.setText(currentItem.type);
        holder.tv_field.setText(currentItem.subject);
        holder.tv_workplace.setText(currentItem.institution);
        holder.tv_start_date.setText(commonFunctions.parseDateToName(currentItem.startDate));
        if (currentItem.endDate.equals("0")) {
            holder.tv_end_date.setText(activity.getString(R.string.present));
        } else {
            holder.tv_end_date.setText(commonFunctions.parseDateToName(currentItem.endDate));
        }

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workAndEducationAdapterCallback.workAndEducationListItemClickCallback(position,
                        workHistoryList.get(position), AppKeys.EDIT);
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workAndEducationAdapterCallback.workAndEducationListItemClickCallback(position,
                        workHistoryList.get(position), AppKeys.DELETE);
            }
        });

        if (!user_id.equals(targetUserId)) {
            holder.iv_delete.setVisibility(View.GONE);
            holder.iv_edit.setVisibility(View.GONE);
        } else {
            holder.iv_delete.setVisibility(View.VISIBLE);
            holder.iv_edit.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return workHistoryList.size();
        //  return hierarchyList.size();
    }

    public static interface WorkAndEducationAdapterCallback {
        void workAndEducationListItemClickCallback(int position, PojoWorkHistory item, String type);
    }
}

