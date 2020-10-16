package com.avadna.luneblaze.pojo.pojonormalpost;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.List;

public class PojoNormalPostComment {
    @SerializedName("comment_id")
    @Expose
    public String commentId;
    @SerializedName("node_id")
    @Expose
    public String nodeId;
    @SerializedName("node_type")
    @Expose
    public String nodeType;
    @SerializedName("user_id")
    @Expose
    public String userId;
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
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_verified")
    @Expose
    public String userVerified;
    @SerializedName("text_plain")
    @Expose
    public String textPlain;
    @SerializedName("author_id")
    @Expose
    public String authorId;
    @SerializedName("author_picture")
    @Expose
    public String authorPicture;
    @SerializedName("author_url")
    @Expose
    public String authorUrl;
    @SerializedName("author_name")
    @Expose
    public String authorName;
    @SerializedName("author_verified")
    @Expose
    public String authorVerified;
    @SerializedName("connection")
    @Expose
    public String connection;
    @SerializedName("edit_comment")
    @Expose
    public Boolean editComment;
    @SerializedName("delete_comment")
    @Expose
    public Boolean deleteComment;
    @SerializedName("i_like")
    @Expose
    public Boolean iLike;

    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers;

    @SerializedName("i_reported")
    @Expose
    public Integer iReported;

    public List<ClickableSpan> clickableSpansList;

    public TextCrawler textCrawler;

    public SourceContent sourceContent;
}
