package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionsAttended;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSponsorPlanDetailsData {
    @SerializedName("currentplan")
    @Expose
    public PojoCurrentplan currentplan;
    @SerializedName("attendee_countleft")
    @Expose
    public Integer attendeeCountleft;
    @SerializedName("sponsored_session")
    @Expose
    public List<PojoSessionsAttended> sponsoredSession = null;
    @SerializedName("totalattendee")
    @Expose
    public Integer totalattendee;

    @SerializedName("totalamountspent")
    @Expose
    public String totalamountspent;
    @SerializedName("interests")
    @Expose
    public List<PojoGraphInterestItem> interests = null;
}
