package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 19-03-2018.
 */

public class PojoUserInterestsListResponseData {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("text")
    @Expose
    public String text;
}
