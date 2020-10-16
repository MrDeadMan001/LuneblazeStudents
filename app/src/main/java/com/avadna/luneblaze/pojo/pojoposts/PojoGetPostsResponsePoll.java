package com.avadna.luneblaze.pojo.pojoposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetPostsResponsePoll {

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
    public List<PojoGetPostsResponseOption> options = null;
}

