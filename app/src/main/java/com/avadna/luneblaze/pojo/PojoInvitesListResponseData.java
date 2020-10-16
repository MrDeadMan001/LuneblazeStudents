package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInvitesListResponseData {
    @SerializedName("privacy_connection_request")
    @Expose
    public String privacyConnectionRequest;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_verified")
    @Expose
    public String userVerified;
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
    @SerializedName("user_current_city")
    @Expose
    public String userCurrentCity;
    @SerializedName("invited_status")
    @Expose
    public Integer invitedStatus;
    @SerializedName("connection")
    @Expose
    public String connection;
    @SerializedName("mutual_friends_count")
    @Expose
    public Integer mutualFriendsCount;
    @SerializedName("no_of_followers")
    @Expose
    public Integer noOfFollowers;



    @SerializedName("number_of_mutual_interests")
    @Expose
    public Integer numberOfMutualInterests;

    @SerializedName("number_of_answers")
    @Expose
    public Integer numberOfAnswers;

    @SerializedName("no_of_session_attended")
    @Expose
    public Integer noOfSessionAttended;

    @SerializedName("number_of_mutual_venues")
    @Expose
    public Integer numberOfMutualVenues;

    @SerializedName("number_of_sessions_conducted")
    @Expose
    public Integer numberOfSessionsConducted;

}
