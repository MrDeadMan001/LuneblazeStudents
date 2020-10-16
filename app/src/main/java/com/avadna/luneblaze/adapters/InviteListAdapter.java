package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoInvitesListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InviteListAdapter extends RecyclerView.Adapter<InviteListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    List<PojoInvitesListResponseData> userList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    boolean showProgressBar = false;
    boolean showNoMore = false;
    String inviteType;

    InviteListAdapterCallback inviteListAdapterCallback;


    public InviteListAdapter(List<PojoInvitesListResponseData> userList, Activity activity) {
        try {
            this.inviteListAdapterCallback = ((InviteListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InviteListAdapterCallback.");
        }
        this.userList = userList;
        this.activity = activity;

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        //  this.hierarchyList = hierarchyList;
    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMore(boolean status) {
        showNoMore = status;
        notifyDataSetChanged();
    }


    public void setInviteType(String inviteType) {
        this.inviteType = inviteType;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;
        TextView tv_invite;
        TextView tv_num_followers;
        TextView tv_interest_name;
        TextView tv_place_and_designation;
        LinearLayout ll_content_wrapper;
        ProgressBar pb_loading_more;
        RelativeLayout rl_content_wrapper;
        TextView tv_no_more_fellows;


        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_invite = (TextView) view.findViewById(R.id.tv_invite);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            tv_no_more_fellows = (TextView) view.findViewById(R.id.tv_no_more_fellows);
            tv_no_more_fellows.setVisibility(View.GONE);
            pb_loading_more.setVisibility(View.GONE);
        }
    }


    @Override
    public InviteListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_list_item, parent, false);

        return new InviteListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InviteListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == userList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.tv_no_more_fellows.setVisibility(View.GONE);
                holder.pb_loading_more.setVisibility(View.VISIBLE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMore) {
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.tv_no_more_fellows.setVisibility(View.VISIBLE);
            }
            else {
                holder.tv_no_more_fellows.setVisibility(View.GONE);

            }
        } else {
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_fellows.setVisibility(View.GONE);

            final PojoInvitesListResponseData currentItem = userList.get(position);
            if (!currentItem.userPicture.isEmpty()) {
                if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .placeholder(R.drawable.blank_profile_male)
                        .into(holder.iv_dp);
            }
            holder.tv_interest_name.setText(currentItem.userFullname);
            holder.tv_invite.setText(activity.getString(R.string.invite));

            String placeAndDesignation = "";
            //if both fields are in data list then show both
            if (currentItem.userWorkPlace != null) {

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
            }

            if (inviteType.equals("invite_interest")) {
                holder.tv_num_followers.setText(currentItem.numberOfMutualInterests + " "
                        + activity.getString(R.string.mutual_interests));
            } else if (inviteType.equals("invite_venue")) {
                holder.tv_num_followers.setText(currentItem.numberOfMutualVenues + " "
                        + activity.getString(R.string.mutual_venues));
            } else if (inviteType.equals("invite_question")) {
                holder.tv_num_followers.setText(currentItem.numberOfAnswers + " "
                        + activity.getString(R.string.answers_posted));
            } else if (inviteType.equals("session_conduct_request")) {
                holder.tv_num_followers.setText(currentItem.numberOfSessionsConducted + " "
                        + activity.getString(R.string.sessions_conducted));
            } else if (inviteType.equals("invite_session")) {
                holder.tv_num_followers.setText(currentItem.noOfSessionAttended + " "
                        + activity.getString(R.string.sessions_attended));
            }

            if (currentItem.invitedStatus == 0) {
                holder.tv_invite.setText(activity.getString(R.string.invite));
                holder.tv_invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inviteListAdapterCallback.inviteListClickMethod(position, currentItem, inviteType);
                        holder.tv_invite.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                holder.tv_invite.setText(activity.getString(R.string.invited));
            }
        }
    }


    @Override
    public int getItemCount() {
        return userList.size() + 1;
        //  return hierarchyList.size();
    }

    public static interface InviteListAdapterCallback {
        public void inviteListClickMethod(int position, PojoInvitesListResponseData pojoInvitesListResponseData,
                                          String inviteType);
    }

}