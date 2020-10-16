package com.avadna.luneblaze.pojo.pojoVenueList;

import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 18-02-2018.
 */

public class PojoGetVenueListResponseData {
    @SerializedName("venue_id")
    @Expose
    public String venueId;

    @SerializedName("venue_type")
    @Expose
    public String venueType;

    @SerializedName("venue_name")
    @Expose
    public String venueName;

    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;

    @SerializedName("distance")
    @Expose
    public String distance;
    @SerializedName("distance_in_meter")
    @Expose
    public Integer distanceInMeter;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;

    @SerializedName("days")
    @Expose
    public List<PojoVenueDay> days = null;

    public PojoGetVenueListResponseData(String venueId) {
        this.venueId = venueId;
        this.venueName = "";
        this.distance = "";
        this.distanceInMeter = 0;
        this.latitude = "";
        this.longitude = "";
    }


}
