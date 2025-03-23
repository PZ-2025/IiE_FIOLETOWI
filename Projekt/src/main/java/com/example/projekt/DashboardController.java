package com.example.projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private void goToTaskManager(ActionEvent event) {
        try {
            Parent taskManagerRoot = FXMLLoader.load(getClass().getResource("/com/example/Projekt/task.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Zarządzanie zadaniami");
            stage.setScene(new Scene(taskManagerRoot, 1000, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}