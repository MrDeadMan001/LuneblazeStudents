package com.avadna.luneblaze.pojo.pojonormalpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddPostCommentResponseData {
    @SerializedName("node_id")
    @Expose
    public String nodeId;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("likes")
    @Expose
    public Integer likes;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("author_picture")
    @Expose
    public String authorPicture;
    @SerializedName("author_url")
    @Expose
    public String authorUrl;
    @SerializedName("author_name")
    @Expose
    public String authorName;
    @SerializedName("author_verified")
    @Expose
    public String authorVerified;
    @SerializedName("comment_id")
    @Expose
    public Integer commentId;
    @SerializedName("text_plain")
    @Expose
    public String textPlain;
    @SerializedName("edit_comment")
    @Expose
    public Boolean editComment;
    @SerializedName("delete_comment")
    @Expose
    public Boolean deleteComment;
}
