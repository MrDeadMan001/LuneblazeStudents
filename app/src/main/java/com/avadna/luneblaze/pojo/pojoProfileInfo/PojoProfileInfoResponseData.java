package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileQuestions;
import com.avadna.luneblaze.pojo.pojoSettings.PojoRelatedInterestArr;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoProfileInfoResponseData {
    @SerializedName("questions_count")
    @Expose
    public Integer questionsCount;
    @SerializedName("answers_count")
    @Expose
    public Integer answersCount;
    @SerializedName("polls_count")
    @Expose
    public Integer pollsCount;
    @SerializedName("activity_count")
    @Expose
    public Integer activityCount;
    @SerializedName("articles_count")
    @Expose
    public Integer articlesCount;
    @SerializedName("total_session_count")
    @Expose
    public Integer totalSessionCount;
    @SerializedName("sessionInterestArrCnt")
    @Expose
    public Integer sessionInterestArrCnt;
    @SerializedName("posts_count")
    @Expose
    public Integer postsCount;

    @SerializedName("profile")
    @Expose
    public PojoProfile profile;

    @SerializedName("questions")
    @Expose
    public List<PojoProfileQuestions> questions = null;

    @SerializedName("answers")
    @Expose
    public List<PojoProfileAnswers> answers = null;


}
