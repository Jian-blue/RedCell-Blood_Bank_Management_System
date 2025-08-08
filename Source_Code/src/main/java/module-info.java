module com.redcell {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.redcell to javafx.fxml;
    exports com.redcell;
} 