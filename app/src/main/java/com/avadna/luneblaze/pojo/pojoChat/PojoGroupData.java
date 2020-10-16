package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGroupData {
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
    @SerializedName("no_of_members")
    @Expose
    public Integer noOfMembers;
    @SerializedName("members")
    @Expose
    public List<PojoGroupMember> members = null;


    public PojoGroupData(String groupId, String conversationId, String groupOwner, String groupName,
                         String groupPhoto, Integer noOfMembers, List<PojoGroupMember> members){
        this.groupId=groupId;
        this.conversationId=conversationId;
        this.groupOwner=groupOwner;
        this.groupName=groupName;
        this.groupPhoto=groupPhoto;
        this.noOfMembers=noOfMembers;
        this.members=members;

    }
}
