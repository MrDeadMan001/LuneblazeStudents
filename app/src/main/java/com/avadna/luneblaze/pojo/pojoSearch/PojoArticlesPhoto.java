package com.avadna.luneblaze.pojo.pojoSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class PojoArticlesPhoto {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("post_id")
    @Expose
    public String postId;
    @SerializedName("article_id")
    @Expose
    public String articleId;
    @SerializedName("source")
    @Expose
    public String source;
}
