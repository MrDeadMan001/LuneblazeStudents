package com.avadna.luneblaze.pojo.pojoArticle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreateArticleResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("article_id")
    @Expose
    public Integer articleId;
}
