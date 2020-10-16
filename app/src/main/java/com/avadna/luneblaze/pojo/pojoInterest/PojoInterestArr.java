package com.avadna.luneblaze.pojo.pojoInterest;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoOrganisedSession;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoUpcomingSession;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoInterestArr {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("interest_name")
    @Expose
    public String interestName;

    @SerializedName("article_name")
    @Expose
    public String articleName;
}
