package com.avadna.luneblaze.pojo.pojoQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetAnswerRepliesResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoGetAnswerRepliesResponseData data;
}
