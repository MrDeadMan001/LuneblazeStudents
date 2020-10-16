package com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData;

import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoArticlesWritten;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileAnswers;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionConducted;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionJoined;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionInitiated;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoSessionsAttended;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoProfileTabResponseData {
    @SerializedName("questions_count")
    @Expose
    public Integer questionsCount;
    @SerializedName("answers_count")
    @Expose
    public Integer answersCount;
    @SerializedName("articles_count")
    @Expose
    public Integer articlesCount;
    @SerializedName("questions")
    @Expose
    public List<PojoProfileQuestions> questions = null;
    @SerializedName("answers")
    @Expose
    public List<PojoProfileAnswers> answers = null;

    @SerializedName("articles")
    @Expose
    public List<PojoArticlesWritten> articles = null;

    @SerializedName("attended_session")
    @Expose
    public List<PojoSessionsAttended> attendedSession = null;
    @SerializedName("conducted_session")
    @Expose
    public List<PojoSessionConducted> conductedSession = null;
    @SerializedName("joined_session")
    @Expose
    public List<PojoSessionJoined> joinedSession = null;
    @SerializedName("initiated_session")
    @Expose
    public List<PojoSessionInitiated> initiatedSession = null;

    @SerializedName("total_session_count")
    @Expose
    public Integer totalSessionCount;
    @SerializedName("sessionInterestArrCnt")
    @Expose
    public Integer sessionInterestArrCnt;

    @SerializedName("profile")
    @Expose
    public PojoTabDataProfile profile;

}
