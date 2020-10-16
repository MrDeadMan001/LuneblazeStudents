package com.avadna.luneblaze.pojo.pojoSearch;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoOrganisedSession;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoUpcomingSession;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSearchSession {
    @SerializedName("assigned")
    @Expose
    public List<PojoAssignedSessions> assigned = null;
    @SerializedName("organized")
    @Expose
    public List<PojoOrganisedSession> organized = null;
    @SerializedName("verified")
    @Expose
    public List<PojoUpcomingSession> verified = null;
    @SerializedName("initiation")
    @Expose
    public List<Object> initiation = null;
    @SerializedName("attending")
    @Expose
    public List<Object> attending = null;
}
