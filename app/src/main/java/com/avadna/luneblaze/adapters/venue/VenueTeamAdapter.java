package com.avadna.luneblaze.adapters.venue;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueTeam;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 17-12-2017.
 */

public class VenueTeamAdapter extends RecyclerView.Adapter<VenueTeamAdapter.MyViewHolder> {
    private List<PojoVenueTeam> venueTeam;
    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_parent;
        TextView tv_username;
        TextView tv_place_and_designation;
        TextView tv_mutual_friends;
        ImageView iv_user_dp;
        ImageView iv_more_options;
        Spinner sp_more_options;
        LinearLayout ll_data_wrapper;

        MyViewHolder(View view) {
            super(view);
            ll_data_wrapper=(LinearLayout)view.findViewById(R.id.ll_data_wrapper);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);
            sp_more_options = (Spinner) view.findViewById(R.id.sp_more_options);
        }
    }

    public VenueTeamAdapter(Activity activity, List<PojoVenueTeam> venueTeam) {
        this.venueTeam = venueTeam;
        this.activity=activity;
    }

    @Override
    public VenueTeamAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_friend_list_item, parent, false);

        return new VenueTeamAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VenueTeamAdapter.MyViewHolder holder, int position) {
        final PojoUserData currentItem=venueTeam.get(position).userDetail;
        holder.tv_username.setText(currentItem.userFullname);
        holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id",currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        //in api there is "s" after the role and we dont need it
        if(venueTeam.get(position).role.contains("Ambass")){
            holder.tv_mutual_friends.setText(activity.getString(R.string.team_lead));
        }
        else if(venueTeam.get(position).role.contains("Team")){
            holder.tv_mutual_friends.setText(activity.getString(R.string.ambassador));
        }

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
        if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(currentItem.userPicture)
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

        holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent profileIntent=new Intent(activity,ProfileInfoActivity.class);
               profileIntent.putExtra(AppKeys.TARGET_USER_ID,currentItem.userId);
               activity.startActivity(profileIntent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return venueTeam.size();
        //  return hierarchyList.size();
    }
}
