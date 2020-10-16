package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoProfileInfoResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoProfileInfoResponseData data;
}
