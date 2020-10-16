package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddSessionDiscussionResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("discussion_post")
    @Expose
    public String discussionPost;
    @SerializedName("session_id")
    @Expose
    public String sessionId;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("time")
    @Expose
    public String time;
}
