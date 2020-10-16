package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 01-02-2018.
 */

public class PojoUserConnectResponse {
    @SerializedName("status")
    @Expose
    public Integer status = 0;
    @SerializedName("message")
    @Expose
    public String message = "";
    @SerializedName("data")
    @Expose
    public PojoUserConnectResponseData data=null;
}
