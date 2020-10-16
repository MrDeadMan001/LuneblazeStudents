package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;

import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class InterestSearchResultAdapter extends RecyclerView.Adapter<InterestSearchResultAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    private int listMaxSize = 6;
    private List<PojoSearchInterestWithTextResponseData> dataSet;
    private InterestResultAdapterCallback interestResultAdapterCallback;

    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name;



        MyViewHolder(View view) {
            super(view);
            tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);


        }
    }


    public InterestSearchResultAdapter(Activity activity, List<PojoSearchInterestWithTextResponseData> dataSet) {
        try {
            this.interestResultAdapterCallback = ((InterestSearchResultAdapter.InterestResultAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.activity = activity;


        //  this.hierarchyList = hierarchyList;
    }

    public InterestSearchResultAdapter(Fragment fragment, List<PojoSearchInterestWithTextResponseData> dataSet) {
        try {
            this.interestResultAdapterCallback = ((InterestSearchResultAdapter.InterestResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.activity = fragment.getActivity();


        //  this.hierarchyList = hierarchyList;
    }

 /*   public InterestSearchResultAdapter(CreateArticleFragment fragment, List<PojoSearchInterestWithTextResponseData> dataSet) {
        try {
            this.interestResultAdapterCallback = ((InterestSearchResultAdapter.InterestResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.fragment = fragment;


        //  this.hierarchyList = hierarchyList;
    }*/

    public InterestSearchResultAdapter(int listSize) {
        this.listSize = listSize;
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public InterestSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_search_result_item, parent, false);

        return new InterestSearchResultAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestSearchResultAdapter.MyViewHolder holder, final int position) {
        holder.tv_item_name.setText(dataSet.get(position).text);
        holder.tv_item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestResultAdapterCallback.interestResultItemClickCallback(position,dataSet.get(position));
            }
        });
    }

    public static interface InterestResultAdapterCallback {
        void interestResultItemClickCallback(int position,PojoSearchInterestWithTextResponseData pojoSearchInterestWithTextResponseData);
    }


    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else {
            return (listMaxSize > listSize) ? listSize : listMaxSize;
        }
        //  return hierarchyList.size();
    }
}


