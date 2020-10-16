package com.avadna.luneblaze.activities.sessions;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.SessionFileUploadAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyFileUtils;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionFile;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.services.LuneBlazeUploadService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadSessionFilesActivity extends AppBaseActivity
        implements SessionFileUploadAdapter.SessionFileUploadAdapterCallback {

    private static final int DOWN_LOAD_FILE_PERMISSION = 2345;
    private ActionBar actionBar;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private RelativeLayout rl_content_wrapper;
    private ProgressBar pb_loading_content;

    private String sessionId;
    private TextView tv_add_more, tv_done;
    private FloatingActionButton fb_add, fb_done;
    private RecyclerView rv_session_photos;
    private SessionFileUploadAdapter sessionFileUploadAdapter;
    private List<PojoSessionFile> fileList;
    final int PICK_FILE_PERMISSION = 169;
    private static final int PICK_FILES_REQUEST_CODE = 122;
    private BroadcastReceiver broadcastReceiver;
    private String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_session_files);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initViews();
        initPhotoListList();
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
                    for (int i = 0; i < fileList.size(); i++) {
                        if (fileList.get(i).id != null
                                && fileList.get(i).id.equals(id)) {
                            fileList.get(i).uploadStatus = AppKeys.UPLOAD_FINISH;
                            sessionFileUploadAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
                if (intent.getAction().equals(Config.UPLOAD_FAILED)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String id = intent.getStringExtra(AppKeys.UPLOAD_ID);
                    for (int i = 0; i < fileList.size(); i++) {
                        if (fileList.get(i).id != null && fileList.get(i).id.equals(id)) {
                            fileList.get(i).uploadStatus = AppKeys.UPLOAD_FAILED;
                            sessionFileUploadAdapter.notifyItemChanged(i);
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
            actionBar.setTitle(getString(R.string.session_files));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(UploadSessionFilesActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(UploadSessionFilesActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.VISIBLE);

        fb_add = (FloatingActionButton) findViewById(R.id.fb_add);
        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission(PICK_FILE_PERMISSION, null);
            }
        });

        fb_done = (FloatingActionButton) findViewById(R.id.fb_done);
        fb_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

     /*   tv_add_more = (TextView) findViewById(R.id.tv_add_more);
        tv_add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission(PICK_FILE_PERMISSION, null);
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


    private void initPhotoListList() {
        fileList = new ArrayList<>();
        rv_session_photos = (RecyclerView) findViewById(R.id.rv_session_photos);
        sessionFileUploadAdapter = new SessionFileUploadAdapter(UploadSessionFilesActivity.this,
                fileList, sessionId);
        //using horizontal linearlayout as we want horizontal list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv_session_photos.setLayoutManager(gridLayoutManager);
        rv_session_photos.setAdapter(sessionFileUploadAdapter);
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
      /*  pb_loading_content.setVisibility(View.VISIBLE);
        rl_content_wrapper.setVisibility(View.GONE);*/
        rl_content_wrapper.setVisibility(View.VISIBLE);

        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (response.body().data != null && response.body().data.files != null) {
                        if (view.equalsIgnoreCase("photos")) {
                            fileList.clear();
                            fileList.addAll(response.body().data.files);
                            sessionFileUploadAdapter.notifyDataSetChanged();
                            //this is used to add items present in upload queue in case user refreshes the list mid upload
                            if (LuneBlazeUploadService.uploadQueue != null) {
                                for (int i = 0; i < LuneBlazeUploadService.uploadQueue.size(); i++) {
                                    if (LuneBlazeUploadService.uploadQueue.get(i).uploadType.equals(AppKeys.TYPE_FILE)) {
                                        PojoSessionFile pojoSessionFile =
                                                new PojoSessionFile(LuneBlazeUploadService.uploadQueue.get(i).filePath,
                                                        LuneBlazeUploadService.uploadQueue.get(i).uploadId, AppKeys.UPLOADING);
                                        fileList.add(pojoSessionFile);
                                    }
                                }
                                sessionFileUploadAdapter.notifyDataSetChanged();
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
                openNoConnectionDialog(getString(R.string.session_files));
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

    private void requestStoragePermission(int requestCode, String fileUrl) {
        downloadUrl = fileUrl;
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
                case PICK_FILE_PERMISSION:
                    pickUploadFile();
                    break;

                case DOWN_LOAD_FILE_PERMISSION:
                    downloadFile(downloadUrl);
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
                case PICK_FILE_PERMISSION:
                    pickUploadFile();
                    break;

                case DOWN_LOAD_FILE_PERMISSION:
                    downloadFile(downloadUrl);
                    break;
            }
        }

    }

    private void pickUploadFile() {
        Intent intent = null;
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        } else {
            String[] mimeTypes =
                    {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                            "text/plain",
                            "application/pdf",
                            "application/zip", "application/vnd.android.package-archive"};

            intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
            intent.setType("*/*");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        }
        if (intent != null) {
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILES_REQUEST_CODE);
        }
    }

    /*   @Override
       public void onActivityResult(int requestCode, int resultCode, Intent data) {
           super.onActivityResult(requestCode, resultCode, data);
           if (requestCode == PICK_FILES_REQUEST_CODE && resultCode == RESULT_OK) {
               int id = new Random().nextInt(1000);
               String filePath = commonFunctions.getPathFromUri(data.getData());
               PojoSessionFile pojoSessionFile = new PojoSessionFile(filePath, String.valueOf(id));
               fileList.add(pojoSessionFile);
               LuneBlazeUploadService.uploadFile(UploadSessionFilesActivity.this, sessionId,
                       filePath, pojoSessionFile.id, AppKeys.TYPE_FILE);
               sessionFileUploadAdapter.notifyDataSetChanged();
           }
       }
   */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILES_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() == null) {
                int id = new Random().nextInt(1000);
                MyFileUtils myFileUtils = new MyFileUtils(this);
                String filePath = myFileUtils.getPath(data.getData());
                // String filePath = commonFunctions.getPathFromUri(data.getData());
                if (new File(filePath).exists()) {
                    PojoSessionFile pojoSessionFile = new PojoSessionFile(filePath, String.valueOf(id),
                            AppKeys.UPLOADING);
                    fileList.add(pojoSessionFile);
                    LuneBlazeUploadService.uploadFile(UploadSessionFilesActivity.this, sessionId,
                            filePath, pojoSessionFile.id, AppKeys.TYPE_FILE);
                    sessionFileUploadAdapter.notifyItemInserted(fileList.size() - 1);
                }

            } else {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    int id = new Random().nextInt(1000);
                    MyFileUtils myFileUtils = new MyFileUtils(this);
                    String filePath = myFileUtils.getPath(data.getData());
                    //String filePath = commonFunctions.getPathFromUri(clipData.getItemAt(i).getUri());
                    if (new File(filePath).exists()) {
                        PojoSessionFile pojoSessionFile = new PojoSessionFile(filePath, String.valueOf(id),
                                AppKeys.UPLOADING);
                        fileList.add(pojoSessionFile);
                        LuneBlazeUploadService.uploadFile(UploadSessionFilesActivity.this, sessionId,
                                filePath, pojoSessionFile.id, AppKeys.TYPE_FILE);
                        sessionFileUploadAdapter.notifyItemInserted(fileList.size() - 1);
                    }
                }
            }
        }
    }

    @Override
    public void onSessionFileUploadAdapterItemClick(int position, String type) {
        switch (type) {
            case AppKeys.ACTION_CANCEL_UPLOAD:
                LuneBlazeUploadService.cancelUpload(UploadSessionFilesActivity.this,
                        fileList.get(position).id);
                fileList.remove(position);
                sessionFileUploadAdapter.notifyItemRemoved(position);
                break;

            case AppKeys.ACTION_RETRY_UPLOAD:
                LuneBlazeUploadService.retryUpload(UploadSessionFilesActivity.this,
                        fileList.get(position).id);
                fileList.get(position).uploadStatus = AppKeys.UPLOADING;
                sessionFileUploadAdapter.notifyItemChanged(position);
                break;

            case AppKeys.ACTION_DOWNLOAD_FILE:
                requestStoragePermission(DOWN_LOAD_FILE_PERMISSION, fileList.get(position).file);
                break;

        }
    }


    public void downloadFile(String link) {

        if (link.contains("http")) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/Luneblaze/Session_files/" + sessionId + "/");
            if (!direct.exists()) {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()
                        + "/Luneblaze/Session_files/" + sessionId + "/");
                wallpaperDirectory.mkdirs();
            }
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(link);
            StringBuilder sb = new StringBuilder(link);
            sb = sb.reverse();
            String name = sb.substring(sb.indexOf("."), sb.indexOf("/"));
            sb = new StringBuilder(name);
            sb = sb.reverse();
            String filename = sb.toString();
            File file = new File(direct, filename);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri fileUri = FileProvider.getUriForFile(UploadSessionFilesActivity.this,
                        getApplicationContext().getPackageName() + ".fileprovider", file);
                intent.setDataAndType(fileUri, "application/" + fileExtension);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link))
                        .setTitle("Session file")// Title of the Download Notification
                        .setDescription("Downloading")// Description of the Download Notification
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                        .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                        .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                        .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                long downloadID = downloadManager.enqueue(request);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String fileExtension = link.substring(link.lastIndexOf("."));
            File file = new File(link);
            /*
            Uri fileUri = FileProvider.getUriForFile(UploadSessionFilesActivity.this,
                    getApplicationContext().getPackageName() + ".fileprovider", file);*/
            intent.setDataAndType(Uri.fromFile(file), "application/" + fileExtension);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
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
