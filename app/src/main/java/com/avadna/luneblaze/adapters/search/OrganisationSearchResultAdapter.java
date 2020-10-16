package com.avadna.luneblaze.adapters.search;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyLocationObject;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchOrganisation;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchVenue;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class OrganisationSearchResultAdapter extends RecyclerView.Adapter<OrganisationSearchResultAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoSearchOrganisation> organisationList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    MyLocationObject userLocation;

    OrganisationSearchResultAdapterCallback organisationSearchResultAdapterCallback;

    public OrganisationSearchResultAdapter(Fragment fragment, List<PojoSearchOrganisation> organisationList) {
        try {
            this.organisationSearchResultAdapterCallback = ((OrganisationSearchResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement OrganisationSearchResultAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.organisationList = organisationList;
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
        TextView tv_company_type;
        TextView tv_about;
        TextView tv_follow;
        TextView tv_city;
        LinearLayout ll_data_wrapper;

        RelativeLayout rl_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_dp);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_company_type = (TextView) view.findViewById(R.id.tv_company_type);
            tv_about = (TextView) view.findViewById(R.id.tv_about);
            tv_follow = (TextView) view.findViewById(R.id.tv_follow);
            tv_city = (TextView) view.findViewById(R.id.tv_city);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }


    @Override
    public OrganisationSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organisation_search_list_item, parent, false);

        return new OrganisationSearchResultAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrganisationSearchResultAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == organisationList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (organisationList.isEmpty()) {
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
            final PojoSearchOrganisation currentItem = organisationList.get(position);
            holder.tv_follow.setVisibility(View.VISIBLE);

            if (!currentItem.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.logo)
                            .apply(new RequestOptions().override(160, 160))
                            .into(holder.iv_dp);
            }

            holder.tv_name.setText(currentItem.name);
            holder.tv_company_type.setText(currentItem.type);
            holder.tv_about.setText(currentItem.description);
            holder.tv_about.setVisibility(View.GONE);
            holder.tv_city.setText(currentItem.city);

            if (currentItem.isFollowing == 1) {
                holder.tv_follow.setText(activity.getString(R.string.following));
                holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                        0, 0, 0);

            } else {
                holder.tv_follow.setText(activity.getString(R.string.follow));
                holder.tv_follow.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                        0, 0, 0);
            }

            holder.tv_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    organisationSearchResultAdapterCallback.organisationSearchClickMethod(position,
                            currentItem, AppKeys.VENUE_FOLLOW);
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
                    Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                    organisationIntent.putExtra(AppKeys.OWNER_ID, currentItem.userId);
                    organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.id);
                    activity.startActivity(organisationIntent);
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
        }
    }

    @Override
    public int getItemCount() {
        return organisationList.size() + 1;
        //  return hierarchyList.size();
    }

    public static interface OrganisationSearchResultAdapterCallback {
        public void organisationSearchClickMethod(int position, PojoSearchOrganisation pojoSearchOrganisation,
                                                  String type);
    }


}
