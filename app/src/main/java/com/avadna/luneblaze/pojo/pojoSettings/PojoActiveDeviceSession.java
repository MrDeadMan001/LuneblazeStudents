package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class PojoActiveDeviceSession {
    @SerializedName("session_id")
    @Expose
    public String sessionId;
    @SerializedName("session_token")
    @Expose
    public String sessionToken;
    @SerializedName("session_date")
    @Expose
    public String sessionDate;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_browser")
    @Expose
    public String userBrowser;
    @SerializedName("user_os")
    @Expose
    public String userOs;
    @SerializedName("user_ip")
    @Expose
    public String userIp;
}
