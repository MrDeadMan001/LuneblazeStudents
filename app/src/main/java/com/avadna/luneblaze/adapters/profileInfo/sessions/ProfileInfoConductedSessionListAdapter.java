package com.avadna.luneblaze.adapters.profileInfo.sessions;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionConducted;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProfileInfoConductedSessionListAdapter extends RecyclerView.Adapter<ProfileInfoConductedSessionListAdapter.MyViewHolder> {

    Activity activity;
    List<PojoSessionConducted> profileInfoSessionList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;

    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;
    int maxSessionImageHeight;
    boolean showProgress;

    public ProfileInfoConductedSessionListAdapter(Activity activity, List<PojoSessionConducted> profileInfoSessionList) {
        this.activity = activity;
        this.profileInfoSessionList = profileInfoSessionList;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));
    }

    public void setShowProgress(boolean status) {
        showProgress = status;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //  TextView tv_activity_performed;
        RelativeLayout rl_parent;
        TextView tv_session_name;
        TextView tv_session_description;
        TextView tv_num_attendees;

        TextView tv_venue;
        TextView tv_date_place_holder;
        TextView tv_venue_place_holder;
        ImageView iv_cover_photo;
        ProgressBar pb_loading_more;
        LinearLayout ll_border_wrapper;


        MyViewHolder(View view) {
            super(view);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            ll_border_wrapper = (LinearLayout) view.findViewById(R.id.ll_border_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
            tv_num_attendees = (TextView) view.findViewById(R.id.tv_num_attendees);
            tv_session_description = (TextView) view.findViewById(R.id.tv_session_description);

            tv_venue = (TextView) view.findViewById(R.id.tv_venue);
            tv_date_place_holder = (TextView) view.findViewById(R.id.tv_date_place_holder);
            tv_venue_place_holder = (TextView) view.findViewById(R.id.tv_venue_place_holder);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);


        }
    }


    @Override
    public ProfileInfoConductedSessionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_info_session_list_item, parent, false);

        return new ProfileInfoConductedSessionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileInfoConductedSessionListAdapter.MyViewHolder holder, int position) {

        if (position == profileInfoSessionList.size()) {
            holder.ll_border_wrapper.setVisibility(View.GONE);
            if (showProgress) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }
        } else {
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_border_wrapper.setVisibility(View.VISIBLE);
            final PojoSessionConducted currentItem = profileInfoSessionList.get(position);
            holder.tv_session_name.setText(Html.fromHtml(currentItem.title));
            holder.tv_num_attendees.setText(currentItem.totalAttends + " " + activity.getString(R.string.attendees));

            if (!currentItem.coverPhoto.isEmpty()) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;

                if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty()) {
                    ImageDimensions dimen = getScaledDimensions(currentItem.imgDimensions);
                    height = dimen.height;
                    width = dimen.width;
                }

                holder.iv_cover_photo.getLayoutParams().height = height;
                holder.iv_cover_photo.getLayoutParams().width = width;

                if (activity != null) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.override(width, height);
                    requestOptions.placeholder(R.drawable.placeholder);

                    Glide.with(activity.getApplicationContext())
                            .load(currentItem.coverPhoto)
                            .apply(requestOptions)
                            .into(holder.iv_cover_photo);
                }
            }

            holder.ll_border_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                    sessionIntent.putExtra("id", currentItem.sessionsId);
                    activity.startActivity(sessionIntent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return profileInfoSessionList.size() + 1;
        //  return hierarchyList.size();
    }

    public ImageDimensions getScaledDimensions(String imgDimen) {
        ImageDimensions dimen = new ImageDimensions();
        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);
        if (targetHeight > maxSessionImageHeight) {
            targetHeight = maxSessionImageHeight;
        }
        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }
}
