package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGroupMember {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_full_name")
    @Expose
    public String userFullName;
    @SerializedName("user_email")
    @Expose
    public String userEmail;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("seen")
    @Expose
    public String seen;
    @SerializedName("deleted")
    @Expose
    public String deleted;

    @SerializedName("group_status")
    @Expose
    public String groupStatus;


    public PojoGroupMember(String userId,String userFullName,String userPicture,String role){
        this.userId=userId;
        this.userFullName=userFullName;
        this.userPicture=userPicture;
        this.role=role;
    }

    public String seenTime;


    public PojoGroupMember(String userId, String userFullname, String userPicture, String seenTime,String abc) {
        this.userId = userId;
        this.userFullName = userFullname;
        this.userPicture = userPicture;
        this.seenTime = seenTime;

    }
}
