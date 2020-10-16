package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoVenueAmbassador {
    @SerializedName("venue_teams_id")
    @Expose
    public String venueTeamsId;
    @SerializedName("venue_id")
    @Expose
    public String venueId;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("user")
    @Expose
    public PojoUserData user;
}
