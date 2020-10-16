package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetCollegeListForDriveResponse {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoGetCollegeListForDriveResponseData> data = null;
    @SerializedName("status")
    @Expose
    public Integer status;
}
