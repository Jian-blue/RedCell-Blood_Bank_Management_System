package com.redcell;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Popup;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



public class DashboardController {


    private static boolean isLoggedIn = false;

    public static void setLoggedInState(boolean state) {
        isLoggedIn = state;
    }

    @FXML
    private StackPane contentArea;
    @FXML
    private Text userNameText;
    
    @FXML
    private ImageView userProfileImage;
    
    @FXML
    private Text notificationCount;

    @FXML
    private StackPane notificationBadgePane;
    
    @FXML
    private Button notificationButton;
    
    @FXML
    private Button dashboardButton;
    
    
    private Popup notificationPopup;
    private List<Notification> notifications;

    @FXML
    private void initialize() {
        // Always load the home view by default
        loadView("home");

        // Initialize notifications
        notifications = new ArrayList<>();
        updateNotificationCount();

        refreshNotifications();

        // Update UI based on initial login state (which is false by default)
        updateUIForLoggedInState(isLoggedIn);
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        if (isLoggedIn) {
            userNameText.setText("Logged In User"); // Replace with actual username
        } else {
            userNameText.setText("Guest");
        }
    }

    private void updateNotificationCount() {
        int unread = (int) notifications.stream().filter(n -> !n.isRead()).count();
        notificationCount.setText(String.valueOf(unread));
        
        // Control visibility of the notification count text and the badge itself
        boolean hasUnread = unread > 0;
        notificationCount.setVisible(hasUnread);
        if (notificationBadgePane != null) {
            notificationBadgePane.setVisible(hasUnread);
        }
    }

    private void addNotification(String title, String message) {
        notifications.add(0, new Notification(title, message));
        updateNotificationCount();
    }

    @FXML
    private void handleNotificationsAction() {
        if (notificationPopup != null && notificationPopup.isShowing()) {
            notificationPopup.hide();
            return;
        }

        VBox notificationContainer = new VBox();
        notificationContainer.getStyleClass().add("notification-popup");

        // Add header
        HBox header = new HBox();
        header.getStyleClass().add("notification-header");
        Text headerText = new Text("Notifications");
        headerText.getStyleClass().add("header-text");
        header.getChildren().add(headerText);

        VBox notificationList = new VBox();
        notificationList.getStyleClass().add("notification-list");

        if (notifications.isEmpty()) {
            VBox emptyState = new VBox();
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(30, 20, 30, 20));
            emptyState.setSpacing(10);
            
            Text noNotifications = new Text("No notifications");
            noNotifications.getStyleClass().add("notification-title");
            Text subText = new Text("You're all caught up!");
            subText.getStyleClass().add("notification-message");
            
            emptyState.getChildren().addAll(noNotifications, subText);
            notificationList.getChildren().add(emptyState);
        } else {
            for (Notification notification : notifications) {
                VBox notificationItem = createNotificationItem(notification);
                notificationList.getChildren().add(notificationItem);
            }
        }

        ScrollPane scrollPane = new ScrollPane(notificationList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(Math.min(400, Math.max(150, notifications.size() * 80)));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        notificationContainer.getChildren().addAll(header, scrollPane);

        notificationPopup = new Popup();
        notificationPopup.getContent().add(notificationContainer);
        notificationPopup.setAutoHide(true);

        Bounds bounds = notificationButton.localToScreen(notificationButton.getBoundsInLocal());
        notificationPopup.show(notificationButton.getScene().getWindow(),
                bounds.getMinX() - 300 + bounds.getWidth(),
                bounds.getMaxY() + 10);
    }

