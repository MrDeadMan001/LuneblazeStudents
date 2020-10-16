package com.avadna.luneblaze.pojo.pojoVenueSlots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoVenueAllowedDayItem {
    @SerializedName("slots")
    @Expose
    public List<PojoVenueDaySlot> slots = null;
}
