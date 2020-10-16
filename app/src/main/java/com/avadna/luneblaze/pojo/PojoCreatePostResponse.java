package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreatePostResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoGetNewsFeedResponseData data;
}
