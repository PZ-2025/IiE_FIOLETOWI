module com.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;
    requires org.jfree.jfreechart;
    requires kernel;
    requires layout;
    requires io;
    requires com.example.reportlib;


    opens com.example.projekt to javafx.fxml;
    exports com.example.projekt;
}
