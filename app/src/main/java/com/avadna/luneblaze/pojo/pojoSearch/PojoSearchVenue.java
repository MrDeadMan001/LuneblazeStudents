package com.avadna.luneblaze.pojo.pojoSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSearchVenue {

    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;
    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;
    @SerializedName("is_college")
    @Expose
    public String is_college;

    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;

}
