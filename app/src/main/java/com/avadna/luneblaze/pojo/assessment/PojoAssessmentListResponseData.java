package com.avadna.luneblaze.pojo.assessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAssessmentListResponseData {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("report")
    @Expose
    public String report;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("transaction_id")
    @Expose
    public String transactionId;
    @SerializedName("order_id")
    @Expose
    public String orderId;
    @SerializedName("payment_id")
    @Expose
    public String paymentId;
}
