package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoVenueIdName {
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;

}
