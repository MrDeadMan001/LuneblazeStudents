package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;

import java.util.List;

public class AssessmentCategorySearchResultAdapter extends RecyclerView.Adapter<AssessmentCategorySearchResultAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    private int listMaxSize = 6;
    private List<PojoAssessmentCategory> dataSet;
    private AssessmentCategorySearchResultCallback interestResultAdapterCallback;

    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name;


        MyViewHolder(View view) {
            super(view);
            tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);


        }
    }


    public AssessmentCategorySearchResultAdapter(Activity activity, List<PojoAssessmentCategory> dataSet) {
        try {
            this.interestResultAdapterCallback = ((AssessmentCategorySearchResultCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.activity = activity;


        //  this.hierarchyList = hierarchyList;
    }
    

 /*   public AssessmentCategorySearchResultAdapter(CreateArticleFragment fragment, List<PojoAllInterestListItem> dataSet) {
        try {
            this.interestResultAdapterCallback = ((AssessmentCategorySearchResultAdapter.InterestResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.fragment = fragment;


        //  this.hierarchyList = hierarchyList;
    }*/

    public AssessmentCategorySearchResultAdapter(int listSize) {
        this.listSize = listSize;
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_search_result_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (dataSet.get(position) != null&&dataSet.get(position).text!=null) {

            holder.tv_item_name.setText(dataSet.get(position).text);
            holder.tv_item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestResultAdapterCallback.onAssessmentCategorySearchResultItemClick(position, dataSet.get(position));
                }
            });
        }
    }

    public static interface AssessmentCategorySearchResultCallback {
        void onAssessmentCategorySearchResultItemClick(int position, PojoAssessmentCategory PojoAllInterestListItem);
    }


    @Override
    public int getItemCount() {
     /*   if (dataSet != null) {
            return dataSet.size();
        } else {
            return (listMaxSize > listSize) ? listSize : listMaxSize;
        }*/
        return dataSet.size();
    }
}
