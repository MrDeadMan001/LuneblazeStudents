package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.home.NotificationsListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 14-01-2018.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private ArrayList<HashMap<String, String>> tipsArray;
    Activity activity;
    NotificationsListFragment fragment;
    private NotificationListAdapterCallback notificationListAdapterCallback;
    List<PojoGetNotificationListResponseData> notifList;
    int maxheight = 0;

    String user_id = "";
    String username = "";
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    private boolean showProgressBar = false;
    private boolean showNoMoreResults = false;

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMoreResults(boolean status) {
        showNoMoreResults = status;
        notifyDataSetChanged();
    }

    public NotificationListAdapter(Activity activity, List<PojoGetNotificationListResponseData> notifList) {

        try {
            this.notificationListAdapterCallback = ((NotificationListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.notifList = notifList;

        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public NotificationListAdapter(NotificationsListFragment fragment, List<PojoGetNotificationListResponseData> notifList) {

        try {
            this.notificationListAdapterCallback = ((NotificationListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.notifList = notifList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);

        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }


    @Override
    public NotificationListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);

        return new NotificationListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_notification_name, tv_notification_description, tv_notification_time_stamp;
        LinearLayout ll_item_wrapper;
        ImageView iv_notification_image;
        LinearLayout ll_yes_no_wrapper;
        TextView tv_yes, tv_no;

        ProgressBar pb_loading_more;
        TextView tv_no_more_results;

        MyViewHolder(View view) {
            super(view);
            iv_notification_image = (ImageView) view.findViewById(R.id.iv_notification_image);
            tv_notification_name = (TextView) view.findViewById(R.id.tv_notification_name);
            tv_notification_description = (TextView) view.findViewById(R.id.tv_notification_description);
            tv_notification_time_stamp = (TextView) view.findViewById(R.id.tv_notification_time_stamp);
            ll_item_wrapper = (LinearLayout) view.findViewById(R.id.ll_item_wrapper);
            ll_yes_no_wrapper = (LinearLayout) view.findViewById(R.id.ll_yes_no_wrapper);
            tv_yes = (TextView) view.findViewById(R.id.tv_yes);
            tv_no = (TextView) view.findViewById(R.id.tv_no);

            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }


    @Override
    public void onBindViewHolder(final NotificationListAdapter.MyViewHolder holder, final int position) {

        if (position == notifList.size()) {
            holder.ll_item_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (notifList.isEmpty()) {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_notifications_present));
                } else {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_more_notifications));
                }
                holder.tv_no_more_results.setVisibility(View.VISIBLE);
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        } else {
            holder.ll_item_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);
            final PojoGetNotificationListResponseData currentItem = notifList.get(position);
            if (currentItem.action.equals("venue_changed_confirmation")) {
                holder.ll_yes_no_wrapper.setVisibility(View.VISIBLE);
                holder.tv_notification_description.setMaxLines(8);
                holder.tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationListAdapterCallback.notificationListMethodCallback(position, AppKeys.FINALISE_YES);
                    }
                });

                holder.tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationListAdapterCallback.notificationListMethodCallback(position, AppKeys.FINALISE_NO);
                    }
                });

            } else {
                holder.tv_notification_description.setMaxLines(4);
                holder.ll_yes_no_wrapper.setVisibility(View.GONE);
            }
            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(holder.iv_notification_image);

            holder.tv_notification_name.setText(currentItem.userFullname);
            holder.tv_notification_name.setVisibility(View.GONE);

            if(currentItem.fullMessage!=null){
                holder.tv_notification_description.setText(Html.fromHtml(currentItem.fullMessage));
            }
            else {
                holder.tv_notification_description.setText("");
            }

            // holder.tv_notification_description.setText(setMessage(position));
            holder.tv_notification_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.time));
            holder.ll_item_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationListAdapterCallback.notificationListMethodCallback(position, AppKeys.NOTIFICATION_SEEN);
                }
            });

            if (currentItem.seen.equals("1")) {
                holder.ll_item_wrapper.setBackgroundResource(R.drawable.white_rounded_border_white_fill);
            } else {
                holder.ll_item_wrapper.setBackgroundResource(R.drawable.unseen_notification_bg);
            }

      /*  if (position == (notifList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.ll_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.ll_parent.setPadding(0, 0, 0, 0);
        }*/
        }
    }


    public static interface NotificationListAdapterCallback {
        void notificationListMethodCallback(int position, String action);
    }

    @Override
    public int getItemCount() {
        return notifList.size()+1;
        //  return hierarchyList.size();
    }
}

