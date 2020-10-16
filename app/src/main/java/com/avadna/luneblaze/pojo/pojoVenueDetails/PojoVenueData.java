package com.avadna.luneblaze.pojo.pojoVenueDetails;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 17-02-2018.
 */

public class PojoVenueData {
    @SerializedName("venue_id")
    @Expose
    public String venueId="";
    @SerializedName("venue_type")
    @Expose
    public String venueType="";

    @SerializedName("venue_name")
    @Expose
    public String venueName="";
    @SerializedName("institute_id")
    @Expose
    public String instituteId="";
    @SerializedName("description")
    @Expose
    public String description="";
    @SerializedName("courses_offered")
    @Expose
    public String coursesOffered="";
    @SerializedName("total_rating")
    @Expose
    public String totalRating="";
    @SerializedName("teams")
    @Expose
    String teams = "";
    @SerializedName("status")
    @Expose
    public String status="";
    @SerializedName("latitude")
    @Expose
    public String latitude="0";
    @SerializedName("longitude")
    @Expose
    public String longitude="0";
    @SerializedName("link")
    @Expose
    public String link="";
    @SerializedName("added_on")
    @Expose
    public String addedOn="";
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn="";
    @SerializedName("name")
    @Expose
    public String name="";

    @SerializedName("cover_photo")
    @Expose
    public String cover_photo="";
    @SerializedName("photo")
    @Expose
    public String photo="";
    @SerializedName("student_ids")
    @Expose
    public String studentIds="";

    @SerializedName("i_follow")
    @Expose
    public Integer iFollow=0;

    @SerializedName("session_count")
    @Expose
    public Integer sessionCount=0;
    @SerializedName("session_attend_count")
    @Expose
    public Integer sessionAttendCount=0;
    @SerializedName("followers_count")
    @Expose
    public Integer followersCount=0;

    @SerializedName("location")
    @Expose
    public String location;

    @SerializedName("website_link")
    @Expose
    public String websiteLink;

    @SerializedName("venue_attendes_count")
    @Expose
    public Integer venueAttendesCount;

    @SerializedName("rating1")
    @Expose
    public Float rating1;
    @SerializedName("rating2")
    @Expose
    public Float rating2;
    @SerializedName("rating3")
    @Expose
    public Float rating3;
    @SerializedName("rating4")
    @Expose
    public Float rating4;

    @SerializedName("venue_attendes")
    @Expose
    public List<PojoUserData> venueAttendes = null;

    @SerializedName("venue_followers")
    @Expose
    public List<PojoUserData> venueFollowers = null;
}
