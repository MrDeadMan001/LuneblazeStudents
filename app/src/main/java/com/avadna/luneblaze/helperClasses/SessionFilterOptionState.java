package com.avadna.luneblaze.helperClasses;

import java.util.List;

public class SessionFilterOptionState {
    public String user_id;
    public int globalOffset;
    public double latitude;
    public double longitude;
    public String oldSessionRole;
    public String oldLocation;
    public List<String> selectedVenueIdList;
    public String oldIsMyVenuesSelected;
    public List<String> selectedInterestIdList;
    public String oldIsMyInterestSelected;
    public String oldTime;
    public String oldStartDate;
    public String oldEndDate;


    public SessionFilterOptionState(String user_id, int globalOffset, double latitude, double longitude,
                                    String oldSessionRole, String oldLocation, List<String> selectedVenueIdList,
                                    String oldIsMyVenuesSelected,  List<String> selectedInterestIdList,
                                    String oldIsMyInterestSelected, String oldTime, String oldStartDate,
                                    String oldEndDate) {
        this.user_id = user_id;
        this.globalOffset = globalOffset;
        this.latitude = latitude;
        this.longitude = longitude;
        this.oldSessionRole = oldSessionRole;
        this.oldLocation = oldLocation;
        this.selectedVenueIdList = selectedVenueIdList;
        this.oldIsMyVenuesSelected = oldIsMyVenuesSelected;
        this.selectedInterestIdList = selectedInterestIdList;
        this.oldIsMyInterestSelected = oldIsMyInterestSelected;
        this.oldTime = oldTime;
        this.oldStartDate = oldStartDate;
        this.oldEndDate = oldEndDate;
    }
}
