package com.avadna.luneblaze.pojo.pojonormalpost;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPoll;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoNormalPost {

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
    public String groupId;
    @SerializedName("in_wall")
    @Expose
    public String inWall;
    @SerializedName("wall_id")
    @Expose
    public String wallId;
    @SerializedName("post_type")
    @Expose
    public String postType;
    @SerializedName("origin_id")
    @Expose
    public String originId;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("privacy")
    @Expose
    public String privacy;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("feeling_action")
    @Expose
    public String feelingAction;
    @SerializedName("feeling_value")
    @Expose
    public String feelingValue;
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
    @SerializedName("score")
    @Expose
    public String score;
    @SerializedName("post_type_value")
    @Expose
    public String postTypeValue;
    @SerializedName("total")
    @Expose
    public String total;
    @SerializedName("total_user_ids")
    @Expose
    public String totalUserIds;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_work")
    @Expose
    public String userWork;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;
    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;
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
    public String userPinnedPost;
    @SerializedName("page_id")
    @Expose
    public String pageId;
    @SerializedName("page_admin")
    @Expose
    public String pageAdmin;
    @SerializedName("page_category")
    @Expose
    public String pageCategory;
    @SerializedName("page_name")
    @Expose
    public String pageName;
    @SerializedName("page_title")
    @Expose
    public String pageTitle;
    @SerializedName("page_description")
    @Expose
    public String pageDescription;
    @SerializedName("page_picture")
    @Expose
    public String pagePicture;
    @SerializedName("page_picture_id")
    @Expose
    public String pagePictureId;
    @SerializedName("page_cover")
    @Expose
    public String pageCover;
    @SerializedName("page_cover_id")
    @Expose
    public String pageCoverId;
    @SerializedName("page_album_pictures")
    @Expose
    public String pageAlbumPictures;
    @SerializedName("page_album_covers")
    @Expose
    public String pageAlbumCovers;
    @SerializedName("page_album_timeline")
    @Expose
    public String pageAlbumTimeline;
    @SerializedName("page_pinned_post")
    @Expose
    public String pagePinnedPost;
    @SerializedName("page_verified")
    @Expose
    public String pageVerified;
    @SerializedName("page_boosted")
    @Expose
    public String pageBoosted;
    @SerializedName("page_likes")
    @Expose
    public String pageLikes;
    @SerializedName("page_date")
    @Expose
    public String pageDate;
    @SerializedName("group_name")
    @Expose
    public String groupName;
    @SerializedName("group_picture_id")
    @Expose
    public String groupPictureId;
    @SerializedName("group_cover_id")
    @Expose
    public String groupCoverId;
    @SerializedName("group_title")
    @Expose
    public String groupTitle;
    @SerializedName("group_admin")
    @Expose
    public String groupAdmin;
    @SerializedName("group_pinned_post")
    @Expose
    public String groupPinnedPost;
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
    public Integer iLike;
    @SerializedName("photos_num")
    @Expose
    public Integer photosNum;
    @SerializedName("photos")
    @Expose
    public List<PojoNewsFeedPhoto> photos = null;
    @SerializedName("text_plain")
    @Expose
    public String textPlain;

    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers;

    @SerializedName("origin")
    @Expose
    public PojoPostOrigin origin;


    @SerializedName("post_title")
    @Expose
    public String postTitle;

    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;
    @SerializedName("poll")
    @Expose
    public PojoNewsFeedPoll poll;



}
