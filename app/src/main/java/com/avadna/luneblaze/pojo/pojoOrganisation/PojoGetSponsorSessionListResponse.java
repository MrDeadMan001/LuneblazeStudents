package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetSponsorSessionListResponse {

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoSponsorSessionListItem> data = null;
    @SerializedName("status")
    @Expose
    public Integer status;
}
