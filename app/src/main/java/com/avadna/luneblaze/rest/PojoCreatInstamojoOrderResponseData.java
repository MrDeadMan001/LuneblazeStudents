package com.avadna.luneblaze.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreatInstamojoOrderResponseData {
    @SerializedName("order_id")
    @Expose
    public String orderId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("amount")
    @Expose
    public String amount;
}
