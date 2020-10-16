package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.avadna.luneblaze.pojo.PojoGetFriendsListResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetRegisteredContactsListResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoUserData> data = null;
}
