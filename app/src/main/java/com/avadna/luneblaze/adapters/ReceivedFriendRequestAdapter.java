package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 26-03-2018.
 */

public class ReceivedFriendRequestAdapter extends RecyclerView.Adapter<ReceivedFriendRequestAdapter.MyViewHolder> {
    int type;
    Fragment fragment;
    Activity activity;
    List<PojoGetFriendRequestResponseData> requestList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ReceivedRequestAdapterCallBack receivedRequestAdapterCallBack;


    public ReceivedFriendRequestAdapter(Fragment fragment, List<PojoGetFriendRequestResponseData> requestList) {
        try {
            this.receivedRequestAdapterCallBack = ((ReceivedRequestAdapterCallBack) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement ReceivedFriendRequestAdapter.");
        }

        this.fragment=fragment;
        this.activity=fragment.getActivity();
        this.requestList=requestList;
        //  this.hierarchyList = hierarchyList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils=new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id=preferenceUtils.get_user_id();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username,tv_place_and_designation,tv_mutual_friends,tv_accept,tv_reject;
        ImageView iv_user_dp;
        LinearLayout ll_button_wrapper ;
        ProgressBar pb_action_status;

        MyViewHolder(View view) {
            super(view);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_accept = (TextView) view.findViewById(R.id.tv_accept);
            tv_reject = (TextView) view.findViewById(R.id.tv_reject);
            iv_user_dp=(ImageView)view.findViewById(R.id.iv_user_dp);
            ll_button_wrapper=(LinearLayout)view.findViewById(R.id.ll_button_wrapper);
            pb_action_status=(ProgressBar)view.findViewById(R.id.pb_action_status);
        }
    }


    @Override
    public ReceivedFriendRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.received_friend_req_item, parent, false);

        return new ReceivedFriendRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoGetFriendRequestResponseData currentItem=requestList.get(position);
        holder.pb_action_status.setVisibility(View.GONE);
        holder.ll_button_wrapper.setVisibility(View.VISIBLE);
        holder.tv_username.setText(currentItem.userFullname);
        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id",currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });
        if(currentItem.mutualFriendsCount==0){
            holder.tv_mutual_friends.setVisibility(View.GONE);
        }else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
        }
        holder.tv_mutual_friends.setText(currentItem.mutualFriendsCount+ " Mutual Friends");
      //  holder.tv_place_and_designation.setText(currentItem.userGender);

        if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(currentItem.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

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
        }

        holder.tv_place_and_designation.setText(placeAndDesignation);

        holder.tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receivedRequestAdapterCallBack.receivedRequestMethod(position,"accept");
            }
        });

        holder.tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receivedRequestAdapterCallBack.receivedRequestMethod(position,"decline");

            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
        //  return hierarchyList.size();
    }

    public static interface ReceivedRequestAdapterCallBack {
        void receivedRequestMethod(int position,String type);
    }

}
