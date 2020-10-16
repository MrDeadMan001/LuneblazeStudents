package com.avadna.luneblaze.pojo.pojoActiveSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoActiveSessionsResponseData {
    @SerializedName("sessions")
    @Expose
    public List<PojoActiveSession> sessions = null;
}
