package com.redcell;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginController {

    private static boolean isLoggedIn = false;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Text errorMessage;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        // Add listeners to clear error message when user starts typing
        username.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
        password.textProperty().addListener((observable, oldValue, newValue) -> hideErrorMessage());
    }
    
    @FXML
    private void handleLogin() {
        String user = username.getText().trim();
        String pass = password.getText().trim();
        
        // Validate input fields
        if (user.isEmpty() || pass.isEmpty()) {
            showErrorMessage("Please fill in all fields");
            return;
        }
        
        try {
            // Authenticate user using database
            ConcreteUser authenticatedUser = DbHelper.authenticateUser(user, pass);
            
            if (authenticatedUser == null) {
                showErrorMessage("Invalid username or password");
                return;
            }
            
            // Authentication successful - set login state
            isLoggedIn = true;
            DashboardController.setLoggedInState(true);
            if (dashboardController != null) {
                dashboardController.setLoggedInUsername(user);
            }
            
            // Determine user type based on role from database
            String userRole = authenticatedUser.getRole();
            if ("donor".equalsIgnoreCase(userRole)) {
                DonorDashboardController.setLoggedInState(true);
                DonorDashboardController.setLoggedInUsername(user);
            } else if ("facility".equalsIgnoreCase(userRole)) {
                FacilityDashboardController.setLoggedInState(true);
            }

            RequestBloodController.setLoggedInState(true);
            RegisterController.setLoggedInState(true);
            LogoutController.setLoggedInState(false); // Set logout to false when logged in

            // Refresh notifications
            if (dashboardController != null) {
                dashboardController.refreshNotifications();
            }

            // Show success pop-up
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            alert.setContentText("Welcome " + authenticatedUser.getName() + "! You have successfully logged in.");

            // Apply custom style to the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();

            // Load and display the logout scene in the content area
            if (dashboardController != null) {
                dashboardController.loadView("logout");
            } else {
                System.err.println("DashboardController not set on LoginController.");
            }
            
        } catch (Exception e) {
            showErrorMessage("Login error: " + e.getMessage());
            e.printStackTrace();
        }

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
}