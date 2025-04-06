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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontroler zarządzający widokiem listy zadań.
 * Obsługuje operacje dodawania, usuwania i oznaczania zadań jako zakończone.
 */
public class TaskController {
    private static final Logger LOGGER = Logger.getLogger(TaskController.class.getName());
    private static final String DASHBOARD_VIEW_PATH = "/com/example/projekt/dashboard.fxml";
    private static final String DASHBOARD_TITLE = "Dashboard";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;
    private static final String COMPLETED_PREFIX = "[Ukończono] ";

    /**
     * Pole tekstowe do wprowadzania nowych zadań
     */
    @FXML
    private TextField taskInput;

    /**
     * Lista wyświetlająca zadania
     */
    @FXML
    private ListView<String> taskList;

    /**
     * Przycisk do dodawania zadań
     */
    @FXML
    private Button addTaskButton;

    /**
     * Przycisk do usuwania zadań
     */
    @FXML
    private Button deleteTaskButton;

    /**
     * Przycisk do oznaczania zadań jako zakończone
     */
    @FXML
    private Button markAsCompletedButton;

    /**
     * Inicjalizuje kontroler po załadowaniu widoku FXML.
     * Automatycznie wywoływana przez JavaFX.
     */
    @FXML
    public void initialize() {
        // Inicjalizacja kontrolera (opcjonalnie)
    }

    /**
     * Dodaje nowe zadanie do listy.
     * Zadanie jest dodawane tylko jeśli pole tekstowe nie jest puste.
     */
    @FXML
    private void handleAddTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskList.getItems().add(task);
            taskInput.clear();
        }
    }

    /**
     * Usuwa zaznaczone zadanie z listy.
     */
    @FXML
    private void handleDeleteTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            taskList.getItems().remove(selectedIndex);
        }
    }

    /**
     * Oznacza zaznaczone zadanie jako zakończone.
     * Dodaje prefix "[Ukończono]" do nazwy zadania.
     */
    @FXML
    private void handleMarkAsCompleted() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String task = taskList.getItems().get(selectedIndex);
            if (!task.startsWith(COMPLETED_PREFIX)) {
                taskList.getItems().set(selectedIndex, COMPLETED_PREFIX + task);
            }
        }
    }

    /**
     * Przechodzi z powrotem do widoku dashboardu.
     *
     * @param event zdarzenie wywołujące zmianę widoku
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            Parent dashboardRoot = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(DASHBOARD_VIEW_PATH)));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(DASHBOARD_TITLE);
            stage.setScene(new Scene(dashboardRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
            stage.show();

        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania dashboardu", e);
            showAlert("Nie można załadować dashboardu");
        }
    }

    /**
     * Wyświetla komunikat o błędzie (w przyszłości można rozszerzyć o Alert).
     *
     * @param message treść komunikatu
     */
    private void showAlert(String message) {
        System.err.println(message);
    }
}