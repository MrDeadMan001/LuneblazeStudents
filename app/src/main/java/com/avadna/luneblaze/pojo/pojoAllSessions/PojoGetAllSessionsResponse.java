package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 14-02-2018.
 */

public class PojoGetAllSessionsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoAllSessionsData data;
}
