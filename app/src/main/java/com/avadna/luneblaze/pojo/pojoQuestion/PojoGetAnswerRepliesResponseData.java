package com.avadna.luneblaze.pojo.pojoQuestion;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionQuestions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetAnswerRepliesResponseData {
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
    @SerializedName("user")
    @Expose
    public PojoUserData user;
    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;
    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;
    @SerializedName("total_replies")
    @Expose
    public Integer totalReplies;
    @SerializedName("replies")
    @Expose
    public List<PojoAnswerReply> replies = null;


    @SerializedName("report_id")
    @Expose
    public String reportId;
    @SerializedName("i_reported")
    @Expose
    public Integer iReported;

    @SerializedName("i_like")
    @Expose
    public Integer iLike;


    @SerializedName("no_of_replies")
    @Expose
    public String no_of_replies;

    @SerializedName("question")
    @Expose
    public PojoSessionQuestions question;


    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("load_more")
    @Expose
    public Integer loadMore;


    public List<ClickableSpan> clickableSpansList;
}
