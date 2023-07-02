package com.example.mobiledevicemanager.models;

import java.util.Map;

public class Users {
    private String userId;
    private String username;
    private String phoneNumber;
    private String email;
    private String deviceManufacturer;
    private String deviceModel;
    private String deviceSerialNumber;
    private String deviceIMEI;
    private LockScreenInfo lockScreenInfo;

    private LocationInfo locationInfo;

    private Map<String, Boolean> backupData;

    public Users() {
        // Default constructor required for Firebase Realtime Database
    }

    public Users(String userId, String username, String phoneNumber, String email, String deviceManufacturer, String deviceModel, String deviceSerialNumber, String deviceIMEI) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
        this.deviceSerialNumber = deviceSerialNumber;
        this.deviceIMEI = deviceIMEI;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceIMEI() {
        return deviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        this.deviceIMEI = deviceIMEI;
    }

    public LockScreenInfo getLockScreenInfo() {
        return lockScreenInfo;
    }

    public void setLockScreenInfo(LockScreenInfo lockScreenInfo) {
        this.lockScreenInfo = lockScreenInfo;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public Map<String, Boolean> getBackupData() {
        return backupData;
    }

    public void setBackupData(Map<String, Boolean> backupData) {
        this.backupData = backupData;
    }
}
