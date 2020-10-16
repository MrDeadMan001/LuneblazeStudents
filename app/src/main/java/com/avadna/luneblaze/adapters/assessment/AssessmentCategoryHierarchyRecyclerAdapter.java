package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;

import java.util.List;

public class AssessmentCategoryHierarchyRecyclerAdapter extends RecyclerView.Adapter<AssessmentCategoryHierarchyRecyclerAdapter.MyViewHolder> {
    Activity activity;
    private AssessmentCategoryHierarchyRecyclerAdapterCallback interestHierarchyRecyclerAdapterCallback;
    private List<PojoAssessmentCategory> hierarchyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hierarchy_item;

        public MyViewHolder(View view) {
            super(view);
            tv_hierarchy_item = (TextView) view.findViewById(R.id.tv_hierarchy_item);

        }
    }


    public AssessmentCategoryHierarchyRecyclerAdapter(Activity activity,List<PojoAssessmentCategory> hierarchyList) {
        try {
            this.interestHierarchyRecyclerAdapterCallback =((AssessmentCategoryHierarchyRecyclerAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }
        this.activity=activity;
        this.hierarchyList = hierarchyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_hierarchy_list_item, parent, false);

        return new AssessmentCategoryHierarchyRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssessmentCategoryHierarchyRecyclerAdapter.MyViewHolder holder, final int position) {
        //add -> arrow only if it is not the root directory
        if(position==0){
            holder.tv_hierarchy_item.setText(hierarchyList.get(position).text);
        }
        else{
            holder.tv_hierarchy_item.setText("-> "+hierarchyList.get(position).text);
        }
        holder.tv_hierarchy_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestHierarchyRecyclerAdapterCallback.onHierarchyListMethodCallback(position);
            }
        });

    }

    public static interface AssessmentCategoryHierarchyRecyclerAdapterCallback {
        void onHierarchyListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return hierarchyList.size();
    }
}