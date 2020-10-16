package com.avadna.luneblaze.pojo.pojoAllSessions;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 14-02-2018.
 */

public class PojoAssignedSessions {
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("is_session_live")
    @Expose
    public String is_session_live;

    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;
    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;
    @SerializedName("event_photos")
    @Expose
    public List<PojoEventPhoto> eventPhotos = null;
    @SerializedName("created_by")
    @Expose
    public PojoCreatedBy createdBy;
    @SerializedName("presentors")
    @Expose
    public List<PojoUserData> presentors;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("total_shares")
    @Expose
    public String totalShares;
    @SerializedName("noofverifieddiscussion")
    @Expose
    public Integer noofverifieddiscussion;
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
    public List<PojoTopic> eventDate;
    @SerializedName("event_time")
    @Expose
    public List<PojoTopic> eventTime;

    @SerializedName("verification_time")
    @Expose
    public String verificationTime;

    @SerializedName("assigning_time")
    @Expose
    public String assigningTime;

    @SerializedName("conclusion_time")
    @Expose
    public String conclusionTime;

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

    @SerializedName("topics")
    @Expose
    public List<PojoTopic> topics = null;
    @SerializedName("prerequisite")
    @Expose
    public List<PojoTopic> prerequisite = null;
    @SerializedName("total_allowed_members")
    @Expose
    public String totalAllowedMembers;

    @SerializedName("finalized_data")
    @Expose
    public String finalizedData;

    @SerializedName("verification_code")
    @Expose
    public String verificationCode;
    @SerializedName("co_ordinators")
    @Expose
    public String coOrdinators;
    @SerializedName("final_place")
    @Expose
    public String finalPlace;
    @SerializedName("venues")
    @Expose
    public List<PojoVenueIdName> venues = null;
    @SerializedName("event_expired")
    @Expose
    public Boolean eventExpired;
    @SerializedName("sharer")
    @Expose
    public List<PojoUserData> sharer = null;
    @SerializedName("i_attend")
    @Expose
    public Boolean iAttend;
    @SerializedName("i_join")
    @Expose
    public Boolean iJoin;
    @SerializedName("i_accept")
    @Expose
    public Boolean iAccept;
    @SerializedName("i_thanks")
    @Expose
    public Integer iThanks;
    @SerializedName("total_attend")
    @Expose
    public String totalAttend;

}
