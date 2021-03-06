package com.avadna.luneblaze.pojo.pojoInterest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInterestData {
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
    @SerializedName("parent_interest")
    @Expose
    public String parentInterest;
}
