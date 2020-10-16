package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoCampusDriveListResponseData {
    @SerializedName("scheduleid")
    @Expose
    public String scheduleid;
    @SerializedName("organization")
    @Expose
    public String organization;
    @SerializedName("college")
    @Expose
    public String college;
    @SerializedName("collegename")
    @Expose
    public String collegename;

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("userid")
    @Expose
    public String userid;
    @SerializedName("added_on")
    @Expose
    public String addedOn;

    @SerializedName("eligible")
    @Expose
    public Integer eligible;

    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("jobs")
    @Expose
    public List<PojoJobListItem> jobs = null;

    @SerializedName("sessionroles")
    @Expose
    public String sessionroles;
    @SerializedName("noofsessions")
    @Expose
    public String noofsessions;
    @SerializedName("interest")
    public List<PojoInterestArr> interest = null;


    @SerializedName("interests")
    @Expose
    public String interests;
    @SerializedName("categories")
    @Expose
    public List<PojoAssessmentCategory> categories = null;
    @SerializedName("companyaddress")
    @Expose
    public String companyaddress;

    @SerializedName("eigible_users")
    @Expose
    public String eigibleUsers;
    @SerializedName("seen_status")
    @Expose
    public String seenStatus;


    public PojoFilterCriteria filterCriteria;

}