    private VBox createNotificationItem(Notification notification) {
        VBox item = new VBox();
        item.getStyleClass().add("notification-item");
        if (!notification.isRead()) {
            item.getStyleClass().add("unread");
        }
        item.setMaxWidth(300);

        Text title = new Text(notification.getTitle());
        title.getStyleClass().add("notification-title");

        Text message = new Text(notification.getMessage());
        message.getStyleClass().add("notification-message");
        message.setWrappingWidth(280);

        Text time = new Text(notification.getFormattedTime());
        time.getStyleClass().add("notification-time");

        item.getChildren().addAll(title, message, time);
        
        // Mark as read when clicked
        item.setOnMouseClicked(e -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                item.getStyleClass().remove("unread");
                updateNotificationCount();
            }
            // Optional: hide popup after clicking
            // notificationPopup.hide();
        });

        return item;
    }

    @FXML
    private void handleHomeAction() {
        loadView("home");
    }
    
    @FXML
    private void handleDashboardAction() {
        if (isLoggedIn) {
            if (DonorDashboardController.isLoggedIn) {
            loadView("donordashboard");
            } else {
            loadView("facilitydashboard");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Required");
            alert.setHeaderText(null);
            alert.setContentText("Please log in first to view the dashboard.");

            // Add custom styling to the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }
    }


    @FXML
    private void handleRegisterAction() {
        loadView("register");
    }

    @FXML
    private void handleRequestBloodAction() {
        if (isLoggedIn) {
            loadView("request-blood");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Required");
            alert.setHeaderText(null);
            alert.setContentText("Please log in first to request blood.");

            // Add custom styling to the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleTrackRequestAction() {
        loadView("track-request");
    }

    @FXML
    private void handleAboutAction() {
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About RedCell");
        aboutDialog.setHeaderText("RedCell Blood Bank Management System");
        aboutDialog.setContentText("RedCell is a comprehensive blood bank management system designed to streamline the process of blood donation and distribution.\n\n" +
                "Version: 1.0\n" +
                "© 2024 RedCell. All rights reserved.\n\n" +
                "This software helps connect donors with those in need of blood, making the process of blood donation and requests more efficient and accessible.");
        
        // Add custom styling to the dialog
        DialogPane dialogPane = aboutDialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        
        aboutDialog.showAndWait();
    }

    @FXML
    private void handleContactAction() {
        Alert contactDialog = new Alert(Alert.AlertType.INFORMATION);
        contactDialog.setTitle("Contact Us");
        contactDialog.setHeaderText("Get in Touch");
        contactDialog.setContentText("We're here to help! Contact us through any of these channels:\n\n" +
                "Email: support@redcell.com\n" +
                "Phone: +1 (555) 123-4567\n" +
                "Address: 123 Medical Center Drive\n" +
                "           Healthcare District\n" +
                "           City, State 12345\n\n" +
                "Emergency Hotline: +1 (555) 999-9999\n" +
                "Available 24/7 for urgent blood requests");
        
        // Add custom styling to the dialog
        DialogPane dialogPane = contactDialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        
        contactDialog.showAndWait();
    }

    public <T> T loadView(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + viewName + ".fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Get the controller and set DashboardController if needed
            Object controller = loader.getController();
            if (controller instanceof LoginController) {
                ((LoginController) controller).setDashboardController(this);
            } else if (controller instanceof LogoutController) {
                ((LogoutController) controller).setDashboardController(this);
            }

            // Return the controller, casting it to the expected type T
            return (T) controller;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + viewName + " view: " + e.getMessage());
            return null;
        }
    }

    // Method to update UI elements based on login state (e.g., hide/show login/logout buttons)
    private void updateUIForLoggedInState(boolean loggedIn) {
        if (loggedIn) {
            userNameText.setText("Logged In User"); // Replace with actual username
        } else {
            userNameText.setText("Guest");
        }


    }

    @FXML
    private void handleLoginAction() {
        if (!isLoggedIn) {
            // If not logged in, load the login view
            LoginController loginController = loadView("login");
            if (loginController != null) {
                loginController.setDashboardController(this);
            }
            // Note: isLoggedIn will be set by the LoginController upon successful login
            // updateUIForLoggedInState(isLoggedIn); // UI update should happen after successful login
        } else {
            // If logged in, initiate logout and load the logout view
            loadView("logout"); // This will load the view containing the logout button
            // The actual logout logic (setting flags, showing pop-up, and loading login view)
            // is handled by the LogoutController's handleLogout method, triggered by the button in the logout view.
            // updateUIForLoggedInState(isLoggedIn); // UI update should happen after successful logout
        }
    }


    private void addDefaultNotification() {
        addNotification("Please Log In", "Please log in to view your notifications here.");
    }

    public void refreshNotifications() {
        notifications.clear(); // Clear existing notifications
        if (!isLoggedIn) {
            addDefaultNotification();
        } else {
            // Add some sample notifications
            addNotification("Blood Request", "New A+ blood request from City Hospital");
            addNotification("Donation Reminder", "Your next donation date is approaching");
        }
        updateNotificationCount();
    }


    private static class Notification {
        private final String title;
        private final String message;
        private final LocalDateTime time;
        private boolean read;

        public Notification(String title, String message) {
            this.title = title;
            this.message = message;
            this.time = LocalDateTime.now();
            this.read = false;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }

        public String getFormattedTime() {
            return time.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
        }
    }
} 