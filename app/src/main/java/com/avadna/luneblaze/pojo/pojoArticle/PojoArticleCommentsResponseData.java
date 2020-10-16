package com.avadna.luneblaze.pojo.pojoArticle;

import android.text.style.ClickableSpan;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.ArrayList;
import java.util.List;

public class PojoArticleCommentsResponseData {

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
    public Integer totalLikes;

    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;
    @SerializedName("edit_comment")
    @Expose
    public Boolean editComment;
    @SerializedName("user")
    @Expose
    public PojoUserData user;
    @SerializedName("total_replies")
    @Expose
    public Integer totalReplies;

/*    @SerializedName("replies")
    @Expose
    public List<PojoArticleReply> replies = null;*/


    @SerializedName("i_like")
    @Expose
    public Integer iLike;

    @SerializedName("i_reported")
    @Expose
    public Integer iReported;
    public List<ClickableSpan> clickableSpansList;

    public TextCrawler textCrawler;

    public boolean urlLoaded=false;

    public SourceContent sourceContent;



    public PojoArticleCommentsResponseData(String post,String user_id,String addedOn) {
        this.post=post;
        this.userId=user_id;
        this.addedOn=addedOn;
    //    replies=new ArrayList<>();
        this.iLike=0;
    }


}

