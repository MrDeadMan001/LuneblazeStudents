package com.avadna.luneblaze.activities.venue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.SessionImageSliderPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.utils.CustomViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class VenueImageGalleryViewerActivity extends AppBaseActivity {

    CustomViewPager vp_image_pager;
    List<PojoVenuePhoto> dataSet;
    ActionBar actionBar;
    ImageButton ib_back,ib_share;
    View.OnClickListener onClickListener;
    String venueId="3";
    String venueName="BSAITM";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_photo_gallery_viewer);

        setUpActionBar();
        initViews();
        initPager();
        iniClickListener();
        setClickListener();
    }


    private void initViews() {
        ib_back=(ImageButton)findViewById(R.id.ib_back);
        ib_share=(ImageButton)findViewById(R.id.ib_share);
    }

    private void initPager() {
        vp_image_pager = (CustomViewPager) findViewById(R.id.vp_image_pager);
        Type type = new TypeToken<List<PojoVenuePhoto>>() {}.getType();
        dataSet = new Gson().fromJson(getIntent().getStringExtra("data"), type);
        vp_image_pager.setAdapter(new SessionImageSliderPagerAdapter(this,dataSet));
        int position = getIntent().getIntExtra("position", 0);
        vp_image_pager.setCurrentItem(position);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.photos));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VenueImageGalleryViewerActivity.this, R.color.transparent)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VenueImageGalleryViewerActivity.this, R.color.status_bar_color));
            }
        }

    }



    private void iniClickListener() {
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.ib_back:
                        finish();
                        break;
                    case R.id.ib_share:
                        shareTextUrl();
                        break;
                }
            }
        };
    }

    private void setClickListener() {
        ib_share.setOnClickListener(onClickListener);
        ib_back.setOnClickListener(onClickListener);
    }


    private void shareTextUrl() {
        final Context context=this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(dataSet.get(vp_image_pager.getCurrentItem()).photo)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                                                             DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(VenueImageGalleryViewerActivity.this,
                                          AppKeys.SHARE_SESSION_GALLERY_IMAGE,venueId,venueName,bitmap);
                                  return false;
                              }
                          }
                ).submit();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
