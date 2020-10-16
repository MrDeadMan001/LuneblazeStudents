package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;

import java.util.List;

public class TaggedAssessmentCategoriesAdapter extends RecyclerView.Adapter<TaggedAssessmentCategoriesAdapter.MyViewHolder> {

    List<PojoAssessmentCategory> dataSet;
    Activity activity;
    TaggedAssessmentCategoriesAdapterCallback taggedInterestAdapterCallback;
    int TYPE_REMOVE = 1;
    int TYPE_OPEN = 2;
    int type;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_interest_tag_name;
        LinearLayout ll_parent;

        MyViewHolder(View view) {
            super(view);

            tv_interest_tag_name = (TextView) view.findViewById(R.id.tv_interest_tag_name);
            ll_parent = (LinearLayout) view.findViewById(R.id.ll_parent);
        }
    }

    public TaggedAssessmentCategoriesAdapter(Activity activity, List<PojoAssessmentCategory> dataSet, int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedAssessmentCategoriesAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement taggedInterestListAdapterCallback.");
        }
        this.activity = activity;
        this.dataSet = dataSet;
        this.type = type;
        //  this.hierarchyList = hierarchyList;
    }

    public TaggedAssessmentCategoriesAdapter(Fragment fragment, List<PojoAssessmentCategory> dataSet, int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedAssessmentCategoriesAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement taggedInterestListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.dataSet = dataSet;
        this.type = type;
        //  this.hierarchyList = hierarchyList;
    }


    @Override
    public TaggedAssessmentCategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_tag_list_item, parent, false);

        return new TaggedAssessmentCategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaggedAssessmentCategoriesAdapter.MyViewHolder holder, final int position) {
        if (dataSet != null) {
            PojoAssessmentCategory currentItem=dataSet.get(position);
            if(currentItem.isCbChecked){
                holder.ll_parent.setVisibility(View.VISIBLE);
                holder.tv_interest_tag_name.setText(dataSet.get(position).text);
                holder.tv_interest_tag_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (type == TYPE_OPEN) {
                      /*  Intent interestIntent = new Intent(activity, InterestActivity.class);
                        interestIntent.putExtra("id", dataSet.get(position).categoryId);
                        activity.startActivity(interestIntent);*/
                        } else if (type == TYPE_REMOVE) {
                            taggedInterestAdapterCallback.onTaggedAssessmentCategoryItemClick(position, dataSet.get(position));
                        }
                    }
                });
            }
            else {
                holder.ll_parent.setVisibility(View.GONE);
            }
        }
    }

    public static interface TaggedAssessmentCategoriesAdapterCallback {
        void onTaggedAssessmentCategoryItemClick(int position, PojoAssessmentCategory pojoAssessmentCategory);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();

        //  return hierarchyList.size();
    }
}

