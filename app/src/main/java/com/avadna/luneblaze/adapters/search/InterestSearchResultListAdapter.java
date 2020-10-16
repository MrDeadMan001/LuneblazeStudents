package com.avadna.luneblaze.adapters.search;

import android.app.Activity;
import android.content.Intent;
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
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSearch.PojoInterestSession;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class InterestSearchResultListAdapter extends RecyclerView.Adapter<InterestSearchResultListAdapter.MyViewHolder> {
    List<PojoInterestSession> interestList;
    Activity activity;
    int type;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    InterestSearchResultAdapterCallback interestSearchResultAdapterCallback;

    public InterestSearchResultListAdapter(Fragment fragment, List<PojoInterestSession> interestList) {
        try {
            this.interestSearchResultAdapterCallback = ((InterestSearchResultAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement InterestSearchResultAdapterCallback.");
        }

        this.activity = fragment.getActivity();
        this.interestList = interestList;
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
        TextView tv_interest_name;
        TextView tv_interest_type;
        TextView tv_num_followers;
        TextView tv_add_friend;
        RelativeLayout rl_info_wrapper;
        RelativeLayout rl_parent;

        RelativeLayout rl_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            tv_interest_type = (TextView) view.findViewById(R.id.tv_interest_type);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);

            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);

        }
    }

    @Override
    public InterestSearchResultListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_search_list_item, parent, false);

        return new InterestSearchResultListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestSearchResultListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == interestList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }
            if (showNoMoreResults) {
                if (interestList.isEmpty()) {
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
            final PojoInterestSession currentItem = interestList.get(position);
            holder.tv_add_friend.setVisibility(View.VISIBLE);

            if (!currentItem.image.isEmpty()) {
                if(activity!=null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.image)
                        .apply(new RequestOptions().override(128, 128))
                        .into(holder.iv_dp);
            }

            holder.tv_interest_name.setText(currentItem.text);
            //   holder.tv_place_and_designation.setText(currentItem.description);
            if (currentItem.followersCount == 0) {
                holder.tv_num_followers.setVisibility(View.GONE);
            } else {
                holder.tv_num_followers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_followers.setText("" + currentItem.followersCount + " " + activity.getString(R.string.followers));

            if(currentItem.parent!=null){
                holder.tv_interest_type.setText(currentItem.parent);
            }

            if (currentItem.iFollow == 1) {
                holder.tv_add_friend.setText(activity.getString(R.string.following));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_interest_follow_filled,0,0,0);
            } else {
                holder.tv_add_friend.setText(activity.getString(R.string.follow));
                holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_interest_follow_unfilled,0,0,0);
            }

            holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestSearchResultAdapterCallback.interestSearchClickMethod(position,
                            currentItem,AppKeys.INTEREST_FOLLOW);

                    holder.tv_add_friend.setVisibility(View.INVISIBLE);
                }
            });

            holder.rl_info_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interestIntent = new Intent(activity, InterestActivity.class);
                    interestIntent.putExtra("id", currentItem.interestId);
                    activity.startActivity(interestIntent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return interestList.size()+1;
        //  return hierarchyList.size();
    }

    public static interface InterestSearchResultAdapterCallback {
        public void interestSearchClickMethod(int position, PojoInterestSession pojoInterestSession,String type);
    }

}

