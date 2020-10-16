package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoGetCitiesResponseData {
    @SerializedName("cityid")
    @Expose
    public String cityid;
    @SerializedName("cityname")
    @Expose
    public String cityname;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
}
