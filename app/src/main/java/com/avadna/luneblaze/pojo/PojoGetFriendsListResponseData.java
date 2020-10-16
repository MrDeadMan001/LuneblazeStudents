package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetFriendsListResponseData{
    @SerializedName("friends")
    @Expose
    public List<PojoUserData> friends = null;

    @SerializedName("nooffriend")
    @Expose
    public Integer noOfFriend ;

}
