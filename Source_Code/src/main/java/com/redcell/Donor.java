package com.redcell;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; // Import ArrayList

// Represents a blood donor
public class Donor extends User {

    // Attributes
    private String bloodType;
    private LocalDate lastDonationDate;
    private DonorStatus status;
    private int totalDonations;
    private ArrayList<Request> donationHistory;

    // Constructor
    public Donor(String name, String bloodType, LocalDate lastDonationDate, String area) {
        super(name, area);
        this.donationHistory = new ArrayList<>();
        // Add sample Request objects
        donationHistory.add(new Request("REQ001", bloodType, 2, "City Hospital", area, "Completed", LocalDate.now().minusMonths(6).toString(), "General Checkup", "10:00", "Dr. Smith", "System"));
        donationHistory.add(new Request("REQ002", bloodType, 3, "General Hospital", area,  "Completed", LocalDate.now().minusMonths(12).toString(), "Follow Up", "11:00", "Dr. Jones", "System"));
        donationHistory.add(new Request("REQ003", bloodType, 1, "St. Mary's", area, "Completed", LocalDate.now().minusMonths(18).toString(), "Regular Visit", "14:00", "Dr. Lee", "System"));
        this.bloodType = bloodType;
        this.lastDonationDate = lastDonationDate;
        this.status = DonorStatus.ELIGIBLE;
        this.totalDonations = 0;
    }

    // Methods
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }
    
    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public DonorStatus getStatus() {
        return status;
    }

    public void setStatus(DonorStatus status) {
        this.status = status;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
    }

    public ArrayList<Request> getDonationHistory() {
        return donationHistory;
    }

    // Method to count completed donations in the history
    public int getCompletedDonationCount() {
        int count = 0;
        for (Request request : donationHistory) {
            if (request.getStatus().equalsIgnoreCase("Completed")) {
                count++;
            }
        }
        return count;
    }

    // Methods to check badge eligibility
    public boolean hasFirstDonationBadge() {
        return getCompletedDonationCount() >= 1;
    }

    public boolean hasTenDonationsBadge() {
        return getCompletedDonationCount() >= 10;
    }

    public String getTimeSinceLastDonation() {
        if (lastDonationDate == null) {
            return "Never donated";
        }
        
        long months = ChronoUnit.MONTHS.between(lastDonationDate, LocalDate.now());
        long days = ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());
        
        if (months > 0) {
            return months + (months == 1 ? " month ago" : " months ago");
        } else {
            return days + (days == 1 ? " day ago" : " days ago");
        }
    }
}