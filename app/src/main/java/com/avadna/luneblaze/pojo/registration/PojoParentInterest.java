package com.avadna.luneblaze.pojo.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoParentInterest {
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
    @SerializedName("no_of_followers")
    @Expose
    public Integer noOfFollowers;
}
