package com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetNormalPostReplyResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoGetNormalPostReplyResponseData data;
}
