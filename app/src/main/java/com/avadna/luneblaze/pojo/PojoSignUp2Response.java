
package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSignUp2Response {

    @SerializedName("status")
    @Expose
    public Integer status=0;
    @SerializedName("message")
    @Expose
    public String message="";
    @SerializedName("data")
    @Expose
    public PojoSignUp2ResponseData data;

}