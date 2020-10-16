package com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddNewCommentReplyResponseData {

    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("action")
    @Expose
    public String action;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("parent_id")
    @Expose
    public String parentId;
    @SerializedName("id")
    @Expose
    public String replyId;
    @SerializedName("time")
    @Expose
    public String time;
}
