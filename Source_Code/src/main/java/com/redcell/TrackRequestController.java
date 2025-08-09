package com.redcell;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.control.ProgressIndicator;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class TrackRequestController {
    @FXML
    private TextField requestId;
    
    @FXML
    private Button trackButton;
    
    @FXML
    private VBox requestDetails;
    
    @FXML
    private Text status;
    
    @FXML
    private Text bloodType;
    
    @FXML
    private Text units;
    
    @FXML
    private Text facilityName;
    
    @FXML
    private Text requiredDate;
    
    @FXML
    private Text patientCondition;
    
    @FXML
    private Text time;
    
    @FXML
    private Text contact;
    
    @FXML
    private Text errorMessage;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private HBox statusContainer;
    
    @FXML
    public void initialize() {
        setupUI();
        setupEventHandlers();
    }

    private void setupUI() {
        requestId.setPromptText("Enter Request ID (e.g., REQ001)");
        errorMessage.setVisible(false);
        requestDetails.setVisible(false);
        loadingIndicator.setVisible(false);
        
        // Add input validation
        requestId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z0-9-]*")) {
                requestId.setText(newValue.replaceAll("[^A-Za-z0-9-]", ""));
            }
            trackButton.setDisable(newValue.trim().isEmpty());
        });
    }

    private void setupEventHandlers() {
        trackButton.setOnAction(e -> handleTrack());
        requestId.setOnAction(e -> handleTrack()); // Allow pressing Enter
    }
    
    private void handleTrack() {
        String id = requestId.getText().trim().toUpperCase();
        
        if (id.isEmpty()) {
            showError("Please enter a request ID");
            requestDetails.setVisible(false);
            return;
        }

        if (!id.matches("REQ\\d{3}")) {
            showError("Invalid request ID format. Example: REQ001");
            requestDetails.setVisible(false);
            return;
        }

        // Show loading state
        loadingIndicator.setVisible(true);
        trackButton.setDisable(true);
        requestDetails.setVisible(false);
        errorMessage.setVisible(false);

        // Simulate API call with delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    trackButton.setDisable(false);
                    
                    // TODO: Replace with actual API call
                    // TODO: Replace with actual API call that returns a Request object
                    if (id.equals("REQ001")) {
                        showError("");
                        // Create a dummy Request object for now
                        Request dummyRequest = new Request("REQ001", "A+", 2, "City Hospital", "Downtown", "Processing", "August 12, 2025", "Critical Condition", "08:00 AM", "01994567894(Rashed)", "System");
                        displayRequestDetails(
                            dummyRequest.getStatus(),
                            dummyRequest.getBloodType(),
                            String.valueOf(dummyRequest.getUnits()),
                            dummyRequest.getDate(),
                            dummyRequest
                        );
                    } else {
                        showError("Request not found: " + id);
                    }
                });
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    showError("An error occurred while tracking the request");
                    loadingIndicator.setVisible(false);
                    trackButton.setDisable(false);
                });
            }
        }).start();
    }
    
    private void displayRequestDetails(String statusText, String bloodTypeText,
                                     String unitsText, String dateText, Request request) {
        status.setText(statusText);
        bloodType.setText(bloodTypeText);
        units.setText(unitsText);
        facilityName.setText(request.getHospital());
        requiredDate.setText(dateText);
        patientCondition.setText(request.getPatientCondition());
        time.setText(request.getTime());
        contact.setText(request.getContact());
        requestDetails.setVisible(true);

        // Apply status-specific styling
        status.getStyleClass().clear();
        status.getStyleClass().add("detail-value");
        switch (statusText.toLowerCase()) {
            case "completed":
                status.setFill(Color.web("#28a745"));
                break;
            case "processing":
                status.setFill(Color.web("#ffc107"));
                break;
            case "pending":
                status.setFill(Color.web("#17a2b8"));
                break;
            case "cancelled":
                status.setFill(Color.web("#dc3545"));
                break;
            default:
                status.setFill(Color.web("#6c757d"));
        }
    }
    
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(!message.isEmpty());
    }
} 