package com.avadna.luneblaze.pojo.pojoArticle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoArticleCommentsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoArticleCommentsResponseData> data = null;

    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("loadmore")
    @Expose
    public Integer loadMore;

}
