package com.avadna.luneblaze.pojo.pojoSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInterestSession {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("parent_interest")
    @Expose
    public String parentInterest;

    @SerializedName("parent")
    @Expose
    public String parent;

    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;
}
