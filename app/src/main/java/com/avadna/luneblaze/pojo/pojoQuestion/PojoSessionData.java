package com.avadna.luneblaze.pojo.pojoQuestion;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoCreatedBy;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoEventPhoto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSessionData {
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("topics")
    @Expose
    public String topics;
    @SerializedName("prerequisite")
    @Expose
    public String prerequisite;
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
    @SerializedName("finalized_data")
    @Expose
    public String finalizedData;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("page_picture_default")
    @Expose
    public Boolean pagePictureDefault;
    @SerializedName("page_picture")
    @Expose
    public String pagePicture;
}
