package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 26-03-2018.
 */

public class PojoCreateMessageResponse {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoCreateMessageResponseData data;
}
