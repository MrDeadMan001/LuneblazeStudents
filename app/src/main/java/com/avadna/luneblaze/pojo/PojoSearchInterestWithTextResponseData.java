package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoSearchInterestWithTextResponseData {
    @SerializedName("interest_id")
    @Expose
    public String interestId;
    @SerializedName("parent_id")
    @Expose
    public String parentId;
    @SerializedName("text")
    @Expose
    public String text;

}
