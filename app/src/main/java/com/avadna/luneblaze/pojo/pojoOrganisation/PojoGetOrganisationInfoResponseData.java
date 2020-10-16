package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetOrganisationInfoResponseData {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("Name")
    @Expose
    public String name;
    @SerializedName("Type")
    @Expose
    public String type;
    @SerializedName("Typename")
    @Expose
    public String typename;

    @SerializedName("City")
    @Expose
    public String city;
    @SerializedName("is_following")
    @Expose
    public Integer isFollowing;
    @SerializedName("totalfollower")
    @Expose
    public Integer totalfollower;
    @SerializedName("Description")
    @Expose
    public String description;
    @SerializedName("Address")
    @Expose
    public String address;
    @SerializedName("NoOfEmployee")
    @Expose
    public String noOfEmployee;
    @SerializedName("Website")
    @Expose
    public String website;

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
    @SerializedName("ondate")
    @Expose
    public String ondate;
    @SerializedName("updateddate")
    @Expose
    public String updateddate;

    @SerializedName("ads")
    @Expose
    public List<PojoGetNewsFeedResponseData> ads = null;

    @SerializedName("post")
    @Expose
    public List<PojoGetNewsFeedResponseData> post = null;


}
