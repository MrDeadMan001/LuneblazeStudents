package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoCreatedBy;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessionEngagedPersons;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestQuestions;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoQuestions;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSessionDetailsResponseData {
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("topics")
    @Expose
    public List<PojoTopic> topics = null;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;

    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;

    @SerializedName("sponsorid")
    @Expose
    public String sponsorid;

    @SerializedName("img_width")
    @Expose
    public String imgWidth;

    @SerializedName("img_height")
    @Expose
    public String imgHeight;

    @SerializedName("is_session_live")
    @Expose
    public String is_session_live;

    @SerializedName("created_by")
    @Expose
    public PojoCreatedBy createdBy;
    @SerializedName("total_shares")
    @Expose
    public String totalShares;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("shares")
    @Expose
    public String shares;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("event_date")
    @Expose
    public List<PojoTopic> eventDate = null;
    @SerializedName("event_time")
    @Expose
    public List<PojoTopic> eventTime = null;
    @SerializedName("prerequisite")
    @Expose
    public List<PojoTopic> prerequisite = null;
    @SerializedName("location")
    @Expose
    public String location;

    @SerializedName("event_duration")
    @Expose
    public String eventDuration;
    @SerializedName("presentors")
    @Expose
    public List<PojoUserData> presentors = null;
    @SerializedName("venues")
    @Expose
    public List<PojoVenue> venues = null;
    @SerializedName("event_expired")
    @Expose
    public Boolean eventExpired;

    @SerializedName("session_venue")
    @Expose
    public String sessionVenue;
    @SerializedName("total_allowed_members")
    @Expose
    public String totalAllowedMembers;
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
    @SerializedName("page_picture_default")
    @Expose
    public Boolean pagePictureDefault;
    @SerializedName("page_picture")
    @Expose
    public String pagePicture;
    @SerializedName("total_attend")
    @Expose
    public String totalAttend;
    @SerializedName("session_attend_friends")
    @Expose
    public List<PojoUserData> sessionAttendFriends = null;
    @SerializedName("session_engaged_peoples")
    @Expose
    public List<PojoUserData> sessionEngagedPeoples = null;
    @SerializedName("session_attend_peoples")
    @Expose
    public List<PojoUserData> sessionAttendPeoples = null;
    @SerializedName("user_thanks_status")
    @Expose
    public Integer userThanksStatus;
    @SerializedName("question_count")
    @Expose
    public String questionCount;
    @SerializedName("thanks_count")
    @Expose
    public String thanksCount;
    @SerializedName("sessionInterestArr")
    @Expose
    public List<PojoInterestArr> sessionInterestArr = null;

    @SerializedName("discussions")
    @Expose
    public List<PojoComment> discussions = null;
    @SerializedName("photos_cnt")
    @Expose
    public Integer photosCnt;
    @SerializedName("discussions_cnt")
    @Expose
    public String discussionsCnt;
    @SerializedName("comments_cnt")
    @Expose
    public String commentsCnt;
    @SerializedName("qaData_cnt")
    @Expose
    public String qaDataCnt;

    @SerializedName("files")
    @Expose
    public List<PojoSessionFile> files = null;
    @SerializedName("videos")
    @Expose
    public List<PojoSessionVideo> videos = null;

    @SerializedName("comments")
    @Expose
    public List<PojoComment> comments = null;
    @SerializedName("photos")
    @Expose
    public List<PojoVenuePhoto> photos = null;
    @SerializedName("qaData")
    @Expose
    public List<PojoSessionQuestions> questions = null;

    @SerializedName("voting")
    @Expose
    public List<PojoVoting> voting = null;

    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("event_photos")
    @Expose
    public List<PojoVenuePhoto> eventPhotos;
    @SerializedName("total_attends")
    @Expose
    public String totalAttends;
    @SerializedName("friends_attend")
    @Expose
    public String friendsAttend;
    @SerializedName("total_joins")
    @Expose
    public String totalJoins;
    @SerializedName("finalized_data")
    @Expose
    public String finalizedData;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("verification_code")
    @Expose
    public String verificationCode;
    @SerializedName("co_ordinators")
    @Expose
    public String coOrdinators;
    @SerializedName("final_place")
    @Expose
    public String finalPlace;

    @SerializedName("sharer")
    @Expose
    public List<PojoUserData> sharer = null;
    @SerializedName("i_attend")
    @Expose
    public Boolean iAttend;

    @SerializedName("i_wait")
    @Expose
    public Boolean iWait;
    @SerializedName("i_join")
    @Expose
    public Boolean iJoin;
    @SerializedName("i_accept")
    @Expose
    public Boolean iAccept;
    @SerializedName("i_thanks")
    @Expose
    public Integer iThanks;


    @SerializedName("last_comment")
    @Expose
    public PojoSessionFirstComment lastComment;
    @SerializedName("first_comment")
    @Expose
    public PojoSessionFirstComment firstComment;

    @SerializedName("first_question")
    @Expose
    public PojoSessionFirstQuestion firstQuestion;
    @SerializedName("last_question")
    @Expose
    public PojoSessionFirstQuestion lastQuestion;

    @SerializedName("first_discussion")
    @Expose
    public PojoSessionFirstDiscussion firstDiscussion;

    @SerializedName("last_discussion")
    @Expose
    public PojoSessionFirstDiscussion lastDiscussion;

    @SerializedName("co_ordinators_data")
    @Expose
    public List<PojoUserData> coOrdinatorsData = null;

    @SerializedName("thanked_users")
    @Expose
    public List<PojoUserData> thankedUsers = null;


    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("load_more")
    @Expose
    public Integer loadMore;
}
