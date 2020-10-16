package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 14-02-2018.
 */

public class PojoAllInitiationSessions {
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;
    @SerializedName("event_photos")
    @Expose
    public List<PojoEventPhoto> eventPhotos = null;
    @SerializedName("created_by")
    @Expose
    public PojoCreatedBy createdBy;
    @SerializedName("presentors")
    @Expose
    public String presentors;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("total_shares")
    @Expose
    public String totalShares;
    @SerializedName("total_attends")
    @Expose
    public String totalAttends;
    @SerializedName("total_joins")
    @Expose
    public String totalJoins;
    @SerializedName("venue")
    @Expose
    public String venue;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("event_date")
    @Expose
    public String eventDate;
    @SerializedName("event_time")
    @Expose
    public String eventTime;
    @SerializedName("event_duration")
    @Expose
    public String eventDuration;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("shares")
    @Expose
    public String shares;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("session_attend_friends")
    @Expose
    public List<PojoSessionAttendPersons> sessionAttendFriends = null;
    @SerializedName("session_engaged_peoples")
    @Expose
    public List<PojoSessionEngagedPersons> sessionEngagedPeoples = null;
    @SerializedName("session_attend_peoples")
    @Expose
    public List<PojoSessionAttendPersons> sessionAttendPeoples = null;
}
