package com.avadna.luneblaze.adapters.venue;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.avadna.luneblaze.R;

import com.avadna.luneblaze.activities.venue.VenueImageGalleryViewerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;


public class VenueImageEditListAdapter extends RecyclerView.Adapter<VenueImageEditListAdapter.MyViewHolder> {

    List<PojoVenuePhoto> photos;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    VenueImageEditListAdapterCallback venueImageEditListAdapterCallback;


    public VenueImageEditListAdapter(List<PojoVenuePhoto> photos, Activity activity) {
        try {
            this.venueImageEditListAdapterCallback = ((VenueImageEditListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  venueImageEditListAdapterCallback.");
        }

        this.photos = photos;
        this.activity = activity;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_gallery_photo;
        ImageButton ib_options;
        PopupMenu popupMenu;

        ProgressBar pb_uploading;

        MyViewHolder(View view) {
            super(view);
            iv_gallery_photo = (ImageView) view.findViewById(R.id.iv_gallery_photo);
            ib_options = (ImageButton) view.findViewById(R.id.ib_options);
            pb_uploading = (ProgressBar) view.findViewById(R.id.pb_uploading);

            ib_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity, ib_options);
        }
    }

    @Override
    public VenueImageEditListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_image_edit_list_item, parent, false);

        return new VenueImageEditListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenueImageEditListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position < photos.size()) {
            holder.ib_options.setVisibility(View.VISIBLE);
            Glide.with(activity.getApplicationContext())
                    .load(photos.get(position).photo)
                    .apply(new RequestOptions()
                            .override(displayMetrics.widthPixels / 2, displayMetrics.widthPixels / 3)
                            .placeholder(R.drawable.placeholder))
                    .into(holder.iv_gallery_photo);

            holder.iv_gallery_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Type type = new TypeToken<List<PojoVenuePhoto>>() {}.getType();
                    String dataStr=new Gson().toJson(photos,type);
                    Intent imgViewerIntent=new Intent(activity, VenueImageGalleryViewerActivity.class);
                    imgViewerIntent.putExtra("data",dataStr);
                    imgViewerIntent.putExtra("position",position);
                    activity.startActivity(imgViewerIntent);
                }
            });

            holder.popupMenu.getMenu().clear();
            MenuInflater inflater = holder.popupMenu.getMenuInflater();
            inflater.inflate(R.menu.venue_photo_side_option, holder.popupMenu.getMenu());

            try {
                Field[] fields = holder.popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(holder.popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.primary:
                            venueImageEditListAdapterCallback.venueImageEditItemClickCallback(position, "primary");
                            // openEditDialog(position);
                            break;

                        case R.id.delete:
                            venueImageEditListAdapterCallback.venueImageEditItemClickCallback(position, "remove");
                            //   hitDeleteAnswerApi(user_id, currentItem.sessionsQaId, "delete_question", position);
                            break;


                    }
                    return false;
                }
            });


            if (photos.get(position).uploadStatus.equals(AppKeys.UPLOADING)) {
                holder.ib_options.setVisibility(View.GONE);
                holder.pb_uploading.setVisibility(View.VISIBLE);
            } else {
                holder.ib_options.setVisibility(View.VISIBLE);
                holder.pb_uploading.setVisibility(View.GONE);
            }
        } else {
            holder.ib_options.setVisibility(View.GONE);
            Glide.with(activity.getApplicationContext())
                    .load(R.drawable.placeholder)
                    .apply(new RequestOptions()
                            .override(displayMetrics.widthPixels / 2, displayMetrics.widthPixels / 3))
                    .into(holder.iv_gallery_photo);
        }

    }


    @Override
    public int getItemCount() {
        return photos.size();
        //  return hierarchyList.size();
    }

    public static interface VenueImageEditListAdapterCallback {
        void venueImageEditItemClickCallback(int position, String type);
    }

}

