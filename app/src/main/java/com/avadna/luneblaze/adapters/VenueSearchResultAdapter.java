package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.location.Location;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sunny on 18-02-2018.
 */

public class VenueSearchResultAdapter extends RecyclerView.Adapter<VenueSearchResultAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    private int listMaxSize = 6;
    private List<PojoGetVenueListResponseData> dataSet;
    private VenueSearchResultAdapterCallback venueSearchResultAdapterCallback;
    MyLocationObject location;
    PreferenceUtils preferenceUtils;

    Activity activity;
    Fragment fragment;


    public VenueSearchResultAdapter(Activity activity, List<PojoGetVenueListResponseData> dataSet) {
        try {
            this.venueSearchResultAdapterCallback = ((VenueSearchResultAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.activity = activity;
        preferenceUtils = new PreferenceUtils(activity);
        location = preferenceUtils.getUserCurrentLocation();

        //  this.hierarchyList = hierarchyList;
    }

    public VenueSearchResultAdapter(Fragment fragment, List<PojoGetVenueListResponseData> dataSet) {
        try {
            this.venueSearchResultAdapterCallback = ((VenueSearchResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestListAdapterCallback.");
        }

        this.dataSet = dataSet;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        preferenceUtils = new PreferenceUtils(activity);
        location = preferenceUtils.getUserCurrentLocation();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_venue_name;
        TextView tv_distance;
        ImageView iv_cover_photo;
        RelativeLayout rl_venue_wrapper;

        MyViewHolder(View view) {
            super(view);
            tv_venue_name = (TextView) view.findViewById(R.id.tv_venue_name);
            tv_distance = (TextView) view.findViewById(R.id.tv_location);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
            rl_venue_wrapper = (RelativeLayout) view.findViewById(R.id.rl_venue_wrapper);

        }
    }

    @Override
    public VenueSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_search_result_item, parent, false);

        return new VenueSearchResultAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenueSearchResultAdapter.MyViewHolder holder, final int position) {
        PojoGetVenueListResponseData currentItem = dataSet.get(position);

      /*  if (currentItem.distance != null) {
            holder.tv_distance.setText(dataSet.get(position).distance);
        } else*/

        if (location != null && currentItem.latitude != null && !currentItem.latitude.isEmpty()
                && currentItem.longitude != null && !currentItem.longitude.isEmpty()) {
            holder.tv_distance.setVisibility(View.VISIBLE);

            float[] distances = new float[5];

            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    Double.parseDouble(currentItem.latitude), Double.parseDouble(currentItem.longitude), distances);
            float dist = distances[0];

            if (dist < 1000f) {
                holder.tv_distance.setText(((int) dist) + " m");
            } else {
                dist = dist / 1000f;
                DecimalFormat form = new DecimalFormat("0.00");
                holder.tv_distance.setText("" + form.format(dist) + " Km");
            }

        } else {
            holder.tv_distance.setVisibility(View.GONE);
        }

        holder.tv_venue_name.setText(dataSet.get(position).venueName);

        if (canIAttend(position)) {
            holder.rl_venue_wrapper.setAlpha(1f);
            holder.rl_venue_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    venueSearchResultAdapterCallback.venueResultItemClickCallback
                            (position, dataSet.get(position));
                }
            });
        } else {
            holder.rl_venue_wrapper.setAlpha(0.5f);
            holder.rl_venue_wrapper.setOnClickListener(null);
        }

        if (currentItem.coverPhoto != null && !currentItem.coverPhoto.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext())
                        .load(currentItem.coverPhoto)
                        .apply(new RequestOptions().override(100, 80))
                        .into(holder.iv_cover_photo);
        }
    }

    public static interface VenueSearchResultAdapterCallback {
        void venueResultItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData);
    }


    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else {
            return (listMaxSize > listSize) ? listSize : listMaxSize;
        }
    }

    private boolean canIAttend(int venuePosition) {
        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        String venueType = "1";

        if (dataSet.get(venuePosition).venueType != null) {
            venueType = dataSet.get(venuePosition).venueType;
        }

        String venueId = "0";
        if (dataSet.get(venuePosition).venueId != null) {
            venueId = dataSet.get(venuePosition).venueId;
        }
        if (venueType.equals("1")) {
            return true;
        } else if (venueType.equals("2") && pojoLoginResponseData.oraganisationId.equals(venueId)) {
            return true;
        } else {
            return false;
        }
    }
}


