package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetCollegeListForDriveResponseData {
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
    @SerializedName("venue_type")
    @Expose
    public String venueType;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("total_rating")
    @Expose
    public String totalRating;
    @SerializedName("teams")
    @Expose
    public String teams;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;
    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;
    @SerializedName("is_college")
    @Expose
    public String isCollege;
    @SerializedName("cityid")
    @Expose
    public String cityid;
    @SerializedName("website_link")
    @Expose
    public String websiteLink;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;
    @SerializedName("noofstudent")
    @Expose
    public Integer noofstudent;

    @SerializedName("user_ids")
    @Expose
    public List<String> user_ids = null;

}
