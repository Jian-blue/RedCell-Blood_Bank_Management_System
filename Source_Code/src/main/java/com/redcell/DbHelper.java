package com.redcell;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {

    private static final String DB_URL = "jdbc:sqlite:c:/Users/Jian/Documents/Blue Codium/RedCell/src/main/resources/redcell.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            } else {
                System.out.println("Could not connect to the database.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (\n" +
                          "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                          "    username TEXT NOT NULL UNIQUE,\n" +
                          "    password TEXT NOT NULL,\n" +
                          "    role TEXT NOT NULL\n" +
                          ");";

        String sqlDonors = "CREATE TABLE IF NOT EXISTS donors (\n" +
                           "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                           "    user_id INTEGER NOT NULL,\n" +
                           "    blood_group TEXT NOT NULL,\n" +
                           "    last_donation_date TEXT,\n" +
                           "    FOREIGN KEY (user_id) REFERENCES users(id)\n" +
                           ");";

        String sqlFacilities = "CREATE TABLE IF NOT EXISTS facilities (\n" +
                               "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                               "    user_id INTEGER NOT NULL,\n" +
                               "    name TEXT NOT NULL,\n" +
                               "    location TEXT,\n" +
                               "    FOREIGN KEY (user_id) REFERENCES users(id)\n" +
                               ");";

        String sqlDonations = "CREATE TABLE IF NOT EXISTS donations (\n" +
                              "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                              "    donor_id INTEGER NOT NULL,\n" +
                              "    date TEXT NOT NULL,\n" +
                              "    blood_type TEXT NOT NULL,\n" +
                              "    quantity REAL NOT NULL,\n" +
                              "    FOREIGN KEY (donor_id) REFERENCES donors(id)\n" +
                              ");";

        String sqlRequests = "CREATE TABLE IF NOT EXISTS requests (\n" +
                             "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                             "    facility_id INTEGER NOT NULL,\n" +
                             "    blood_type TEXT NOT NULL,\n" +
                             "    quantity REAL NOT NULL,\n" +
                             "    status TEXT NOT NULL,\n" +
                             "    FOREIGN KEY (facility_id) REFERENCES facilities(id)\n" +
                             ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlDonors);
            stmt.execute(sqlFacilities);
            stmt.execute(sqlDonations);
            stmt.execute(sqlRequests);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createNewDatabase();
        createTables();
        updateSchema();
    }
    
    /**
     * Updates the database schema according to the recommendations in dbplan.md
     * This method adds new tables and alters existing ones to implement the required relationships
     */
    public static void updateSchema() {
        // Create new tables
        createDonationRequestsTable();
        createInventoryTable();
        
        // Alter existing tables
        addColumnsToUsersTable();
        addColumnsToDonationsTable();
        addColumnsToRequestsTable();
        
        // Create indexes for performance
        createIndexes();
    }
    
    /**
     * Creates the donation_requests junction table to handle the Many-to-Many relationship
     * between donations and requests
     */
    public static void createDonationRequestsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS donation_requests (\n" +
                     "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                     "    donation_id INTEGER NOT NULL,\n" +
                     "    request_id INTEGER NOT NULL,\n" +
                     "    quantity REAL NOT NULL,\n" +
                     "    date TEXT NOT NULL,\n" +
                     "    FOREIGN KEY (donation_id) REFERENCES donations(id),\n" +
                     "    FOREIGN KEY (request_id) REFERENCES requests(id)\n" +
                     ");"; 
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("donation_requests table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Creates the inventory table to track blood components by type and facility
     */
    public static void createInventoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS inventory (\n" +
                     "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                     "    facility_id INTEGER NOT NULL,\n" +
                     "    blood_type TEXT NOT NULL,\n" +
                     "    component_type TEXT NOT NULL,\n" +
                     "    quantity REAL NOT NULL,\n" +
                     "    expiry_date TEXT,\n" +
                     "    FOREIGN KEY (facility_id) REFERENCES facilities(id)\n" +
                     ");"; 
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("inventory table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Adds name and area columns to the users table
     */
    public static void addColumnsToUsersTable() {
        String[] sqls = {
            "ALTER TABLE users ADD COLUMN name TEXT;",
            "ALTER TABLE users ADD COLUMN area TEXT;"
        };
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                try {
                    stmt.execute(sql);
                    System.out.println("Column added to users table.");
                } catch (SQLException e) {
                    // Column might already exist
                    if (!e.getMessage().contains("duplicate column name")) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Adds status column to the donations table
     */
    public static void addColumnsToDonationsTable() {
        String sql = "ALTER TABLE donations ADD COLUMN status TEXT NOT NULL DEFAULT 'APPROVED';"; 
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            try {
                stmt.execute(sql);
                System.out.println("Status column added to donations table.");
            } catch (SQLException e) {
                // Column might already exist
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Adds additional columns to the requests table
     */
    public static void addColumnsToRequestsTable() {
        String[] sqls = {
            "ALTER TABLE requests ADD COLUMN patient_condition TEXT;",
            "ALTER TABLE requests ADD COLUMN time TEXT;",
            "ALTER TABLE requests ADD COLUMN contact TEXT;",
            "ALTER TABLE requests ADD COLUMN created_by TEXT;",
            "ALTER TABLE requests ADD COLUMN date TEXT;"
        };
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                try {
                    stmt.execute(sql);
                    System.out.println("Column added to requests table.");
                } catch (SQLException e) {
                    // Column might already exist
                    if (!e.getMessage().contains("duplicate column name")) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Creates indexes for improved query performance
     */
    public static void createIndexes() {
        String[] sqls = {
            // Indexes for foreign keys
            "CREATE INDEX IF NOT EXISTS idx_donors_user_id ON donors(user_id);",
            "CREATE INDEX IF NOT EXISTS idx_facilities_user_id ON facilities(user_id);",
            "CREATE INDEX IF NOT EXISTS idx_donations_donor_id ON donations(donor_id);",
            "CREATE INDEX IF NOT EXISTS idx_requests_facility_id ON requests(facility_id);",
            "CREATE INDEX IF NOT EXISTS idx_donation_requests_donation_id ON donation_requests(donation_id);",
            "CREATE INDEX IF NOT EXISTS idx_donation_requests_request_id ON donation_requests(request_id);",
            "CREATE INDEX IF NOT EXISTS idx_inventory_facility_id ON inventory(facility_id);",
            
            // Indexes for common search fields
            "CREATE INDEX IF NOT EXISTS idx_donations_blood_type ON donations(blood_type);",
            "CREATE INDEX IF NOT EXISTS idx_requests_blood_type ON requests(blood_type);",
            "CREATE INDEX IF NOT EXISTS idx_requests_status ON requests(status);",
            "CREATE INDEX IF NOT EXISTS idx_inventory_blood_type ON inventory(blood_type);"
        };
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                try {
                    stmt.execute(sql);
                    System.out.println("Index created successfully.");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // ==================== USER REGISTRATION AND AUTHENTICATION ====================
    
    /**
     * Registers a new user (donor) in the database
     * @param username The username for the account
     * @param password The password for the account
     * @param fullName The full name of the user
     * @param email The email address
     * @param phone The phone number
     * @param address The address
     * @param bloodType The blood type
     * @param dateOfBirth The date of birth
     * @param area The area/location
     * @return true if registration successful, false otherwise
     */
    public static boolean registerUser(String username, String password, String fullName, 
                                     String email, String phone, String address, 
                                     String bloodType, String dateOfBirth, String area) {
        String insertUserSql = "INSERT INTO users (username, password, role, name, area) VALUES (?, ?, ?, ?, ?)";
        String insertDonorSql = "INSERT INTO donors (user_id, blood_group, last_donation_date) VALUES (?, ?, ?)";
        
        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // Start transaction
            
            // Insert into users table
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, password); // In production, hash the password
                userStmt.setString(3, "DONOR");
                userStmt.setString(4, fullName);
                userStmt.setString(5, area);
                
                int affectedRows = userStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        
                        // Insert into donors table
                        try (PreparedStatement donorStmt = conn.prepareStatement(insertDonorSql)) {
                            donorStmt.setInt(1, userId);
                            donorStmt.setString(2, bloodType);
                            donorStmt.setString(3, null); // No previous donation
                            
                            donorStmt.executeUpdate();
                            conn.commit(); // Commit transaction
                            System.out.println("User registered successfully: " + username);
                            return true;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registers a new facility in the database
     * @param username The username for the facility account
     * @param password The password for the account
     * @param facilityName The name of the facility
     * @param location The location of the facility
     * @param area The area/region
     * @return true if registration successful, false otherwise
     */
    public static boolean registerFacility(String username, String password, String facilityName, 
                                         String location, String area) {
        String insertUserSql = "INSERT INTO users (username, password, role, name, area) VALUES (?, ?, ?, ?, ?)";
        String insertFacilitySql = "INSERT INTO facilities (user_id, name, location) VALUES (?, ?, ?)";
        
        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // Start transaction
            
            // Insert into users table
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, password); // In production, hash the password
                userStmt.setString(3, "FACILITY");
                userStmt.setString(4, facilityName);
                userStmt.setString(5, area);
                
                int affectedRows = userStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
                
                // Get the generated user ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        
                        // Insert into facilities table
                        try (PreparedStatement facilityStmt = conn.prepareStatement(insertFacilitySql)) {
                            facilityStmt.setInt(1, userId);
                            facilityStmt.setString(2, facilityName);
                            facilityStmt.setString(3, location);
                            
                            facilityStmt.executeUpdate();
                            conn.commit(); // Commit transaction
                            System.out.println("Facility registered successfully: " + facilityName);
                            return true;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Facility registration failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticates a user login
     * @param username The username
     * @param password The password
     * @return ConcreteUser object if authentication successful, null otherwise
     */
    public static ConcreteUser authenticateUser(String username, String password) {
        String sql = "SELECT u.id, u.username, u.password, u.role, u.name, u.area " +
                     "FROM users u WHERE u.username = ? AND u.password = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // In production, compare hashed passwords
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ConcreteUser user = new ConcreteUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("area"),
                        rs.getString("role")
                    );
                    
                    System.out.println("Authentication successful for user: " + username);
                    return user;
                } else {
                    System.out.println("Authentication failed for user: " + username);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets user role by username
     * @param username The username
     * @return The user role (DONOR, FACILITY) or null if not found
     */
    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user role: " + e.getMessage());
        }
        return null;
    }
    
    // ==================== BLOOD REQUEST OPERATIONS ====================
    
    /**
     * Creates a new blood request
     * @param request The Request object containing request details
     * @param facilityUsername The username of the facility making the request
     * @return true if request created successfully, false otherwise
     */
    public static boolean createBloodRequest(Request request, String facilityUsername) {
        // First get the facility ID from username
        String getFacilityIdSql = "SELECT f.id FROM facilities f " +
                                  "JOIN users u ON f.user_id = u.id " +
                                  "WHERE u.username = ?";
        
        String insertRequestSql = "INSERT INTO requests (facility_id, blood_type, quantity, status, " +
                                 "patient_condition, time, contact, created_by, date) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connect()) {
            // Get facility ID
            int facilityId = -1;
            try (PreparedStatement getFacilityStmt = conn.prepareStatement(getFacilityIdSql)) {
                getFacilityStmt.setString(1, facilityUsername);
                try (ResultSet rs = getFacilityStmt.executeQuery()) {
                    if (rs.next()) {
                        facilityId = rs.getInt("id");
                    } else {
                        System.out.println("Facility not found for username: " + facilityUsername);
                        return false;
                    }
                }
            }
            
            // Insert the request
            try (PreparedStatement insertStmt = conn.prepareStatement(insertRequestSql)) {
                insertStmt.setInt(1, facilityId);
                insertStmt.setString(2, request.getBloodType());
                insertStmt.setDouble(3, request.getUnits());
                insertStmt.setString(4, request.getStatus());
                insertStmt.setString(5, request.getPatientCondition());
                insertStmt.setString(6, request.getTime());
                insertStmt.setString(7, request.getContact());
                insertStmt.setString(8, request.getCreatedBy());
                insertStmt.setString(9, request.getDate());
                
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Blood request created successfully");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating blood request: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets all pending blood requests
     * @return List of Request objects
     */
    public static java.util.List<Request> getPendingBloodRequests() {
        java.util.List<Request> requests = new java.util.ArrayList<>();
        String sql = "SELECT r.*, f.name as facility_name, f.location " +
                     "FROM requests r " +
                     "JOIN facilities f ON r.facility_id = f.id " +
                     "WHERE r.status = 'Pending' " +
                     "ORDER BY r.date DESC";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Request request = new Request(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("blood_type"),
                    rs.getInt("quantity"),
                    rs.getString("facility_name"),
                    rs.getString("location"),
                    rs.getString("status"),
                    rs.getString("date"),
                    rs.getString("patient_condition"),
                    rs.getString("time"),
                    rs.getString("contact"),
                    rs.getString("created_by")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            System.out.println("Error getting pending blood requests: " + e.getMessage());
        }
        return requests;
    }
    
    // ==================== DONATION OPERATIONS ====================
    
    /**
     * Creates a new donation record
     * @param donation The Donation object
     * @param donorUsername The username of the donor
     * @return true if donation created successfully, false otherwise
     */
    public static boolean createDonation(Donation donation, String donorUsername) {
        // First get the donor ID from username
        String getDonorIdSql = "SELECT d.id FROM donors d " +
                               "JOIN users u ON d.user_id = u.id " +
                               "WHERE u.username = ?";
        
        String insertDonationSql = "INSERT INTO donations (donor_id, date, blood_type, quantity, status) " +
                                  "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connect()) {
            // Get donor ID
            int donorId = -1;
            try (PreparedStatement getDonorStmt = conn.prepareStatement(getDonorIdSql)) {
                getDonorStmt.setString(1, donorUsername);
                try (ResultSet rs = getDonorStmt.executeQuery()) {
                    if (rs.next()) {
                        donorId = rs.getInt("id");
                    } else {
                        System.out.println("Donor not found for username: " + donorUsername);
                        return false;
                    }
                }
            }
            
            // Insert the donation
            try (PreparedStatement insertStmt = conn.prepareStatement(insertDonationSql)) {
                insertStmt.setInt(1, donorId);
                insertStmt.setString(2, donation.getDate().toString());
                insertStmt.setString(3, donation.getBloodType());
                insertStmt.setDouble(4, donation.getUnits());
                insertStmt.setString(5, donation.getStatus().toString());
                
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Donation created successfully");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating donation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates donation status
     * @param donationId The donation ID
     * @param status The new status
     * @return true if update successful, false otherwise
     */
    public static boolean updateDonationStatus(int donationId, String status) {
        String sql = "UPDATE donations SET status = ? WHERE id = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, donationId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Donation status updated successfully");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error updating donation status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets donation history for a donor
     * @param donorUsername The donor's username
     * @return List of Donation objects
     */
    public static java.util.List<Donation> getDonationHistory(String donorUsername) {
        java.util.List<Donation> donations = new java.util.ArrayList<>();
        String sql = "SELECT don.* FROM donations don " +
                     "JOIN donors d ON don.donor_id = d.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE u.username = ? " +
                     "ORDER BY don.date DESC";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, donorUsername);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create donation object from result set
                    // Note: You'll need to implement proper Donation constructor
                    System.out.println("Found donation: " + rs.getString("blood_type") + 
                                     " on " + rs.getString("date"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting donation history: " + e.getMessage());
        }
        return donations;
    }
    
    /**
     * Checks if username already exists
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
}