package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.avadna.luneblaze.pojo.pojoSettings.PojoRelatedInterestArr;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetUserInterestsResponseData {
    @SerializedName("sessionInterestArr")
    @Expose
    public List<PojoRelatedInterestArr> sessionInterestArr = null;
}
