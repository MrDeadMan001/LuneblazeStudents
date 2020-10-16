package com.avadna.luneblaze.pojo.assessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoInitiatePaymentResponse {
    @SerializedName("status")
    @Expose
    public Integer status = 0;
    @SerializedName("message")
    @Expose
    public String message = "";
    @SerializedName("data")
    @Expose
    public String data = null;
}
