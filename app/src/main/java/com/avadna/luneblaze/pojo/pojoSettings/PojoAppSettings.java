package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoAppSettings {
    @SerializedName("email")
    @Expose
    public PojoEmail email;
    @SerializedName("push")
    @Expose
    public PojoPush push;
}

