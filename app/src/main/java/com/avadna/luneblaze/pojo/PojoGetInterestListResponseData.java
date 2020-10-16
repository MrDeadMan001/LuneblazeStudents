package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetInterestListResponseData {
    @SerializedName("subinterest")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> subinterest = null;
    @SerializedName("interests")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> interests = null;
}
