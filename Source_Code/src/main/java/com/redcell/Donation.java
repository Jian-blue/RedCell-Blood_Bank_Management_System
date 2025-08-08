package com.redcell;

import java.time.LocalDate;

public class Donation {
    private String bloodType;
    private int units;
    private String location;
    private LocalDate date;
    private String status;

    public Donation(String bloodType, int units, String location, LocalDate date, String status) {
        this.bloodType = bloodType;
        this.units = units;
        this.location = location;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}