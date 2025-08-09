package com.redcell;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RequestBloodController {

    private static boolean isLoggedIn = false;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    @FXML
    private ComboBox<String> bloodType;
    
    @FXML
    private TextField units;
    
    @FXML
    private TextField facilityName;
    
    @FXML
    private TextField areaField;
    
    @FXML
    private TextField contactField;
    
    @FXML
    private TextArea patientCondition;
    
    @FXML
    private DatePicker requiredDate;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Text errorMessage;
    
    @FXML
    public void initialize() {
        // Initialize blood type options if not already set in FXML
        if (bloodType.getItems().isEmpty()) {
            bloodType.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        }
        
        // Set default date to tomorrow
        requiredDate.setValue(LocalDate.now().plusDays(1));
        
        // Set button action
        submitButton.setOnAction(e -> handleSubmit());
        
        // Hide error message initially
        errorMessage.setVisible(false);
        
        // Check login state
        submitButton.setDisable(!isLoggedIn);
        if (!isLoggedIn) {
            showError("Please log in to submit a blood request");
        }
    }
    
    private void handleSubmit() {
        // Clear previous error
        showError("");
        
        // Check login state
        if (!isLoggedIn) {
            showError("Please log in to submit a blood request");
            return;
        }
        
        // Validate all fields
        if (bloodType.getValue() == null || units.getText().isEmpty() ||
            facilityName.getText().isEmpty() || patientCondition.getText().isEmpty() ||
            requiredDate.getValue() == null || areaField.getText().isEmpty() ||
            contactField.getText().isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        try {
            int unitCount = Integer.parseInt(units.getText());
            if (unitCount <= 0) {
                showError("Please enter a valid number of units");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number of units");
            return;
        }
        
        // Validate contact number
        if (!contactField.getText().matches("\\d{10}")) {
            showError("Please enter a valid 10-digit contact number");
            return;
        }
        
        // Validate date (should not be in the past)
        if (requiredDate.getValue().isBefore(LocalDate.now())) {
            showError("Required date cannot be in the past");
            return;
        }
        
        // Create a new request
        String requestId = generateRequestId();
        String formattedDate = requiredDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        Request newRequest = new Request(
            requestId,
            bloodType.getValue(),
            Integer.parseInt(units.getText()),
            facilityName.getText(),
            areaField.getText(),
            "Pending",
            formattedDate,
            patientCondition.getText(),
            currentTime,
            contactField.getText(),
            "current_user" // This would be replaced with actual logged-in user
        );
        
        // TODO: Save the request to database or storage
        
        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Request Submitted");
        alert.setHeaderText(null);
        alert.setContentText("Your blood request has been submitted successfully!\nRequest ID: " + requestId);
        
        // Apply custom style to the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        
        alert.showAndWait();
        
        // Clear form fields
        clearForm();
    }
    
    private String generateRequestId() {
        // Generate a unique request ID (simplified version)
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void clearForm() {
        bloodType.setValue(null);
        units.clear();
        facilityName.clear();
        areaField.clear();
        patientCondition.clear();
        requiredDate.setValue(LocalDate.now().plusDays(1));
        contactField.clear();
        errorMessage.setVisible(false);
    }
    
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(!message.isEmpty());
    }
}