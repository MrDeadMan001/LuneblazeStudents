package com.avadna.luneblaze.pojo.pojoVenueDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetVenueDetailsResponse {
    @SerializedName("status")
    @Expose
    public Integer status=0;
    @SerializedName("message")
    @Expose
    public String message="";
    @SerializedName("data")
    @Expose

    public PojoGetVenueDetailsResponseData data=new PojoGetVenueDetailsResponseData();

}
