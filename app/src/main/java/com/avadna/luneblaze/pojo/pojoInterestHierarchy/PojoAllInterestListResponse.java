package com.avadna.luneblaze.pojo.pojoInterestHierarchy;

import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoAllInterestListResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> data;
}
