package com.avadna.luneblaze.adapters.interestHierarchy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListItem;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class NewHierarchyListAdapter extends RecyclerView.Adapter<NewHierarchyListAdapter.MyViewHolder> {
    Activity activity;
    private NewHierarchyListAdapterCallback NewHierarchyListAdapterCallback;
    private List<PojoAllInterestListItem> hierarchyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hierarchy_item;

        public MyViewHolder(View view) {
            super(view);
            tv_hierarchy_item = (TextView) view.findViewById(R.id.tv_hierarchy_item);

        }
    }


    public NewHierarchyListAdapter(Activity activity,List<PojoAllInterestListItem> hierarchyList) {
        try {
            this.NewHierarchyListAdapterCallback =((NewHierarchyListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }
        this.activity=activity;
        this.hierarchyList = hierarchyList;
    }

    @Override
    public NewHierarchyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_hierarchy_list_item, parent, false);

        return new NewHierarchyListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewHierarchyListAdapter.MyViewHolder holder, final int position) {
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
                NewHierarchyListAdapterCallback.onNewHierarchyListMethodCallback(position);
            }
        });

    }

    public static interface NewHierarchyListAdapterCallback {
        void onNewHierarchyListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return hierarchyList.size();
    }
}