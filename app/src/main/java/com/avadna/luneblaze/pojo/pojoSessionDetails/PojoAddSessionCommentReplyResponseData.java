package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddSessionCommentReplyResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("parent_comment_id")
    @Expose
    public String parentCommentId;
    @SerializedName("sessions_comment_id")
    @Expose
    public Integer sessionsCommentId;
    @SerializedName("time")
    @Expose
    public String time;
}
