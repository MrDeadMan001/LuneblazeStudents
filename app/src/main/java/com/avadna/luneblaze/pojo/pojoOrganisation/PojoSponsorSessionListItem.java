package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoVenueIdName;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSponsorSessionListItem {
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("topics")
    @Expose
    public List<PojoTopic> topics = null;
    @SerializedName("prerequisite")
    @Expose
    public List<PojoTopic> prerequisite = null;
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
    public List<Object> eventPhotos = null;
    @SerializedName("created_by")
    @Expose
    public PojoUserData createdBy;
    @SerializedName("presentors")
    @Expose
    public List<PojoUserData> presentors = null;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("total_shares")
    @Expose
    public String totalShares;

    @SerializedName("sponsored")
    @Expose
    public boolean sponsored;

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
    public List<PojoTopic> eventDate = null;
    @SerializedName("event_time")
    @Expose
    public List<PojoTopic> eventTime = null;
    @SerializedName("total_allowed_members")
    @Expose
    public String totalAllowedMembers;
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
    @SerializedName("finalized_data")
    @Expose
    public String finalizedData;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("verification_time")
    @Expose
    public String verificationTime;
    @SerializedName("assigning_time")
    @Expose
    public String assigningTime;
    @SerializedName("conclusion_time")
    @Expose
    public String conclusionTime;
    @SerializedName("verification_code")
    @Expose
    public String verificationCode;
    @SerializedName("co_ordinators")
    @Expose
    public String coOrdinators;
    @SerializedName("final_place")
    @Expose
    public String finalPlace;
    @SerializedName("sponsorid")
    @Expose
    public String sponsorid;
    @SerializedName("venues")
    @Expose
    public List<PojoVenueIdName> venues = null;
    @SerializedName("min_distance_m")
    @Expose
    public Integer minDistanceM;
    @SerializedName("min_distance_km")
    @Expose
    public String minDistanceKm;
    @SerializedName("event_expired")
    @Expose
    public Boolean eventExpired;
    @SerializedName("sharer")
    @Expose
    public List<PojoUserData> sharer = null;
    @SerializedName("session_venue")
    @Expose
    public String sessionVenue;
    @SerializedName("users_venue")
    @Expose
    public Integer usersVenue;
    @SerializedName("sharer_following")
    @Expose
    public Integer sharerFollowing;
    @SerializedName("session_reported")
    @Expose
    public Integer sessionReported;
    @SerializedName("user_interest")
    @Expose
    public Boolean userInterest;
    @SerializedName("allow_attend")
    @Expose
    public Boolean allowAttend;
    @SerializedName("allow_accept")
    @Expose
    public Boolean allowAccept;
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
    @SerializedName("noofthanks")
    @Expose
    public Integer noofthanks;
    @SerializedName("total_attend")
    @Expose
    public String totalAttend;
    @SerializedName("total_question_asked")
    @Expose
    public String totalQuestionAsked;
    @SerializedName("session_attend_friends")
    @Expose
    public List<Object> sessionAttendFriends = null;
    @SerializedName("session_engaged_peoples")
    @Expose
    public List<PojoUserData> sessionEngagedPeoples = null;
    @SerializedName("session_attend_peoples")
    @Expose
    public List<PojoUserData> sessionAttendPeoples = null;
    @SerializedName("noofverifieddiscussion")
    @Expose
    public Integer noofverifieddiscussion;
    @SerializedName("blocked_ids")
    @Expose
    public List<String> blockedIds = null;
}
