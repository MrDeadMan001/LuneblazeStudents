package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetAdTargetsResponse {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Integer data;
    @SerializedName("status")
    @Expose
    public Integer status;
}
