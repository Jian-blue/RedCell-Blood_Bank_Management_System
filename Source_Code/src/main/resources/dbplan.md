# RedCell Database Schema Analysis and Recommendations

## Implementation Status

All recommended changes have been implemented in the codebase:

1. The DbHelper.java file has been updated with methods to create new tables and alter existing ones.
2. The RedCellApp.java file has been updated to call the updateSchema method during application startup.
3. A SQL script (db_update_script.sql) has been created with all the necessary SQL statements for reference.

The changes will be applied automatically when the application starts.

## Current Schema Analysis

Based on the examination of the existing SQLite database schema and Java classes, here is the current database structure:

### Tables and Relationships

1. **users**
   - Primary key: `id` (INTEGER)
   - Fields: `username` (TEXT), `password` (TEXT), `role` (TEXT)
   - Relationships: One-to-One with either donors or facilities

2. **donors**
   - Primary key: `id` (INTEGER)
   - Foreign key: `user_id` references users(id)
   - Fields: `blood_group` (TEXT), `last_donation_date` (TEXT)
   - Relationships: One-to-Many with donations

3. **facilities**
   - Primary key: `id` (INTEGER)
   - Foreign key: `user_id` references users(id)
   - Fields: `name` (TEXT), `location` (TEXT)
   - Relationships: One-to-Many with requests

4. **donations**
   - Primary key: `id` (INTEGER)
   - Foreign key: `donor_id` references donors(id)
   - Fields: `date` (TEXT), `blood_type` (TEXT), `quantity` (REAL)
   - Relationships: Many-to-One with donors

5. **requests**
   - Primary key: `id` (INTEGER)
   - Foreign key: `facility_id` references facilities(id)
   - Fields: `blood_type` (TEXT), `quantity` (REAL), `status` (TEXT)
   - Relationships: Many-to-One with facilities

## Entity-Relationship Diagram (ASCII)

```
+--------+       +--------+       +-----------+
|        |       |        |       |           |
|  USERS +-------+ DONORS +-------+ DONATIONS |
|        |  1:1  |        |  1:N  |           |
+--------+       +--------+       +-----------+
    |
    | 1:1
    |
    v
+------------+     +-----------+
|            |     |           |
| FACILITIES +-----+ REQUESTS  |
|            | 1:N |           |
+------------+     +-----------+
    |
    | 1:N
    |
    v
+------------+
|            |
| COMPONENTS |
| (INVENTORY)|
+------------+
```

## Identified Issues and Recommendations

### 1. Missing Many-to-Many Relationships

**Issue**: The current schema doesn't properly handle the relationship between donations and requests. In a blood bank system, a donation might fulfill multiple requests, and a request might be fulfilled by multiple donations.

**Recommendation**: Create a junction table `donation_requests` to handle this Many-to-Many relationship:

```sql
CREATE TABLE IF NOT EXISTS donation_requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    donation_id INTEGER NOT NULL,
    request_id INTEGER NOT NULL,
    quantity REAL NOT NULL,
    date TEXT NOT NULL,
    FOREIGN KEY (donation_id) REFERENCES donations(id),
    FOREIGN KEY (request_id) REFERENCES requests(id)
);
```

### 2. Inventory Management

**Issue**: The current schema doesn't properly track blood inventory. The `Component` enum exists in the Java code, but there's no corresponding table in the database.

**Recommendation**: Create an `inventory` table to track blood components by type and facility:

```sql
CREATE TABLE IF NOT EXISTS inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    facility_id INTEGER NOT NULL,
    blood_type TEXT NOT NULL,
    component_type TEXT NOT NULL,
    quantity REAL NOT NULL,
    expiry_date TEXT,
    FOREIGN KEY (facility_id) REFERENCES facilities(id)
);
```

### 3. Inconsistent Data Types

**Issue**: Dates are stored as TEXT, which makes date calculations and filtering difficult.

**Recommendation**: Use ISO8601 strings (YYYY-MM-DD) consistently for all date fields to ensure proper date handling:

```sql
-- Example of updating a date field format
ALTER TABLE donors RENAME COLUMN last_donation_date TO last_donation_date_old;
ALTER TABLE donors ADD COLUMN last_donation_date TEXT;
UPDATE donors SET last_donation_date = strftime('%Y-%m-%d', last_donation_date_old);
```

### 4. Missing User Profile Information

**Issue**: The User class has name and area fields, but these aren't reflected in the database schema.

**Recommendation**: Update the users table to include these fields:

```sql
ALTER TABLE users ADD COLUMN name TEXT;
ALTER TABLE users ADD COLUMN area TEXT;
```

### 5. Donation Status Tracking

**Issue**: The `DonationStatus` enum exists in the Java code, but the status field isn't in the donations table.

**Recommendation**: Add a status field to the donations table:

```sql
ALTER TABLE donations ADD COLUMN status TEXT NOT NULL DEFAULT 'APPROVED';
```

### 6. Request Additional Fields

**Issue**: The Request class has fields like patientCondition, time, contact, and createdBy that aren't in the database schema.

**Recommendation**: Add these fields to the requests table:

```sql
ALTER TABLE requests ADD COLUMN patient_condition TEXT;
ALTER TABLE requests ADD COLUMN time TEXT;
ALTER TABLE requests ADD COLUMN contact TEXT;
ALTER TABLE requests ADD COLUMN created_by TEXT;
ALTER TABLE requests ADD COLUMN date TEXT;
```

## Complete Table Relationships

### users
- One-to-One with donors (via user_id in donors)
- One-to-One with facilities (via user_id in facilities)

### donors
- One-to-One with users (via user_id)
- One-to-Many with donations (via donor_id in donations)

### facilities
- One-to-One with users (via user_id)
- One-to-Many with requests (via facility_id in requests)
- One-to-Many with inventory (via facility_id in inventory)

### donations
- Many-to-One with donors (via donor_id)
- Many-to-Many with requests (via donation_requests junction table)

### requests
- Many-to-One with facilities (via facility_id)
- Many-to-Many with donations (via donation_requests junction table)

### donation_requests (new junction table)
- Many-to-One with donations (via donation_id)
- Many-to-One with requests (via request_id)

### inventory (new table)
- Many-to-One with facilities (via facility_id)

## SQL Changes Required

```sql
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
```

## Indexes for Performance

To improve query performance, consider adding the following indexes:

```sql
-- Index for foreign keys
CREATE INDEX idx_donors_user_id ON donors(user_id);
CREATE INDEX idx_facilities_user_id ON facilities(user_id);
CREATE INDEX idx_donations_donor_id ON donations(donor_id);
CREATE INDEX idx_requests_facility_id ON requests(facility_id);
CREATE INDEX idx_donation_requests_donation_id ON donation_requests(donation_id);
CREATE INDEX idx_donation_requests_request_id ON donation_requests(request_id);
CREATE INDEX idx_inventory_facility_id ON inventory(facility_id);

-- Index for common search fields
CREATE INDEX idx_donations_blood_type ON donations(blood_type);
CREATE INDEX idx_requests_blood_type ON requests(blood_type);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_inventory_blood_type ON inventory(blood_type);
```

## Future Considerations

1. **Data Validation**: Implement constraints to ensure data integrity (e.g., CHECK constraints for blood types, status values).
2. **Audit Trail**: Consider adding timestamp fields (created_at, updated_at) to track when records are modified.
3. **User Authentication**: Enhance the users table with fields for email verification, password reset tokens, etc.
4. **Normalization**: Consider normalizing blood types and component types into separate lookup tables.
5. **Transactions**: Implement transaction support for critical operations like donation processing and inventory updates.