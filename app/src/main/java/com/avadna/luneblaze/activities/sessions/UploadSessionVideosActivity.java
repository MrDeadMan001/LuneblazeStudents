package com.avadna.luneblaze.activities.sessions;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.SessionVideoUploadAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionVideo;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.services.LuneBlazeUploadService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadSessionVideosActivity extends AppBaseActivity implements
        SessionVideoUploadAdapter.SessionVideoUploadAdapterCallback {

    private ActionBar actionBar;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private RelativeLayout rl_content_wrapper;
    private ProgressBar pb_loading_content;

    private String sessionId;
    private RecyclerView rv_session_photos;
    private SessionVideoUploadAdapter sessionVideoUploadAdapter;
    private List<PojoSessionVideo> videoList;
    private TextView tv_add_more, tv_done;
    private FloatingActionButton fb_add, fb_done;

    private final int PICK_VIDEO_PERMISSION = 169;
    private static final int PICK_MUTILPLE_VIDEOS_CODE = 122;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_session_videos);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initViews();
        initVideoList();
        sessionId = getIntent().getStringExtra(AppKeys.SESSION_ID);
        if (sessionId == null) {
            sessionId = "266";
        }
        hitGetSessionDetailsApi(user_id, sessionId, "photos");
        initBroadCastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.UPLOAD_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.UPLOAD_FAILED));
    }

    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.UPLOAD_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String id = intent.getStringExtra(AppKeys.UPLOAD_ID);
                    for (int i = 0; i < videoList.size(); i++) {
                        if (videoList.get(i).videoId != null
                                && videoList.get(i).videoId.equals(id)) {
                            videoList.get(i).uploadStatus = AppKeys.UPLOAD_FINISH;
                            sessionVideoUploadAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
                if (intent.getAction().equals(Config.UPLOAD_FAILED)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String id = intent.getStringExtra(AppKeys.UPLOAD_ID);
                    for (int i = 0; i < videoList.size(); i++) {
                        if (videoList.get(i).videoId != null
                                && videoList.get(i).videoId.equals(id)) {
                            //photosList.get(i).isUploading=false;
                            videoList.get(i).uploadStatus = AppKeys.UPLOAD_FAILED;
                            sessionVideoUploadAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        };
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.upload_video));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(UploadSessionVideosActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(UploadSessionVideosActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.VISIBLE);

        tv_add_more = (TextView) findViewById(R.id.tv_add_more);

        fb_add = (FloatingActionButton) findViewById(R.id.fb_add);
        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission(PICK_VIDEO_PERMISSION);
            }
        });

        fb_done = (FloatingActionButton) findViewById(R.id.fb_done);
        fb_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*tv_add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission(PICK_VIDEO_PERMISSION);

            }
        });

        tv_done = (TextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }


    private void initVideoList() {
        videoList = new ArrayList<>();
        rv_session_photos = (RecyclerView) findViewById(R.id.rv_session_photos);
        updateVideoList();
    }

    private void updateVideoList() {
        sessionVideoUploadAdapter = new SessionVideoUploadAdapter(UploadSessionVideosActivity.this,
                videoList, sessionId);
        //using horizontal linearlayout as we want horizontal list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (videoList.size() == 2) {
                    return 1;
                } else {
                    if (position == 0) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            }
        });
        rv_session_photos.setLayoutManager(gridLayoutManager);
        rv_session_photos.setAdapter(sessionVideoUploadAdapter);
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        pb_loading_content.setVisibility(View.VISIBLE);
        rl_content_wrapper.setVisibility(View.GONE);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (response.body().data != null && response.body().data.videos != null) {
                        if (view.equalsIgnoreCase("photos")) {
                            videoList.clear();
                            videoList.addAll(response.body().data.videos);
                            updateVideoList();
                            //sessionVideoUploadAdapter.notifyDataSetChanged();

                            //this is used to add items present in upload queue in case user refreshes the list mid upload
                            if (LuneBlazeUploadService.uploadQueue != null) {
                                for (int i = 0; i < LuneBlazeUploadService.uploadQueue.size(); i++) {
                                    if (LuneBlazeUploadService.uploadQueue.get(i).uploadType.equals(AppKeys.TYPE_VIDEO)) {
                                        PojoSessionVideo pojoSessionVideo =
                                                new PojoSessionVideo(LuneBlazeUploadService.uploadQueue.get(i).filePath,
                                                        LuneBlazeUploadService.uploadQueue.get(i).uploadId, AppKeys.UPLOADING);
                                        videoList.add(pojoSessionVideo);
                                    }
                                }
                                updateVideoList();
                                // sessionVideoUploadAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    pb_loading_content.setVisibility(View.GONE);
                    rl_content_wrapper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
                openNoConnectionDialog(getString(R.string.session_videos));
                //  ll_parent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSessionDetailsApi(user_id, sessionId, "photos");
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
            switch (requestCode) {
                case PICK_VIDEO_PERMISSION:
                    addSessionVideos();
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PICK_VIDEO_PERMISSION:
                    addSessionVideos();
                    break;
            }
        }

    }

    private void addSessionVideos() {
        Intent intent = new Intent();
        intent.setType("video/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
           // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_MUTILPLE_VIDEOS_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MUTILPLE_VIDEOS_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() == null) {
                String path = commonFunctions.getPathFromUri(data.getData());
                int id = new Random().nextInt(1000);
                PojoSessionVideo pojoSessionVideo = new PojoSessionVideo(path, String.valueOf(id),
                        AppKeys.UPLOADING);
                videoList.add(pojoSessionVideo);
                LuneBlazeUploadService.uploadFile(UploadSessionVideosActivity.this, sessionId,
                        path, pojoSessionVideo.videoId, AppKeys.TYPE_VIDEO);
                updateVideoList();
                // sessionVideoUploadAdapter.notifyItemInserted(videoList.size() - 1);
            } else {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    String path = commonFunctions.getPathFromUri(clipData.getItemAt(i).getUri());
                    //String path = commonFunctions.getRealPathFromUri(clipData.getItemAt(i).getUri());
                    int id = new Random().nextInt(1000);
                    PojoSessionVideo pojoSessionVideo = new PojoSessionVideo(path, String.valueOf(id),
                            AppKeys.UPLOADING);
                    videoList.add(pojoSessionVideo);
                    LuneBlazeUploadService.uploadFile(UploadSessionVideosActivity.this, sessionId,
                            path, pojoSessionVideo.videoId, AppKeys.TYPE_VIDEO);
                    updateVideoList();
//                    sessionVideoUploadAdapter.notifyItemInserted(videoList.size() - 1);
                }
            }
        }
    }

    @Override
    public void onSessionVideoUploadAdapterItemClick(int position, String type) {
        switch (type) {
            case AppKeys.ACTION_CANCEL_UPLOAD:
                LuneBlazeUploadService.cancelUpload(UploadSessionVideosActivity.this,
                        videoList.get(position).videoId);
                videoList.remove(position);
                updateVideoList();
                //sessionVideoUploadAdapter.notifyItemRemoved(position);
                break;

            case AppKeys.ACTION_RETRY_UPLOAD:
                LuneBlazeUploadService.retryUpload(UploadSessionVideosActivity.this,
                        videoList.get(position).videoId);
                videoList.get(position).uploadStatus = AppKeys.UPLOADING;
                updateVideoList();
                sessionVideoUploadAdapter.notifyItemChanged(position);
                break;
/*
            case AppKeys.ACTION_DELETE_PHOTO:
                hitDeleteSessionPhotoApi(user_id, sessionId, photosList.get(position).photoname);
                photosList.remove(position);
                sessionPhotoUploadAdapter.notifyItemRemoved(position);
                break;*/
        }
    }

    private void hitDeleteSessionPhotoApi(final String user_id, final String session_id, String name) {
        Call<PojoNoDataResponse> call = apiService.sessionPhotoDelete(user_id, session_id, name);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
