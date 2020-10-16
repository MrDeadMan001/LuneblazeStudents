package com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.List;

public class PojoNormalPostCommentReplyListItem {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_email")
    @Expose
    public String userEmail;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_firstname")
    @Expose
    public String userFirstname;
    @SerializedName("user_lastname")
    @Expose
    public String userLastname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_work")
    @Expose
    public String userWork;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;
    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;
    @SerializedName("comment_id")
    @Expose
    public String commentId;
    @SerializedName("node_id")
    @Expose
    public String nodeId;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("replies")
    @Expose
    public String replies;
    @SerializedName("reply_comment_id")
    @Expose
    public String replyCommentId;


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

    public void PojoNormalPostCommentReplyListItem(){
        this.iLike=0;
    }
}
