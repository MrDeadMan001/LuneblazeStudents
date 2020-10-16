package com.avadna.luneblaze.pojo.pojoVenueList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 18-02-2018.
 */

public class PojoGetVenueListResponse {
    @SerializedName("status")
    @Expose
    public Integer status=0;
    @SerializedName("message")
    @Expose
    public String message="";
    @SerializedName("data")
    @Expose
    public List<PojoGetVenueListResponseData> data = new ArrayList<>();
}
