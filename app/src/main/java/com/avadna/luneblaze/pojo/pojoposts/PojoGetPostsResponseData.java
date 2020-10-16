package com.avadna.luneblaze.pojo.pojoposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetPostsResponseData {
    @SerializedName("post_id")
    @Expose
    public String postId;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("in_group")
    @Expose
    public String inGroup;
    @SerializedName("group_id")
    @Expose
    public Object groupId;
    @SerializedName("in_wall")
    @Expose
    public String inWall;
    @SerializedName("wall_id")
    @Expose
    public Object wallId;
    @SerializedName("post_type")
    @Expose
    public String postType;
    @SerializedName("origin_id")
    @Expose
    public Object originId;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("location")
    @Expose
    public Object location;
    @SerializedName("privacy")
    @Expose
    public String privacy;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("feeling_action")
    @Expose
    public Object feelingAction;
    @SerializedName("feeling_value")
    @Expose
    public Object feelingValue;
    @SerializedName("boosted")
    @Expose
    public String boosted;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("comments")
    @Expose
    public String comments;
    @SerializedName("shares")
    @Expose
    public String shares;
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
    @SerializedName("user_picture_id")
    @Expose
    public String userPictureId;
    @SerializedName("user_cover_id")
    @Expose
    public String userCoverId;
    @SerializedName("user_verified")
    @Expose
    public String userVerified;
    @SerializedName("user_subscribed")
    @Expose
    public String userSubscribed;
    @SerializedName("user_pinned_post")
    @Expose
    public Object userPinnedPost;
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
    @SerializedName("group_name")
    @Expose
    public Object groupName;
    @SerializedName("group_picture_id")
    @Expose
    public Object groupPictureId;
    @SerializedName("group_cover_id")
    @Expose
    public Object groupCoverId;
    @SerializedName("group_title")
    @Expose
    public Object groupTitle;
    @SerializedName("group_admin")
    @Expose
    public Object groupAdmin;
    @SerializedName("group_pinned_post")
    @Expose
    public Object groupPinnedPost;
    @SerializedName("author_id")
    @Expose
    public String authorId;
    @SerializedName("is_page_admin")
    @Expose
    public Boolean isPageAdmin;
    @SerializedName("is_group_admin")
    @Expose
    public Boolean isGroupAdmin;
    @SerializedName("post_author_picture")
    @Expose
    public String postAuthorPicture;
    @SerializedName("post_author_url")
    @Expose
    public String postAuthorUrl;
    @SerializedName("post_author_name")
    @Expose
    public String postAuthorName;
    @SerializedName("post_author_verified")
    @Expose
    public String postAuthorVerified;
    @SerializedName("pinned")
    @Expose
    public Boolean pinned;
    @SerializedName("manage_post")
    @Expose
    public Boolean managePost;
    @SerializedName("i_save")
    @Expose
    public Boolean iSave;
    @SerializedName("i_like")
    @Expose
    public Boolean iLike;
    @SerializedName("text_plain")
    @Expose
    public Object textPlain;
    @SerializedName("poll")
    @Expose
    public PojoGetPostsResponsePoll poll;
    @SerializedName("post_comments")
    @Expose
    public List<PojoGetPostsResponsePostComment> postComments = null;
    @SerializedName("photos_num")
    @Expose
    public Integer photosNum;
    @SerializedName("photos")
    @Expose
    public List<PojoGetPostsResponsePhoto> photos = null;
}
