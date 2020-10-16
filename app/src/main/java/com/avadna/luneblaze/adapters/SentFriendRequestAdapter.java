package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoSentRequestListResponseData;
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

public class SentFriendRequestAdapter extends RecyclerView.Adapter<SentFriendRequestAdapter.MyViewHolder> {
    int type;
    Fragment fragment;
    Activity activity;
    List<PojoSentRequestListResponseData> sentRequestList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    SentRequestAdapterCallBack sentRequestAdapterCallBack;

    public SentFriendRequestAdapter(Fragment fragment, List<PojoSentRequestListResponseData> sentRequestList) {
        try {
            this.sentRequestAdapterCallBack = ((SentRequestAdapterCallBack) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement SentRequestAdapterCallBack.");
        }

        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.sentRequestList = sentRequestList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_username;
        TextView tv_place_and_designation;
        TextView tv_mutual_friends;
        TextView tv_cancel;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        }
    }


    @Override
    public SentFriendRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sent_req_list_item, parent, false);

        return new SentFriendRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoSentRequestListResponseData currentItem = sentRequestList.get(position);


        if (activity != null) Glide.with(activity.getApplicationContext())
                .load(currentItem.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_dp);
        holder.tv_username.setText(currentItem.userFullname);
        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        holder.tv_place_and_designation.setText(currentItem.userWorkTitle);
        if (currentItem.mutualFriendsCount == 0) {
            holder.tv_mutual_friends.setVisibility(View.GONE);
        } else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
        }
        holder.tv_mutual_friends.setText(currentItem.mutualFriendsCount + " Mutual friends");

      /*  String placeAndDesignation = "";
        //if both fields are in data list then show both
        if ((!currentItem.isEmpty()) && (!currentItem.userWorkPlace.isEmpty())) {
            placeAndDesignation = currentItem. + " (" + currentItem.userWorkPlace + ")";
        }
        //if work title not available show only work place
        else if (currentItem..isEmpty()) {
            placeAndDesignation = currentItem.userWorkPlace;
        }
        //if work place not available show only work title
        else if (currentItem.userWorkPlace.isEmpty()) {
            placeAndDesignation = currentItem.;
        }
        //if none is present then remove visibility of the field
        else if (currentItem..isEmpty() && currentItem.userWorkPlace.isEmpty()) {
            holder.tv_place_and_designation.setVisibility(View.GONE);
        }

        holder.tv_place_and_designation.setText(placeAndDesignation);*/

      holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              sentRequestAdapterCallBack.sentRequestMethod(position);
          }
      });

    }

    @Override
    public int getItemCount() {
        return sentRequestList.size();
        //  return hierarchyList.size();
    }


    public static interface SentRequestAdapterCallBack {
        void sentRequestMethod(int position);
    }

}


