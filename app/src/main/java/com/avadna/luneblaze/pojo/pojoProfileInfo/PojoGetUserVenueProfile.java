package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetUserVenueProfile {
    @SerializedName("user_venues")
    @Expose
    public List<PojoUserVenue> userVenues = null;
}
