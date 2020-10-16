package com.avadna.luneblaze.pojo.pojoInterest;

import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoInterestDetailsResponseData {
    @SerializedName("interest_data")
    @Expose
    public PojoInterestData interestData;
    @SerializedName("related_interest")
    @Expose
    public List<PojoRelatedInterest> relatedInterest = null;
    @SerializedName("totalSessions")
    @Expose
    public Integer totalSessions;
    @SerializedName("totalQues")
    @Expose
    public Integer totalQues;
    @SerializedName("totalArticles")
    @Expose
    public Integer totalArticles;

    @SerializedName("has_children")
    @Expose
    public String hasChildren;

    @SerializedName("totalAttendes")
    @Expose
    public Integer totalAttendes;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;
    @SerializedName("sessionInterestArr")
    @Expose
    public PojoSessions sessions;

    @SerializedName("articles")
    @Expose
    public List<PojoRelatedArticles> articles = null;

    @SerializedName("questions")
    @Expose
    public List<PojoInterestQuestions> questions = null;

    @SerializedName("users_followed")
    @Expose
    public List<PojoUserData> usersFollowed = null;
}
