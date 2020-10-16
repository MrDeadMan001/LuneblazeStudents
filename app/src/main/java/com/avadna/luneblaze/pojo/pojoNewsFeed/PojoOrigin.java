package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.avadna.luneblaze.pojo.pojoOrganisation.PojoOrganizationData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoOrigin {

    @SerializedName("origin")
    @Expose
    public PojoOrigin origin = null;

    //origin normal post
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

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

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

   /* @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;*/

    @SerializedName("comments")
    @Expose
    public String comments;
    @SerializedName("thanks_count")
    @Expose
    public String thanksCount;
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
    @SerializedName("tagged")
    @Expose
    public String tagged;
    @SerializedName("total_user_ids")
    @Expose
    public String totalUserIds;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("privacy_connection_request")
    @Expose
    public String privacyConnectionRequest;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;
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
    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;

    @SerializedName("total_replies")
    @Expose
    public Integer totalReplies;

    @SerializedName("text_plain")
    @Expose
    public String textPlain;

    @SerializedName("post_title")
    @Expose
    public String postTitle;


    //origin session_shared

    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("topics")
    @Expose
    public String topics;
    @SerializedName("prerequisite")
    @Expose
    public String prerequisite;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;
    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;
    @SerializedName("event_photos")
    @Expose
    public String eventPhotos;

    @SerializedName("i_attend")
    @Expose
    public Integer iAttend;
    @SerializedName("created_by")
    @Expose
    public PojoUserData createdBy;
    @SerializedName("presentors")
    @Expose
    public String presentors;

    @SerializedName("total_shares")
    @Expose
    public String totalShares;
    @SerializedName("total_attends")
    @Expose
    public String totalAttends;
    @SerializedName("total_joins")
    @Expose
    public String totalJoins;
    @SerializedName("venue")
    @Expose
    public String venue;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("event_date")
    @Expose
    public String eventDate;
    @SerializedName("event_time")
    @Expose
    public String eventTime;
    @SerializedName("total_allowed_members")
    @Expose
    public String totalAllowedMembers;
    @SerializedName("event_duration")
    @Expose
    public String eventDuration;

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("finalized_data")
    @Expose
    public String finalizedData;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("verification_code")
    @Expose
    public String verificationCode;
    @SerializedName("co_ordinators")
    @Expose
    public String coOrdinators;
    @SerializedName("final_place")
    @Expose
    public String finalPlace;
    @SerializedName("discussions_cnt")
    @Expose
    public String discussionsCnt;
    @SerializedName("comments_cnt")
    @Expose
    public String commentsCnt;
    @SerializedName("qaData_cnt")
    @Expose
    public String qaDataCnt;
    @SerializedName("i_thanks")
    @Expose
    public Integer iThanks;


    //venue shared

    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
    @SerializedName("total_rating")
    @Expose
    public String totalRating;
    @SerializedName("teams")
    @Expose
    public String teams;
    @SerializedName("website_link")
    @Expose
    public String websiteLink;
    @SerializedName("venue_followers")
    @Expose
    public Integer venueFollowers;
    @SerializedName("all_attendees")
    @Expose
    public Integer allAttendees;
    @SerializedName("all_sessions")
    @Expose
    public Integer allSessions;
    @SerializedName("interest_id")
    @Expose
    public String interestId;

    @SerializedName("parent_id")
    @Expose
    public String parentId;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("parent")
    @Expose
    public String parent;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;
    @SerializedName("totalAttendes")
    @Expose
    public Integer totalAttendes;
    @SerializedName("totalSessions")
    @Expose
    public Integer totalSessions;
    @SerializedName("totalQues")
    @Expose
    public Integer totalQues;
    @SerializedName("totalArticles")
    @Expose
    public Integer totalArticles;
    @SerializedName("sessions_qa_id")
    @Expose
    public String sessionsQaId;
    @SerializedName("post")
    @Expose
    public String post;

    @SerializedName("qa_type")
    @Expose
    public String qaType;
    @SerializedName("parent_question_id")
    @Expose
    public String parentQuestionId;
    @SerializedName("total_answers")
    @Expose
    public Integer totalAnswers;

    @SerializedName("followers")
    @Expose
    public Integer followers;
    @SerializedName("articles_id")
    @Expose
    public String articlesId;
    @SerializedName("total_comments")
    @Expose
    public Integer totalComments;

    @SerializedName("comments_array")
    @Expose
    public List<PojoNewFeedOriginComments> commentsArray;

    @SerializedName("organization_data")
    @Expose
    public PojoOrganizationData organizationData;


    @SerializedName("user_work")
    @Expose
    public String userWork;

    /* @SerializedName("i_like")
     @Expose
     public Boolean iLike;*/
    @SerializedName("poll")
    @Expose
    public PojoNewsFeedPoll poll;


    @SerializedName("photos_num")
    @Expose
    public Integer photosNum;
    @SerializedName("photos")
    @Expose
    public List<PojoNewsFeedPhoto> photos = null;
}
