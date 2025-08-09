package com.redcell;

public class Request {
    private String requestId;
    private String patientCondition;
    private String bloodType;
    private int units;
    private String date;
    private String time;
    private Facility facilityInfo;
    private String contact;
    private String status;
    private String createdBy;

    public Request(String requestId, String bloodType, int units, String facilityName, String area, String status, String date, String patientCondition, String time, String contact, String createdBy) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.units = units;
        this.facilityInfo = new Facility(facilityName, area);
        this.status = status;
        this.date = date;
        this.patientCondition = patientCondition;
        this.time = time;
        this.contact = contact;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    // Modified getters to delegate to Facility object
    public String getHospital() { return this.facilityInfo.getName(); }
    public String getArea() { return this.facilityInfo.getArea(); }

    // New getter/setter for the Facility object
    public Facility getFacilityInfo() { return facilityInfo; }
    public void setFacilityInfo(Facility facilityInfo) { this.facilityInfo = facilityInfo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPatientCondition() { return patientCondition; }
    public void setPatientCondition(String patientCondition) { this.patientCondition = patientCondition; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
} 