package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.listdiffcallbacks.UserListDiffCallback;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AddChatMemberListAdapter extends RecyclerView.Adapter<AddChatMemberListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    boolean enableApiCall = true;
    List<PojoUserData> friendList;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ApiInterface apiService;
    boolean showProgressBar = false;

    AddChatMemberAdapterCallback addChatMemberAdapterCallback;
    String type;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_user_name;
        TextView tv_designation;
        TextView tv_mutual_friends_count;
        TextView tv_add_friend;
        TextView tv_no_more_results;
        ProgressBar pb_loading_more;
        RelativeLayout rl_info_wrapper;
        RelativeLayout rl_content_wrapper;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_designation = (TextView) view.findViewById(R.id.tv_designation);
            tv_mutual_friends_count = (TextView) view.findViewById(R.id.tv_mutual_friends_count);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            rl_info_wrapper = (RelativeLayout) view.findViewById(R.id.rl_info_wrapper);
        }
    }


    public void setShowProgressBar(boolean status) {
        this.showProgressBar = status;
        notifyDataSetChanged();
    }

    public AddChatMemberListAdapter(Activity activity, List<PojoUserData> friendList,
                                    String type) {
        try {
            this.addChatMemberAdapterCallback = ((AddChatMemberAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.type = type;
        this.friendList = friendList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    @Override
    public AddChatMemberListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_group_member_item, parent, false);
        return new AddChatMemberListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddChatMemberListAdapter.MyViewHolder holder, final int position) {
        if (position < friendList.size()) {
            final PojoUserData currentItem = friendList.get(position);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.tv_add_friend.setVisibility(View.VISIBLE);

            if (!currentItem.userPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                            .apply(new RequestOptions().override(128, 128))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .placeholder(R.drawable.blank_profile_male)
                            .into(holder.iv_dp);
            }

            holder.tv_user_name.setText(currentItem.userFullname);
            holder.tv_designation.setText(currentItem.userWorkTitle);
            holder.tv_mutual_friends_count.setText("" + currentItem.mutualFriendsCount + " "
                    + activity.getString(R.string.mutual_friends));

            switch (type) {
                case "add_group_members":
                    holder.tv_add_friend.setText(activity.getString(R.string.add));
                    break;

                case "assign_admin":
                    holder.tv_add_friend.setText(activity.getString(R.string.assign_admin));
                    break;

                case "remove_member":
                    holder.tv_add_friend.setText(activity.getString(R.string.remove));
                    break;

            }

            holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //addChatMemberAdapterCallback.onAddChatMemberMethodCallback(position,currentItem,type);
                    switch (type) {
                        case "add_group_members":
                            addChatMemberAdapterCallback.onAddChatMemberMethodCallback(position, currentItem, "add_group_members");
                            break;

                        case "assign_admin":
                            break;

                        case "remove_member":
                            break;

                    }
                }
            });
        } else {
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
                holder.rl_content_wrapper.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.rl_content_wrapper.setVisibility(View.GONE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        }
    }


   /* @Override
    public void onBindViewHolder(final AddChatMemberListAdapter.CommentViewHolder holder,
                                 final int position, List<Object> payloads) {
        if (position < friendList.size()) {
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.tv_add_friend.setVisibility(View.VISIBLE);

            if (payloads.isEmpty()) return;
            else {
                PojoUserData currentItem = friendList.get(position);
                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    if (key.equals(AppKeys.USER_FULL_NAME)) {
                        holder.tv_user_name.setText(currentItem.userFullname);
                    } else if (key.equals(AppKeys.USER_PICTURE)) {
                        if (!currentItem.userPicture.isEmpty()) {
                            if (activity != null)
                                Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                                        .apply(new RequestOptions().override(128, 128))
                                        .apply(bitmapTransform(new CropCircleTransformation()))
                                        .into(holder.iv_dp);
                        }
                    } else if (key.equals(AppKeys.USER_MUTUAL_FRIEND_COUNT)) {
                        holder.tv_mutual_friends_count.setText("" + currentItem.mutualFriendsCount + " "
                                + activity.getString(R.string.mutual_friends));
                    } else if (key.equals(AppKeys.USER_WORK_TITLE)) {
                        holder.tv_designation.setText(currentItem.userWorkTitle);
                    }
                }
            }
        }
        else {
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
                holder.rl_content_wrapper.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.rl_content_wrapper.setVisibility(View.GONE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        }
    }
*/

    public void onNewUsers(List<PojoUserData> newUsers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UserListDiffCallback(friendList, newUsers));
        diffResult.dispatchUpdatesTo(this);
    }


    public static interface AddChatMemberAdapterCallback {
        void onAddChatMemberMethodCallback(int position, PojoUserData item, String type);
    }

    @Override
    public int getItemCount() {
        return friendList.size() + 1;
        //  return hierarchyList.size();
    }


}
