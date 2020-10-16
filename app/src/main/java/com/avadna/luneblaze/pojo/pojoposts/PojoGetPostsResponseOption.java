package com.avadna.luneblaze.pojo.pojoposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetPostsResponseOption {
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
}
