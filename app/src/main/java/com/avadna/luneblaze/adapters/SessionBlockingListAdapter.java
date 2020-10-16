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

public class SessionBlockingListAdapter extends RecyclerView.Adapter<SessionBlockingListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoUserData> followingList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    SessionBlockedListAdapterCallback sessionBlockedListAdapterCallback;

    public SessionBlockingListAdapter(Activity activity, List<PojoUserData> followingList) {
        try {
            this.sessionBlockedListAdapterCallback = ((SessionBlockedListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  SessionBlockedListAdapterCallback.");
        }

        this.followingList = followingList;
        this.activity = activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_block;
        TextView tv_name;
        TextView tv_designation;
        LinearLayout ll_content_wrapper;
        ProgressBar pb_action_status;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_block = (TextView) view.findViewById(R.id.tv_block);
            tv_name = (TextView) view.findViewById(R.id.tv_venue);
            tv_designation = (TextView) view.findViewById(R.id.tv_designation);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_action_status = (ProgressBar) view.findViewById(R.id.pb_action_status);

        }
    }


    @Override
    public SessionBlockingListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_blocking_list_item, parent, false);

        return new SessionBlockingListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionBlockingListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.pb_action_status.setVisibility(View.GONE);
        holder.tv_block.setVisibility(View.VISIBLE);
        final PojoUserData currentItem = followingList.get(position);

        if (currentItem.connection.contains("blocked")) {
            holder.tv_block.setText(activity.getString(R.string.unblock));
        } else {
            holder.tv_block.setText(activity.getString(R.string.block));
        }

        if (!currentItem.userPicture.isEmpty()) {
            if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .placeholder(R.drawable.blank_profile_male)
                    .into(holder.iv_dp);
        }
        holder.tv_name.setText(currentItem.userFullname);


        holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent = new Intent(activity, InterestActivity.class);
                activity.startActivity(interestIntent);
            }
        });

        holder.tv_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionBlockedListAdapterCallback.sessionBlockedListItemClickCallback(position, currentItem,
                        "session_unblock");
            }
        });


    }


    @Override
    public int getItemCount() {
        return followingList.size();
        //  return hierarchyList.size();
    }

    public static interface SessionBlockedListAdapterCallback {
        void sessionBlockedListItemClickCallback(int position, PojoUserData currentItem, String type);
    }
}


