package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetUserVenueResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoGetUserVenueResponseData data;
}
