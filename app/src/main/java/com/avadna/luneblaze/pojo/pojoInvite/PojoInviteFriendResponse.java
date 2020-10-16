package com.avadna.luneblaze.pojo.pojoInvite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInviteFriendResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoInviteFriendResponseData data;
}
