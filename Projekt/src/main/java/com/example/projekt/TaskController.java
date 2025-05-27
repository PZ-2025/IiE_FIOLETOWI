package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Level;

import static com.example.projekt.DashboardController.LOGGER;

public class TaskController {

    @FXML
    private TextField taskInput;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> nameColumn;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, String> dateColumn;

    @FXML
    private TableColumn<Task, String> userColumn;

    @FXML
    private TableColumn<Task, String> statusColumn;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    // Konstruktor bezargumentowy wymagany przez FXMLLoader
    public TaskController() {
    }

    @FXML
    public void initialize() {
        // Inicjalizacja kolumn powiązaniem z właściwościami klasy Task
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        taskTable.setItems(tasks);
    }

    @FXML
    private void handleAddTask() {
        String taskName = taskInput.getText().trim();
        if (!taskName.isEmpty()) {
            // Ustaw dane przykładowo — dopasuj według swojej logiki
            Task newTask = new Task(taskName, "Średni", "2025-05-27", "Admin");
            tasks.add(newTask);
            taskInput.clear();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
        }
    }

    @FXML
    private void handleMarkAsCompleted() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setStatus("Zakończone");
            taskTable.refresh();
        }
    }

    @FXML
    private void goBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCurrentUser(UserSession.getInstance().getUser());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Błąd powrotu do dashboardu", e);
        }
    }
}
