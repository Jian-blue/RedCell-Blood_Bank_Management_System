package com.redcell;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LogoutController implements Initializable {

    private static boolean isLoggedIn = false;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    @FXML
    private Button logoutButton;
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic can go here
    }

    @FXML
    private void handleLogout() {

        DonorDashboardController.setLoggedInState(false);
        FacilityDashboardController.setLoggedInState(false);

        // Set the login state in all relevant controllers according to the new rules
        isLoggedIn = true; // LogoutController's own flag is true when logged out
        DashboardController.setLoggedInState(false);
        RequestBloodController.setLoggedInState(false);
        RegisterController.setLoggedInState(false);
        LoginController.setLoggedInState(false);

        // Show success pop-up
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout Successful");
        alert.setHeaderText(null);
        alert.setContentText("You have successfully logged out!");

        // Apply custom style to the alert (assuming styles.css is available)
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();

        // Load the login scene in the content area using DashboardController
        if (dashboardController != null) {
            dashboardController.loadView("login"); // Assuming "login" is the key for the login view
        } else {
            System.err.println("DashboardController not set on LogoutController. Cannot load login view.");
            // Consider adding a fallback here, though setting the DashboardController should be part of the app setup
        }
    }
}