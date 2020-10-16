package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.content.res.Resources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

//TODO set proper data in list items

/**
 * Created by Sunny on 18-03-2018.
 */

public class MyFriendsListAdapter extends RecyclerView.Adapter<MyFriendsListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Fragment fragment;
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    List<PojoUserData> friendList;
    FriendListAdapterCallback friendListAdapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        TextView tv_place_and_designation;
        TextView tv_mutual_friends;
        ImageView iv_user_dp;
        ImageView iv_more_options;
        PopupMenu popupMenu;
        LinearLayout ll_data_wrapper;
        RelativeLayout rl_parent;

        MyViewHolder(View view) {
            super(view);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity, iv_more_options);
        }
    }

    public MyFriendsListAdapter(Activity activity, List<PojoUserData> friendList) {
        try {
            this.friendListAdapterCallback = ((FriendListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    public MyFriendsListAdapter(Fragment fragment, List<PojoUserData> friendList) {
        try {
            this.friendListAdapterCallback = ((FriendListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    @Override
    public MyFriendsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_friend_list_item, parent, false);

        return new MyFriendsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyFriendsListAdapter.MyViewHolder holder, final int position) {

        final PojoUserData currentItem = friendList.get(position);
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

        if (currentItem.mutualFriendsCount == null || currentItem.mutualFriendsCount.equals("0")) {
            holder.tv_mutual_friends.setVisibility(View.GONE);
        } else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
        }

        holder.tv_mutual_friends.setText(currentItem.mutualFriendsCount + " " + activity.getString(R.string.mutual_friends));
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
        if (activity != null) Glide.with(activity.getApplicationContext())
                .load(currentItem.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

        holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendListAdapterCallback.onFriendListMethodCallback(position, friendList.get(position), AppKeys.PROFILE);
            }
        });

        if (position == (friendList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.rl_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.rl_parent.setPadding(0, 0, 0, 0);
        }

        MenuInflater inflater = holder.popupMenu.getMenuInflater();
        holder.popupMenu.getMenu().clear();
        inflater.inflate(R.menu.my_friend_list_item_menu, holder.popupMenu.getMenu());

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.message:
                        friendListAdapterCallback.onFriendListMethodCallback(position, currentItem,
                                AppKeys.MESSAGE);
                        // openEditDialog(position);
                        break;

                    case R.id.block:
                        openConfirmationDialog(position, currentItem, AppKeys.BLOCK);
                        //   hitDeleteAnswerApi(user_id, currentItem.sessionsQaId, "delete_question", position);
                        break;

                    case R.id.remove:
                        openConfirmationDialog(position, currentItem, AppKeys.UNFRIEND);

                        break;
                }
                return false;
            }
        });


    }

    private void openConfirmationDialog(final int position, final PojoUserData currentItem, final String action) {
        final TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(activity);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equals(AppKeys.BLOCK)) {
                    friendListAdapterCallback.onFriendListMethodCallback(position, currentItem,
                            AppKeys.BLOCK);
                } else if (action.equals(AppKeys.UNFRIEND)) {
                    friendListAdapterCallback.onFriendListMethodCallback(position, currentItem,
                            AppKeys.UNFRIEND);
                }
                confirmationDialog.dismiss();
            }
        });

        if (action.equals(AppKeys.BLOCK)) {
            tv_dialog_description.setText(R.string.block_confirmation_dialog_description);

        } else if (action.equals(AppKeys.UNFRIEND)) {
            tv_dialog_description.setText(R.string.remove_confirmation_dialog_description);
        }
        confirmationDialog.show();
    }

    public static interface FriendListAdapterCallback {
        void onFriendListMethodCallback(int position, PojoUserData item, String type);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
        //  return hierarchyList.size();
    }
}