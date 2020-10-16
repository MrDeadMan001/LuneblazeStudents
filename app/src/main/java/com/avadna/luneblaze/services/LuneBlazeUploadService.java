package com.avadna.luneblaze.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ProgressRequestBody;
import com.avadna.luneblaze.helperClasses.videocompressor.VideoCompress;
import com.avadna.luneblaze.pojo.PojoFileUploadItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.avadna.luneblaze.helperClasses.videocompressor.VideoController.RESOLUTION_360P;
import static com.avadna.luneblaze.helperClasses.videocompressor.VideoController.RESOLUTION_480P;
import static com.avadna.luneblaze.helperClasses.videocompressor.VideoController.RESOLUTION_720P;


public class LuneBlazeUploadService extends IntentService implements ProgressRequestBody.UploadCallbacks {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPLOAD_VIDEO = "upload_video";
    private static final String ACTION_UPLOAD_FILE = "upload_file";

    // TODO: Rename parameters
    private static final String PATH = "path";
    private static final String EXTRA_PARAM2 = "com.avadna.luneblaze.services.extra.PARAM2";
    private static final String ACTION_UPLOAD_IMAGES = "upload_iamge";

    static String session_id;
    static String user_id;
    static ApiInterface apiService;
    static PreferenceUtils preferenceUtils;
    static CommonFunctions commonFunctions;

    static NotificationManagerCompat notificationManager;
    static NotificationCompat.Builder builder;

    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;

    public static List<PojoFileUploadItem> uploadQueue;
    final int MAX_NUM_CALLS = 2;
    int num_of_calls = 0;


    public LuneBlazeUploadService() {
        super("VideoUploadService");
    }

    public static void uploadFile(Context context, String passed_session_id, String path,
                                  String upload_id, String type) {
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(context);
        commonFunctions = new CommonFunctions(context);
        user_id = preferenceUtils.get_user_id();
        session_id = passed_session_id;

        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, AppKeys.channel_ids[1]);


