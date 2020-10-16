package com.avadna.luneblaze.pojo.pojonormalpost;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoNormalPostResponseData {

    @SerializedName("post")
    @Expose
    public PojoGetNewsFeedResponseData post;
    @SerializedName("comments")
    @Expose
    public List<PojoNormalPostComment> comments = null;

    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("load_more")
    @Expose
    public Integer loadMore;

}
