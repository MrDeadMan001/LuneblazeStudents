package com.avadna.luneblaze.pojo.pojoQuestion;

import android.text.style.ClickableSpan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//todo uncomment the likes field when it is fixed in api
public class PojoAnswer {
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
    public List<PojoUserData> likes;
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
    public Integer totalLikes;
    @SerializedName("user_data")
    @Expose
    public PojoUserData userData;
    @SerializedName("total_replies")
    @Expose
    public String total_replies;

    @SerializedName("i_follow")
    @Expose
    public Boolean iFollow;

    @SerializedName("i_reported")
    @Expose
    public Integer iReported;
    public List<ClickableSpan> clickableSpansList;
}
