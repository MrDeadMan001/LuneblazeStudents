package com.avadna.luneblaze.pojo.pojoVenueSlots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoVenueDaySlot {
    @SerializedName("start")
    @Expose
    public String start;
    @SerializedName("end")
    @Expose
    public String end;
    @SerializedName("slotid")
    @Expose
    public String slotid;
    @SerializedName("isbook")
    @Expose
    public Integer isbook;
}
