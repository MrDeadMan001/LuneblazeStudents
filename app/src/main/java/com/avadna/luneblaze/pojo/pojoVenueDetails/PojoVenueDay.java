package com.avadna.luneblaze.pojo.pojoVenueDetails;

import com.avadna.luneblaze.pojo.pojoVenueSlots.PojoVenueDaySlot;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoVenueDay {
    @SerializedName("slots")
    @Expose
    public List<PojoVenueDaySlot> slots = null;
}
