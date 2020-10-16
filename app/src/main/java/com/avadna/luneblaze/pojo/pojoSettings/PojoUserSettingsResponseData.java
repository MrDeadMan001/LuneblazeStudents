package com.avadna.luneblaze.pojo.pojoSettings;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoUserSettingsResponseData {
    @SerializedName("followers")
    @Expose
    public List<PojoUserData> followers = null;
    @SerializedName("total_users")
    @Expose
    public List<PojoUserData> totalUsers = null;
    @SerializedName("related_interest_arr")
    @Expose
    public List<PojoRelatedInterestArr> relatedInterestArr = null;
    @SerializedName("blocks")
    @Expose
    public List<PojoUserData> blocks = null;

    @SerializedName("sessions")
    @Expose
    public List<PojoActiveDeviceSession> sessions = null;

    @SerializedName("privacy")
    @Expose
    public PojoPrivacy privacy;
    @SerializedName("app_settings")
    @Expose
    public PojoAppSettings appSettings;


    // when type=account
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_email")
    @Expose
    public String userEmail;

    //when type= verification
    @SerializedName("case")
    @Expose
    public String kase;

}
