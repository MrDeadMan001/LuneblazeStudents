package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoUserVenue {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
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
    @SerializedName("website_link")
    @Expose
    public String websiteLink;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;



    @SerializedName("i_like")
    @Expose
    public Boolean iLike;
    @SerializedName("venue_followers")
    @Expose
    public List<PojoUserData> venueFollowers = null;
    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;

}
