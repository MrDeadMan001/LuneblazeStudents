package com.avadna.luneblaze.pojo.pojoposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetPostsResponsePostComment {
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
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_verified")
    @Expose
    public String userVerified;
    @SerializedName("page_id")
    @Expose
    public Object pageId;
    @SerializedName("page_admin")
    @Expose
    public Object pageAdmin;
    @SerializedName("page_category")
    @Expose
    public Object pageCategory;
    @SerializedName("page_name")
    @Expose
    public Object pageName;
    @SerializedName("page_title")
    @Expose
    public Object pageTitle;
    @SerializedName("page_description")
    @Expose
    public Object pageDescription;
    @SerializedName("page_picture")
    @Expose
    public Object pagePicture;
    @SerializedName("page_picture_id")
    @Expose
    public Object pagePictureId;
    @SerializedName("page_cover")
    @Expose
    public Object pageCover;
    @SerializedName("page_cover_id")
    @Expose
    public Object pageCoverId;
    @SerializedName("page_album_pictures")
    @Expose
    public Object pageAlbumPictures;
    @SerializedName("page_album_covers")
    @Expose
    public Object pageAlbumCovers;
    @SerializedName("page_album_timeline")
    @Expose
    public Object pageAlbumTimeline;
    @SerializedName("page_pinned_post")
    @Expose
    public Object pagePinnedPost;
    @SerializedName("page_verified")
    @Expose
    public Object pageVerified;
    @SerializedName("page_boosted")
    @Expose
    public Object pageBoosted;
    @SerializedName("page_likes")
    @Expose
    public Object pageLikes;
    @SerializedName("page_date")
    @Expose
    public Object pageDate;
    @SerializedName("text_plain")
    @Expose
    public String textPlain;
    @SerializedName("author_id")
    @Expose
    public String authorId;
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
    @SerializedName("i_like")
    @Expose
    public Boolean iLike;
    @SerializedName("edit_comment")
    @Expose
    public Boolean editComment;
    @SerializedName("delete_comment")
    @Expose
    public Boolean deleteComment;
}
