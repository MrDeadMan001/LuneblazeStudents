package com.avadna.luneblaze.pojo.pojoVenueDetails;

import com.avadna.luneblaze.pojo.pojoVenueSlots.PojoVenueDaySlot;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetVenueDetailsResponseData {

    @SerializedName("sessions")
    @Expose
    public PojoSessions sessions=new PojoSessions();
    @SerializedName("teams")
    @Expose
    public List<PojoVenueTeam> teams = new ArrayList<>();
    @SerializedName("venue_data")
    @Expose
    public PojoVenueData venueData=new PojoVenueData();

    @SerializedName("allphoto")
    @Expose
    public List<PojoVenuePhoto> allphoto = null;
    @SerializedName("days")
    @Expose
    public List<PojoVenueDay> days = null;

}
