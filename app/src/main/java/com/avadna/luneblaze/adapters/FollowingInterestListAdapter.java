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
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoRelatedInterestArr;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny on 03-02-2018.
 */

public class FollowingInterestListAdapter extends RecyclerView.Adapter<FollowingInterestListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoRelatedInterestArr> relatedInterestArr;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;


    public FollowingInterestListAdapter(int type,  List<PojoRelatedInterestArr> relatedInterestArr, Activity activity) {
        this.type = type;
        this.relatedInterestArr=relatedInterestArr;
        this.activity=activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils=new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id=preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_unfollow;
        TextView tv_interest_name;
        TextView tv_mutual_friends;
        LinearLayout ll_content_wrapper;
        ProgressBar pb_action_status;
        TextView tv_place_and_designation;

        MyViewHolder(View view) {
            super(view);
            iv_dp=(ImageView)view.findViewById(R.id.iv_user_dp);
            tv_unfollow = (TextView) view.findViewById(R.id.tv_unfollow);
            tv_interest_name= (TextView) view.findViewById(R.id.tv_interest_name);
            tv_mutual_friends= (TextView) view.findViewById(R.id.tv_num_followers);
            ll_content_wrapper=(LinearLayout)view.findViewById(R.id.ll_content_wrapper);
            pb_action_status=(ProgressBar)view.findViewById(R.id.pb_action_status);
            tv_place_and_designation= (TextView) view.findViewById(R.id.tv_place_and_designation);
        }
    }


    @Override
    public FollowingInterestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.following_list_item, parent, false);

        return new FollowingInterestListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FollowingInterestListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.pb_action_status.setVisibility(View.GONE);
        holder.tv_unfollow.setVisibility(View.VISIBLE);
        final PojoRelatedInterestArr currentItem=relatedInterestArr.get(position);
        if(currentItem.image!=null&&!currentItem.image.isEmpty()){
            if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.image)
                    .apply(new RequestOptions().override(96,96))
                    .into(holder.iv_dp);
        }
        holder.tv_interest_name.setText(currentItem.text);


        holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent=new Intent(activity, InterestActivity.class);
                interestIntent.putExtra("id",currentItem.interestId);
                activity.startActivity(interestIntent);
            }
        });

        if(currentItem.iFollowRelated==1){
            holder.tv_unfollow.setText(activity.getString(R.string.following));
            holder.tv_unfollow.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_dark));
            holder.tv_unfollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                    0,0,0);
        }
        else {
            holder.tv_unfollow.setText(activity.getString(R.string.follow));
            holder.tv_unfollow.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
            holder.tv_unfollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                    0,0,0);
        }

        holder.tv_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem.iFollowRelated==1) {
                    hitUpdateRequestStatusApi(user_id,currentItem.interestId,"interest_unfollow",position);
                    currentItem.iFollowRelated=0;
                    notifyDataSetChanged();
                }
                else {
                    hitUpdateRequestStatusApi(user_id,currentItem.interestId,"interest_follow",position);
                    currentItem.iFollowRelated=1;
                    notifyDataSetChanged();
                }

            }
        });

        if(currentItem.noOfFollowers==0){
            holder.tv_mutual_friends.setVisibility(View.GONE);
        }else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
        }
        holder.tv_mutual_friends.setText(currentItem.noOfFollowers+" "+activity.getString(R.string.followers));
        holder.tv_place_and_designation.setVisibility(View.GONE);

    }

    private void hitUpdateRequestStatusApi(String user_id, String id, String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id,connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(activity, message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return relatedInterestArr.size();
        //  return hierarchyList.size();
    }


}

