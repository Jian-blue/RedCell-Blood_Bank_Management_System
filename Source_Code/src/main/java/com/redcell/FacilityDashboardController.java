package com.redcell;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

public class FacilityDashboardController {

    public static boolean isLoggedIn = false;
    private Facility currentFacility;

    @FXML private Text facilityNameText;
    @FXML private Text dateText;
    @FXML private Text componentsText;
    @FXML private GridPane componentsGrid;
    @FXML private CheckBox wholeBloodCheck;
    @FXML private CheckBox rccPrbcCheck;
    @FXML private CheckBox sdpCheck;
    @FXML private CheckBox ffpCheck;
    @FXML private Button updateComponentButton;
    
    @FXML private Text totalRequestsText;
    @FXML private Text todaysRequestsText;
    @FXML private Text donationCompletionText;
    @FXML private Text todaysCompletionText;
    
    @FXML private GridPane inventoryGrid;
    @FXML private Button updateStatusButton;
    
    @FXML private VBox bloodRequestsContainer;
    @FXML private VBox donationStatusContainer;
    
    @FXML private TextField requestIdField;
    @FXML private Button deleteButton;
    
    @FXML private Button flagDonorButton;
    @FXML private Button updateInfoButton;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    @FXML
    public void initialize() {
        // Create a sample facility for demonstration
        currentFacility = new Facility("ABC Hospital", "Downtown");
        
        // Initialize the dashboard with facility data
        updateDashboard();
        
        // Set up event handlers
        if (updateComponentButton != null) {
            updateComponentButton.setOnAction(e -> handleUpdateComponents());
        }
        
        if (updateStatusButton != null) {
            updateStatusButton.setOnAction(e -> handleUpdateStatus());
        }
        
        if (deleteButton != null) {
            deleteButton.setOnAction(e -> handleDeleteRequest());
        }
        
        if (flagDonorButton != null) {
            flagDonorButton.setOnAction(e -> handleFlagDonor());
        }
        
        if (updateInfoButton != null) {
            updateInfoButton.setOnAction(e -> handleUpdateInfo());
        }
    }
    
    private void updateDashboard() {
        // Update facility information
        if (facilityNameText != null) {
            facilityNameText.setText(currentFacility.getName());
        }
        
        if (dateText != null) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
            dateText.setText("Today is " + today.format(formatter));
        }
        
        // Update component availability checkboxes
        updateComponentCheckboxes();
        
        // Update statistics
        if (totalRequestsText != null) {
            totalRequestsText.setText(String.valueOf(currentFacility.getTotalRequestCount()));
        }
        
        if (todaysRequestsText != null) {
            todaysRequestsText.setText(String.valueOf(currentFacility.getTodaysRequestCount()));
        }
        
        if (donationCompletionText != null) {
            donationCompletionText.setText(String.valueOf(currentFacility.getTotalDonationCount()));
        }
        
        if (todaysCompletionText != null) {
            todaysCompletionText.setText(String.valueOf(currentFacility.getTodayDonationCount()));
        }
        
        // Update inventory grid
        updateInventoryGrid();
    }
    
    private void updateComponentCheckboxes() {
        if (wholeBloodCheck != null && rccPrbcCheck != null && sdpCheck != null && ffpCheck != null) {
            List<Component> availableComponents = currentFacility.getComponents();
            // For demonstration, let's set some components as available
            if (availableComponents.isEmpty()) {
                availableComponents.add(Component.WHOLE_BLOOD);
                availableComponents.add(Component.RCC_PRBC);
                availableComponents.add(Component.SDP);
                currentFacility.setComponents(availableComponents);
            }
            wholeBloodCheck.setSelected(availableComponents.contains(Component.WHOLE_BLOOD));
            rccPrbcCheck.setSelected(availableComponents.contains(Component.RCC_PRBC));
            sdpCheck.setSelected(availableComponents.contains(Component.SDP));
            ffpCheck.setSelected(availableComponents.contains(Component.FFP));
        }
    }
    
    private void updateInventoryGrid() {
        if (inventoryGrid != null) {
            Map<String, Integer> inventory = currentFacility.getInventory();
            
            // Clear existing content
            inventoryGrid.getChildren().clear();
            
            // Add blood type inventory data
            String[] bloodTypes = {"A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-"};
            int row = 0;
            int col = 0;
            
            for (String type : bloodTypes) {
                Text bloodTypeText = new Text(type + ": " + inventory.get(type));
                inventoryGrid.add(bloodTypeText, col, row);
                
                col++;
                if (col > 3) {
                    col = 0;
                    row++;
                }
            }
        }
    }
    
    @FXML
    private void handleUpdateComponents() {
        List<Component> selectedComponents = Arrays.asList(
            wholeBloodCheck.isSelected() ? Component.WHOLE_BLOOD : null,
            rccPrbcCheck.isSelected() ? Component.RCC_PRBC : null,
            sdpCheck.isSelected() ? Component.SDP : null,
            ffpCheck.isSelected() ? Component.FFP : null
        ).stream().filter(c -> c != null).toList();
        
        currentFacility.setComponents(selectedComponents);
        
        showAlert("Components Updated", "Available blood components have been updated successfully.");
    }
    
    @FXML
    private void handleUpdateStatus() {
        // In a real application, this would open a dialog to update inventory
        // For now, just show a confirmation message
        showAlert("Inventory Updated", "Blood inventory status has been updated successfully.");
    }
    
    @FXML
    private void handleDeleteRequest() {
        String requestId = requestIdField.getText().trim();
        if (requestId.isEmpty()) {
            showAlert("Error", "Please enter a valid Request ID.", Alert.AlertType.ERROR);
            return;
        }
        
        // In a real application, this would delete the request from the database
        // For now, just show a confirmation message
        showAlert("Request Deleted", "Request " + requestId + " has been deleted successfully.");
        requestIdField.clear();
    }
    
    @FXML
    private void handleFlagDonor() {
        // In a real application, this would open a dialog to flag a donor
        // For now, just show a confirmation message
        showAlert("Donor Flagged", "The donor has been flagged for review.");
    }
    
    @FXML
    private void handleUpdateInfo() {
        // In a real application, this would open a dialog to update facility information
        // For now, just show a confirmation message
        showAlert("Information Updated", "Facility information has been updated successfully.");
    }
    
    @FXML
    public void handleLogout() {
        isLoggedIn = false;
        // In a real application, this would redirect to the login page
        showAlert("Logged Out", "You have been logged out successfully.");
    }
    
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Add custom styling to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        
        alert.showAndWait();
    }
}