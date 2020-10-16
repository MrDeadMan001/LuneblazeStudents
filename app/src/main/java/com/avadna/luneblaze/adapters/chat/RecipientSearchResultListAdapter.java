package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class RecipientSearchResultListAdapter extends RecyclerView.Adapter<RecipientSearchResultListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    List<PojoUserData> friendList;
    RecipientSearchResultListAdapter.RecipientListAdapterCallback recipientListAdapterCallback;
    boolean showCheckBox=false;

    public void checkBoxVisible(Boolean groupCreate) {
        showCheckBox=groupCreate;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        TextView tv_place_and_designation;
        TextView tv_mutual_friends;
        ImageView iv_user_dp;
        ImageView iv_online_status;

        //   LinearLayout ll_data_wrapper;
        RelativeLayout rl_parent;
        CheckBox cb_selected;


        MyViewHolder(View view) {
            super(view);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
         //   ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_online_status = (ImageView) view.findViewById(R.id.iv_online_status);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
        }
    }

    public RecipientSearchResultListAdapter(Activity activity, List<PojoUserData> friendList) {
        try {
            this.recipientListAdapterCallback = ((RecipientSearchResultListAdapter.RecipientListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public RecipientSearchResultListAdapter(Fragment fragment, List<PojoUserData> friendList) {
        try {
            this.recipientListAdapterCallback = ((RecipientSearchResultListAdapter.RecipientListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    @Override
    public RecipientSearchResultListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipient_search_result_list_item, parent, false);

        return new RecipientSearchResultListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecipientSearchResultListAdapter.MyViewHolder holder, final int position) {


        final PojoUserData currentItem = friendList.get(position);

        if(currentItem.isOnline.equals("1")){
            holder.iv_online_status.setVisibility(View.VISIBLE);
        }
        else {
            holder.iv_online_status.setVisibility(View.GONE);
        }

        if(showCheckBox){
            holder.cb_selected.setVisibility(View.VISIBLE);
        }
        else {
            holder.cb_selected.setVisibility(View.GONE);
        }
        holder.cb_selected.setChecked(currentItem.selected);

      /*  if (currentItem.selected) {
            holder.ll_data_wrapper.setBackgroundResource(R.drawable.app_theme_border);
        } else {
            holder.ll_data_wrapper.setBackgroundResource(R.color.white);
        }*/

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
        if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

        holder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientListAdapterCallback.onRecipientListMethodCallback(position, friendList.get(position));
            }
        });

     /*   if (position == (friendList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.rl_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.rl_parent.setPadding(0, 0, 0, 0);
        }*/


    }


    public static interface RecipientListAdapterCallback {
        void onRecipientListMethodCallback(int position, PojoUserData item);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
        //  return hierarchyList.size();
    }
}
