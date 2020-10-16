package com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetNormalPostReplyResponseData {

    @SerializedName("comment")
    @Expose
    public PojoReplyListParentComment comment;

    @SerializedName("replies")
    @Expose
    public List<PojoNormalPostCommentReplyListItem> replies = null;

    @SerializedName("more_present")
    @Expose
    public Integer morePresent;

    @SerializedName("loadmore")
    @Expose
    public Integer loadMore;
}
