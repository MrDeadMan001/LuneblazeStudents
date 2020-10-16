package com.avadna.luneblaze.pojo.pojoSuggestedInterests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoSuggestedInterestResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoSuggestedInterestResponseData> data = null;
}
