package com.avadna.luneblaze.pojo.pojonormalpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddPostCommentResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoAddPostCommentResponseData data;
}
