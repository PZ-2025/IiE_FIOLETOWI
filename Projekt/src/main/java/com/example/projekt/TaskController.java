package com.example.projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskController {

    @FXML
    private TextField taskInput;

    @FXML
    private ListView<String> taskList;

    @FXML
    private Button addTaskButton;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private Button markAsCompletedButton;

    @FXML
    public void initialize() {
        // Inicjalizacja kontrolera (opcjonalnie)
    }

    @FXML
    private void handleAddTask() {
        String task = taskInput.getText();
        if (!task.isEmpty()) {
            taskList.getItems().add(task);
            taskInput.clear();
        }
    }

    @FXML
    private void handleDeleteTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            taskList.getItems().remove(selectedIndex);
        }
    }

    @FXML
    private void handleMarkAsCompleted() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String task = taskList.getItems().get(selectedIndex);
            taskList.getItems().set(selectedIndex, "[Completed] " + task);
        }
    }

    // Metoda do przejścia do dashboardu
    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            // Załaduj plik FXML dla dashboardu
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/Projekt/dashboard.fxml"));

            // Pobierz obecną scenę i zmień ją na dashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(dashboardRoot, 1000, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}