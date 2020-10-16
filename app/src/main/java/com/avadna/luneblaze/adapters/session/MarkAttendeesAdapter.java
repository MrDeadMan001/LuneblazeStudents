package com.avadna.luneblaze.adapters.session;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MarkAttendeesAdapter extends RecyclerView.Adapter<MarkAttendeesAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoUserData> userList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    MarkAttendeesAdapterCallback markAttendeesAdapterCallback;

    public MarkAttendeesAdapter(Fragment fragment, List<PojoUserData> userList) {
        try {
            this.markAttendeesAdapterCallback = ((MarkAttendeesAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement MarkAttendeesAdapterCallback.");
        }

        this.activity = fragment.getActivity();
        this.userList = userList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public MarkAttendeesAdapter(Activity activity, List<PojoUserData> userList) {
        try {
            this.markAttendeesAdapterCallback = ((MarkAttendeesAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement MarkAttendeesAdapterCallback.");
        }

        this.activity = activity;
        this.userList = userList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
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
        ImageButton ib_marker;
        TextView tv_place_and_designation;
        ProgressBar pb_action_status;
        RelativeLayout rl_info_wrapper;
        RelativeLayout rl_parent;

        RelativeLayout rl_content_wrapper;

        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            ib_marker = (ImageButton) view.findViewById(R.id.ib_marker);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }


    @Override
    public MarkAttendeesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mark_attendee_list_item, parent, false);

        return new MarkAttendeesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MarkAttendeesAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (position == userList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (userList.isEmpty()) {
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

            final PojoUserData currentItem = userList.get(position);
            holder.pb_action_status.setVisibility(View.GONE);
            if (activity != null) {
                if (!currentItem.userPicture.isEmpty()) {
                    Glide.with(activity.getApplicationContext())
                            .load(currentItem.userPicture)
                            .apply(new RequestOptions().override(128, 128))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(holder.iv_dp);
                } else {
                    Glide.with(activity.getApplicationContext())
                            .load(R.drawable.placeholder)
                            .apply(new RequestOptions().override(128, 128))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(holder.iv_dp);
                }
            }

            holder.tv_name.setText(currentItem.userFullname);
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });

            holder.iv_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.tv_name.callOnClick();
                }
            });

            String placeAndDesignation = "";
            //if both fields are in data list then show both

            if ((!currentItem.userWorkTitle.isEmpty()) && (!currentItem.userWorkPlace.isEmpty())) {
                placeAndDesignation = currentItem.userWorkTitle + " (" + currentItem.userWorkPlace + ")";
            }
            //if work title not available show only work place
            else if (currentItem.userWorkTitle.isEmpty()) {
                placeAndDesignation = currentItem.userWorkPlace;
            }
            //if work place not available show only work title
            else if (currentItem.userWorkPlace.isEmpty()) {
                placeAndDesignation = currentItem.userWorkTitle;
            }
            //if none is present then remove visibility of the field
            else if (currentItem.userWorkTitle.isEmpty() && currentItem.userWorkPlace.isEmpty()) {
                holder.tv_place_and_designation.setVisibility(View.GONE);
            } else {
                holder.tv_place_and_designation.setVisibility(View.VISIBLE);
            }

            holder.tv_place_and_designation.setText(placeAndDesignation);


          /*  if (false) {
                holder.ib_marker.setBackgroundResource(R.drawable.light_grey_rounded);

            } else {
                holder.ib_marker.setBackgroundResource(R.drawable.med_blue_rounded_bg);
            }*/


            holder.ib_marker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markAttendeesAdapterCallback.onMarkAttendeesMethod(position, "absent");
                }
            });

          /*  if (position == (userList.size() - 1)) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            } */
        }

    }

    @Override
    public int getItemCount() {
        return userList.size() + 1;
        //  return hierarchyList.size();
    }

    public static interface MarkAttendeesAdapterCallback {
        void onMarkAttendeesMethod(int position, String type);
    }

}