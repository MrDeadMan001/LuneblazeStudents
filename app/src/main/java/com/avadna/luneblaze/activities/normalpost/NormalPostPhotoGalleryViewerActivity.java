package com.avadna.luneblaze.activities.normalpost;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.normalPost.NormalPostImageSliderPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.utils.CustomViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class NormalPostPhotoGalleryViewerActivity extends AppBaseActivity {


    CustomViewPager vp_image_pager;

    List<PojoNewsFeedPhoto> dataSet;
    ActionBar actionBar;

    ImageButton ib_back, ib_share;

    View.OnClickListener onClickListener;
    final int READ_STORAGE_PERMISSION=2234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_post_photo_gallery_viewer);
        setUpActionBar();
        initViews();
        initPager();
        iniClickListener();
        setClickListener();


    }


    private void initViews() {
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_share = (ImageButton) findViewById(R.id.ib_share);
    }

    private void initPager() {
        vp_image_pager = (CustomViewPager) findViewById(R.id.vp_image_pager);
        Type type = new TypeToken<List<PojoNewsFeedPhoto>>() {
        }.getType();
        dataSet = new Gson().fromJson(getIntent().getStringExtra("data"), type);
        vp_image_pager.setAdapter(new NormalPostImageSliderPagerAdapter(this, dataSet));

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
                    .getColor(NormalPostPhotoGalleryViewerActivity.this, R.color.transparent)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(NormalPostPhotoGalleryViewerActivity.this, R.color.status_bar_color));
            }
        }

    }


    private void iniClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_back:
                        finish();
                        break;
                    case R.id.ib_share:
                        requestStoragePermission(READ_STORAGE_PERMISSION);
                        break;
                }
            }
        };
    }

    private void setClickListener() {
        ib_share.setOnClickListener(onClickListener);
        ib_back.setOnClickListener(onClickListener);
    }

    private void requestStoragePermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            shareTextUrl();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareTextUrl();
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void shareTextUrl() {

        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(dataSet.get(vp_image_pager.getCurrentItem()).source)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                  Intent share = new Intent(Intent.ACTION_SEND);
                                  share.setType("image/jpeg");
                                  ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                  String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                          bitmap, "Title", null);

                                  share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                                  startActivity(Intent.createChooser(share, "Share Image"));
                                  return false;
                              }
                          }
                ).submit();


    }

}
