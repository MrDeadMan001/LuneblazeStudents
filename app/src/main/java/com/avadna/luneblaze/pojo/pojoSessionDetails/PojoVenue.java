package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoVenue {
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
    @SerializedName("venue_ambassadors")
    @Expose
    public List<PojoVenueAmbassador> venueAmbassadors = null;

}
