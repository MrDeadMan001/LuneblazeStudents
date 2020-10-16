package com.avadna.luneblaze.adapters.search;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.activities.search.SearchHomeActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class TagUserListAdapter extends RecyclerView.Adapter<TagUserListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoUserData> userList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    String type;
    boolean showNoResultsFound = false;
    boolean showSeeResults=false;

    public void setShowNoResultsFound(boolean status) {
        showNoResultsFound = status;
        notifyDataSetChanged();
    }

    public void setSeeResults(boolean status){
        showSeeResults = status;
        notifyDataSetChanged();
    }

    public TagUserListAdapter(Activity activity, List<PojoUserData> userList, String type) {
        this.activity = activity;
        this.userList = userList;
        this.type = type;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name;
        ImageView iv_user_dp;
        TextView tv_designation;
        TextView tv_see_more_results;
        RelativeLayout rl_info_wrapper;


        MyViewHolder(View view) {
            super(view);
            tv_user_name = view.findViewById(R.id.tv_user_name);
            tv_see_more_results = view.findViewById(R.id.tv_see_more_results);
            iv_user_dp = view.findViewById(R.id.iv_user_dp);
            tv_designation = view.findViewById(R.id.tv_designation);
            rl_info_wrapper = view.findViewById(R.id.rl_info_wrapper);
        }
    }

    @Override
    public TagUserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggested_user_tag_item, parent, false);

        return new TagUserListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TagUserListAdapter.MyViewHolder holder, final int position) {

        if (position == 0) {
            holder.rl_info_wrapper.setVisibility(View.GONE);
            if (showNoResultsFound) {
                holder.tv_see_more_results.setVisibility(View.VISIBLE);
                holder.tv_see_more_results.setText(activity.getString(R.string.no_results_found));
                holder.tv_see_more_results.setTextColor(ContextCompat.getColor(activity, R.color.dark_grey));
            } else {
                holder.tv_see_more_results.setVisibility(View.GONE);
            }
        } else if (position == userList.size() + 1) {
            if (!userList.isEmpty()||showNoResultsFound) {
                holder.rl_info_wrapper.setVisibility(View.GONE);
                holder.tv_see_more_results.setVisibility(View.VISIBLE);
                holder.tv_see_more_results.setText(activity.getString(R.string.see_results_in_all_categories));
                holder.tv_see_more_results.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_dark));

                holder.tv_see_more_results.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent searchIntent = new Intent(activity, SearchActivity.class);
                        searchIntent.putExtra("query", ((SearchHomeActivity) activity).getCurrentSearchQuery());
                        searchIntent.putExtra("tab_type", AppKeys.USER);
                        activity.startActivity(searchIntent);
                        activity.finish();
                    }
                });
            } else {
                holder.rl_info_wrapper.setVisibility(View.GONE);
                holder.tv_see_more_results.setVisibility(View.GONE);
            }
        } else {
            holder.rl_info_wrapper.setVisibility(View.VISIBLE);
            holder.tv_see_more_results.setVisibility(View.GONE);
            final PojoUserData currentItem = userList.get(position - 1);

            if (position == 1) {
                Resources r = activity.getResources();
                int DP8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
                holder.rl_info_wrapper.setPadding(0, DP8, 0, 0);


            } else {
                holder.rl_info_wrapper.setPadding(0, 0, 0, 0);
            }

            holder.tv_user_name.setText(currentItem.userFullname);
            holder.tv_designation.setVisibility(View.VISIBLE);
            holder.tv_designation.setText(currentItem.userWorkTitle);

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(holder.iv_user_dp);

            holder.rl_info_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (type.equals("search")) {
            return userList.size()+2;

        } else {
            return userList.size();

        }
        //  return hierarchyList.size();
    }


}
