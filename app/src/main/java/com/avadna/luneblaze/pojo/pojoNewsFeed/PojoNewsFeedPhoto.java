package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoNewsFeedPhoto {

    @SerializedName("photo_id")
    @Expose
    public String photoId;
    @SerializedName("post_id")
    @Expose
    public String postId;
    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;
    @SerializedName("album_id")
    @Expose
    public String albumId;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("comments")
    @Expose
    public String comments;
    @SerializedName("width")
    @Expose
    public String width;
    @SerializedName("height")
    @Expose
    public String height;
}
