package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoOrganizationData {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("Name")
    @Expose
    public String name;
    @SerializedName("Type")
    @Expose
    public String type;
    @SerializedName("City")
    @Expose
    public String city;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("Logo")
    @Expose
    public String logo;
    @SerializedName("Typename")
    @Expose
    public String typename;

}
