package com.avadna.luneblaze.pojo.pojoProfileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoWorkHistory {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("subject")
    @Expose
    public String subject;
    @SerializedName("institution")
    @Expose
    public String institution;
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("end_date")
    @Expose
    public String endDate;

    @SerializedName("oraganisation_id")
    @Expose
    public String oraganisationId;




    public PojoWorkHistory(String id, String userId, String type, String subject, String institution, String startDate, String endDate) {
        super();
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.subject = subject;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        oraganisationId="";
    }
}
