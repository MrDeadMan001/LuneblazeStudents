package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoNewsFeedVoter {

    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("poll_id")
    @Expose
    public String pollId;
    @SerializedName("option_id")
    @Expose
    public String optionId;
    @SerializedName("user_data")
    @Expose
    public PojoUserData userData;
}
