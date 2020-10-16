package com.avadna.luneblaze.pojo.pojoSessionDetails;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.List;

public class PojoComment {
    @SerializedName("sessions_discussion_id")
    @Expose
    public String sessionsDiscussionId;
    @SerializedName("sessions_comment_id")
    @Expose
    public String sessionsCommentId;
    @SerializedName("sessions_id")
    @Expose
    public String sessionsId;
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

    @SerializedName("i_like")
    @Expose
    public boolean iLike;

    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;
    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;
    @SerializedName("total_replies")
    @Expose
    public Integer totalReplies;

    @SerializedName("i_reported")
    @Expose
    public Integer iReported;


    public List<ClickableSpan> clickableSpansList;

    public TextCrawler textCrawler;

    public SourceContent sourceContent;
}
