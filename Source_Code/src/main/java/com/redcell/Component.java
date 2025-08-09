package com.redcell;

/**
 * Enum representing different blood component types
 */
public enum Component {
    WHOLE_BLOOD("Whole Blood"),
    RCC_PRBC("RCC/PRBC"),
    SDP("SDP"),
    FFP("FFP");
    
    private final String displayName;
    
    Component(String displayName) {
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