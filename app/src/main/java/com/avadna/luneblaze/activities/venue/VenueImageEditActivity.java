package com.avadna.luneblaze.activities.venue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.venue.VenueImageEditListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueImageEditActivity extends AppBaseActivity implements
        VenueImageEditListAdapter.VenueImageEditListAdapterCallback {

    PreferenceUtils preferenceUtils;
    String user_id;
    ApiInterface apiService;
    ActionBar actionBar;

    String venueId;
    PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;

    RecyclerView rv_venue_gallery;
    VenueImageEditListAdapter venueImageEditListAdapter;
    List<PojoVenuePhoto> photos;

    TextView tv_add_image;
    CommonFunctions commonFunctions;

    ProgressBar pb_loading_content;

    boolean isCreateApiCalled;

    File photo_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_image_edit);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        venueId = getIntent().getStringExtra(AppKeys.VENUE_ID);
        Type type = new TypeToken<List<PojoVenuePhoto>>() {
        }.getType();
        photos = new Gson().fromJson(getIntent().getStringExtra("data"), type);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        hitGetVenueByIdApi(user_id, venueId);

        initViews();
        initImageGallery();
        setUpActionBar();
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        tv_add_image = (TextView) findViewById(R.id.tv_add_image);

        tv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });
    }

    private void requestStoragePermission() {
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
                        123);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(VenueImageEditActivity.this);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(VenueImageEditActivity.this);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photo_new = commonFunctions.getScaledDownImage(result.getUri().getPath());

                hitUploadImageApi(user_id, venueId);
                photos.add(new PojoVenuePhoto(photo_new.getPath(), ""));
                venueImageEditListAdapter.notifyDataSetChanged();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void hitUploadImageApi(String user_id, String venue_id) {
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody venueIdBody = RequestBody.create(MediaType.parse("text/plain"), venue_id);
        MultipartBody.Part fileToUpload = null;

        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(photo_new)), photo_new);
        fileToUpload = MultipartBody.Part.createFormData("photo", photo_new.getName(), mFile);

        tv_add_image.setVisibility(View.GONE);
        Call<PojoNoDataResponse> call = apiService.addVenueImage(userIdBody, venueIdBody, fileToUpload);
        if (!isCreateApiCalled) {
            isCreateApiCalled = true;
            call.enqueue(new Callback<PojoNoDataResponse>() {
                @Override
                public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                    if (response.body() != null) {
                      /*  commonFunctions.setToastMessage(VenueImageEditActivity.this,
                                getString(R.string.article_created_successfully), Toast.LENGTH_LONG,
                                AppKeys.TOAST_USER);*/
                        hitGetVenueByIdApi(user_id, venueId);
                        photos.get(photos.size() - 1).uploadStatus = AppKeys.UPLOAD_FINISH;
                        venueImageEditListAdapter.notifyDataSetChanged();
                        tv_add_image.setVisibility(View.VISIBLE);
                        Intent groupUpdate = new Intent(Config.VENUE_PHOTOS_UPDATED);
                        LocalBroadcastManager.getInstance(VenueImageEditActivity.this).sendBroadcast(groupUpdate);
                    }
                    isCreateApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {

                    // Log error here since request failed
                    commonFunctions.setToastMessage(VenueImageEditActivity.this, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    hitGetVenueByIdApi(user_id, venueId);
                    photos.get(photos.size() - 1).uploadStatus = AppKeys.UPLOAD_FAILED;
                    venueImageEditListAdapter.notifyDataSetChanged();
                    tv_add_image.setVisibility(View.VISIBLE);
                    isCreateApiCalled = false;

                }
            });
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.update_photo));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VenueImageEditActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VenueImageEditActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initImageGallery() {
        //Photos List
        if (photos == null) {
            photos = new ArrayList<>();
        }
        rv_venue_gallery = (RecyclerView) findViewById(R.id.rv_venue_gallery);
        venueImageEditListAdapter = new VenueImageEditListAdapter(photos,
                VenueImageEditActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(VenueImageEditActivity.this, 2);
        rv_venue_gallery.setLayoutManager(gridLayoutManager);
        rv_venue_gallery.setAdapter(venueImageEditListAdapter);
    }

    private void hitGetVenueByIdApi(String user_id, String venue_id) {
        Call<PojoGetVenueDetailsResponse> call = apiService.getVenueDetailsFromIdApi(user_id, venue_id,
                "upcoming");
        call.enqueue(new Callback<PojoGetVenueDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoGetVenueDetailsResponse> call, Response<PojoGetVenueDetailsResponse> response) {
                String message = "";
                if (response.body() != null && response.body().data != null) {
                    message = response.body().message;
                    pojoGetVenueDetailsResponse = response.body();
                    photos.clear();
                    photos.addAll(response.body().data.allphoto);
                    venueImageEditListAdapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<PojoGetVenueDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (photos.isEmpty()) {
                    openNoConnectionDialog(getString(R.string.update_photo));
                }
            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetVenueByIdApi(user_id, venueId);
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }


    }

    @Override
    public void venueImageEditItemClickCallback(int position, String type) {
        switch (type) {
            case "remove":
                removeVenuePhoto(user_id, venueId, photos.get(position).photoid);
                photos.remove(position);
                venueImageEditListAdapter.notifyDataSetChanged();
                break;

            case "primary":
                makeVenueCoverPhoto(user_id, venueId, photos.get(position).photoid);
                break;
        }

    }


    private void makeVenueCoverPhoto(String user_id, String venue_id, String photoId) {
        Call<PojoNoDataResponse> call = apiService.makeVenueCoverPhoto(user_id, venue_id,
                photoId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response.body() != null) {
                    Intent groupUpdate = new Intent(Config.VENUE_PHOTOS_UPDATED);
                    LocalBroadcastManager.getInstance(VenueImageEditActivity.this).sendBroadcast(groupUpdate);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed

            }
        });
    }


    private void removeVenuePhoto(String user_id, String venue_id, String photoId) {
        Call<PojoNoDataResponse> call = apiService.deleteVenueImage(user_id, venue_id,
                photoId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response.body() != null) {
                    Intent groupUpdate = new Intent(Config.VENUE_PHOTOS_UPDATED);
                    LocalBroadcastManager.getInstance(VenueImageEditActivity.this).sendBroadcast(groupUpdate);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
