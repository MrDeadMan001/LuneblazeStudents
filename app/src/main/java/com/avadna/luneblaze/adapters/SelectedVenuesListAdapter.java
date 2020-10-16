package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;

import java.util.List;

public class SelectedVenuesListAdapter extends RecyclerView.Adapter<SelectedVenuesListAdapter.MyViewHolder> {

    List<PojoGetVenueListResponseData> venueList;
    int listsize = 0;
    Activity activity;
    Fragment fragment;
    SelectedVenuesListAdapterCallback selectedVenuesListAdapterCallback;
    int type;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_interest_tag_name;

        MyViewHolder(View view) {
            super(view);

            tv_interest_tag_name = (TextView) view.findViewById(R.id.tv_interest_tag_name);

        }
    }


    public SelectedVenuesListAdapter(Fragment fragment, List<PojoGetVenueListResponseData> venueList) {
        try {
            this.selectedVenuesListAdapterCallback = ((SelectedVenuesListAdapter.SelectedVenuesListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement taggedVenuetListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.venueList = venueList;
    }

    public SelectedVenuesListAdapter(Activity activity, List<PojoGetVenueListResponseData> venueList) {
        try {
            this.selectedVenuesListAdapterCallback = ((SelectedVenuesListAdapter.SelectedVenuesListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement taggedVenueListAdapterCallback.");
        }
        this.activity = activity;
        this.venueList = venueList;
    }


    @Override
    public SelectedVenuesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_tag_list_item, parent, false);

        return new SelectedVenuesListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectedVenuesListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
            holder.tv_interest_tag_name.setText(venueList.get(position).venueName);

            holder.tv_interest_tag_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedVenuesListAdapterCallback.selectedVenuesItemClickCallback(position,
                            venueList.get(position));
                }
            });

    }

    public static interface SelectedVenuesListAdapterCallback {
        void selectedVenuesItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData);
    }


    @Override
    public int getItemCount() {
        return venueList.size();

        //  return hierarchyList.size();
    }
}
