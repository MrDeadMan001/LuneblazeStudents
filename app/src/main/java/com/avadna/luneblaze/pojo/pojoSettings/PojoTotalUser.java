package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class PojoTotalUser {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("blocked")
    @Expose
    public Integer blocked;
}
