package com.avadna.luneblaze.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreatInstamojoOrderResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoCreatInstamojoOrderResponseData data;
}
