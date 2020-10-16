package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddSessionDiscussionReplyResponseData {

    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("parent_discussion_id")
    @Expose
    public String parentDiscussionId;
    @SerializedName("sessions_discussion_id")
    @Expose
    public Integer sessionsDiscussionId;
    @SerializedName("time")
    @Expose
    public String time;
}
