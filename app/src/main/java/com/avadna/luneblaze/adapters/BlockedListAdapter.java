package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class BlockedListAdapter extends RecyclerView.Adapter<BlockedListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoUserData> blockedList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;

    BlockedListAdapterCallback blockedListAdapterCallback;


    public BlockedListAdapter(List<PojoUserData> blockedList, Activity activity) {
        try {
            this.blockedListAdapterCallback = ((BlockedListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  BlockedListAdapterCallback.");
        }

        this.blockedList = blockedList;
        this.activity = activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_block;
        TextView tv_venue;
        LinearLayout ll_content_wrapper;
        ProgressBar pb_action_status;
        TextView tv_designation;


        MyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_block = (TextView) view.findViewById(R.id.tv_block);
            tv_venue = (TextView) view.findViewById(R.id.tv_venue);
            tv_designation = (TextView) view.findViewById(R.id.tv_designation);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);
        }
    }


    @Override
    public BlockedListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_blocking_list_item, parent, false);

        return new BlockedListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BlockedListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.pb_action_status.setVisibility(View.GONE);
        holder.tv_block.setVisibility(View.VISIBLE);
        final PojoUserData currentItem = blockedList.get(position);
        if (!currentItem.userPicture.isEmpty()) {
            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .placeholder(R.drawable.blank_profile_male)
                    .into(holder.iv_user_dp);
        }
        holder.tv_venue.setText(currentItem.userFullname);

        holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent = new Intent(activity, InterestActivity.class);
                activity.startActivity(interestIntent);
            }
        });

        holder.tv_block.setText(R.string.unblock);

        holder.tv_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              blockedListAdapterCallback.blockedListItemClickCallback(position,currentItem,"remove");
            }
        });

       /* String placeAndDesignation = "";
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
        holder.tv_place_and_designation.setText(placeAndDesignation);*/


    }



    @Override
    public int getItemCount() {
        return blockedList.size();
        //  return hierarchyList.size();
    }

    public static interface BlockedListAdapterCallback {
        void blockedListItemClickCallback(int position, PojoUserData currentItem, String type);
    }

}

