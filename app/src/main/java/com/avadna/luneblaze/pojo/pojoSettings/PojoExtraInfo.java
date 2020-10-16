package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoExtraInfo {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("activation_key")
    @Expose
    public String activationKey;
    @SerializedName("users_extra_info_id")
    @Expose
    public String usersExtraInfoId;
    @SerializedName("primary")
    @Expose
    public String primary;
}
