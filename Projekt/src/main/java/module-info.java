module com.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.projekt to javafx.fxml;
    exports com.example.projekt;
}