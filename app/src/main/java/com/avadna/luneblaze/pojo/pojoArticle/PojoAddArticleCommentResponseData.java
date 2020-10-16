package com.avadna.luneblaze.pojo.pojoArticle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAddArticleCommentResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("article_id")
    @Expose
    public String articleId;
    @SerializedName("comment_id")
    @Expose
    public Integer commentId;
    @SerializedName("time")
    @Expose
    public String time;
}
