package com.avadna.luneblaze.pojo.razorpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoRazorpayGetOrder {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("entity")
    @Expose
    public String entity;
    @SerializedName("amount")
    @Expose
    public Integer amount;
    @SerializedName("amount_paid")
    @Expose
    public Integer amountPaid;
    @SerializedName("amount_due")
    @Expose
    public Integer amountDue;
    @SerializedName("currency")
    @Expose
    public String currency;
    @SerializedName("receipt")
    @Expose
    public Object receipt;
    @SerializedName("offer_id")
    @Expose
    public Object offerId;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("attempts")
    @Expose
    public Integer attempts;
    @SerializedName("created_at")
    @Expose
    public Integer createdAt;
}
