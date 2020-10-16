package com.avadna.luneblaze.pojo.pojoQuestion;

import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestQuestions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetQuestionDetailsResponseData {
    @SerializedName("qas")
    @Expose
    public PojoQuestions qas;
    @SerializedName("session_data")
    @Expose
    public PojoSessionData sessionData;



}
