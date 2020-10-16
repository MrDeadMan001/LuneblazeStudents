package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 01-02-2018.
 */

public class PojoGetSuggestedFriendListResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId="";
    @SerializedName("user_fullname")
    @Expose
    public String userFullname="";
    @SerializedName("user_work")
    @Expose
    public String userWork="";
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle="";
    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace="";
    @SerializedName("user_gender")
    @Expose
    public String userGender="";
    @SerializedName("user_picture")
    @Expose
    public String userPicture="";
    @SerializedName("mutual_friends_count")
    @Expose
    public Integer mutualFriendsCount=0;


    //added to keep track of add friend button clicked on suggested friends list activity
    // 0 for not clicked
    // 1 for clicked
    public Integer friendRequestStatus=0;
}
