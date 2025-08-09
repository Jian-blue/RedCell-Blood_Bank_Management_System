package com.redcell;

import javafx.application.Platform;
import javafx.application.Platform;
import javafx.application.Platform;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
    
    private TableView<Donation> donationReviewTable;
    private TableView<Donation> donationStatusTable;
    
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
            totalRequestsText.setText(String.valueOf((int)(Math.random() * 100) + 1)); // Random number between 1 and 100
        }
        
        if (todaysRequestsText != null) {
            todaysRequestsText.setText(String.valueOf((int)(Math.random() * 20) + 1)); // Random number between 1 and 20
        }
        
        if (donationCompletionText != null) {
            donationCompletionText.setText(String.valueOf((int)(Math.random() * 100) + 1)); // Random number between 1 and 100
        }
        
        if (todaysCompletionText != null) {
            todaysCompletionText.setText(String.valueOf((int)(Math.random() * 20) + 1)); // Random number between 1 and 20
        }
        
        // Update inventory grid
        updateInventoryGrid();
        
        // Initialize the donation review table
        initializeDonationReviewTable();
        
        // Initialize the donation status table
        initializeDonationStatusTable();
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
    
    private void initializeDonationReviewTable() {
        // Clear the container first
        if (bloodRequestsContainer != null) {
            bloodRequestsContainer.getChildren().clear();
            
            // Add the section header (already in FXML)
            Text headerText = new Text("Blood Request Review & Approval");
            headerText.getStyleClass().add("section-header");
            bloodRequestsContainer.getChildren().add(headerText);
            
            // Create the table view
            donationReviewTable = new TableView<>();
            donationReviewTable.getStyleClass().add("recent-requests-table");
            
            // Initialize table columns
            TableColumn<Donation, String> requestIdCol = new TableColumn<>("Request ID");
            requestIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
            
            TableColumn<Donation, String> bloodTypeCol = new TableColumn<>("Blood Type");
            bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
            
            TableColumn<Donation, Integer> unitsCol = new TableColumn<>("Units");
            unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
            
            TableColumn<Donation, String> locationCol = new TableColumn<>("Hospital");
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            
            TableColumn<Donation, LocalDate> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            // Custom cell factory for date column to format the date
            dateCol.setCellFactory(column -> new TableCell<Donation, LocalDate>() {
                @Override
                protected void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    }
                }
            });
            
            TableColumn<Donation, String> selectCol = new TableColumn<>("Select");
            selectCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Custom cell factory for select column to add Select buttons
            selectCol.setCellFactory(column -> new TableCell<Donation, String>() {
                private final Button selectButton = new Button("Select");
                
                {
                    // Configure button style
                    selectButton.getStyleClass().add("donate-button");
                    selectButton.setMaxWidth(Double.MAX_VALUE);
                    
                    // Add action handler for the select button
                    selectButton.setOnAction(event -> {
                        Donation donation = getTableView().getItems().get(getIndex());
                        Platform.runLater(() -> showDonationReviewDialog(donation));
                    });
                }
                
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(selectButton);
                    }
                }
            });
            
            donationReviewTable.getColumns().clear();
            donationReviewTable.getColumns().addAll(requestIdCol, bloodTypeCol, unitsCol, locationCol, dateCol, selectCol);
            
            // Get donations from the facility
            List<Donation> donations = currentFacility.getBloodDonations();
            if (donations == null || donations.isEmpty()) {
                // Add sample data for demonstration
                donations = new ArrayList<>();
                donations.add(new Donation("REQ001", "A+", 2, "Daffodil Medical Institute", LocalDate.now(), DonationStatus.APPROVED.toString()));
                donations.add(new Donation("REQ002", "O-", 3, "General Hospital", LocalDate.now().minusDays(1), DonationStatus.APPROVED.toString()));
                currentFacility.setBloodDonations(donations);
            }
            
            ObservableList<Donation> donationsList = FXCollections.observableArrayList(donations);
            donationReviewTable.setItems(donationsList);
            
            // Configure table properties
            donationReviewTable.setFixedCellSize(45);
            donationReviewTable.prefHeightProperty().bind(donationReviewTable.fixedCellSizeProperty().multiply(javafx.beans.binding.Bindings.size(donationReviewTable.getItems()).add(1.01)));
            donationReviewTable.minHeightProperty().bind(donationReviewTable.prefHeightProperty());
            donationReviewTable.maxHeightProperty().bind(donationReviewTable.prefHeightProperty());
            
            // Set column widths (percentage-based)
            requestIdCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.15));
            bloodTypeCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.15));
            unitsCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.1));
            locationCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.244));
            dateCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.15));
            selectCol.prefWidthProperty().bind(donationReviewTable.widthProperty().multiply(0.2));
            
            // Add the table to the container
            bloodRequestsContainer.getChildren().add(donationReviewTable);
        }
    }
    
    private void showDonationReviewDialog(Donation donation) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Donation Review");
        
        // Create the content for the popup
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        content.getStyleClass().add("section-container");
        
        // Add donation information
        Text headerText = new Text("Donation Information");
        headerText.getStyleClass().add("section-header");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10));
        
        // Add donation details to the grid
        infoGrid.add(new Text("Request ID:"), 0, 0);
        infoGrid.add(new Text(donation.getRequestId()), 1, 0);
        
        infoGrid.add(new Text("Blood Type:"), 0, 1);
        infoGrid.add(new Text(donation.getBloodType()), 1, 1);
        
        infoGrid.add(new Text("Units:"), 0, 2);
        infoGrid.add(new Text(String.valueOf(donation.getUnits())), 1, 2);
        
        infoGrid.add(new Text("Hospital:"), 0, 3);
        infoGrid.add(new Text(donation.getLocation()), 1, 3);
        
        infoGrid.add(new Text("Date:"), 0, 4);
        infoGrid.add(new Text(donation.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 1, 4);
        
        infoGrid.add(new Text("Status:"), 0, 5);
        infoGrid.add(new Text(donation.getStatus()), 1, 5);
        
        // Add buttons for approval/rejection
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button approveButton = new Button("Approve");
        approveButton.getStyleClass().add("donate-button");
        approveButton.setOnAction(e -> {
            donation.setStatus(DonationStatus.APPROVED.toString());
            donationReviewTable.refresh();
            
            // Move the approved donation to the Donation Status & Completion table
            List<Donation> completionDonations = currentFacility.getCompletionDonations();
            if (completionDonations == null) {
                completionDonations = new ArrayList<>();
                currentFacility.setCompletionDonations(completionDonations);
            }
            completionDonations.add(donation);
            
            // Remove from review table
            donationReviewTable.getItems().remove(donation);
            
            // Refresh the donation status table
            initializeDonationStatusTable();
            
            popupStage.close();
            showAlert("Donation Approved", "The donation has been approved successfully and moved to Donation Status & Completion section.");
        });
        
        Button rejectButton = new Button("Reject");
        rejectButton.getStyleClass().add("donate-button");
        rejectButton.setOnAction(e -> {
            donation.setStatus(DonationStatus.REJECTED.toString());
            donationReviewTable.refresh();
            popupStage.close();
            showAlert("Donation Rejected", "The donation has been rejected.");
        });
        
        buttonBox.getChildren().addAll(approveButton, rejectButton);
        
        // Add all components to the content
        content.getChildren().addAll(headerText, infoGrid, buttonBox);
        
        // Set the scene
        Scene scene = new Scene(content, 400, 350);
        popupStage.setScene(scene);
        
        // Show the popup
        popupStage.showAndWait();
    }
    
    @FXML
    private void handleUpdateStatus() {
        // In a real application, this would open a dialog to update inventory
        // For now, just show a confirmation message
        showAlert("Inventory Updated", "Blood inventory status has been updated successfully.");
    }
    
    private void initializeDonationStatusTable() {
        if (donationStatusContainer != null) {
            donationStatusContainer.getChildren().clear();
            
            // Add the section header (already in FXML)
            Text headerText = new Text("Donation Status & Completion");
            headerText.getStyleClass().add("section-header");
            donationStatusContainer.getChildren().add(headerText);
            
            // Create the table view
            donationStatusTable = new TableView<>();
            donationStatusTable.getStyleClass().add("recent-requests-table");
            
            // Initialize table columns
            TableColumn<Donation, String> requestIdCol = new TableColumn<>("Request ID");
            requestIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
            
            TableColumn<Donation, String> bloodTypeCol = new TableColumn<>("Blood Type");
            bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
            
            TableColumn<Donation, Integer> unitsCol = new TableColumn<>("Units");
            unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
            
            TableColumn<Donation, String> locationCol = new TableColumn<>("Hospital");
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            
            TableColumn<Donation, LocalDate> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            // Custom cell factory for date column to format the date
            dateCol.setCellFactory(column -> new TableCell<Donation, LocalDate>() {
                @Override
                protected void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    }
                }
            });
            
            TableColumn<Donation, String> selectCol = new TableColumn<>("Select");
            selectCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Custom cell factory for select column to add Select buttons
            selectCol.setCellFactory(column -> new TableCell<Donation, String>() {
                private final Button selectButton = new Button("Select");
                
                {
                    // Configure button style
                    selectButton.getStyleClass().add("donate-button");
                    selectButton.setMaxWidth(Double.MAX_VALUE);
                    
                    // Add action handler for the select button
                    selectButton.setOnAction(event -> {
                        Donation donation = getTableView().getItems().get(getIndex());
                        showDonationStatusDialog(donation);
                    });
                }
                
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(selectButton);
                    }
                }
            });
            
            donationStatusTable.getColumns().clear();
            donationStatusTable.getColumns().addAll(requestIdCol, bloodTypeCol, unitsCol, locationCol, dateCol, selectCol);
            
            // Get donations from the facility
            List<Donation> completionDonations = currentFacility.getCompletionDonations();
            if (completionDonations == null || completionDonations.isEmpty()) {
                // Add sample data for demonstration
                completionDonations = new ArrayList<>();
                completionDonations.add(new Donation("REQ003", "B+", 1, "St. Mary's", LocalDate.now().minusDays(2), DonationStatus.APPROVED.toString()));
                currentFacility.setCompletionDonations(completionDonations);
            }
            
            ObservableList<Donation> donationsList = FXCollections.observableArrayList(completionDonations);
            donationStatusTable.setItems(donationsList);
            
            // Configure table properties
            donationStatusTable.setFixedCellSize(45);
            // Set a fixed height to prevent visual issues
            double tableHeight = (completionDonations.size() + 1) * 45 + 5; // +1 for header, +5 for padding
            donationStatusTable.setPrefHeight(tableHeight);
            donationStatusTable.setMinHeight(tableHeight);
            donationStatusTable.setMaxHeight(tableHeight);
            
            // Set column widths (percentage-based)
            requestIdCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.15));
            bloodTypeCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.15));
            unitsCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.1));
            locationCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.244));
            dateCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.15));
            selectCol.prefWidthProperty().bind(donationStatusTable.widthProperty().multiply(0.2));
            
            // Add the table to the container
            donationStatusContainer.getChildren().add(donationStatusTable);
        } else {
            // If the container is not available, show a message in the console
            System.out.println("Donation status container not found in FXML");
        }
    }
    
    private void showDonationStatusDialog(Donation donation) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Donation Status");
        
        // Create the content for the popup
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        content.getStyleClass().add("section-container");
        
        // Add donation information
        Text headerText = new Text("Donation Information");
        headerText.getStyleClass().add("section-header");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10));
        
        // Add donation details to the grid
        infoGrid.add(new Text("Request ID:"), 0, 0);
        infoGrid.add(new Text(donation.getRequestId()), 1, 0);
        
        infoGrid.add(new Text("Blood Type:"), 0, 1);
        infoGrid.add(new Text(donation.getBloodType()), 1, 1);
        
        infoGrid.add(new Text("Units:"), 0, 2);
        infoGrid.add(new Text(String.valueOf(donation.getUnits())), 1, 2);
        
        infoGrid.add(new Text("Hospital:"), 0, 3);
        infoGrid.add(new Text(donation.getLocation()), 1, 3);
        
        infoGrid.add(new Text("Date:"), 0, 4);
        infoGrid.add(new Text(donation.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 1, 4);
        
        infoGrid.add(new Text("Status:"), 0, 5);
        infoGrid.add(new Text(donation.getStatus()), 1, 5);
        
        // Add buttons for completion status
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button completedButton = new Button("Completed");
        completedButton.getStyleClass().add("donate-button");
        completedButton.setOnAction(e -> {
            donation.setStatus(DonationStatus.COMPLETED.toString());
            // Remove the donation from the completion list
            currentFacility.getCompletionDonations().remove(donation);
            // Refresh the table to reflect the changes
            initializeDonationStatusTable();
            popupStage.close();
            showAlert("Donation Completed", "The donation has been marked as completed and removed from the list.");
        });
        
        Button notCompletedButton = new Button("Not Completed");
        notCompletedButton.getStyleClass().add("donate-button");
        notCompletedButton.setOnAction(e -> {
            donation.setStatus(DonationStatus.NOT_COMPLETED.toString());
            // Remove the donation from the completion list
            currentFacility.getCompletionDonations().remove(donation);
            // Refresh the table to reflect the changes
            initializeDonationStatusTable();
            popupStage.close();
            showAlert("Donation Not Completed", "The donation has been marked as not completed and removed from the list.");
        });
        
        buttonBox.getChildren().addAll(completedButton, notCompletedButton);
        
        // Add all components to the content
        content.getChildren().addAll(headerText, infoGrid, buttonBox);
        
        // Set the scene
        Scene scene = new Scene(content, 400, 350);
        popupStage.setScene(scene);
        
        // Show the popup
        popupStage.showAndWait();
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