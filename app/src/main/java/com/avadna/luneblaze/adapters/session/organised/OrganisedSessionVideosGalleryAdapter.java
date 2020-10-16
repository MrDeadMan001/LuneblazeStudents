package com.avadna.luneblaze.adapters.session.organised;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avadna.luneblaze.activities.VideoPlayerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionVideo;
import com.bumptech.glide.Glide;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.SessionPhotoGalleryActivity;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 06-01-2018.
 */

public class OrganisedSessionVideosGalleryAdapter extends RecyclerView.Adapter<OrganisedSessionVideosGalleryAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Context context;
    List<PojoSessionVideo> videoList = new ArrayList<>();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    String thumbnail="";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_play_video;
        ImageView iv_thumbnail;
        RelativeLayout rl_image_parent;
        // RelativeLayout rl_more_button;

        MyViewHolder(View view) {
            super(view);
            iv_play_video = (ImageView) view.findViewById(R.id.iv_play_video);
            iv_thumbnail= (ImageView) view.findViewById(R.id.iv_thumbnail);
            rl_image_parent = (RelativeLayout) view.findViewById(R.id.rl_image_parent);
            //  rl_more_button=(RelativeLayout) view.findViewById(R.id.rl_more_button);
        }
    }


    public OrganisedSessionVideosGalleryAdapter(List<PojoSessionVideo> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
        this.thumbnail=thumbnail;
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //  this.hierarchyList = hierarchyList;
    }

    public void setThumbnail(String thumbnail){
        this.thumbnail=thumbnail;
    }

    @Override
    public OrganisedSessionVideosGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_video_gallery_item, parent, false);

        return new OrganisedSessionVideosGalleryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrganisedSessionVideosGalleryAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        RelativeLayout.LayoutParams parms =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        (int) (displayMetrics.widthPixels / 4f)); //aspect ratio of 4:3
        holder.iv_play_video.setLayoutParams(parms);
        holder.iv_thumbnail.setLayoutParams(parms);
        if (context != null) Glide.with(context.getApplicationContext())
                .load(thumbnail)
                .apply(new RequestOptions().override(256, (int) (displayMetrics.widthPixels / 4f)))
                .into(holder.iv_thumbnail);

        holder.iv_play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent(context, VideoPlayerActivity.class);
                playerIntent.putExtra(AppKeys.PATH, videoList.get(position).video);
                context.startActivity(playerIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
        //  return hierarchyList.size();
    }
}
