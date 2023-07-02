package com.example.mobiledevicemanager.models;

public class LocationInfo {
    private double latitude;
    private double longitude;

    public LocationInfo() {
        // Default constructor required for Firebase Realtime Database
    }

    public LocationInfo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
