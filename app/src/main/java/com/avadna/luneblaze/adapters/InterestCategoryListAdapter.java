package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sunny on 20-Oct-17.
 */

public class InterestCategoryListAdapter extends RecyclerView.Adapter<InterestCategoryListAdapter.MyViewHolder> {
    List<PojoGetInterestListResponseDataListItem> dataSet = new ArrayList<PojoGetInterestListResponseDataListItem>();
    HashMap<String, PojoGetInterestListResponseDataListItem> checkedInterestMap;
    private InterestListAdapterCallback mInterestListAdapterCallback;
    Activity activity;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_interest_name;
        CheckBox cb_selected;
        ImageView iv_expand;

        MyViewHolder(View view) {
            super(view);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
            iv_expand = (ImageView) view.findViewById(R.id.iv_expand);
        }

    }

    public InterestCategoryListAdapter(Activity activity, List<PojoGetInterestListResponseDataListItem> dataSet,
                                       HashMap<String, PojoGetInterestListResponseDataListItem> checkedInterestMap) {
        try {
            this.mInterestListAdapterCallback = ((InterestListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.activity = activity;
        this.dataSet = dataSet;
        this.checkedInterestMap = checkedInterestMap;

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public InterestCategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_list_item, parent, false);

        return new InterestCategoryListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestCategoryListAdapter.MyViewHolder holder, final int position) {
        holder.tv_interest_name.setText(dataSet.get(position).text);

        //check if the interest is present in selected interest list


        if (checkedInterestMap.containsKey(dataSet.get(position).interestId)) {
            dataSet.get(position).isCbChecked = true;
        } else {
            dataSet.get(position).isCbChecked = false;
        }


        holder.cb_selected.setChecked(dataSet.get(position).isCbChecked);

        if (dataSet.get(position).hasChildren==null||dataSet.get(position).hasChildren.equals("No")) {
            holder.iv_expand.setVisibility(View.GONE);
        } else {
            holder.iv_expand.setVisibility(View.VISIBLE);
        }

        holder.cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.cb_selected.isChecked()) {
                    mInterestListAdapterCallback.onInterestListMethodCallback(position, "cb", true);
                    dataSet.get(position).isCbChecked = true;
                } else {
                    mInterestListAdapterCallback.onInterestListMethodCallback(position, "cb", false);
                    dataSet.get(position).isCbChecked = false;
                }
            }
        });

        holder.cb_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.iv_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterestListAdapterCallback.onInterestListMethodCallback(position, "iv", false);
            }
        });

        holder.tv_interest_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_selected.toggle();
            }
        });
    }

    public static interface InterestListAdapterCallback {
        void onInterestListMethodCallback(int position, String type, Boolean cbChecked);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}
