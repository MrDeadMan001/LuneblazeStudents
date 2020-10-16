package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoChat.PojoGroupMember;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SeenUserListAdapter extends RecyclerView.Adapter<SeenUserListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoGroupMember> userList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;


    public SeenUserListAdapter(List<PojoGroupMember> userList, Activity activity) {

        this.userList = userList;
        this.activity = activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
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
    public SeenUserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seen_user_list_item, parent, false);

        return new SeenUserListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SeenUserListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.pb_action_status.setVisibility(View.GONE);
        holder.tv_block.setVisibility(View.GONE);

        final PojoGroupMember currentItem = userList.get(position);
        if (!currentItem.userPicture.isEmpty()) {
            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .placeholder(R.drawable.blank_profile_male)
                    .into(holder.iv_user_dp);
        }
        holder.tv_venue.setText(currentItem.userFullName);
        holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent = new Intent(activity, InterestActivity.class);
                activity.startActivity(interestIntent);
            }
        });
        holder.tv_designation.setText(commonFunctions.allMsgListTimeParser(currentItem.seenTime,"chat_time"));
    }


    @Override
    public int getItemCount() {
        return userList.size();
        //  return hierarchyList.size();
    }


}

