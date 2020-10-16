package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoUserSettingsResponse{
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("data")
        @Expose
        public PojoUserSettingsResponseData data;
}
