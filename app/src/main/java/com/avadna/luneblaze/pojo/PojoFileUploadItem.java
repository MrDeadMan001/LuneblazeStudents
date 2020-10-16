package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.helperClasses.ProgressRequestBody;

import retrofit2.Call;

public class PojoFileUploadItem {
    public Call<PojoNoDataResponse> apiCall;
    public int notificationId;
    public ProgressRequestBody.UploadCallbacks uploadCallbacks;
    public String uploadType;
    public String uploadId;
    public String filePath;

}
