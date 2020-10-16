package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetGroupDetailsResponseData {
    @SerializedName("group_id")
    @Expose
    public String groupId;
    @SerializedName("conversation_id")
    @Expose
    public String conversationId;
    @SerializedName("group_owner")
    @Expose
    public String groupOwner;
    @SerializedName("group_name")
    @Expose
    public String groupName;
    @SerializedName("group_photo")
    @Expose
    public String groupPhoto;
    @SerializedName("members")
    @Expose
    public List<PojoChatGroupMember> members = null;
}
