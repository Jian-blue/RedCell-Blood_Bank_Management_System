package com.redcell;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;


public class RegisterController {

    private static boolean isLoggedIn = false;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }


    @FXML private TextField fullName;
    @FXML private DatePicker dateOfBirth;
    @FXML private ComboBox<String> bloodType;
    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextField address;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button registerButton;
    @FXML private Text errorMessage;

    @FXML
    private void initialize() {
        // Initialize blood type options
        bloodType.getItems().addAll(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        );

        // Add listeners to clear error message when user starts typing
        fullName.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        email.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        phone.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        address.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        username.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        password.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        dateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        bloodType.valueProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
    }

    @FXML
    private void handleRegister() {
        // Validate all fields
        if (!validateFields()) {
            return;
        }

        try {
            // Check if username already exists
            if (DbHelper.usernameExists(username.getText().trim())) {
                showErrorMessage("Username already exists. Please choose a different username.");
                return;
            }
            
            // Register the user in the database
            boolean registrationSuccess = DbHelper.registerUser(
                username.getText().trim(),
                password.getText(), // In production, hash the password
                fullName.getText().trim(),
                email.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                bloodType.getValue(),
                dateOfBirth.getValue().toString(),
                "" // area - can be extracted from address or left empty for now
            );
            
            if (registrationSuccess) {
                showErrorMessage("Registration successful! You can now log in.");
                // Clear all fields after successful registration
                clearFields();
            } else {
                showErrorMessage("Registration failed. Please try again.");
            }

        } catch (Exception e) {
            showErrorMessage("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        // Check if any field is empty
        if (fullName.getText().trim().isEmpty() ||
            email.getText().trim().isEmpty() ||
            phone.getText().trim().isEmpty() ||
            address.getText().trim().isEmpty() ||
            username.getText().trim().isEmpty() ||
            password.getText().trim().isEmpty() ||
            dateOfBirth.getValue() == null ||
            bloodType.getValue() == null) {
            
            showErrorMessage("Please fill in all fields");
            return false;
        }

        // Validate full name (at least two words)
        String[] nameParts = fullName.getText().trim().split("\\s+");
        if (nameParts.length < 2) {
            showErrorMessage("Please enter your full name (first and last name)");
            return false;
        }

        // Validate email format
        if (!email.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showErrorMessage("Please enter a valid email address");
            return false;
        }

        // Validate phone number (simple validation for demonstration)
        if (!phone.getText().matches("\\d{10,}")) {
            showErrorMessage("Please enter a valid phone number");
            return false;
        }

        // Validate age (must be at least 18 years old)
        LocalDate today = LocalDate.now();
        LocalDate minimumAge = today.minusYears(18);
        if (dateOfBirth.getValue().isAfter(minimumAge)) {
            showErrorMessage("You must be at least 18 years old to register");
            return false;
        }

        return true;
    }

    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    private void hideErrorMessage() {
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
    }
    
    private void clearFields() {
        fullName.clear();
        email.clear();
        phone.clear();
        address.clear();
        username.clear();
        password.clear();
        dateOfBirth.setValue(null);
        bloodType.setValue(null);
    }

    @FXML
    private void handleFacilityRegistrationLink() {
        try {
            Desktop.getDesktop().browse(new URI("https://docs.google.com/forms/d/e/1FAIpQLScE0ok3RN6hr2KAvXxSVHubbxGcQUzE9VzlUQXZ2a22GqxgFw/viewform"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}