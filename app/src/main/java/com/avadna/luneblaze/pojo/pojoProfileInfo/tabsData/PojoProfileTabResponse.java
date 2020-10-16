package com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoProfileTabResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoProfileTabResponseData data;
}
