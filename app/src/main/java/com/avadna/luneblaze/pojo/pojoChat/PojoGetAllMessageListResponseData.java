package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetAllMessageListResponseData {

    @SerializedName("messages")
    @Expose
    public List<PojoConversationListItem> messages = null;

}
