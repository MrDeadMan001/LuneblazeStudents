package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 25-03-2018.
 */

public class PojoGetChatMessagesResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("messages")
    @Expose
    public List<PojoChatMessage> messages = null;
    @SerializedName("group_data")
    @Expose
    public PojoGroupData groupData;

    @SerializedName("blocked")
    @Expose
    public String blocked;

}
