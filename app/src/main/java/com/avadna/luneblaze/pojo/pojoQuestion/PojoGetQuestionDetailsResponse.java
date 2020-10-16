package com.avadna.luneblaze.pojo.pojoQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetQuestionDetailsResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoGetQuestionDetailsResponseData data;

}
