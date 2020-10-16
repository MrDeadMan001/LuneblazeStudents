package com.avadna.luneblaze.adapters.session.organised;

import android.app.Activity;
import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avadna.luneblaze.activities.sessions.SessionPhotoGalleryViewerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.bumptech.glide.Glide;
import com.avadna.luneblaze.R;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Sunny on 05-01-2018.
 */

public class OrganisedSessionPhotoGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //  pivate List<String> hierarchyList;
    Activity activity;
    List<PojoVenuePhoto> dataSet;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    String type;
    String sessionId;

    String sessionName;


    public void setSessionName(String name){
        sessionName=name;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_gallery_photo;
        RelativeLayout rl_image_parent;
        //    RelativeLayout rl_more_button;

        GridViewHolder(View view) {
            super(view);
            iv_gallery_photo = (ImageView) view.findViewById(R.id.iv_gallery_photo);
            rl_image_parent = (RelativeLayout) view.findViewById(R.id.rl_image_parent);
            //  rl_more_button=(RelativeLayout) view.findViewById(R.id.rl_more_button);
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        //    RelativeLayout rl_more_button;

        HorizontalViewHolder(View view) {
            super(view);
            iv_image = (ImageView) view.findViewById(R.id.iv_image);

        }
    }

    public OrganisedSessionPhotoGalleryAdapter(Activity activity, String sessionId, List<PojoVenuePhoto> dataSet, String type) {
        this.activity = activity;
        this.dataSet = dataSet;
        this.type = type;
        this.sessionId=sessionId;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        switch (type) {
            case "grid":
                itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.session_photo_gallery_item, parent, false);
                return new GridViewHolder(itemView);

            case "hor":
                itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_image_gallery_item, parent, false);
                return new HorizontalViewHolder(itemView);

            default:
                itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_image_gallery_item, parent, false);

                return new HorizontalViewHolder(itemView);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (type.equals("grid")) {
            GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
            configGridViewHolder(gridViewHolder,position);
        } else if (type.equals("hor")) {
            HorizontalViewHolder horizontalViewHolder= (HorizontalViewHolder) viewHolder;
            configHorizontalViewHolder(horizontalViewHolder,position);
        }
    }

    private void configGridViewHolder(GridViewHolder gridViewHolder, final int position) {
        if(activity!=null)
            Glide.with(activity.getApplicationContext())
                .load(dataSet.get(position).photo)
                .apply(new RequestOptions()
                        .override(displayMetrics.widthPixels/2,displayMetrics.widthPixels/3)
                        .placeholder(R.drawable.placeholder))
                .into(gridViewHolder.iv_gallery_photo);

        gridViewHolder.iv_gallery_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Type type = new TypeToken<List<PojoVenuePhoto>>() {}.getType();
                String dataStr=new Gson().toJson(dataSet,type);
                Intent imgViewerIntent=new Intent(activity,SessionPhotoGalleryViewerActivity.class);
                imgViewerIntent.putExtra("data",dataStr);
                imgViewerIntent.putExtra("position",position);
                imgViewerIntent.putExtra(AppKeys.SESSION_ID,sessionId);
                imgViewerIntent.putExtra(AppKeys.SESSION_NAME,sessionName);

                activity.startActivity(imgViewerIntent);
                // openImageGalleryDialog(position);
            }
        });

    }

    private void configHorizontalViewHolder(HorizontalViewHolder horizontalViewHolder, int position) {
        /*if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(dataSet.get(position).photo)
                .into(horizontalViewHolder.iv_image);*/

    }


    private void openImageGalleryDialog(int position) {
        Dialog imageGalleryDialog;
        RecyclerView rv_images;
        imageGalleryDialog = new MyCustomThemeDialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        imageGalleryDialog.setContentView(R.layout.session_image_viewer_dialog);
        rv_images = (RecyclerView) imageGalleryDialog.findViewById(R.id.rv_images);
        SnapHelper startSnapHelper = new PagerSnapHelper();
        startSnapHelper.attachToRecyclerView(rv_images);
        rv_images.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        rv_images.setAdapter(new OrganisedSessionPhotoGalleryAdapter(activity, sessionId, dataSet, "hor"));
        rv_images.scrollToPosition(position);
        imageGalleryDialog.show();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}

