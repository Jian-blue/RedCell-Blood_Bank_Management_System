package com.redcell;

/**
 * Concrete implementation of User class for authentication and basic user operations
 */
public class ConcreteUser extends User {
    private int id;
    private String username;
    private String role;
    
    // Default constructor
    public ConcreteUser() {
        super("", "");
    }
    
    // Constructor with name and area (calls parent constructor)
    public ConcreteUser(String name, String area) {
        super(name, area);
    }
    
    // Full constructor
    public ConcreteUser(int id, String username, String name, String area, String role) {
        super(name, area);
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String getUserType() {
        return role != null ? role.toLowerCase() : "donor";
    }
}