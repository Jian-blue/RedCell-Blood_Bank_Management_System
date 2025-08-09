package com.redcell;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class DonorDashboardController {

    public static boolean isLoggedIn = false;
    private static String loggedInUsername = "Guest"; // Default to Guest

    @FXML
    private Text userNameText;
    
    @FXML
    private Text dateText;
    
    @FXML
    private Text bloodGroupText;
    
    @FXML
    private Text statusText;
    
    @FXML
    private Text locationText;
    
    @FXML
    private Text totalDonationCount;
    
    @FXML
    private Text lastDonationInfo;
    
    @FXML
    private ImageView badge1;
    
    @FXML
    private ImageView badge2;
    
    @FXML
    private TableView<Request> bookDonationTable;
    
    @FXML
    private TableView<BloodAvailability> availableBloodTable;
    
    @FXML
    private ImageView adImageView;
    
    @FXML
    private VBox adSlideshow;
    
    private List<String> adImages = Arrays.asList(
        "/img/ad_01_emergency.png",
        "/img/ad_02_dangue.png",
        "/img/ad_03_FFP.png",
        "/img/ad_04_RCCorPRBC.png",
        "/img/ad_05_SDP.png",
        "/img/ad_06_Whole_blood.png",
        "/img/banner-ad-01.png",
        "/img/banner-ad-02.png"
    );
    
    private int currentAdIndex = 0;
    private Timeline slideShowTimeline;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        // Initialize user information
        initializeUserInfo();
        
        // Initialize donation table
        initializeBookDonationTable();
        
        // Initialize available blood table
        initializeAvailableBloodTable();
        
        // Initialize donation statistics
        initializeDonationStats();
        
        // Initialize badges
        initializeBadges();
        
        // Initialize ad slideshow
        initializeAdSlideshow();
    }
    
    private void initializeUserInfo() {
        // Set current user information
        userNameText.setText("Hello " + loggedInUsername + ",");
        dateText.setText("Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        bloodGroupText.setText("My Blood Group: " +"A+");
        statusText.setText("My Status: " + "ELIGIBLE");
        locationText.setText("Current Location: " + "Mirpur, Dhaka");
    }
    
    private void initializeBookDonationTable() {
        // Initialize table columns
        TableColumn<Request, String> bloodTypeCol = new TableColumn<>("Blood Type");
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));

        TableColumn<Request, Integer> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));

        TableColumn<Request, String> locationCol = new TableColumn<>("Hospital");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("hospital"));

        TableColumn<Request, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<Request, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-completed", "status-pending", "status-processing");
                } else {
                    setText(status);
                    // Apply different styles based on status
                    getStyleClass().removeAll("status-completed", "status-pending", "status-processing");
                    if (status.equalsIgnoreCase("Pending")) {
                        getStyleClass().add("status-pending");
                    } else if (status.equalsIgnoreCase("Completed")) {
                        getStyleClass().add("status-completed");
                    } else if (status.equalsIgnoreCase("Processing")) {
                        getStyleClass().add("status-processing");
                    }
                }
            }
        });
        
        TableColumn<Request, String> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Custom cell factory for select column to add Donate buttons
        selectCol.setCellFactory(column -> new TableCell<Request, String>() {
            private final javafx.scene.control.Button donateButton = new javafx.scene.control.Button("Donate");
            
            {
                // Configure button style
                donateButton.getStyleClass().add("donate-button");
                donateButton.setMaxWidth(Double.MAX_VALUE);
                
                // Add action handler for the donate button
                donateButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleDonateButtonClick(request);
                });
            }
            
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(donateButton);
                }
            }
        });

        bookDonationTable.getColumns().clear();
        bookDonationTable.getColumns().addAll(bloodTypeCol, unitsCol, locationCol, dateCol, statusCol, selectCol);

        // Add sample data - only show actual needed rows
        ObservableList<Request> allRequests = FXCollections.observableArrayList(
            new Request("REQ001", "A+", 2, "Daffodil Medical Institute", "Mirpur", "Pending", "Aug 05, 2025", "Critical", "10:00 AM", "123-456-7890", "Admin"),
            new Request("REQ002", "O-", 3, "General Hospital", "Gulshan", "Completed", "Aug 04, 2025", "Stable", "11:30 AM", "123-456-7891", "Admin"),
            new Request("REQ003", "B+", 1, "Dhanmondi Diagonostics", "Dhanmondi", "Processing", "Aug 03, 2025", "Emergency", "09:15 AM", "123-456-7892", "Admin"),

            new Request("REQ004", "AB-", 4, "Emergency Care", "Uttara", "Pending", "Aug 02, 2025", "Critical", "02:45 PM", "123-456-7893", "Admin"),
            new Request("REQ005", "O+", 2, "Central Hospital", "Banani", "Pending", "Aug 01, 2025", "Stable", "04:30 PM", "123-456-7894", "Admin")

        );
        
        // Filter out requests with "Completed" status
        ObservableList<Request> filteredRequests = allRequests.filtered(request -> 
            !request.getStatus().equalsIgnoreCase("Completed"));
        
        bookDonationTable.setItems(filteredRequests);
        
        // Configure table properties to prevent scrolling and show only needed rows
        bookDonationTable.setFixedCellSize(45);
        bookDonationTable.prefHeightProperty().bind(bookDonationTable.fixedCellSizeProperty().multiply(javafx.beans.binding.Bindings.size(bookDonationTable.getItems()).add(1.01)));
        bookDonationTable.minHeightProperty().bind(bookDonationTable.prefHeightProperty());
        bookDonationTable.maxHeightProperty().bind(bookDonationTable.prefHeightProperty());
        
        // Set column widths (percentage-based)
        bloodTypeCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.15));
        unitsCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.1));
        locationCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.25));
        dateCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.15));
        statusCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.15));
        selectCol.prefWidthProperty().bind(bookDonationTable.widthProperty().multiply(0.2));
    }
    
    private void initializeAvailableBloodTable() {
        // Initialize table columns
        TableColumn<BloodAvailability, String> hospitalCol = new TableColumn<>("Hospital/Facility");
        hospitalCol.setCellValueFactory(new PropertyValueFactory<>("hospital"));

        TableColumn<BloodAvailability, String> componentsCol = new TableColumn<>("Available Components");
        componentsCol.setCellValueFactory(new PropertyValueFactory<>("components"));

        TableColumn<BloodAvailability, Integer> totalUnitsCol = new TableColumn<>("Total Units");
        totalUnitsCol.setCellValueFactory(new PropertyValueFactory<>("totalUnits"));
        
        // Add a status column
        TableColumn<BloodAvailability, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            int units = cellData.getValue().getTotalUnits();
            String status;
            if (units > 50) {
                status = "Available";
            } else if (units >= 25 && units <= 50) {
                status = "Low Stock";
            } else {
                status = "Critical";
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        // Custom cell factory for status column to apply styling
        statusCol.setCellFactory(column -> new TableCell<BloodAvailability, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-completed", "status-pending", "status-processing");
                } else {
                    setText(status);
                    getStyleClass().removeAll("status-completed", "status-pending", "status-processing");
                    switch (status.toLowerCase()) {
                        case "available":
                            getStyleClass().add("status-completed");
                            break;
                        case "low stock":
                            getStyleClass().add("status-pending");
                            break;
                        case "critical":
                            getStyleClass().add("status-processing");
                            break;
                    }
                }
            }
        });

        availableBloodTable.getColumns().clear();
        availableBloodTable.getColumns().addAll(hospitalCol, componentsCol, totalUnitsCol, statusCol);

        // Add sample data with random components
        List<String> bloodComponents = Arrays.asList("Whole blood", "RCC/PRBC", "SDP", "FFP");
        java.util.Random random = new java.util.Random();

        ObservableList<BloodAvailability> bloodAvailability = FXCollections.observableArrayList(
            new BloodAvailability("Central Hospital", bloodComponents.get(random.nextInt(bloodComponents.size())), 60),
            new BloodAvailability("City Medical Center", bloodComponents.get(random.nextInt(bloodComponents.size())), 30),
            new BloodAvailability("University Hospital", bloodComponents.get(random.nextInt(bloodComponents.size())), 10),
            new BloodAvailability("General Hospital", bloodComponents.get(random.nextInt(bloodComponents.size())), 45),
            new BloodAvailability("Evercare Hospital", bloodComponents.get(random.nextInt(bloodComponents.size())), 70),
            new BloodAvailability("Square Hospital", bloodComponents.get(random.nextInt(bloodComponents.size())), 20)
        );
        
        availableBloodTable.setItems(bloodAvailability);
        
        // Configure table properties to prevent scrolling and show only needed rows
        availableBloodTable.setFixedCellSize(45);
        availableBloodTable.prefHeightProperty().bind(availableBloodTable.fixedCellSizeProperty().multiply(javafx.beans.binding.Bindings.size(availableBloodTable.getItems()).add(1.01)));
        availableBloodTable.minHeightProperty().bind(availableBloodTable.prefHeightProperty());
        availableBloodTable.maxHeightProperty().bind(availableBloodTable.prefHeightProperty());
        
        // Set column widths (percentage-based)
        hospitalCol.prefWidthProperty().bind(availableBloodTable.widthProperty().multiply(0.25));
        componentsCol.prefWidthProperty().bind(availableBloodTable.widthProperty().multiply(0.35));
        totalUnitsCol.prefWidthProperty().bind(availableBloodTable.widthProperty().multiply(0.15));
        statusCol.prefWidthProperty().bind(availableBloodTable.widthProperty().multiply(0.25));
    }
    
    private void initializeDonationStats() {
        // Set donation statistics
        totalDonationCount.setText("3");
        lastDonationInfo.setText("A(+ ve)\n2 months ago");
    }
    
    private void initializeBadges() {
        int totalDonations = Integer.parseInt(totalDonationCount.getText());

        // Badge 1: Obtained if totalDonations >= 1
        if (totalDonations >= 1) {
            badge1.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/img/badge1_obtained.png")));
        } else {
            badge1.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/img/badge1_not_obtain.png")));
        }

        // Badge 2: Obtained if totalDonations >= 5
        if (totalDonations >= 10) {
            badge2.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/img/badge2_obtained.png")));
        } else {
            badge2.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/img/badge2_not_obtain.png")));
        }
    }
    
    @FXML
    private void handleUpdateStatus() {
        // Handle status update logic
        System.out.println("Update status clicked");
    }
    
    @FXML
    private void handleUpdateLocation() {
        // Handle location update logic
        System.out.println("Update location clicked");
    }

    @FXML
    private void handleBadgeClick(javafx.scene.input.MouseEvent event) {
        ImageView clickedBadge = (ImageView) event.getSource();
        javafx.scene.image.Image fullImage = clickedBadge.getImage();

        // Create a new stage (window) to display the full-resolution image
        javafx.stage.Stage stage = new javafx.stage.Stage();
        javafx.scene.image.ImageView fullImageView = new javafx.scene.image.ImageView(fullImage);
        javafx.scene.Scene scene = new javafx.scene.Scene(new javafx.scene.layout.StackPane(fullImageView));
        stage.setScene(scene);
        stage.setTitle("Badge Image");
        stage.show();
    }
    
    private void initializeAdSlideshow() {
        // Set initial ad image
        updateAdImage();
        
        // Create automatic slideshow with 5 second intervals
        slideShowTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> nextAd()));
        slideShowTimeline.setCycleCount(Timeline.INDEFINITE);
        slideShowTimeline.play();
    }
    
    private void updateAdImage() {
        String imagePath = adImages.get(currentAdIndex);
        adImageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }
    
    @FXML
    private void nextAd() {
        currentAdIndex = (currentAdIndex + 1) % adImages.size();
        updateAdImage();
    }
    
    @FXML
    private void previousAd() {
        currentAdIndex = (currentAdIndex - 1 + adImages.size()) % adImages.size();
        updateAdImage();
    }
    
    @FXML
    private void handleAdImageClick(javafx.scene.input.MouseEvent event) {
        // Pause the slideshow when viewing the full image
        slideShowTimeline.pause();
        
        // Get the current ad image
        Image fullImage = adImageView.getImage();
        
        // Create a new stage (window) to display the full-resolution image
        javafx.stage.Stage stage = new javafx.stage.Stage();
        javafx.scene.image.ImageView fullImageView = new javafx.scene.image.ImageView(fullImage);
        
        // Make the image fit the screen while maintaining aspect ratio
        fullImageView.setPreserveRatio(true);
        fullImageView.fitWidthProperty().bind(stage.widthProperty().multiply(0.8));
        fullImageView.fitHeightProperty().bind(stage.heightProperty().multiply(0.8));
        
        javafx.scene.Scene scene = new javafx.scene.Scene(new javafx.scene.layout.StackPane(fullImageView));
        stage.setScene(scene);
        stage.setTitle("Announcements");
        
        // Resume slideshow when the window is closed
        stage.setOnHidden(e -> slideShowTimeline.play());
        
        // Set initial size
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }
    
    // Method to handle donate button click
    private void handleDonateButtonClick(Request request) {
        // Get user's blood group from the UI
        String userBloodGroup = bloodGroupText.getText().replace("My Blood Group: ", "").trim();
        String requestBloodGroup = request.getBloodType();
        
        // Check if blood groups are compatible
        boolean isCompatible = isBloodGroupCompatible(userBloodGroup, requestBloodGroup);
        
        // Create alert dialog
        javafx.scene.control.Alert alert;
        if (isCompatible) {
            alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Donation Confirmation");
            alert.setHeaderText("Blood Group Compatible");
            alert.setContentText("You are about to donate " + request.getUnits() + " units of blood to " + 
                                request.getHospital() + ". Proceed?");
            
            // Add confirmation button
            javafx.scene.control.ButtonType confirmButton = new javafx.scene.control.ButtonType("Confirm Donation");
            alert.getButtonTypes().setAll(confirmButton, javafx.scene.control.ButtonType.CANCEL);
            
            // Apply custom styling to the dialog
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
            
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == confirmButton) {
                    // Create a new Donation object from the Request
                    String donorId = "DONOR" + UUID.randomUUID().toString().substring(0, 8); // Generate a donor ID
                    Donation newDonation = new Donation(request, donorId);
                    
                    // Add the donation to the facility's list of donations
                    Facility facility = request.getFacilityInfo();
                    if (facility.getBloodDonations() == null) {
                        facility.setBloodDonations(new ArrayList<>());
                    }
                    facility.getBloodDonations().add(newDonation);
                    
                    // Update the request status to Processing
                    request.setStatus("Processing");
                    
                    // Refresh the table to show the updated status
                    bookDonationTable.refresh();
                    
                    // Show confirmation message
                    showAlert("Donation Confirmed", "Your donation has been confirmed and is now processing. You will be notified when the facility approves your donation.");
                }
            });
        } else {
            alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Incompatible Blood Group");
            alert.setHeaderText("Blood Group Incompatible");
            alert.setContentText("You cannot donate to this request as your blood group (" + 
                                userBloodGroup + ") is not compatible with the requested blood group (" + 
                                requestBloodGroup + ").");
            
            // Apply custom styling to the dialog
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
            
            alert.showAndWait();
        }
    }
    
    // Method to check blood group compatibility
    private boolean isBloodGroupCompatible(String donorBloodGroup, String recipientBloodGroup) {
        // For simplicity, we'll just check if blood groups match exactly
        // In a real application, you would implement proper blood type compatibility rules
        return donorBloodGroup.equalsIgnoreCase(recipientBloodGroup);
    }
    
    // Helper method to show alerts
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Apply custom styling to the dialog
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        
        alert.showAndWait();
    }
    
    // Inner class for blood availability
    public static class BloodAvailability {
        private String hospital;
        private String components;
        private int totalUnits;
        
        public BloodAvailability(String hospital, String components, int totalUnits) {
            this.hospital = hospital;
            this.components = components;
            this.totalUnits = totalUnits;
        }
        
        public String getHospital() { return hospital; }
        public void setHospital(String hospital) { this.hospital = hospital; }
        
        public String getComponents() { return components; }
        public void setComponents(String components) { this.components = components; }
        
        public int getTotalUnits() { return totalUnits; }
        public void setTotalUnits(int totalUnits) { this.totalUnits = totalUnits; }
    }
}