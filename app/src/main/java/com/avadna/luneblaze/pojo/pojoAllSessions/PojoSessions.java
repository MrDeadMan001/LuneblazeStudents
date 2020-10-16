package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 14-02-2018.
 */

public class PojoSessions {
    @SerializedName("initiation")
    @Expose
    public List<PojoUpcomingSession> initiation = new ArrayList<>();
    @SerializedName("assigned")
    @Expose
    public List<PojoAssignedSessions> assigned = new ArrayList<>();
    @SerializedName("verified")
    @Expose
    public List<PojoUpcomingSession> verified = new ArrayList<>();
    @SerializedName("organized")
    @Expose
    public List<PojoOrganisedSession> organized = new ArrayList<>();
    @SerializedName("upcoming")
    @Expose
    public List<PojoUpcomingSession> upcoming = null;
    @SerializedName("running")
    @Expose
    public List<Object> running = null;
    @SerializedName("expired")
    @Expose
    public List<Object> expired = null;

}
