package com.avadna.luneblaze.pojo.call;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PojoUserId {
    @SerializedName("user_id")
    @Expose
    private String user_id;
    public PojoUserId(String user_id) {
        this.user_id=user_id;
    }
}
