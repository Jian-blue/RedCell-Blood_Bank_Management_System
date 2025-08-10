-- RedCell Database Update Script
-- This script implements the changes recommended in dbplan.md

-- 1. Create junction table for donations and requests
CREATE TABLE IF NOT EXISTS donation_requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    donation_id INTEGER NOT NULL,
    request_id INTEGER NOT NULL,
    quantity REAL NOT NULL,
    date TEXT NOT NULL,
    FOREIGN KEY (donation_id) REFERENCES donations(id),
    FOREIGN KEY (request_id) REFERENCES requests(id)
);

-- 2. Create inventory table
CREATE TABLE IF NOT EXISTS inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    facility_id INTEGER NOT NULL,
    blood_type TEXT NOT NULL,
    component_type TEXT NOT NULL,
    quantity REAL NOT NULL,
    expiry_date TEXT,
    FOREIGN KEY (facility_id) REFERENCES facilities(id)
);

-- 3. Add missing fields to users table
ALTER TABLE users ADD COLUMN name TEXT;
ALTER TABLE users ADD COLUMN area TEXT;

-- 4. Add status field to donations table
ALTER TABLE donations ADD COLUMN status TEXT NOT NULL DEFAULT 'APPROVED';

-- 5. Add missing fields to requests table
ALTER TABLE requests ADD COLUMN patient_condition TEXT;
ALTER TABLE requests ADD COLUMN time TEXT;
ALTER TABLE requests ADD COLUMN contact TEXT;
ALTER TABLE requests ADD COLUMN created_by TEXT;
ALTER TABLE requests ADD COLUMN date TEXT;

-- 6. Create indexes for performance

-- Indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_donors_user_id ON donors(user_id);
CREATE INDEX IF NOT EXISTS idx_facilities_user_id ON facilities(user_id);
CREATE INDEX IF NOT EXISTS idx_donations_donor_id ON donations(donor_id);
CREATE INDEX IF NOT EXISTS idx_requests_facility_id ON requests(facility_id);
CREATE INDEX IF NOT EXISTS idx_donation_requests_donation_id ON donation_requests(donation_id);
CREATE INDEX IF NOT EXISTS idx_donation_requests_request_id ON donation_requests(request_id);
CREATE INDEX IF NOT EXISTS idx_inventory_facility_id ON inventory(facility_id);

-- Indexes for common search fields
CREATE INDEX IF NOT EXISTS idx_donations_blood_type ON donations(blood_type);
CREATE INDEX IF NOT EXISTS idx_requests_blood_type ON requests(blood_type);
CREATE INDEX IF NOT EXISTS idx_requests_status ON requests(status);
CREATE INDEX IF NOT EXISTS idx_inventory_blood_type ON inventory(blood_type);