package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetFriendRequestResponseData {
    @SerializedName("id")
    @Expose
    public String id;
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
    @SerializedName("mutual_friends_count")
    @Expose
    public Integer mutualFriendsCount;
}
