package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.avadna.luneblaze.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class PostCreationAddedImageAdapter extends RecyclerView.Adapter<PostCreationAddedImageAdapter.MyViewHolder> {
    Activity activity;
    List<Uri> imagesUri;
    PostCreationImageAdapterCallback postCreationImageAdapterCallback;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        ImageButton ib_remove;

        MyViewHolder(View view) {
            super(view);
            iv_image = (ImageView) view.findViewById(R.id.iv_image);
            ib_remove= (ImageButton) view.findViewById(R.id.ib_remove);
        }
    }


    public PostCreationAddedImageAdapter(Activity activity, List<Uri> imagesUri, List<String> imagePaths) {
        try {
            this.postCreationImageAdapterCallback = ((PostCreationImageAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement PostCreationImageAdapterCallback.");
        }
        this.activity = activity;
        this.imagesUri = imagesUri;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public PostCreationAddedImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_creation_image_item, parent, false);

        return new PostCreationAddedImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostCreationAddedImageAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(imagesUri.get(position))
                .into(holder.iv_image);

        holder.ib_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCreationImageAdapterCallback.postCreationImageMethodCallback(position,"remove");
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesUri.size();
        //  return hierarchyList.size();
    }

    public static interface PostCreationImageAdapterCallback {
        void postCreationImageMethodCallback(int position, String type);
    }
}



