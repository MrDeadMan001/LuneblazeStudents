package com.avadna.luneblaze.pojo.pojoChat;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoChatMessage {
    @SerializedName("message_id")
    @Expose
    public String messageId;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("seen_time")
    @Expose
    public String seenTime;
    @SerializedName("seen")
    @Expose
    public String seen;
    @SerializedName("image")
    @Expose
    public String image;

    public String localImagePath;


    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("actor_id")
    @Expose
    public String actorId;
    @SerializedName("actee_id")
    @Expose
    public String acteeId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;

    @SerializedName("temp_msg_id")
    @Expose
    public String tempMsgId;

    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("is_me")
    @Expose
    public Boolean isMe;

    @SerializedName("seendetail")
    @Expose
    public List<PojoSeendetail> seendetail = null;

    //the following variable is used to keep track of which messages in a conversation were added by
    //app in chat activity when user clicks send button
    public boolean isAppLocal = false;

    //this variable is used to keep track is the message has been tried to be sent before
    public boolean isRetried = false;

    public int sentState;

    // public boolean isSent=true;
    public String cid = "";
    public String recipient = "";
    public boolean isFromNewUser = false;
    public boolean isImageUploading = false;
    public boolean isHighLighted = false;

    public int messageType;

    public List<ClickableSpan> clickableSpans = null;


    public PojoChatMessage() {
        this.sentState = AppKeys.MESSAGE_STATE_SENT;

    }

    public PojoChatMessage(String message, int messageType) {
        super();
        this.message = message;
        this.messageType = messageType;
    }

/*
    public PojoChatMessage(String messageId, String message, String image, String time,
                           String userId, String userName, String userFullname, String userGender,
                           String userPicture,boolean isMe) {
        super();
        this.messageId = messageId;
        this.message = message;
        this.image = image;
        this.time = time;
        this.userId = userId;
        this.userName = userName;
        this.userFullname = userFullname;
        this.userGender = userGender;
        this.userPicture = userPicture;
        this.messageType=AppKeys.MESSAGE_TYPE_USER;
        this.isMe=isMe;
        this.seen="0";
    }

    public PojoChatMessage(String userId, String message, String image, String time,
                           String recipient) {
        super();
        this.messageId = "";
        this.message = message;
        this.image = image;
        this.time = time;
        this.userId = userId;
        this.userName = "";
        this.userFullname = "";
        this.userGender = "";
        this.userPicture = "";
        this.sentState = AppKeys.MESSAGE_STATE_UNSENT;
        this.messageType=AppKeys.MESSAGE_TYPE_USER;
        this.recipient = recipient;
        this.tempMsgId = tempMsgId;
    }*/

    public PojoChatMessage(String userId, String message, String image, String imgDimensions,
                           String time, String tempMsgId, PojoSeendetail seendetail) {
        super();
        this.messageId = "-1";
        this.message = message;
        this.image = image;
        this.imgDimensions = imgDimensions;
        this.time = time;
        this.userId = userId;
        this.userName = "";
        this.userFullname = "";
        this.userGender = "";
        this.userPicture = "";
        this.sentState = AppKeys.MESSAGE_STATE_UNSENT;
        this.messageType = AppKeys.MESSAGE_TYPE_USER;
        this.tempMsgId = tempMsgId;
        this.seen = "0";
        this.seenTime = "";
        this.isMe = true;
        this.isAppLocal = true;
        this.seendetail = new ArrayList<>();
        this.seendetail.add(seendetail);
    }

    public void setSentState(int sentState) {
        this.sentState = sentState;
    }

    /*  public void setSentStatus(boolean isSent){
          this.isSent=isSent;
      }*/
    public void setTempMsgId(String tempMsgId) {
        this.tempMsgId = tempMsgId;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public Boolean getIsMe() {
        return isMe;
    }

    public void setIsMe(Boolean isMe) {
        this.isMe = isMe;
    }
}
