package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoUserEmailPhoneResponseData {

    @SerializedName("mobile")
    @Expose
    public List<PojoExtraInfo> mobile = null;
    @SerializedName("email")
    @Expose
    public List<PojoExtraInfo> email = null;

}
