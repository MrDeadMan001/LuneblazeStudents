package com.avadna.luneblaze.adapters.interestHierarchy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class InterestSearchResultAdapter extends RecyclerView.Adapter<InterestSearchResultAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    private int listMaxSize = 6;
    private List<PojoGetInterestListResponseDataListItem> dataSet;
    private NewInterestResultAdapterCallback interestResultAdapterCallback;

    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name;


        MyViewHolder(View view) {
            super(view);
            tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);


        }
    }


    public InterestSearchResultAdapter(Activity activity, List<PojoGetInterestListResponseDataListItem> dataSet) {
        try {
            this.interestResultAdapterCallback = ((NewInterestResultAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.activity = activity;


        //  this.hierarchyList = hierarchyList;
    }
    

 /*   public InterestSearchResultAdapter(CreateArticleFragment fragment, List<PojoAllInterestListItem> dataSet) {
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
        if (dataSet.get(position) != null&&dataSet.get(position).text!=null) {

            holder.tv_item_name.setText(dataSet.get(position).text);
            holder.tv_item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestResultAdapterCallback.interestResultItemClickCallback(position, dataSet.get(position));
                }
            });
        }
    }

    public static interface NewInterestResultAdapterCallback {
        void interestResultItemClickCallback(int position, PojoGetInterestListResponseDataListItem PojoAllInterestListItem);
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


