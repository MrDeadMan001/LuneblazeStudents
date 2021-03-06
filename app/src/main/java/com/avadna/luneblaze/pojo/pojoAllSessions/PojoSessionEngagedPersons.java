package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 14-02-2018.
 */

public class PojoSessionEngagedPersons {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("connection")
    @Expose
    public String connection;
}
