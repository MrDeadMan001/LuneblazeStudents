package com.avadna.luneblaze.pojo.pojoSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSearchOrganisation {

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
    @SerializedName("Address")
    @Expose
    public String address;
    @SerializedName("NoOfEmployee")
    @Expose
    public String noOfEmployee;
    @SerializedName("Logo")
    @Expose
    public String logo;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("Balance")
    @Expose
    public String balance;
    @SerializedName("currentplan")
    @Expose
    public String currentplan;
    @SerializedName("Website")
    @Expose
    public String website;
    @SerializedName("ondate")
    @Expose
    public String ondate;
    @SerializedName("updateddate")
    @Expose
    public String updateddate;
    @SerializedName("is_following")
    @Expose
    public Integer isFollowing;
    @SerializedName("totalfollower")
    @Expose
    public Integer totalfollower;


}
