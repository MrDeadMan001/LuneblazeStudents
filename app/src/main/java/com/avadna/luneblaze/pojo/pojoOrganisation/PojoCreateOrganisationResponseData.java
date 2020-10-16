package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCreateOrganisationResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("noofemployee")
    @Expose
    public String noofemployee;
    @SerializedName("website")
    @Expose
    public String website;
}
