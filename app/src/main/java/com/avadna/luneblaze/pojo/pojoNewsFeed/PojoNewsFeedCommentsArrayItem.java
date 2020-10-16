package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoNewsFeedCommentsArrayItem {
    @SerializedName("comment_id")
    @Expose
    public String commentId;
    @SerializedName("node_id")
    @Expose
    public String nodeId;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_type")
    @Expose
    public String userType;
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
    public String likes;
    @SerializedName("replies")
    @Expose
    public String replies;
    @SerializedName("created_by")
    @Expose
    public PojoUserData createdBy;
    @SerializedName("author_picture")
    @Expose
    public String authorPicture;
    @SerializedName("author_url")
    @Expose
    public String authorUrl;
    @SerializedName("author_name")
    @Expose
    public String authorName;




    //below items are only used to article comment post type
    @SerializedName("articles_discussion_id")
    @Expose
    public String articlesDiscussionId;
    @SerializedName("articles_id")
    @Expose
    public String articlesId;
    @SerializedName("post")
    @Expose
    public String post;

    @SerializedName("parent_comment_id")
    @Expose
    public String parentCommentId;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;



}
