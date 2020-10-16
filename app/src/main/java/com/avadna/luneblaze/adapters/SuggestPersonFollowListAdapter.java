package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
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

/**
 * Created by Sunny on 03-02-2018.
 */

public class SuggestPersonFollowListAdapter extends RecyclerView.Adapter<SuggestPersonFollowListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoUserData> receivedUserList;
    Fragment fragment;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    SuggestedPersonFollowCallback suggestedPersonFollowCallback;


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

    public SuggestPersonFollowListAdapter(Fragment fragment, List<PojoUserData> receivedUserList) {
        try {
            this.suggestedPersonFollowCallback = ((SuggestedPersonFollowCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement SuggestedPersonFollowCallback");
        }
        this.activity = fragment.getActivity();
        this.receivedUserList = receivedUserList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_interest_name;
        TextView tv_interest_type;
        TextView tv_mutual_friends;
        TextView tv_add_friend;
        ProgressBar pb_action_status;
        RelativeLayout rl_info_wrapper;

        RelativeLayout rl_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            tv_interest_type = (TextView) view.findViewById(R.id.tv_interest_type);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);

        }
    }


    @Override
    public SuggestPersonFollowListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggested_follow_list_item, parent, false);

        return new SuggestPersonFollowListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SuggestPersonFollowListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (position == receivedUserList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (receivedUserList.isEmpty()) {
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

            final PojoUserData currentItem = receivedUserList.get(position);
            holder.pb_action_status.setVisibility(View.GONE);
            holder.tv_add_friend.setVisibility(View.VISIBLE);

            if (!currentItem.userPicture.isEmpty()) {
                if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .placeholder(R.drawable.blank_profile_male)
                        .into(holder.iv_dp);
            }

            holder.tv_interest_name.setText(currentItem.userFullname);
            holder.tv_interest_type.setText(currentItem.userWorkTitle);

            if (currentItem.mutualFriendsCount.equals("0")) {
                holder.tv_mutual_friends.setVisibility(View.GONE);
            } else {
                holder.tv_mutual_friends.setVisibility(View.VISIBLE);
            }
            holder.tv_mutual_friends.setText("" + currentItem.mutualFriendsCount + " " + activity.getString(R.string.mutual_friends));

            if (currentItem.connection == null || currentItem.connection.equals("follow") || currentItem.connection.equals("add")) {
                holder.tv_add_friend.setText(activity.getString(R.string.follow));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow,
                        0, 0, 0);

            } else {
                holder.tv_add_friend.setText(activity.getString(R.string.following));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_friend_filled,
                        0, 0, 0);
            }


            holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    suggestedPersonFollowCallback.suggestedPersonMethod(position, AppKeys.USER_FOLLOW);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return receivedUserList.size()+1;
        //  return hierarchyList.size();
    }

    public static interface SuggestedPersonFollowCallback {
        public void suggestedPersonMethod(int position, String type);
    }

}

