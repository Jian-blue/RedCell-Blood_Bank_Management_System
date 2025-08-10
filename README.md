# RedCell Blood Donation Management System

RedCell is a comprehensive blood donation management system designed to streamline the process of blood donation, request, and inventory management. It connects donors, facilities, and administrators to ensure efficient and timely blood supply.

## Features

*   **User Authentication:** Secure login and registration for donors, facilities, and administrators.
*   **Donor Management:**
    *   Donor registration and profile management.
    *   Tracking of donation history and eligibility.
    *   Badges and recognition for frequent donors.
*   **Facility Management:**
    *   Facility registration and profile management.
    *   Ability to request blood based on patient needs.
    *   Inventory management of blood components.
*   **Blood Request System:**
    *   Facilities can submit blood requests with details like blood type, units, and patient condition.
    *   Donors can view and respond to nearby blood requests.
*   **Donation Tracking:**
    *   Record and track individual blood donations.
    *   Update donation status (approved, rejected, completed).
*   **Inventory Management:**
    *   Facilities can manage their blood component inventory.
    *   Real-time updates on available blood units.
*   **Dashboard:** Personalized dashboards for donors and facilities to view relevant information and actions.
*   **Database Integration:** Uses SQLite for local data storage.

## Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 21:** [Download JDK](https://www.oracle.com/java/technologies/downloads/)
*   **Maven:** [Download Maven](https://maven.apache.org/download.cgi)
*   **SQLite Browser (Optional):** For viewing and managing the `redcell.db` database.

## Installation

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/RedCell.git
    cd RedCell
    ```

2.  **Build the project using Maven:**

    ```bash
    mvn clean install
    ```

    This command will compile the project and download all necessary dependencies.

## Running the Application

You can run the application using one of the following methods:

### Method 1: Using `run.bat` (Windows)

For Windows users, a convenience script `run.bat` is provided to start the application.

1.  Navigate to the project root directory in your command prompt.
2.  Run the batch file:

    ```bash
    run.bat
    ```

### Method 2: Using Maven

1.  Navigate to the project root directory in your terminal.
2.  Execute the application using Maven:

    ```bash
    mvn javafx:run
    ```

    This will start the JavaFX application.

## Project Structure

```
RedCell/
├── pom.xml
├── README.md
├── run.bat
└── src/
    └── main/
        ├── java/                 # Java source code
        │   └── com/redcell/
        │       ├── controllers/  # FXML controllers
        │       ├── models/       # Data models and business logic
        │       └── RedCellApp.java # Main application entry point
        └── resources/            # FXML, CSS, images, and database
            ├── dashboard.fxml
            ├── db_update_script.sql
            ├── dbplan.md
            ├── img/              # Application images
            ├── redcell.db        # SQLite database file
            ├── styles.css
            └── views/            # FXML view files
```

## Database Schema

The `redcell.db` SQLite database contains the following tables:

*   **`users`**: Stores general user information (ID, username, password, role).
*   **`donors`**: Extends `users` with donor-specific details (blood type, last donation date, status, total donations).
*   **`facilities`**: Extends `users` with facility-specific details (request count, donation count, inventory).
*   **`requests`**: Stores blood request details (request ID, patient condition, blood type, units, date, time, facility info, contact, status, created by).
*   **`donations`**: Stores donation records (donation ID, request ID, blood type, units, location, date, status, donor ID).
*   **`inventory`**: Manages blood component inventory for facilities.
*   **`donation_requests`**: Links donations to specific requests.

## Contributing

We welcome contributions! Please follow these steps:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature-name`).
3.  Make your changes.
4.  Commit your changes (`git commit -m 'Add new feature'`).
5.  Push to the branch (`git push origin feature/your-feature-name`).
6.  Create a Pull Request.

## License

This project is licensed under License - see the LICENSE file for details.

## Contact

For any inquiries, please contact me.
