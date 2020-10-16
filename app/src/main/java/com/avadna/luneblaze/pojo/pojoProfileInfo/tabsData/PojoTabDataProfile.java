package com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoTabDataProfile {
    @SerializedName("saved_posts")
    @Expose
    public List<PojoGetNewsFeedResponseData> savedPosts = null;

    @SerializedName("post_activities")
    @Expose
    public List<PojoGetNewsFeedResponseData> post_activities = null;

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
    @SerializedName("current_FOI")
    @Expose
    public String currentFOI;
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
    public String userLiveMessagesCounter;
    @SerializedName("user_live_messages_lastid")
    @Expose
    public String userLiveMessagesLastid;
    @SerializedName("user_live_notifications_counter")
    @Expose
    public String userLiveNotificationsCounter;
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
}
