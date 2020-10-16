package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenueDay;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoVoting {
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("venue_name")
    @Expose
    public String venueName;
    @SerializedName("venue_type")
    @Expose
    public String venueType;
    @SerializedName("dates")
    @Expose
    public List<PojoVoteDate> dates = null;
    @SerializedName("total_votes")
    @Expose
    public Integer totalVotes;

    @SerializedName("days")
    @Expose
    public List<PojoVenueDay> days = null;

    public boolean checked;
}
