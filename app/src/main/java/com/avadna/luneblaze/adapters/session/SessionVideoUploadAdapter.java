package com.avadna.luneblaze.adapters.session;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.VideoPlayerActivity;
import com.avadna.luneblaze.activities.sessions.SessionPhotoGalleryViewerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionVideo;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SessionVideoUploadAdapter extends RecyclerView.Adapter<SessionVideoUploadAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoSessionVideo> videoList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    String user_id;
    String session_id;

    SessionVideoUploadAdapterCallback sessionVideoUploadAdapterCallback;

    public SessionVideoUploadAdapter(Activity activity, List<PojoSessionVideo> videoList, String session_id) {
        try {
            this.sessionVideoUploadAdapterCallback = ((SessionVideoUploadAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  SessionVideoUploadAdapterCallback.");
        }
        this.videoList = videoList;
        this.activity = activity;
        this.session_id = session_id;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo;
        ProgressBar pb_progress;
        ImageButton ib_delete;
        RelativeLayout rl_retry_cancel_wrapper;
        ImageButton ib_retry;
        TextView tv_retry;
        ImageView iv_play_video;

        MyViewHolder(View view) {
            super(view);
            iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
            iv_play_video = (ImageView) view.findViewById(R.id.iv_play_video);
            iv_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
            ib_delete = (ImageButton) view.findViewById(R.id.ib_delete);
            ib_retry = (ImageButton) view.findViewById(R.id.ib_retry);
            tv_retry = (TextView) view.findViewById(R.id.tv_retry);
            rl_retry_cancel_wrapper = (RelativeLayout) view.findViewById(R.id.rl_retry_cancel_wrapper);
        }
    }


    @Override
    public SessionVideoUploadAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_video_upload_item, parent, false);

        return new SessionVideoUploadAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionVideoUploadAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoSessionVideo currentItem = videoList.get(position);
        holder.ib_delete.setVisibility(View.GONE);

      /*  Picasso.get()
                .load(currentItem.photo)
                .placeholder(R.drawable.placeholder)
                .into(holder.iv_photo);*/

        Glide.with(activity.getApplicationContext())
                .load(currentItem.video)
                .apply(new RequestOptions()
                        .override(displayMetrics.widthPixels / 2, displayMetrics.widthPixels / 3)
                        .placeholder(R.drawable.placeholder)
                        .frame(1000))
                .into(holder.iv_photo);

        if (position!=0){
            holder.iv_photo.getLayoutParams().height = displayMetrics.widthPixels / 3;
            holder.iv_photo.getLayoutParams().width = displayMetrics.widthPixels / 2;
        }

        holder.iv_photo.setMaxHeight(displayMetrics.widthPixels / 2);

        holder.iv_play_video.setVisibility(View.GONE);
        holder.iv_play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoPlayerIntent = new Intent(activity, VideoPlayerActivity.class);
                videoPlayerIntent.putExtra(AppKeys.PATH, currentItem.video);
                activity.startActivity(videoPlayerIntent);
                // openImageGalleryDialog(position);
            }
        });

        // item is fetched from api
        if (currentItem.uploadStatus == null && currentItem.videoId == null) {
            holder.pb_progress.setVisibility(View.GONE);
            //todo show delete button when delete api is available
            holder.ib_delete.setVisibility(View.GONE);
            holder.rl_retry_cancel_wrapper.setVisibility(View.GONE);
            holder.iv_play_video.setVisibility(View.VISIBLE);
        }
        //  item has been just uploaded
        else if (currentItem.uploadStatus.equals(AppKeys.UPLOAD_FINISH)) {
            holder.pb_progress.setVisibility(View.GONE);
            holder.ib_delete.setVisibility(View.GONE);
            holder.rl_retry_cancel_wrapper.setVisibility(View.GONE);
            holder.iv_play_video.setVisibility(View.VISIBLE);
        }
        // item is currently uploading
        else if (currentItem.uploadStatus.equals(AppKeys.UPLOADING)) {
            holder.pb_progress.setVisibility(View.VISIBLE);
            holder.ib_delete.setVisibility(View.GONE);
            holder.rl_retry_cancel_wrapper.setVisibility(View.VISIBLE);
            holder.tv_retry.setText(activity.getString(R.string.cancel));
            holder.ib_retry.setImageResource(R.drawable.ic_no_cross);
        }
        // item has failed to upload
        else if (currentItem.uploadStatus.equals(AppKeys.UPLOAD_FAILED)) {
            holder.pb_progress.setVisibility(View.GONE);
            holder.ib_delete.setVisibility(View.GONE);
            holder.tv_retry.setVisibility(View.VISIBLE);
            holder.tv_retry.setText(activity.getString(R.string.retry));
            holder.ib_retry.setImageResource(R.drawable.ic_retry_white);
        }

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sessionVideoUploadAdapterCallback.onSessionVideoUploadAdapterItemClick(position, AppKeys.ACTION_DELETE_PHOTO);
            }
        });

        holder.tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tv_retry.getText().equals(activity.getString(R.string.retry))) {
                    sessionVideoUploadAdapterCallback.onSessionVideoUploadAdapterItemClick(position, AppKeys.ACTION_RETRY_UPLOAD);
                } else if (holder.tv_retry.getText().equals(activity.getString(R.string.cancel))) {
                    sessionVideoUploadAdapterCallback.onSessionVideoUploadAdapterItemClick(position, AppKeys.ACTION_CANCEL_UPLOAD);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return videoList.size();
        //  return hierarchyList.size();
    }

    public static interface SessionVideoUploadAdapterCallback {
        void onSessionVideoUploadAdapterItemClick(int position, String type);
    }

}
