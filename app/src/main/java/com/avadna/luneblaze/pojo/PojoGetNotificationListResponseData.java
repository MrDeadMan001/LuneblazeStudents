package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetNotificationListResponseData {
    @SerializedName("notification_id")
    @Expose
    public String notificationId;
    @SerializedName("to_user_id")
    @Expose
    public String toUserId;
    @SerializedName("from_user_id")
    @Expose
    public String fromUserId;
    @SerializedName("action")
    @Expose
    public String action;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("node_url")
    @Expose
    public String nodeUrl;
    @SerializedName("notify_id")
    @Expose
    public String notifyId;

    @SerializedName("reply_id")
    @Expose
    public String reply_id;

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("seen")
    @Expose
    public String seen;
    @SerializedName("total")
    @Expose
    public String total;
    @SerializedName("total_user_ids")
    @Expose
    public String totalUserIds;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_name")
    @Expose
    public String userName;
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
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("full_message")
    @Expose
    public String fullMessage;
}
