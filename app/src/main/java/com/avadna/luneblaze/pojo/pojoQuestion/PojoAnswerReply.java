package com.avadna.luneblaze.pojo.pojoQuestion;



import android.text.style.ClickableSpan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoAnswerReply {
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
    @SerializedName("user_data")
    @Expose
    public PojoUserData userData;


    @SerializedName("report_id")
    @Expose
    public String reportId;
    @SerializedName("total_likes")
    @Expose
    public String totalLikes;
    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;
    @SerializedName("i_like")
    @Expose
    public Integer iLike;
    @SerializedName("i_reported")
    @Expose
    public Integer iReported;
    public List<ClickableSpan> clickableSpansList;

    public void PojoAnswerReply(){
        this.iLike=0;
    }

}
