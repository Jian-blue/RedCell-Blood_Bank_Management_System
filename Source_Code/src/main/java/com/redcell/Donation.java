package com.redcell;

import java.time.LocalDate;

public class Donation {
    private String requestId;
    private String bloodType;
    private int units;
    private String location;
    private LocalDate date;
    private String status;
    private String donorId;

    public Donation(String requestId, String bloodType, int units, String location, LocalDate date, String status) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.units = units;
        this.location = location;
        this.date = date;
        this.status = status;
    }
    
    // Constructor from Request
    public Donation(Request request, String donorId) {
        this.requestId = request.getRequestId();
        this.bloodType = request.getBloodType();
        this.units = request.getUnits();
        this.location = request.getHospital();
        this.date = LocalDate.now();
        this.status = DonationStatus.APPROVED.toString();
        this.donorId = donorId;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
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
    
    public String getDonorId() {
        return donorId;
    }
    
    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }
}