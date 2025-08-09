package com.redcell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class RedCellApp extends Application {
    private static final double DEFAULT_WIDTH = 1280;
    private static final double DEFAULT_HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
        Scene scene = new Scene(root);
        
        // Add stylesheet to the scene
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Set minimum window size
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Set default window size (720p)
        primaryStage.setWidth(DEFAULT_WIDTH);
        primaryStage.setHeight(DEFAULT_HEIGHT);
        
        // Center on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - DEFAULT_WIDTH) / 2);
        primaryStage.setY((screenBounds.getHeight() - DEFAULT_HEIGHT) / 2);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/redcell-logo-down.png")));
        primaryStage.setTitle("RedCell - Blood Bank Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(RedCellApp.class,args);
    }
} 