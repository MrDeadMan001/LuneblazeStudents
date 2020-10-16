package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoTopic {
    @SerializedName("value")
    @Expose
    public String value;

    public String editable;

    public PojoTopic(String value){
        this.value=value;
        this.editable="1";
    }
}
