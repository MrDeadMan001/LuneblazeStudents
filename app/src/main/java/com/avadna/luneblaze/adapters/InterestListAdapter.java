package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestListAdapter extends RecyclerView.Adapter<InterestListAdapter.MyViewHolder> {
    List<PojoGetInterestListResponseDataListItem> receivedInterestList;
    private InterestListAdapter.InterestListAdapterCallback mInterestListAdapterCallback;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;


    public InterestListAdapter(Activity activity, List<PojoGetInterestListResponseDataListItem> receivedInterestList) {

        this.activity = activity;
        this.receivedInterestList = receivedInterestList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils=new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id=preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_add_friend;
        TextView tv_interest_name;
        LinearLayout ll_content_wrapper;
        ProgressBar pb_action_status;
        TextView tv_interest_type;
        TextView tv_num_followers;
        TextView tv_no_more_results;
        ProgressBar pb_loading_more;

        RelativeLayout rl_info_wrapper;

        MyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            tv_interest_type = (TextView) view.findViewById(R.id.tv_interest_type);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);
            pb_action_status.setVisibility(View.GONE);

            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
            tv_no_more_results.setVisibility(View.GONE);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);

        }

    }


    @Override
    public InterestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggested_follow_list_item, parent, false);

        return new InterestListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestListAdapter.MyViewHolder holder, final int position) {
        final PojoGetInterestListResponseDataListItem currentItem = receivedInterestList.get(position);
        holder.pb_action_status.setVisibility(View.GONE);
        holder.tv_add_friend.setVisibility(View.VISIBLE);
        if (!currentItem.image.isEmpty()) {
            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.image)
                    .into(holder.iv_user_dp);
        }

        holder.tv_interest_name.setText(currentItem.text);
        //check if the interest is present in selected interest list
        if (currentItem.iFollowRelated == 1) {
            holder.tv_add_friend.setText(activity.getString(R.string.following));
            holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                    0,0,0);
            holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));

        } else {
            holder.tv_add_friend.setText(activity.getString(R.string.follow));
            holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                    0,0,0);
            holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));

        }

        holder.rl_info_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent = new Intent(activity, InterestActivity.class);
                interestIntent.putExtra("id", currentItem.interestId);
                activity.startActivity(interestIntent);
            }
        });
        if(currentItem.noOfFollowers==0){
            holder.tv_num_followers.setVisibility(View.GONE);
        }else {
            holder.tv_num_followers.setVisibility(View.VISIBLE);
        }
        holder.tv_num_followers.setText(currentItem.noOfFollowers + " " + activity.getString(R.string.followers));
        holder.tv_interest_type.setVisibility(View.GONE);
        holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem.iFollowRelated==0){
                    hitFollowInterestApi(user_id,currentItem.interestId,"interest_follow",position);
                    holder.tv_add_friend.setText(activity.getString(R.string.following));
                    holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                            0,0,0);
                    holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                    currentItem.iFollowRelated=1;
                }
                else {
                    hitFollowInterestApi(user_id,currentItem.interestId,"interest_unfollow",position);
                    holder.tv_add_friend.setText(activity.getString(R.string.follow));
                    holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                            0,0,0);
                    holder.tv_add_friend.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
                    currentItem.iFollowRelated=0;
                }

            }
        });

    }

    public static interface InterestListAdapterCallback {
        void onInterestListMethodCallback(int position, String type, Boolean cbChecked);
    }

    @Override
    public int getItemCount() {
        return receivedInterestList.size();
        //  return hierarchyList.size();
    }

    public void hitFollowInterestApi(String user_id, String friend_id, String connection_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(activity, message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                notifyDataSetChanged();
            }
        });

    }
}

