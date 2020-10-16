package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCurrentplan {
    @SerializedName("planid")
    @Expose
    public String planid;
    @SerializedName("planname")
    @Expose
    public String planname;
    @SerializedName("planprice")
    @Expose
    public String planprice;
    @SerializedName("currentbalance")
    @Expose
    public String currentbalance;
    @SerializedName("costperstudent")
    @Expose
    public Integer costperstudent;

}
