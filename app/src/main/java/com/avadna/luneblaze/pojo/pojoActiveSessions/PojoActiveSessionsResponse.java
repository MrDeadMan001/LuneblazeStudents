package com.avadna.luneblaze.pojo.pojoActiveSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoActiveSessionsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoActiveSessionsResponseData data;
}
