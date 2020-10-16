package com.avadna.luneblaze.pojo.pojoInterest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInterestDetailsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoInterestDetailsResponseData data;
}