        Intent intent = new Intent(context, LuneBlazeUploadService.class);
        intent.putExtra(PATH, path);
        intent.putExtra(AppKeys.UPLOAD_ID, upload_id);
        intent.putExtra(AppKeys.TYPE, type);
        context.startService(intent);
    }

    public static void cancelUpload(Context context, String upload_id) {
        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, AppKeys.channel_ids[1]);
        Intent intent = new Intent(context, LuneBlazeUploadService.class);
        intent.putExtra(AppKeys.UPLOAD_ID, upload_id);
        intent.putExtra(AppKeys.TYPE, AppKeys.ACTION_CANCEL_UPLOAD);
        context.startService(intent);
    }

    public static void retryUpload(Context context, String upload_id) {
        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, AppKeys.channel_ids[1]);
        Intent intent = new Intent(context, LuneBlazeUploadService.class);
        intent.putExtra(AppKeys.UPLOAD_ID, upload_id);
        intent.putExtra(AppKeys.TYPE, AppKeys.ACTION_RETRY_UPLOAD);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String path = intent.getStringExtra(PATH);
            final String upload_id = intent.getStringExtra(AppKeys.UPLOAD_ID);
            final String type = intent.getStringExtra(AppKeys.TYPE);
            switch (type) {
                case AppKeys.TYPE_IMAGE:
                case AppKeys.TYPE_FILE:
                    addToUploadQueue(user_id, session_id, type, path, upload_id);
                    break;

                case AppKeys.TYPE_VIDEO:
                    handleUploadVideo(user_id, session_id, path, type, upload_id);
                    break;

                case AppKeys.ACTION_CANCEL_UPLOAD:
                    handleCancelUpload(upload_id);
                    break;

                case AppKeys.ACTION_RETRY_UPLOAD:
                    handleRetryUpload(upload_id);
                    break;

            }
        }
    }

    private void handleRetryUpload(String upload_id) {
        if (uploadQueue != null) {
            for (int i = 0; i < uploadQueue.size(); i++) {
                if (uploadQueue.get(i).uploadId.equals(upload_id)) {
                    uploadQueue.get(i).apiCall = uploadQueue.get(i).apiCall.clone();
                    hitUploadApi();
                }
            }
        }
    }

    private void handleCancelUpload(String upload_id) {
        if (uploadQueue != null) {
            for (int i = 0; i < uploadQueue.size(); i++) {
                if (uploadQueue.get(i).uploadId.equals(upload_id)) {
                    uploadQueue.get(i).apiCall.cancel();
                    uploadQueue.remove(i);
                    if (getPendingUploadCount() == 0 && uploadQueue.size() > i) {
                        notificationManager.cancel(uploadQueue.get(i).notificationId);
                    }
                    break;
                }
            }
        }
    }

    private int getPendingUploadCount() {
        int count = 0;
        if (uploadQueue != null) {
            for (int i = 0; i < uploadQueue.size(); i++) {
                if (!uploadQueue.get(i).apiCall.isExecuted()
                        || !uploadQueue.get(i).apiCall.isCanceled()) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleUploadVideo(String user_id, String session_id, String path, String type, String upload_id) {

        builder.setContentTitle("File upload")
                .setContentText("Preparing video for upload")
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        File inputFile = new File(path);

        File destinationDirectory = new File(Environment.getExternalStorageDirectory() + File.separator
                + "Luneblaze/Luneblaze_videos/");

        if (!destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            destinationDirectory.mkdirs();
        }
        File destination = new File(new File(Environment.getExternalStorageDirectory() + File.separator
                + "Luneblaze/Luneblaze_videos/"), "temp_video.mp4");
        if (destination.exists()) {
            destination.delete();
        }
        long fileSize = inputFile.length() / 1024;
        fileSize = fileSize / 1024;
        //compress the video
        int quality;
        if (fileSize < 100) {
            quality = RESOLUTION_720P;
        } else if (fileSize < 400) {
            quality = RESOLUTION_480P;
        } else {
            quality = RESOLUTION_360P;
        }
        VideoCompress.VideoCompressTask task = VideoCompress.compressVideo(path,
                destination.getPath(), quality, new VideoCompress.CompressListener() {
                    @Override
                    public void onStart() {
                        //Start Compress
                        Log.d("Compression", "started");
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(54321, builder.build());
                    }

                    @Override
                    public void onSuccess() {
                        //Finish successfully
                        Log.d("Compression", "finish");
                        if (destination.length() > inputFile.length()) {
                            addToUploadQueue(user_id, session_id, type, inputFile.getAbsolutePath(), upload_id);
                            //  hitUploadFileApi(user_id, session_id, "video", inputFile.getAbsolutePath());
                        } else {
                            addToUploadQueue(user_id, session_id, type, destination.getAbsolutePath(), upload_id);
                            // hitUploadFileApi(user_id, session_id, "video", destination.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("Compression", "failed");
                        //Failed
                    }

                    @Override
                    public void onProgress(float percent) {
                        //Progress
                        builder.setProgress(PROGRESS_MAX, (int) percent, false);
                        notificationManager.notify(54321, builder.build());
                    }
                });
    }

    private void addToUploadQueue(String user_id, String session_id, String type, String filePath,
                                  String upload_id) {
        int notifId = 10000;
        if (uploadQueue == null) {
            uploadQueue = new ArrayList<>();
        } /*else {
            notifId = uploadQueue.get(uploadQueue.size() - 1).notificationId + 1;
        }*/

        PojoFileUploadItem uploadItem = new PojoFileUploadItem();
        uploadItem.notificationId = notifId;
        uploadItem.uploadType = type;
        uploadItem.filePath = filePath;
        uploadItem.uploadId = upload_id;
        uploadItem.uploadCallbacks = new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                // builder.setProgress(PROGRESS_MAX, (int) percentage, false);
                // notificationManager.notify(uploadItem.notificationId, builder.build());
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {
                if (uploadQueue.isEmpty()) {
                    builder.setContentText("Upload complete")
                            .setProgress(0, 0, false);
                    notificationManager.notify(uploadItem.notificationId, builder.build());
                    //notificationManager.cancel(uploadItem.notificationId);
                } /*else {
                    builder.setContentText(uploadQueue.size() + "remaining");

                    notificationManager.notify(uploadItem.notificationId, builder.build());
                }*/

                //  notificationManager.cancel(uploadItem.notificationId);
            }

            @Override
            public void uploadStart() {
                builder.setContentTitle("Uploading....")
                        .setContentText((getPendingUploadCount() + 1) + " remaining")
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setOnlyAlertOnce(true)
                        .setPriority(NotificationCompat.PRIORITY_LOW);

                notificationManager.notify(uploadItem.notificationId, builder.build());
            }
        };
        MultipartBody.Part fileToUpload = null;
        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody session_id_body = RequestBody.create(MediaType.parse("text/plain"), session_id);
        RequestBody type_body = RequestBody.create(MediaType.parse("text/plain"), type);

        switch (type) {
            case AppKeys.TYPE_VIDEO:
                if (filePath.isEmpty()) {
                    fileToUpload = null;
                } else {
                    File file = new File(filePath);
                    ProgressRequestBody mFile = new ProgressRequestBody(file, uploadItem.uploadCallbacks);
                    fileToUpload = MultipartBody.Part.createFormData("video", file.getName(), mFile);
                }
                break;

            case AppKeys.TYPE_FILE:
                if (filePath.isEmpty()) {
                    fileToUpload = null;
                } else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        ProgressRequestBody mFile = new ProgressRequestBody(file, uploadItem.uploadCallbacks);
                        fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                    } else {
                        fileToUpload = null;
                    }
                }
                break;

            case AppKeys.TYPE_IMAGE:
                File photo = commonFunctions.getScaledDownImage(filePath);
                ProgressRequestBody mFile = new ProgressRequestBody(photo, uploadItem.uploadCallbacks);
                fileToUpload = MultipartBody.Part.createFormData("photo[0]", photo.getName(), mFile);

                break;
        }
        uploadItem.apiCall = apiService.uploadSessionFile(user_id_body, session_id_body,
                type_body, fileToUpload);
        if (fileToUpload != null) {
            uploadQueue.add(uploadItem);
        }
        hitUploadApi();
    }

    private void hitUploadApi() {
        if (!uploadQueue.isEmpty()) {
            PojoFileUploadItem currentItem = null;
            for (int i = uploadQueue.size() - 1; i >= 0; i--) {
                if (!uploadQueue.get(i).apiCall.isExecuted()) {
                    currentItem = uploadQueue.get(i);
                }
            }
            if (currentItem != null) {
                PojoFileUploadItem finalCurrentItem = currentItem;
                currentItem.apiCall.enqueue(new Callback<PojoNoDataResponse>() {
                    @Override
                    public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                        if (response.body() != null && response.body().status == 1) {
                            uploadQueue.remove(finalCurrentItem);
                            if (uploadQueue.isEmpty()) {
                                builder.setContentText("Upload complete")
                                        .setProgress(0, 0, false);
                                notificationManager.notify(finalCurrentItem.notificationId, builder.build());
                                notificationManager.cancel(finalCurrentItem.notificationId);
                            } else {
                                builder.setContentText(getPendingUploadCount() + " remaining");
                                notificationManager.notify(finalCurrentItem.notificationId, builder.build());
                            }

                            Intent intent = new Intent(Config.UPLOAD_COMPLETE);
                            intent.putExtra(AppKeys.UPLOAD_ID, finalCurrentItem.uploadId);
                            LocalBroadcastManager.getInstance(LuneBlazeUploadService.this).sendBroadcast(intent);
                            hitUploadApi();
                        } else {
                            Intent intent = new Intent(Config.UPLOAD_FAILED);
                            intent.putExtra(AppKeys.UPLOAD_ID, finalCurrentItem.uploadId);
                            LocalBroadcastManager.getInstance(LuneBlazeUploadService.this).sendBroadcast(intent);
                        }
                        num_of_calls--;
                    }

                    @Override
                    public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                        // Log error here since request failed
                        commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        /*builder.setContentText("Upload Failed")
                                .setProgress(0, 0, false);
                        notificationManager.notify(currentItem.notificationId, builder.build());*/
                        Intent intent = new Intent(Config.UPLOAD_FAILED);
                        intent.putExtra(AppKeys.UPLOAD_ID, finalCurrentItem.uploadId);
                        LocalBroadcastManager.getInstance(LuneBlazeUploadService.this).sendBroadcast(intent);

                        // notificationManager.cancel(uploadQueue.get(num_of_calls).notificationId);
                        num_of_calls--;
                        //currentItem.apiCall=currentItem.apiCall.clone();
                        //hitUploadApi();
                    }
                });
                num_of_calls++;
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */

    private void hitUploadFileApi(String userID, String sessionID, String type, String filePath) {
        MultipartBody.Part fileToUpload;
        if (filePath.isEmpty()) {
            fileToUpload = null;
        } else {
            File file = new File(filePath);
            ProgressRequestBody mFile = new ProgressRequestBody(file, this);
            fileToUpload = MultipartBody.Part.createFormData("video", file.getName(), mFile);
        }
        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), userID);
        RequestBody session_id_body = RequestBody.create(MediaType.parse("text/plain"), sessionID);
        RequestBody type_body = RequestBody.create(MediaType.parse("text/plain"), type);

        //addChatMemberListAdapter the creator to group as well
        //  recipientMap.put("recipients[" + recipients.size() + "]", RequestBody.create(MediaType.parse("text/plain"), user));

        Call<PojoNoDataResponse> call = apiService.uploadSessionFile(user_id_body, session_id_body,
                type_body, fileToUpload);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    builder.setContentText("Upload complete")
                            .setProgress(0, 0, false);
                    notificationManager.notify(54321, builder.build());
                    notificationManager.cancel(54321);
                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }


    private void handleUploadImage(String user_id, String session_id, String type, String filePath) {
        // TODO: Handle action Baz
        MultipartBody.Part fileToUpload = null;
        File photo = commonFunctions.getScaledDownImage(filePath);
        ProgressRequestBody mFile = new ProgressRequestBody(photo, this);
        fileToUpload = MultipartBody.Part.createFormData("video", photo.getName(), mFile);
        fileToUpload = MultipartBody.Part.createFormData("photo[0]", photo.getName(), mFile);
        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody session_id_body = RequestBody.create(MediaType.parse("text/plain"), session_id);
        RequestBody type_body = RequestBody.create(MediaType.parse("text/plain"), type);

        //addChatMemberListAdapter the creator to group as well
        //  recipientMap.put("recipients[" + recipients.size() + "]", RequestBody.create(MediaType.parse("text/plain"), user));

        Call<PojoNoDataResponse> call = apiService.uploadSessionFile(user_id_body, session_id_body,
                type_body, fileToUpload);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    Log.d("upload status", "Uploaded " + filePath);
                    builder.setContentText("Upload complete");
                    notificationManager.notify(54321, builder.build());
                    notificationManager.cancel(54321);
                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                Log.d("upload status", "Failed to upload " + filePath);

                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void handleUploadFile(String user_id, String session_id, String type, String filePath) {
        // TODO: Handle action Baz
        MultipartBody.Part fileToUpload;
        if (filePath.isEmpty()) {
            fileToUpload = null;
        } else {
            File file = new File(filePath);
            ProgressRequestBody mFile = new ProgressRequestBody(file, this);
            fileToUpload = MultipartBody.Part.createFormData("video", file.getName(), mFile);
        }
        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody session_id_body = RequestBody.create(MediaType.parse("text/plain"), session_id);
        RequestBody type_body = RequestBody.create(MediaType.parse("text/plain"), type);

        //addChatMemberListAdapter the creator to group as well
        //  recipientMap.put("recipients[" + recipients.size() + "]", RequestBody.create(MediaType.parse("text/plain"), user));

        Call<PojoNoDataResponse> call = apiService.uploadSessionFile(user_id_body, session_id_body,
                type_body, fileToUpload);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    builder.setContentText("Upload complete");
                    notificationManager.notify(54321, builder.build());
                    notificationManager.cancel(54321);
                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {

                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                builder.setContentText("Upload failed");
                notificationManager.notify(54321, builder.build());
                notificationManager.cancel(54321);
            }
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        builder.setProgress(PROGRESS_MAX, (int) percentage, false);
        notificationManager.notify(54321, builder.build());
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        builder.setContentText("Upload complete");
        notificationManager.notify(54321, builder.build());
        notificationManager.cancel(54321);
    }

    @Override
    public void uploadStart() {
        builder.setContentTitle("File upload")
                .setContentText("Uploading file to server")
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(54321, builder.build());
    }


}
