package com.avadna.luneblaze.pojo.pojoNewsFeed;

import android.text.style.ClickableSpan;
import android.view.View;

import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoOrganizationData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoPostComment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.SourceContent;

import java.util.List;

public class PojoGetNewsFeedResponseData {
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

    @SerializedName("activity_time")
    @Expose
    public String activityTime;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;


    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("privacy")
    @Expose
    public String privacy;
    @SerializedName("feeling_action")
    @Expose
    public String feelingAction;
    @SerializedName("feeling_value")
    @Expose
    public String feelingValue;
    @SerializedName("boosted")
    @Expose
    public String boosted;
    /*@SerializedName("likes")
    @Expose
    public String likes;*/
    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;

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

    @SerializedName("user_work")
    @Expose
    public String userWork;

    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;

    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;

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
    @SerializedName("text_plain")
    @Expose
    public String textPlain;
    @SerializedName("post_comments")
    @Expose
    public List<PojoNewsFeedPostComment> postComments = null;

    @SerializedName("comments_array")
    @Expose
    public List<PojoNewsFeedCommentsArrayItem> commentsArray = null;

    @SerializedName("origin")
    @Expose
    public PojoOrigin origin;


    @SerializedName("session_name")
    @Expose
    public String sessionName;
    @SerializedName("photos_num")
    @Expose
    public Integer photosNum;

    @SerializedName("post_title")
    @Expose
    public String postTitle;

    @SerializedName("photos")
    @Expose
    public List<PojoNewsFeedPhoto> photos = null;

    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;


    @SerializedName("liked_by")
    @Expose
    public PojoUserData liked_by = null;


    @SerializedName("answer")
    @Expose
    public PojoNewsFeedAnswer answer = null;


    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("poll")
    @Expose
    public PojoNewsFeedPoll poll;

    @SerializedName("total")
    @Expose
    public String total;
    @SerializedName("tagged")
    @Expose
    public String tagged;


    @SerializedName("total_user_ids")
    @Expose
    public String totalUserIds;
    @SerializedName("like_ids")
    @Expose
    public String likeIds;
    @SerializedName("comment_ids")
    @Expose
    public String commentIds;
    @SerializedName("origin_type")
    @Expose
    public String originType;
    @SerializedName("oraganisation_id")
    @Expose
    public String oraganisationId;
    @SerializedName("payment")
    @Expose
    public String payment;

    @SerializedName("organization_data")
    @Expose
    public PojoOrganizationData organizationData;


    public List<ClickableSpan> clickableSpansList = null;

    public SpannableClickItem spannableClickItem;

    public boolean isHidden = false;


    //this is used to store web previews
    public SourceContent sourceContent = null;

    //this is used to keep track of show more button
    public boolean expanded = false;

    public boolean canTextFit = true;

    public String url = "";
}
