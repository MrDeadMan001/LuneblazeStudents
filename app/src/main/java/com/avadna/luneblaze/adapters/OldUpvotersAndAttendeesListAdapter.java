package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class OldUpvotersAndAttendeesListAdapter extends RecyclerView.Adapter<OldUpvotersAndAttendeesListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoUserData> userList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;


    public OldUpvotersAndAttendeesListAdapter(Fragment fragment, List<PojoUserData> userList) {


        this.activity = fragment.getActivity();
        this.userList = userList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public OldUpvotersAndAttendeesListAdapter(Activity activity, List<PojoUserData> userList) {


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
        TextView tv_place_and_designation;
        TextView tv_current_city;
        TextView tv_num_mutual_friends;
        TextView tv_add_friend;
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
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_current_city = (TextView) view.findViewById(R.id.tv_current_city);
            tv_num_mutual_friends = (TextView) view.findViewById(R.id.tv_num_mutual_friends);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }


    @Override
    public OldUpvotersAndAttendeesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_search_list_item, parent, false);

        return new OldUpvotersAndAttendeesListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OldUpvotersAndAttendeesListAdapter.MyViewHolder holder, final int position) {
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

            if (currentItem.userCurrentCity == null || currentItem.userCurrentCity.isEmpty()) {
                holder.tv_current_city.setVisibility(View.GONE);
            } else {
                holder.tv_current_city.setVisibility(View.VISIBLE);
                holder.tv_current_city.setText(currentItem.userCurrentCity);
            }

            if (currentItem.mutualFriendsCount == null || currentItem.mutualFriendsCount.equals("0")) {
                holder.tv_num_mutual_friends.setVisibility(View.GONE);
            } else {
                holder.tv_num_mutual_friends.setVisibility(View.VISIBLE);
                holder.tv_num_mutual_friends.setText("" + currentItem.mutualFriendsCount + " " + activity.getString(R.string.mutual_friends));
                //  holder.tv_num_mutual_friends.setText(currentItem.userPhone);
            }

            if (currentItem.connection.equals("add") || currentItem.connection.equals("remove")) {
                holder.tv_add_friend.setText(activity.getString(R.string.follow));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow,
                        0, 0, 0);
                holder.tv_add_friend.setVisibility(View.VISIBLE);

            } else if (currentItem.connection.equals("followed")) {
                holder.tv_add_friend.setText(activity.getString(R.string.following));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_invite_filled,
                        0, 0, 0);
                holder.tv_add_friend.setVisibility(View.VISIBLE);
            } else {
                holder.tv_add_friend.setVisibility(View.GONE);
            }

            if (currentItem.userId.equals(user_id)) {
                holder.tv_add_friend.setVisibility(View.GONE);
            } else {
                holder.tv_add_friend.setVisibility(View.VISIBLE);

            }

            holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentItem.connection.equals("add")
                            || currentItem.connection.equals("remove")) {
                        hitUpdateRequestStatusApi(user_id, currentItem.userId, "follow", position);
                        currentItem.connection = "followed";

                    } else if (currentItem.connection.equals("followed")) {
                        hitUpdateRequestStatusApi(user_id, currentItem.userId, "unfollow", position);
                        currentItem.connection = "add";
                    }
                    notifyDataSetChanged();
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

    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (activity != null)
                        commonFunctions.setToastMessage(activity.getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (activity != null)
                    commonFunctions.setToastMessage(activity.getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size() + 1;
        //  return hierarchyList.size();
    }


}
