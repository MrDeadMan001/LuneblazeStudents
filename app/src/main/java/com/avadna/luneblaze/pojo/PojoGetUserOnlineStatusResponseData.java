package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetUserOnlineStatusResponseData {
    @SerializedName("is_online")
    @Expose
    public String isOnline;
    @SerializedName("last_seen")
    @Expose
    public String lastSeen;
    @SerializedName("user_id")
    @Expose
    public String userId;

}
