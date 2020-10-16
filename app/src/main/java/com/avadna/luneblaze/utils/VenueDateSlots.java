package com.avadna.luneblaze.utils;

import java.util.ArrayList;
import java.util.List;

public class VenueDateSlots {

    List<VenueDays> venueSlots = new ArrayList<>();

    public void initData(){
        for(int i=0;i<7;i++){
            VenueDays day=new VenueDays();
            venueSlots.add(day);
        }
        venueSlots.get(0).timeSlots=new String[]{"11:00:00 AM","2:00:00 PM","5:00:00 PM","7:00:00 PM"};
        venueSlots.get(2).timeSlots=new String[]{"10:00:00 AM","1:00:00 PM","3:00:00 PM","5:00:00 PM"};
        venueSlots.get(5).timeSlots=new String[]{"1:00:00 AM","3:00:00 PM","6:00:00 PM","8:00:00 PM"};
    }

    public class VenueDays {
        public String[] timeSlots;
    }
}




