package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.logging.Level;

import static com.example.projekt.DashboardController.LOGGER;

public class UserTaskPanelController {

    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> nameColumn;
    @FXML
    private TableColumn<Task, String> statusColumn;
    @FXML
    private TableColumn<Task, String> priorityColumn;
    @FXML
    private TableColumn<Task, String> dateColumn;

    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private ListView<String> historyList;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final ObservableList<String> history = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicjalizacja kolumn
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        // Ustawienie danych w tabeli
        taskTable.setItems(tasks);

        // Inicjalizacja ComboBox
        statusComboBox.setItems(FXCollections.observableArrayList("Nowe", "W trakcie", "Zakończone"));

        // Ustawienie danych w historii
        historyList.setItems(history);

        // Obsługa wyboru zadania
        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                commentTextArea.setText(newSelection.getComment());
                statusComboBox.setValue(newSelection.getStatus());
            }
        });
    }

    @FXML
    private void changeTaskStatus() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String newStatus = statusComboBox.getValue();
            if (newStatus != null && !newStatus.isEmpty()) {
                selectedTask.setStatus(newStatus);
                taskTable.refresh();
                if ("Zakończone".equalsIgnoreCase(newStatus)) {
                    history.add(selectedTask.getName() + " - " + selectedTask.getDate());
                }
            }
        }
    }

    @FXML
    private void saveTaskComment() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setComment(commentTextArea.getText());
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
