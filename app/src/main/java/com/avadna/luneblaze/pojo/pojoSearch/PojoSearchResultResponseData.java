package com.avadna.luneblaze.pojo.pojoSearch;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSearchResultResponseData {

    @SerializedName("posts")
    @Expose
    public List<PojoGetNewsFeedResponseData> posts = null;

    @SerializedName("poll")
    @Expose
    public List<PojoGetNewsFeedResponseData> poll = null;

    @SerializedName("users")
    @Expose
    public List<PojoUserData> users = null;
    @SerializedName("articles")
    @Expose
    public List<PojoSearchArticle> articles = null;
    @SerializedName("venues")
    @Expose
    public List<PojoSearchVenue> venues = null;
    @SerializedName("sessions")
    @Expose
    public PojoSearchSession sessions = null;
    @SerializedName("qa")
    @Expose
    public List<PojoSearchQa> qa = null;
    @SerializedName("interest_sessions")
    @Expose
    public List<PojoInterestSession> interestSessions = null;

    @SerializedName("organization")
    @Expose
    public List<PojoSearchOrganisation> organization = null;

    @SerializedName("posts_count")
    @Expose
    public Integer postsCount;
    @SerializedName("users_count")
    @Expose
    public Integer usersCount;
    @SerializedName("articles_count")
    @Expose
    public Integer articlesCount;

    @SerializedName("poll_count")
    @Expose
    public Integer pollCount;

    @SerializedName("venues_count")
    @Expose
    public Integer venuesCount;
    @SerializedName("sessions_count")
    @Expose
    public Integer sessionsCount;
    @SerializedName("qa_count")
    @Expose
    public Integer qaCount;
    @SerializedName("interest_count")
    @Expose
    public Integer interestCount;
    @SerializedName("query")
    @Expose
    public String query;

}
