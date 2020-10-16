package com.avadna.luneblaze.pojo.pojoSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSearchResultResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoSearchResultResponseData data;
}
