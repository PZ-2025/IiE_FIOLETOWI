module com.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.projekt to javafx.fxml;
    exports com.example.projekt;
}