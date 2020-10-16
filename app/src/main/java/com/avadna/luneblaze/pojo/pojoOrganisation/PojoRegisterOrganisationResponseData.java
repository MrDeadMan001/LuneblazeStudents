package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoRegisterOrganisationResponseData {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("companyname")
    @Expose
    public String companyname;
    @SerializedName("legalname")
    @Expose
    public String legalname;
    @SerializedName("companywebsite")
    @Expose
    public String companywebsite;
    @SerializedName("companycontactmail")
    @Expose
    public String companycontactmail;
    @SerializedName("companycontact")
    @Expose
    public String companycontact;
    @SerializedName("id")
    @Expose
    public Integer id;
}
