package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoNewsFeedPoll {

    @SerializedName("poll_id")
    @Expose
    public String pollId;
    @SerializedName("post_id")
    @Expose
    public String postId;
    @SerializedName("votes")
    @Expose
    public String votes;
    @SerializedName("options")
    @Expose
    public List<PojoNewsFeedPollOption> options = null;
}
