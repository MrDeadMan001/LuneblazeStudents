package com.avadna.luneblaze.pojo.pojoInterest;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoAnswer;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoInterestQuestions {
    @SerializedName("sessions_qa_id")
    @Expose
    public String sessionsQaId;
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("qa_type")
    @Expose
    public String qaType;
    @SerializedName("parent_question_id")
    @Expose
    public String parentQuestionId;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("i_like")
    @Expose
    public Boolean iLike;
    @SerializedName("total_likes")
    @Expose
    public String totalLikes;
    @SerializedName("total_ans")
    @Expose
    public Integer totalAns;
    @SerializedName("answers")
    @Expose
    public List<PojoAnswer> answers = null;
    @SerializedName("user_data")
    @Expose
    public PojoUserData userData;

    @SerializedName("i_follow")
    @Expose
    public Boolean iFollow;
    @SerializedName("session_name")
    @Expose
    public String sessionName;

}
