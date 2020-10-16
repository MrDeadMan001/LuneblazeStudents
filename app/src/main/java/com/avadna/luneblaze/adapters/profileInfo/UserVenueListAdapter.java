package com.avadna.luneblaze.adapters.profileInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserVenue;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserVenueListAdapter extends RecyclerView.Adapter<UserVenueListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoUserVenue> venueList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    MyLocationObject location;


    public UserVenueListAdapter(Activity activity, List<PojoUserVenue> venueList) {
        this.activity = activity;
        this.venueList = venueList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        location = preferenceUtils.getUserCurrentLocation();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_name;
        TextView tv_distance;
        TextView tv_num_followers;
        TextView tv_follow;
        RelativeLayout ll_parent;
        LinearLayout ll_data_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_dp);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_follow = (TextView) view.findViewById(R.id.tv_follow);
            ll_parent = (RelativeLayout) view.findViewById(R.id.ll_parent);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);

            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
            tv_no_more_results.setVisibility(View.GONE);
        }
    }


    @Override
    public UserVenueListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_global_search_list_item, parent, false);

        return new UserVenueListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserVenueListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoUserVenue currentItem = venueList.get(position);
        holder.tv_follow.setVisibility(View.VISIBLE);

        if (!currentItem.coverPhoto.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.coverPhoto)
                        .apply(new RequestOptions().override(200, 160))
                        .into(holder.iv_dp);
        }
        holder.tv_name.setText(Html.fromHtml(currentItem.venueName));

        //   holder.tv_num_followers.setText("" + currentItem.followersCount + " " + activity.getString(R.string.followers));

        if (currentItem.iLike) {
            holder.tv_follow.setText(activity.getString(R.string.following));
            holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed, 0, 0, 0);
            holder.tv_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitFollowVenueApi(user_id, currentItem.venueId, "remove_notify_me_venue", position);
                    currentItem.iLike = false;
                    currentItem.followersCount--;
                    holder.tv_follow.setText(activity.getString(R.string.follow));
                    holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                    holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                            0, 0, 0);
                }
            });
        } else {
            holder.tv_follow.setText(activity.getString(R.string.follow));
            holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed, 0, 0, 0);
            holder.tv_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitFollowVenueApi(user_id, currentItem.venueId, "notify_me_venue", position);
                    currentItem.iLike = true;
                    currentItem.followersCount++;
                    holder.tv_follow.setText(activity.getString(R.string.following));
                    holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                    holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                            0, 0, 0);
                }
            });
        }
        if (currentItem.followersCount == 0) {
            holder.tv_num_followers.setVisibility(View.GONE);
        } else {
            holder.tv_num_followers.setVisibility(View.VISIBLE);
        }
        holder.tv_num_followers.setText(currentItem.followersCount + " " + activity.getString(R.string.followers));

        if (position == (venueList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.ll_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.ll_parent.setPadding(0, 0, 0, 0);
        }

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


        if (location != null && currentItem.latitude != null && !currentItem.latitude.isEmpty()
                && currentItem.longitude != null && !currentItem.longitude.isEmpty()) {
            holder.tv_distance.setVisibility(View.VISIBLE);

            float[] distances = new float[5];
            Location.distanceBetween(location.latitude, location.longitude,
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
    }

    @Override
    public int getItemCount() {
        return venueList.size();
        //  return hierarchyList.size();
    }


    private void hitFollowVenueApi(String user_id, String venue_id, String connection_type, final int position) {
        {
            Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, venue_id, connection_type);
            call.enqueue(new Callback<PojoUserConnectResponse>() {
                @Override
                public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        notifyDataSetChanged();
                        commonFunctions.setToastMessage(activity, message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }

                @Override
                public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    notifyDataSetChanged();
                }
            });


        }
    }


}