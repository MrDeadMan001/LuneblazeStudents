package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoProfileAnswers {
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
    @SerializedName("total_likes")
    @Expose
    public String totalLikes;
    @SerializedName("session_name")
    @Expose
    public String sessionName;
    @SerializedName("question")
    @Expose
    public String question;
    @SerializedName("question_id")
    @Expose
    public String questionId;
    @SerializedName("asked_by")
    @Expose
    public PojoUserData askedBy;
    @SerializedName("given_by")
    @Expose
    public PojoUserData givenBy;
    @SerializedName("ans_cnt")
    @Expose
    public String ansCnt;
    @SerializedName("qa_upvotes_cnt")
    @Expose
    public String qaUpvotesCnt;
    @SerializedName("ans_upvotes_cnt")
    @Expose
    public String ansUpvotesCnt;
    @SerializedName("i_like")
    @Expose
    public Integer iLike;
    @SerializedName("i_follow")
    @Expose
    public Integer iFollow;

    @SerializedName("reply_count")
    @Expose
    public Integer replyCount;

}
