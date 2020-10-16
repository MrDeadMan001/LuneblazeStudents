package com.avadna.luneblaze.pojo.pojoNewsFeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoNewsFeedPollOption {
    @SerializedName("option_id")
    @Expose
    public String optionId;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("votes")
    @Expose
    public Integer votes;
    @SerializedName("checked")
    @Expose
    public Boolean checked;

    @SerializedName("voters")
    @Expose
    public List<PojoNewsFeedVoter> voters = null;


    public PojoNewsFeedPollOption(String text){
        optionId="";
        this.text=text;
        votes=0;
        checked=false;
        voters=new ArrayList<>();

    }
}
