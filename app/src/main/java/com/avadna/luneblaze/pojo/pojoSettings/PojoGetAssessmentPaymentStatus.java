package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetAssessmentPaymentStatus {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
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
    @SerializedName("report")
    @Expose
    public String report;
}
