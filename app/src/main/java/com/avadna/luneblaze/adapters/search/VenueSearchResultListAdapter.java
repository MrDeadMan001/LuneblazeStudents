package com.avadna.luneblaze.adapters.search;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchVenue;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

public class VenueSearchResultListAdapter extends RecyclerView.Adapter<VenueSearchResultListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoSearchVenue> venueList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    MyLocationObject userLocation;

    VenueSearchResultAdapterCallback venueSearchResultAdapterCallback;

    public VenueSearchResultListAdapter(Fragment fragment, List<PojoSearchVenue> venueList) {
         try {
            this.venueSearchResultAdapterCallback = ((VenueSearchResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement VenueSearchResultAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.venueList = venueList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        userLocation = preferenceUtils.getUserCurrentLocation();
    }

    private boolean showProgressBar = false;
    private boolean showNoMoreResults = false;

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMoreResults(boolean status) {
        showNoMoreResults = status;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_name;
        TextView tv_distance;
        TextView tv_num_followers;
        TextView tv_follow;
        LinearLayout ll_data_wrapper;

        RelativeLayout rl_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_dp);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_follow = (TextView) view.findViewById(R.id.tv_follow);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }


    @Override
    public VenueSearchResultListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_global_search_list_item, parent, false);

        return new VenueSearchResultListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenueSearchResultListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == venueList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (venueList.isEmpty()) {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_results_found));
                } else {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_more_results_found));
                }
                holder.tv_no_more_results.setVisibility(View.VISIBLE);
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        } else {
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);
            final PojoSearchVenue currentItem = venueList.get(position);
            holder.tv_follow.setVisibility(View.VISIBLE);

            if (!currentItem.coverPhoto.isEmpty()) {
                if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.coverPhoto)
                        .apply(new RequestOptions().override(200, 160))
                        .into(holder.iv_dp);
            }

            holder.tv_name.setText(currentItem.venueName);

            if (currentItem.followersCount == 0) {
                holder.tv_num_followers.setVisibility(View.GONE);
            } else {
                holder.tv_num_followers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_followers.setText("" + currentItem.followersCount + " " + activity.getString(R.string.followers));

            if (currentItem.iFollow == 1) {
                holder.tv_follow.setText(activity.getString(R.string.following));
                holder.tv_follow.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                        0,0,0);

            } else {
                holder.tv_follow.setText(activity.getString(R.string.follow));
                holder.tv_follow.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
                holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                        0,0,0);
            }

            holder.tv_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    venueSearchResultAdapterCallback.venueSearchClickMethod(position,currentItem,AppKeys.VENUE_FOLLOW);
                }
            });

            holder.iv_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ll_data_wrapper.callOnClick();
                }
            });

            holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent venueIntent = new Intent(activity, VenueActivity.class);
                    venueIntent.putExtra("id", currentItem.venueId);
                    activity.startActivity(venueIntent);
                }
            });

      /*  Location startPoint=new Location("user");
        startPoint.setLatitude(userLocation.getLatitude());
        startPoint.setLongitude(userLocation.getLongitude());

        Location endPoint=new Location("venue");
        endPoint.setLatitude(Double.valueOf(currentItem.latitude));
        endPoint.setLongitude(Double.valueOf(currentItem.longitude));

        double distance=startPoint.distanceTo(endPoint);

        //convert to KiloMeters
        distance=distance/1000;

        Double truncatedDouble=BigDecimal.valueOf(distance)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();*/


            if (userLocation != null && currentItem.latitude != null&& !currentItem.latitude.isEmpty()
                    && currentItem.longitude != null&&!currentItem.longitude.isEmpty()) {
                holder.tv_distance.setVisibility(View.VISIBLE);

                float[] distances = new float[5];
                Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                        Double.parseDouble(currentItem.latitude), Double.parseDouble(currentItem.longitude), distances);
                float dist = distances[0];

                if(dist<1000f){
                    holder.tv_distance.setText(((int)dist) + " m");
                }
                else {
                    dist = dist / 1000f;
                    DecimalFormat form = new DecimalFormat("0.00");
                    holder.tv_distance.setText("" + form.format(dist) + " Km");
                }

            }
            else {
                holder.tv_distance.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return venueList.size()+1;
        //  return hierarchyList.size();
    }

    public static interface VenueSearchResultAdapterCallback {
        public void venueSearchClickMethod(int position, PojoSearchVenue pojoSearchVenue,String type);
    }




}

