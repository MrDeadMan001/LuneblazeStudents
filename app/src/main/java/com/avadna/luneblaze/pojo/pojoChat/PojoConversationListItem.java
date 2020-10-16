package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoConversationListItem {


    @SerializedName("conversation_id")
    @Expose
    public String conversationId;
    @SerializedName("last_message_id")
    @Expose
    public String lastMessageId;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("actor_id")
    @Expose
    public String actorId;
    @SerializedName("actee_id")
    @Expose
    public String acteeId;
    @SerializedName("seen")
    @Expose
    public String seen;
    @SerializedName("group_status")
    @Expose
    public String groupStatus;
    @SerializedName("name_list")
    @Expose
    public String nameList;
    @SerializedName("ids")
    @Expose
    public String ids;
    @SerializedName("recipients")
    @Expose
    public List<PojoGetMessageListResponseDataRecipient> recipients = null;
    @SerializedName("multiple_recipients")
    @Expose
    public Boolean multipleRecipients;
    @SerializedName("picture_left")
    @Expose
    public String pictureLeft;
    @SerializedName("picture_right")
    @Expose
    public String pictureRight;
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("blocked")
    @Expose
    public String blocked;

    @SerializedName("seendetail")
    @Expose
    public List<PojoSeendetail> seendetail = null;

    /*  @SerializedName("total_messages")
    @Expose
    public Integer totalMessages;*/
    @SerializedName("group_data")
    @Expose
    public PojoGroupData groupData;
    @SerializedName("picture")
    @Expose
    public String picture;


    @SerializedName("unread_count")
    @Expose
    public Integer unreadCount;


    public String isOnline="0";

    public boolean checked=false;


    public PojoConversationListItem(String conversationId, String message, String image, String time,
                                    List<PojoGetMessageListResponseDataRecipient> recipients,
                                    boolean multipleRecipients, String picture, String pictureLeft,
                                    String name,PojoGroupData groupData){
        this.conversationId=conversationId;
        this.message=message;
        this.image=image;
        this.time=time;
        this.recipients=recipients;
        this.multipleRecipients=multipleRecipients;
        this.picture=picture;
        this.pictureLeft=pictureLeft;
        this.name=name;
        this.groupData=groupData;

    }


}
