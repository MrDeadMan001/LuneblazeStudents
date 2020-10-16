package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 24-12-2017.
 */

public class PojoSignUp1ResponseData {
    @SerializedName("user_id")
    @Expose
    public Integer userId=0;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname="";
}