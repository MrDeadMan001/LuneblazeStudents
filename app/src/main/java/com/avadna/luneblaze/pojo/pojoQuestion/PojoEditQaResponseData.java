package com.avadna.luneblaze.pojo.pojoQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoEditQaResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("sessions_qa_id")
    @Expose
    public String sessionsQaId;
    @SerializedName("post")
    @Expose
    public String post;
}
