package com.redcell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Represents a medical facility
public class Facility extends User {

    // Attributes
    private int totalRequestCount;
    private int todaysRequestCount;
    private int totalDonationCount;
    private int todaysDonationCount;
    private List<Component> components;
    private Map<String, Integer> inventory;
    private List<Request> bloodRequests;
    private List<Donation> bloodDonations;
    private List<Donation> completionDonations;

    // Constructor
    public Facility(String name, String area) {
        super(name, area);
        this.totalRequestCount = 0;
        this.todaysRequestCount = 0;
        this.totalDonationCount = 0;
        this.todaysDonationCount = 0;
        this.components = new ArrayList<>();
        this.inventory = new HashMap<>();
        initializeInventory();
        this.bloodRequests = new ArrayList<>();
        this.bloodDonations = new ArrayList<>();
        this.completionDonations = new ArrayList<>();
    }

    private void initializeInventory() {
        Random random = new Random();
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String type : bloodTypes) {
            inventory.put(type, random.nextInt(100) + 10); // Random values between 10 and 109
        }
    }

    // Getters and Setters
    public int getTotalRequestCount() {
        return totalRequestCount;
    }

    public void setTotalRequestCount(int totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    public int getTodaysRequestCount() {
        return todaysRequestCount;
    }

    public void setTodaysRequestCount(int todaysRequestCount) {
        this.todaysRequestCount = todaysRequestCount;
    }

    public int getTotalDonationCount() {
        return totalDonationCount;
    }

    public void setTotalDonationCount(int totalDonationCount) {
        this.totalDonationCount = totalDonationCount;
    }

    public int getTodayDonationCount() {
        return todaysDonationCount;
    }

    public void setTodayDonationCount(int todayDonationCount) {
        this.todaysDonationCount = todayDonationCount;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public List<Request> getBloodRequests() {
        return bloodRequests;
    }

    public void setBloodRequests(List<Request> bloodRequests) {
        this.bloodRequests = bloodRequests;
    }

    public List<Donation> getBloodDonations() {
        return bloodDonations;
    }

    public void setBloodDonations(List<Donation> bloodDonations) {
        this.bloodDonations = bloodDonations;
    }
    
    public List<Donation> getCompletionDonations() {
        return completionDonations;
    }

    public void setCompletionDonations(List<Donation> completionDonations) {
        this.completionDonations = completionDonations;
    }
}