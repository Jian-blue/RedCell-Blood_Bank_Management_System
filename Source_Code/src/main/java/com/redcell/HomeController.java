package com.redcell;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class HomeController {
    
    @FXML
    private Text mostRequestedType;
    
    @FXML
    private Text pendingRequests;
    
    @FXML
    private Text livesSaved;

    @FXML
    private Text activeDonors;

    @FXML
    private TableView<Request> recentRequestsTable;

    @FXML
    private GridPane donorsGrid;

    @FXML
    public void initialize() {
        initializeStatistics();
        initializeRecentRequestsTable();
        initializeDonorsGrid();
    }

    private void initializeStatistics() {
        mostRequestedType.setText("O+");
        pendingRequests.setText("12");
        livesSaved.setText("1,402");
        activeDonors.setText("500");
    }

    private void initializeRecentRequestsTable() {
        // Initialize table columns
        TableColumn<Request, String> bloodTypeCol = new TableColumn<>("Blood Type");
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));

        TableColumn<Request, Integer> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));

        TableColumn<Request, String> hospitalCol = new TableColumn<>("Hospital");
        hospitalCol.setCellValueFactory(new PropertyValueFactory<>("hospital"));

        TableColumn<Request, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Custom cell factory for date column to format the date
        dateCol.setCellFactory(column -> new TableCell<Request, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    // Convert ISO date to more readable format
                    try {
                        LocalDate localDate = LocalDate.parse(date);
                        setText(localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    } catch (Exception e) {
                        setText(date);
                    }
                }
            }
        });

        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Custom cell factory for status column
        statusCol.setCellFactory(column -> new TableCell<Request, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    getStyleClass().removeAll("status-completed", "status-pending", "status-processing");
                    switch (status.toLowerCase()) {
                        case "completed":
                            getStyleClass().add("status-completed");
                            break;
                        case "pending":
                            getStyleClass().add("status-pending");
                            break;
                        case "processing":
                            getStyleClass().add("status-processing");
                            break;
                    }
                }
            }
        });

        recentRequestsTable.getColumns().add(bloodTypeCol);
        recentRequestsTable.getColumns().add(unitsCol);
        recentRequestsTable.getColumns().add(hospitalCol);
        recentRequestsTable.getColumns().add(dateCol);
        recentRequestsTable.getColumns().add(statusCol);

        // Add sample data
        ObservableList<Request> recentRequests = FXCollections.observableArrayList(
            new Request("REQ001", "A+", 2, "জঝঞর", "Downtown", "Pending", LocalDate.now().format(DateTimeFormatter.ISO_DATE), "Severe Anemia", "10:00 AM", "Emily Johnson", "System"),
            new Request("REQ002", "O-", 3, "General Hospital", "Uptown", "Completed", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE), "Trauma Injury", "08:45 AM", "Mary Smith", "System"),
            new Request("REQ003", "B+", 1, "St. Mary's", "Westside", "Processing", LocalDate.now().minusDays(2).format(DateTimeFormatter.ISO_DATE), "Liver Surgery", "14:30 PM", "Paul Walker", "System"),
            new Request("REQ004", "AB-", 4, "Emergency Care", "Eastside", "Pending", LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_DATE), "Planned Transplant", "09:15 AM", "Rachel Adams", "System"),
            new Request("REQ005", "O+", 2, "Central Hospital", "Midtown", "Completed", LocalDate.now().minusDays(4).format(DateTimeFormatter.ISO_DATE), "Postpartum Hemorrhage", "12:00 PM", "Jessica Kim", "System")
        );

        recentRequestsTable.setItems(recentRequests);

        // Configure table properties to prevent scrolling
        recentRequestsTable.setFixedCellSize(45);
        double tableHeight = recentRequestsTable.getFixedCellSize() * (recentRequests.size() + 1) + 2;
        recentRequestsTable.setMinHeight(tableHeight);
        recentRequestsTable.setMaxHeight(tableHeight);
        recentRequestsTable.setPrefHeight(tableHeight);
        recentRequestsTable.setSelectionModel(null); // Disable selection

        // Set column widths (percentage-based)
        bloodTypeCol.prefWidthProperty().bind(recentRequestsTable.widthProperty().multiply(0.15));
        unitsCol.prefWidthProperty().bind(recentRequestsTable.widthProperty().multiply(0.1));
        hospitalCol.prefWidthProperty().bind(recentRequestsTable.widthProperty().multiply(0.35));
        dateCol.prefWidthProperty().bind(recentRequestsTable.widthProperty().multiply(0.2));
        statusCol.prefWidthProperty().bind(recentRequestsTable.widthProperty().multiply(0.2));
    }

    private void initializeDonorsGrid() {
        // Sample donor data
        List<Donor> donors = Arrays.asList(
            new Donor("Dr. Emily Chen", "A+", LocalDate.now().minusDays(92), "Central Hospital Area"),
            new Donor("Robert Williams", "O-", LocalDate.now().minusDays(7), "University District"),
            new Donor("Maria Rodriguez", "B+", LocalDate.now().minusMonths(1), "Downtown Medical Center")
        );

        // Create donor cards
        for (int i = 0; i < donors.size(); i++) {
            Donor donor = donors.get(i);
            
            VBox card = new VBox();
            card.getStyleClass().add("donor-card");
            
            Text name = new Text(donor.getName());
            name.getStyleClass().add("donor-name");
            
            Text bloodType = new Text(donor.getBloodType());
            bloodType.getStyleClass().add("donor-blood-type");
            
            Text lastDonation = new Text("Last donation: " + donor.getTimeSinceLastDonation());
            lastDonation.getStyleClass().add("donor-info");
            
            Text area = new Text("Area: " + donor.getArea());
            area.getStyleClass().add("donor-area");
            
            card.getChildren().addAll(name, bloodType, lastDonation, area);
            
            // Add card to grid
            donorsGrid.add(card, i, 0);
        }
    }

    @FXML
    private void showNewsDetails1() {
        showNewsPopup("World Blood Donor Day Campaign",
                "Join us for World Blood Donor Day on June 14th!\n\n" +
                "We're organizing a massive blood donation drive across multiple locations. " +
                "Your single donation can save up to 3 lives!\n\n" +
                "• Free health checkup for donors\n" +
                "• Refreshments provided\n" +
                "• Special recognition for regular donors\n\n" +
                "Location: Multiple centers citywide\n" +
                "Time: 8 AM - 6 PM\n" +
                "Date: June 14th, 2024");
    }

    @FXML
    private void showNewsDetails2() {
        showNewsPopup("Emergency Blood Drive Alert",
                "Critical Blood Shortage - Immediate Action Required!\n\n" +
                "We are experiencing a severe shortage of B- and O- blood types. " +
                "If you are eligible to donate, please visit your nearest donation center.\n\n" +
                "Current Critical Needs:\n" +
                "• B-negative: Less than 2 days supply\n" +
                "• O-negative: Less than 3 days supply\n\n" +
                "Extended Hours:\n" +
                "We've extended our center hours to 7 AM - 9 PM all week.\n" +
                "Walk-ins welcome!");
    }

    private void showNewsPopup(String title, String content) {
        Stage popupStage = new Stage(StageStyle.DECORATED);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setTextAlignment(TextAlignment.LEFT);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

        VBox popupContent = new VBox(contentLabel);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setPadding(new Insets(20));
        popupContent.setStyle("-fx-background-color: white;");
        popupContent.setSpacing(10);

        Scene scene = new Scene(popupContent, 400, 300);
        popupStage.setScene(scene);
        popupStage.show();
    }
} 