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
import com.avadna.luneblaze.activities.sessions.SessionPhotoGalleryViewerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionFile;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class SessionFileUploadAdapter extends RecyclerView.Adapter<SessionFileUploadAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int type;
    List<PojoSessionFile> fileList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    String user_id;
    String session_id;

    SessionFileUploadAdapterCallback sessionFileUploadAdapterCallback;

    public SessionFileUploadAdapter(Activity activity, List<PojoSessionFile> fileList, String session_id) {
        try {
            this.sessionFileUploadAdapterCallback = ((SessionFileUploadAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  SessionFileUploadAdapterCallback.");
        }
        this.fileList = fileList;
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

        MyViewHolder(View view) {
            super(view);
            iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
            iv_photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

            pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
            ib_delete = (ImageButton) view.findViewById(R.id.ib_delete);
            ib_retry = (ImageButton) view.findViewById(R.id.ib_retry);
            tv_retry = (TextView) view.findViewById(R.id.tv_retry);
            rl_retry_cancel_wrapper = (RelativeLayout) view.findViewById(R.id.rl_retry_cancel_wrapper);
        }
    }


    @Override
    public SessionFileUploadAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_photo_upload_item, parent, false);

        return new SessionFileUploadAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionFileUploadAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoSessionFile currentItem = fileList.get(position);
        holder.ib_delete.setVisibility(View.GONE);
        holder.iv_photo.setVisibility(View.VISIBLE);
        holder.iv_photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Picasso.get()
                .load(R.drawable.ic_file)
                .placeholder(R.drawable.placeholder)
                .into(holder.iv_photo);

        holder.iv_photo.setMaxHeight(displayMetrics.widthPixels / 3);


        holder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionFileUploadAdapterCallback.onSessionFileUploadAdapterItemClick(position, AppKeys.ACTION_DOWNLOAD_FILE);
            }
        });
        // item is fetched from api
        if (currentItem.uploadStatus == null && currentItem.id == null) {
            holder.pb_progress.setVisibility(View.GONE);
            //todo show delete button when delete api is available
            holder.ib_delete.setVisibility(View.GONE);
            holder.rl_retry_cancel_wrapper.setVisibility(View.GONE);
        }
        //  item has been just uploaded
        else if (currentItem.uploadStatus.equals(AppKeys.UPLOAD_FINISH)) {
            holder.pb_progress.setVisibility(View.GONE);
            holder.ib_delete.setVisibility(View.GONE);
            holder.rl_retry_cancel_wrapper.setVisibility(View.GONE);
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

                //sessionFileUploadAdapterCallback.onSessionFileUploadAdapterItemClick(position, AppKeys.ACTION_DELETE_PHOTO);
            }
        });

        holder.tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tv_retry.getText().equals(activity.getString(R.string.retry))) {
                    sessionFileUploadAdapterCallback.onSessionFileUploadAdapterItemClick(position, AppKeys.ACTION_RETRY_UPLOAD);
                } else if (holder.tv_retry.getText().equals(activity.getString(R.string.cancel))) {
                    sessionFileUploadAdapterCallback.onSessionFileUploadAdapterItemClick(position, AppKeys.ACTION_CANCEL_UPLOAD);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return fileList.size();
        //  return hierarchyList.size();
    }

    public static interface SessionFileUploadAdapterCallback {
        void onSessionFileUploadAdapterItemClick(int position, String type);
    }

}
