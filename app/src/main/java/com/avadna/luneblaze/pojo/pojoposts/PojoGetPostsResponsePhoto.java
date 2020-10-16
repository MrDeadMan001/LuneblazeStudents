package com.avadna.luneblaze.pojo.pojoposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetPostsResponsePhoto {
    @SerializedName("photo_id")
    @Expose
    public String photoId;
    @SerializedName("post_id")
    @Expose
    public String postId;
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
}
