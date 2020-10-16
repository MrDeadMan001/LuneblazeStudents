package com.avadna.luneblaze.helperClasses;

public class MyLocationObject {

    public double latitude;
    public double longitude;

    public MyLocationObject(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
