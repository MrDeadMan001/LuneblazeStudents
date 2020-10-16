package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoRelatedInterestArr {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("no_of_followers")
    @Expose
    public Integer noOfFollowers;
    @SerializedName("i_follow_related")
    @Expose
    public Integer iFollowRelated;
}
