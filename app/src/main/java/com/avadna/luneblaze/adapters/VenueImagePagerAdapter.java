package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueImageGalleryViewerActivity;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class VenueImagePagerAdapter extends PagerAdapter {

    private Activity _activity;
    List<PojoVenuePhoto> venuePhotos;
    private LayoutInflater inflater;

    // constructor
    public VenueImagePagerAdapter(Activity activity,  List<PojoVenuePhoto> venuePhotos) {
        this._activity = activity;
        this.venuePhotos = venuePhotos;
    }

    @Override
    public int getCount() {
        return venuePhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv_photo;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.venue_photo_list_item, container, false);

        iv_photo = (ImageView) viewLayout.findViewById(R.id.iv_photo);


        Glide.with(_activity.getApplicationContext())
                .load(venuePhotos.get(position).photo)
                .into(iv_photo);

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Type type = new TypeToken<List<PojoVenuePhoto>>() {}.getType();
                String dataStr=new Gson().toJson(venuePhotos,type);
                Intent imgViewerIntent=new Intent(_activity, VenueImageGalleryViewerActivity.class);
                imgViewerIntent.putExtra("data",dataStr);
                imgViewerIntent.putExtra("position",position);
                _activity.startActivity(imgViewerIntent);
            }
        });


        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}