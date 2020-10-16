package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoUserOrganisationListResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoUserOrganisationListResponseData data;
}
