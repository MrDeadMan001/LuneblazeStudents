package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoSearchInterestWithTextResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public List<PojoSearchInterestWithTextResponseData> data = null;
}
