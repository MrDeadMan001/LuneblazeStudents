package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 01-02-2018.
 */

public class PojoGetSuggestedFriendListResponse {

    @SerializedName("status")
    @Expose
    public Integer status=0;
    @SerializedName("message")
    @Expose
    public String message="";
    @SerializedName("data")
    @Expose
    public List<PojoUserData> data ;
}
