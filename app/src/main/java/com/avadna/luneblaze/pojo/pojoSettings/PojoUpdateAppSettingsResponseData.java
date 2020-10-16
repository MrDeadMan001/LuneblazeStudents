package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class PojoUpdateAppSettingsResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("update_field")
    @Expose
    public String updateField;
    @SerializedName("update_value")
    @Expose
    public String updateValue;
}
