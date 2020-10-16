package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;

import java.util.List;

public class AssessmentCategoryListAdapter extends RecyclerView.Adapter<AssessmentCategoryListAdapter.MyViewHolder> {
    List<PojoAssessmentCategory> dataSet;
    private AssessmentCategoryListAdapterCallback assessmentCategoryListAdapterCallback;
    Activity activity;
    String listType;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_interest_name;
        CheckBox cb_selected;
        ImageView iv_expand;
        CardView cv_item_parent;

        MyViewHolder(View view) {
            super(view);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
            iv_expand = (ImageView) view.findViewById(R.id.iv_expand);
            cv_item_parent = (CardView) view.findViewById(R.id.cv_item_parent);
        }
    }

    public AssessmentCategoryListAdapter(Activity activity, List<PojoAssessmentCategory> dataSet, String listType) {
        try {
            this.assessmentCategoryListAdapterCallback = ((AssessmentCategoryListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AssessmentCategoryListAdapter.");
        }

        this.activity = activity;
        this.dataSet = dataSet;
        this.listType = listType;

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public AssessmentCategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_list_item, parent, false);

        return new AssessmentCategoryListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssessmentCategoryListAdapter.MyViewHolder holder, final int position) {
        holder.tv_interest_name.setText(dataSet.get(position).text);

        //check if the interest is present in selected interest list


      /*  if (checkedInterestMap.containsKey(dataSet.get(position).interestId)) {
            dataSet.get(position).isCbChecked = true;
        } else {
            dataSet.get(position).isCbChecked = false;
        }*/

        holder.cb_selected.setChecked(dataSet.get(position).isCbChecked);

        if (dataSet.get(position).child == null || dataSet.get(position).child.isEmpty()) {
            holder.iv_expand.setVisibility(View.GONE);
        } else {
            holder.iv_expand.setVisibility(View.VISIBLE);
        }

        holder.cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.cb_selected.isPressed()) {
                    assessmentCategoryListAdapterCallback
                            .onAssessmentCategoryListItemClick(position, "cb", true, dataSet.get(position), listType);
                }
               /* if (holder.cb_selected.isChecked()) {
                    assessmentCategoryListAdapterCallback
                            .onNewAssessmentCategoryListAdapterCallback(position, "cb", true, dataSet.get(position));
                } else {
                    assessmentCategoryListAdapterCallback
                            .onNewAssessmentCategoryListAdapterCallback(position, "cb", false, dataSet.get(position));
                }*/
            }
        });

        holder.cv_item_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_selected.setChecked(!holder.cb_selected.isChecked());
                assessmentCategoryListAdapterCallback
                        .onAssessmentCategoryListItemClick(position, "cb", true, dataSet.get(position), listType);
            }
        });

        holder.iv_expand.setVisibility(View.GONE);
/*
        holder.iv_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataSet.get(position).child.isEmpty()) {
                    assessmentCategoryListAdapterCallback.onAssessmentCategoryListItemClick(position,
                            "iv", false, dataSet.get(position),listType);
                }
            }
        });

        holder.tv_interest_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.iv_expand.callOnClick();
            }
        });*/
    }

    public static interface AssessmentCategoryListAdapterCallback {
        void onAssessmentCategoryListItemClick(int position, String type, Boolean cbChecked,
                                               PojoAssessmentCategory pojoAllInterestListItem, String listType);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}
