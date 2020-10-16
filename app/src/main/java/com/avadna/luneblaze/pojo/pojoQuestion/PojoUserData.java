package com.avadna.luneblaze.pojo.pojoQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoUserData {
    @SerializedName("user_id")
    @Expose
    public String userId;
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

    @SerializedName("user_phone")
    @Expose
    public String userPhone;

    @SerializedName("user_work")
    @Expose
    public String userWork;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;
    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;

    @SerializedName("mutual_friends_count")
    @Expose
    public String mutualFriendsCount;

    @SerializedName("blocked")
    @Expose
    public Integer blocked;

    @SerializedName("connection")
    @Expose
    public String connection;

    @SerializedName("user_current_city")
    @Expose
    public String userCurrentCity;

    @SerializedName("no_of_followers")
    @Expose
    public Integer noOfFollowers;

    @SerializedName("is_friend")
    @Expose
    public Integer isFriend;

    @SerializedName("is_following")
    @Expose
    public Integer isFollowing;



    //added to keep track of add friend button clicked on suggested friends list activity
    // 0 for not clicked
    // 1 for clicked
    public Integer friendRequestStatus = 0;

    public boolean selected;

    public String isOnline = "";


    public boolean checked = false;


}
