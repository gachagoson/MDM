package com.example.mobiledevicemanager.models;

public class Alerts {
    private String title;
    private String message;

    public Alerts() {
        // Empty constructor required for Firebase
    }

    public Alerts(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
