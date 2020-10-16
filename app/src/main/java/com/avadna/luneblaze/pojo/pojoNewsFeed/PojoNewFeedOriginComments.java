package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoNewFeedOriginComments {

    @SerializedName("articles_discussion_id")
    @Expose
    public String articlesDiscussionId;
    @SerializedName("comment_id")
    @Expose
    public String commentId;
    @SerializedName("articles_id")
    @Expose
    public String articlesId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("user_id")
    @Expose
    public String userId;
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
}
