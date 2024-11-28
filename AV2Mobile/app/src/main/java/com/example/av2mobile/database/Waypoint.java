package com.example.av2mobile.database;

import android.location.Location;

public class Waypoint {
    private int id;
    private double latitude;
    private double longitude;
    private double altitude;

    public Waypoint(int id, double latitude, double longitude, double altitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public Waypoint() {
        this.id = 0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.altitude = 0.0;
    }

    public Waypoint(Location location) {
        this.id = 0;
        this.latitude = location.getLatitude();
        this.longitude = location.getLatitude();
        this.altitude = location.getAltitude();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
