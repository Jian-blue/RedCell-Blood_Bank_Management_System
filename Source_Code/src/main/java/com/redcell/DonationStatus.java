package com.redcell;

/**
 * Enum representing different donation status types
 */
public enum DonationStatus {
    APPROVED("Approved"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    NOT_COMPLETED("Not Completed");
    
    private final String displayName;
    
    DonationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}