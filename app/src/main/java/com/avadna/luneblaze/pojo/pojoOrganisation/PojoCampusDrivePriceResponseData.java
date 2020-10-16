package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoCampusDrivePriceResponseData {
    @SerializedName("campus_drive_price")
    @Expose
    public String campusDrivePrice;
    @SerializedName("off_campus_fee")
    @Expose
    public String offCampusFee;
    @SerializedName("discount_percentage")
    @Expose
    public String discountPercentage;
    @SerializedName("campus_drive_created")
    @Expose
    public Integer campusDriveCreated;
}
