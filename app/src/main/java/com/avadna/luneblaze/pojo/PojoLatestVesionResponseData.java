package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoLatestVesionResponseData {
    @SerializedName("version")
    @Expose
    public String version;

    @SerializedName("compatible")
    @Expose
    public boolean compatible;
}
