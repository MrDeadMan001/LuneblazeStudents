package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 24-12-2017.
 */




public class PojoSignUp1Response {

    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public PojoSignUp1ResponseData data;

}
