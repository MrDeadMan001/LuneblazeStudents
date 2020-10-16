package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PojoProfile {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_group")
    @Expose
    public String userGroup;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_email")
    @Expose
    public String userEmail;
    @SerializedName("user_phone")
    @Expose
    public String userPhone;
    @SerializedName("user_password")
    @Expose
    public String userPassword;
    @SerializedName("user_latitude")
    @Expose
    public String userLatitude;
    @SerializedName("user_longitude")
    @Expose
    public String userLongitude;
    @SerializedName("user_activated")
    @Expose
    public String userActivated;
    @SerializedName("user_activation_key")
    @Expose
    public String userActivationKey;
    @SerializedName("user_reseted")
    @Expose
    public String userReseted;
    @SerializedName("user_reset_key")
    @Expose
    public String userResetKey;
    @SerializedName("user_subscribed")
    @Expose
    public String userSubscribed;
    @SerializedName("user_package")
    @Expose
    public String userPackage;
    @SerializedName("user_subscription_date")
    @Expose
    public String userSubscriptionDate;
    @SerializedName("user_boosted_posts")
    @Expose
    public String userBoostedPosts;
    @SerializedName("user_boosted_pages")
    @Expose
    public String userBoostedPages;
    @SerializedName("user_started")
    @Expose
    public String userStarted;
    @SerializedName("user_verified")
    @Expose
    public String userVerified;
    @SerializedName("user_banned")
    @Expose
    public String userBanned;
    @SerializedName("user_live_requests_counter")
    @Expose
    public String userLiveRequestsCounter;
    @SerializedName("user_live_requests_lastid")
    @Expose
    public String userLiveRequestsLastid;
    @SerializedName("user_live_messages_counter")
    @Expose
    public Integer userLiveMessagesCounter;
    @SerializedName("user_live_messages_lastid")
    @Expose
    public String userLiveMessagesLastid;
    @SerializedName("user_live_notifications_counter")
    @Expose
    public Integer userLiveNotificationsCounter;


    @SerializedName("user_live_notifications_lastid")
    @Expose
    public String userLiveNotificationsLastid;
    @SerializedName("user_sessions_counter")
    @Expose
    public String userSessionsCounter;
    @SerializedName("user_firstname")
    @Expose
    public String userFirstname;
    @SerializedName("user_lastname")
    @Expose
    public String userLastname;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_pic_dimensions")
    @Expose
    public String userPicDimensions;
    @SerializedName("user_picture_id")
    @Expose
    public String userPictureId;
    @SerializedName("user_cover")
    @Expose
    public String userCover;
    @SerializedName("user_cover_dimensions")
    @Expose
    public String userCoverDimensions;
    @SerializedName("user_cover_id")
    @Expose
    public String userCoverId;
    @SerializedName("user_album_pictures")
    @Expose
    public String userAlbumPictures;
    @SerializedName("user_album_covers")
    @Expose
    public String userAlbumCovers;
    @SerializedName("user_album_timeline")
    @Expose
    public String userAlbumTimeline;
    @SerializedName("user_pinned_post")
    @Expose
    public String userPinnedPost;
    @SerializedName("user_registered")
    @Expose
    public String userRegistered;
    @SerializedName("user_last_login")
    @Expose
    public String userLastLogin;
    @SerializedName("user_birthdate")
    @Expose
    public String userBirthdate;
    @SerializedName("user_relationship")
    @Expose
    public String userRelationship;
    @SerializedName("user_biography")
    @Expose
    public String userBiography;
    @SerializedName("user_website")
    @Expose
    public String userWebsite;
    @SerializedName("user_work")
    @Expose
    public String userWork;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;
    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;



    @SerializedName("current_FOI")
    @Expose
    public String currentFOI;

    @SerializedName("user_current_city")
    @Expose
    public String userCurrentCity;
    @SerializedName("user_hometown")
    @Expose
    public String userHometown;
    @SerializedName("user_edu_major")
    @Expose
    public String userEduMajor;
    @SerializedName("user_edu_school")
    @Expose
    public String userEduSchool;
    @SerializedName("user_edu_class")
    @Expose
    public String userEduClass;
    @SerializedName("user_social_facebook")
    @Expose
    public String userSocialFacebook;
    @SerializedName("user_social_twitter")
    @Expose
    public String userSocialTwitter;
    @SerializedName("user_social_google")
    @Expose
    public String userSocialGoogle;
    @SerializedName("user_social_youtube")
    @Expose
    public String userSocialYoutube;
    @SerializedName("user_social_instagram")
    @Expose
    public String userSocialInstagram;
    @SerializedName("user_social_linkedin")
    @Expose
    public String userSocialLinkedin;
    @SerializedName("user_social_vkontakte")
    @Expose
    public String userSocialVkontakte;
    @SerializedName("user_chat_enabled")
    @Expose
    public String userChatEnabled;
    @SerializedName("user_privacy_wall")
    @Expose
    public String userPrivacyWall;
    @SerializedName("user_privacy_birthdate")
    @Expose
    public String userPrivacyBirthdate;
    @SerializedName("user_privacy_relationship")
    @Expose
    public String userPrivacyRelationship;
    @SerializedName("user_privacy_basic")
    @Expose
    public String userPrivacyBasic;
    @SerializedName("user_privacy_work")
    @Expose
    public String userPrivacyWork;
    @SerializedName("user_privacy_location")
    @Expose
    public String userPrivacyLocation;
    @SerializedName("user_privacy_education")
    @Expose
    public String userPrivacyEducation;
    @SerializedName("user_privacy_other")
    @Expose
    public String userPrivacyOther;
    @SerializedName("user_privacy_friends")
    @Expose
    public String userPrivacyFriends;
    @SerializedName("user_privacy_photos")
    @Expose
    public String userPrivacyPhotos;
    @SerializedName("user_privacy_pages")
    @Expose
    public String userPrivacyPages;
    @SerializedName("user_privacy_groups")
    @Expose
    public String userPrivacyGroups;
    @SerializedName("email_post_likes")
    @Expose
    public String emailPostLikes;
    @SerializedName("email_post_comments")
    @Expose
    public String emailPostComments;
    @SerializedName("email_post_shares")
    @Expose
    public String emailPostShares;
    @SerializedName("email_wall_posts")
    @Expose
    public String emailWallPosts;
    @SerializedName("email_mentions")
    @Expose
    public String emailMentions;
    @SerializedName("email_profile_visits")
    @Expose
    public String emailProfileVisits;
    @SerializedName("email_friend_requests")
    @Expose
    public String emailFriendRequests;
    @SerializedName("facebook_connected")
    @Expose
    public String facebookConnected;
    @SerializedName("facebook_id")
    @Expose
    public String facebookId;
    @SerializedName("twitter_connected")
    @Expose
    public String twitterConnected;
    @SerializedName("twitter_id")
    @Expose
    public String twitterId;
    @SerializedName("google_connected")
    @Expose
    public String googleConnected;
    @SerializedName("google_id")
    @Expose
    public String googleId;
    @SerializedName("instagram_connected")
    @Expose
    public String instagramConnected;
    @SerializedName("instagram_id")
    @Expose
    public String instagramId;
    @SerializedName("linkedin_connected")
    @Expose
    public String linkedinConnected;
    @SerializedName("linkedin_id")
    @Expose
    public String linkedinId;
    @SerializedName("vkontakte_connected")
    @Expose
    public String vkontakteConnected;
    @SerializedName("vkontakte_id")
    @Expose
    public String vkontakteId;
    @SerializedName("user_referrer_id")
    @Expose
    public String userReferrerId;
    @SerializedName("user_affiliate_balance")
    @Expose
    public String userAffiliateBalance;
    @SerializedName("fb_token")
    @Expose
    public String fbToken;
    @SerializedName("google_token")
    @Expose
    public String googleToken;
    @SerializedName("tw_token")
    @Expose
    public String twToken;
    @SerializedName("privacy_connection_request")
    @Expose
    public String privacyConnectionRequest;
    @SerializedName("privacy_message")
    @Expose
    public String privacyMessage;
    @SerializedName("privacy_choices")
    @Expose
    public String privacyChoices;

    @SerializedName("privacy_session")
    @Expose
    public String privacySession;

    @SerializedName("privacy_content")
    @Expose
    public String privacyContent;
    @SerializedName("privacy_tag")
    @Expose
    public String privacyTag;
    @SerializedName("privacy_invite_session")
    @Expose
    public String privacyInviteSession;
    @SerializedName("privacy_invite_conduct_session")
    @Expose
    public String privacyInviteConductSession;
    @SerializedName("privacy_invite_answer")
    @Expose
    public String privacyInviteAnswer;
    @SerializedName("privacy_invite_interest")
    @Expose
    public String privacyInviteInterest;
    @SerializedName("privacy_invite_venue")
    @Expose
    public String privacyInviteVenue;
    @SerializedName("privacy_notification_email_invites_messages")
    @Expose
    public String privacyNotificationEmailInvitesMessages;
    @SerializedName("privacy_notification_email_sessions")
    @Expose
    public String privacyNotificationEmailSessions;
    @SerializedName("privacy_notification_email_qa")
    @Expose
    public String privacyNotificationEmailQa;
    @SerializedName("privacy_notification_email_articles")
    @Expose
    public String privacyNotificationEmailArticles;
    @SerializedName("privacy_notification_email_update")
    @Expose
    public String privacyNotificationEmailUpdate;
    @SerializedName("privacy_notification_push_invites_messages")
    @Expose
    public String privacyNotificationPushInvitesMessages;
    @SerializedName("privacy_notification_push_sessions")
    @Expose
    public String privacyNotificationPushSessions;
    @SerializedName("privacy_notification_push_qa")
    @Expose
    public String privacyNotificationPushQa;
    @SerializedName("privacy_notification_push_articles")
    @Expose
    public String privacyNotificationPushArticles;
    @SerializedName("privacy_notification_push_update")
    @Expose
    public String privacyNotificationPushUpdate;
    @SerializedName("noti_sound")
    @Expose
    public String notiSound;
    @SerializedName("two_step_verifications")
    @Expose
    public String twoStepVerifications;
    @SerializedName("login_otp")
    @Expose
    public String loginOtp;
    @SerializedName("people_influencednew")
    @Expose
    public Integer peopleInfluencednew;
    @SerializedName("package_name")
    @Expose
    public String packageName;
    @SerializedName("package_color")
    @Expose
    public String packageColor;

    public Boolean userPictureDefault;


    @SerializedName("work_history")
    @Expose
    public List<PojoWorkHistory> workHistory = null;

    @SerializedName("user_venues")
    @Expose
    public List<PojoUserVenue> userVenues = null;


   /* @SerializedName("sessions_attended")
    @Expose
    public List<PojoSessionsAttended> sessionsAttended = null;*/
/*
    @SerializedName("articles_written")
    @Expose
    public List<PojoArticlesWritten> articlesWritten = null;*/

 /*   @SerializedName("session_conducted")
    @Expose
    public List<PojoSessionsAttended> sessionConducted = null;*/


    @SerializedName("verify_otp")
    @Expose
    public String verifyOtp;

    @SerializedName("connection")
    @Expose
    public String connection;



    @SerializedName("we_friends")
    @Expose
    public Boolean weFriends;
    @SerializedName("he_request")
    @Expose
    public Boolean heRequest;
    @SerializedName("i_request")
    @Expose
    public Boolean iRequest;
    @SerializedName("i_follow")
    @Expose
    public Boolean iFollow;
    @SerializedName("mutual_friends_count")
    @Expose
    public Integer mutualFriendsCount;

    @SerializedName("session_block")
    @Expose
    public String sessionBlock;

    @SerializedName("i_session_block")
    @Expose
    public String iSessionBlock;

    @SerializedName("nooffriends")
    @Expose
    public Integer nooffriends;
    @SerializedName("noofinterests")
    @Expose
    public Integer noofinterests;
    @SerializedName("noofvenue")
    @Expose
    public Integer noofvenue;

    @SerializedName("oraganisation_id")
    @Expose
    public String oraganisationId;

}
