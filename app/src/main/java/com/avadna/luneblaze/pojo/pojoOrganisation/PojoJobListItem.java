package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoJobListItem {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("scheduleid")
    @Expose
    public String scheduleid;
    @SerializedName("jobname")
    @Expose
    public String jobname;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("noofvacancies")
    @Expose
    public String noofvacancies;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;


    public PojoJobListItem(String jobname, String description, String noofvacancies) {
        this.id = "";
        this.scheduleid = "";
        this.jobname = jobname;
        this.description = description;
        this.noofvacancies = noofvacancies;
        this.modifiedOn = "";
    }
}
