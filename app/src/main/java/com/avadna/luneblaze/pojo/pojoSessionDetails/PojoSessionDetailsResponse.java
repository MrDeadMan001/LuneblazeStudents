package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSessionDetailsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoSessionDetailsResponseData data;
}
