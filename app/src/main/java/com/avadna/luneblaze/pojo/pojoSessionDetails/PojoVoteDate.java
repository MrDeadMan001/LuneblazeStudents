package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoVoteDate {
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("option_id")
    @Expose
    public String optionId;
    @SerializedName("votes")
    @Expose
    public Integer votes;
    @SerializedName("checked")
    @Expose
    public Boolean checked;
    @SerializedName("percentage")
    @Expose
    public String percentage;

}
