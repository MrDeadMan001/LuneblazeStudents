package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;

import java.util.List;

/**
 * Created by Sunny on 02-11-2017.
 */


public class InterestHierarchyRecyclerAdapter extends RecyclerView.Adapter<InterestHierarchyRecyclerAdapter.MyViewHolder> {
    Activity activity;
    private InterestHierarchyRecyclerAdapterCallback interestHierarchyRecyclerAdapterCallback;
    private List<PojoGetInterestListResponseDataListItem> hierarchyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hierarchy_item;

        public MyViewHolder(View view) {
            super(view);
            tv_hierarchy_item = (TextView) view.findViewById(R.id.tv_hierarchy_item);

        }
    }


    public InterestHierarchyRecyclerAdapter(Activity activity,List<PojoGetInterestListResponseDataListItem> hierarchyList) {
        try {
            this.interestHierarchyRecyclerAdapterCallback =((InterestHierarchyRecyclerAdapterCallback) activity);
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

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
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

    public static interface InterestHierarchyRecyclerAdapterCallback {
        void onHierarchyListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return hierarchyList.size();
    }
}