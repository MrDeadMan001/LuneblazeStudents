package com.avadna.luneblaze.pojo.pojoSessionDetails;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoReply;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.List;

public class PojoSessionReply {
    @SerializedName("sessions_discussion_id")
    @Expose
    public String sessionsDiscussionId;
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
    @SerializedName("parent_discussion_id")
    @Expose
    public String parentDiscussionId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("user_id")
    @Expose
    public String userId;
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


    @SerializedName("sessions_comment_id")
    @Expose
    public String sessionsCommentId;
    @SerializedName("parent_comment_id")
    @Expose
    public String parentCommentId;

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

    public TextCrawler textCrawler;
}
