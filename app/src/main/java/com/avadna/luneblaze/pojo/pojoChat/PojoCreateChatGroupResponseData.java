package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreateChatGroupResponseData {

    @SerializedName("conversation_id")
    @Expose
    public Integer conversationId;
    @SerializedName("group_id")
    @Expose
    public Integer groupId;
}
