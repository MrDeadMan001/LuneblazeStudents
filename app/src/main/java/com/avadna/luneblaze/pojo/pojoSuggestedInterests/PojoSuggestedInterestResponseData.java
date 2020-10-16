package com.avadna.luneblaze.pojo.pojoSuggestedInterests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSuggestedInterestResponseData {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("parent_id")
    @Expose
    public String parentId;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("total_follower")
    @Expose
    public String totalFollower;
    @SerializedName("i_follow_related")
    @Expose
    public Integer iFollowRelated;
}
