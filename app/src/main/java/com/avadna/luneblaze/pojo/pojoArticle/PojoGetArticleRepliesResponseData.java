package com.avadna.luneblaze.pojo.pojoArticle;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.List;

public class PojoGetArticleRepliesResponseData {
    @SerializedName("articles_discussion_id")
    @Expose
    public String articlesDiscussionId;
    @SerializedName("articles_id")
    @Expose
    public String articlesId;
    @SerializedName("post")
    @Expose
    public String post;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("parent_comment_id")
    @Expose
    public String parentCommentId;
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
    @SerializedName("total_replies")
    @Expose
    public Integer totalReplies;
    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;
    @SerializedName("i_like")
    @Expose
    public Integer iLike;

    @SerializedName("edit_reply")
    @Expose
    public Boolean editReply;
    @SerializedName("reply")
    @Expose
    public String reply;
    @SerializedName("reply_id")
    @Expose
    public String replyId;
    @SerializedName("user")
    @Expose
    public PojoUserData user;
    @SerializedName("replies")
    @Expose
    public List<PojoArticleReply> replies = null;
    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("load_more")
    @Expose
    public Integer loadMore;

    public List<ClickableSpan> clickableSpansList;

    public TextCrawler textCrawler;

    public boolean urlLoaded = false;

    public SourceContent sourceContent;


}
