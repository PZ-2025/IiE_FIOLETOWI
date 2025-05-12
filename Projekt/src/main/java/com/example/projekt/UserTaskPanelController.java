package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserTaskPanelController {
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dateColumn;

    @FXML private ComboBox<String> statusComboBox;
    @FXML private ListView<String> commentList;
    @FXML private ListView<String> attachmentList;
    @FXML private ListView<String> historyList;
    @FXML private TextField commentField;

    private static final Logger LOGGER = Logger.getLogger(UserTaskPanelController.class.getName());
    private Task selectedTask;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nazwaProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        priorityColumn.setCellValueFactory(data -> data.getValue().priorytetProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dataProperty());

        loadTasks();
        loadStatuses();

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedTask = newSel;
            if (newSel != null) {
                loadComments(newSel.getId());
                loadAttachments(newSel.getId());
            }
        });

        loadTaskHistory();
    }

    private void loadTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        String sql = """
        SELECT z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet, z.data_rozpoczęcia
        FROM zadania z
        JOIN statusy s ON z.id_statusu = s.id_statusu
        JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
        WHERE z.id_pracownika = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                taskList.add(new Task(
                        rs.getInt("id_zadania"),
                        rs.getString("nazwa"),
                        rs.getString("status"),
                        rs.getString("priorytet"),
                        rs.getDate("data_rozpoczęcia").toString()
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania zadań", e);
        }

        taskTable.setItems(taskList);
    }

    private void loadStatuses() {
        statusComboBox.getItems().clear();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT nazwa FROM statusy");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statusComboBox.getItems().add(rs.getString("nazwa"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania statusów", e);
        }
    }

    @FXML
    private void changeTaskStatus() {
        if (selectedTask == null || statusComboBox.getValue() == null) return;

        String sql = """
        UPDATE zadania SET id_statusu = 
        (SELECT id_statusu FROM statusy WHERE nazwa = ?) 
        WHERE id_zadania = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusComboBox.getValue());
            stmt.setInt(2, selectedTask.getId());
            stmt.executeUpdate();
            loadTasks();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd zmiany statusu", e);
        }
    }

    private void loadComments(int taskId) {
        commentList.getItems().clear();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT tresc FROM komentarze WHERE id_zadania = ? ORDER BY data_dodania")) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                commentList.getItems().add(rs.getString("tresc"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania komentarzy", e);
        }
    }

    @FXML
    private void addComment() {
        if (selectedTask == null || commentField.getText().isEmpty()) return;

        String sql = "INSERT INTO komentarze (id_zadania, id_pracownika, tresc) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedTask.getId());
            stmt.setInt(2, UserSession.getInstance().getUser().getId());
            stmt.setString(3, commentField.getText());
            stmt.executeUpdate();
            commentField.clear();
            loadComments(selectedTask.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd dodawania komentarza", e);
        }
    }

    private void loadAttachments(int taskId) {
        attachmentList.getItems().clear();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT nazwa_pliku FROM zalaczniki WHERE id_zadania = ?")) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attachmentList.getItems().add(rs.getString("nazwa_pliku"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania załączników", e);
        }
    }

    @FXML
    private void addAttachment() {
        if (selectedTask == null) return;

        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO zalaczniki (id_zadania, sciezka_pliku, nazwa_pliku) VALUES (?, ?, ?)")) {
                stmt.setInt(1, selectedTask.getId());
                stmt.setString(2, file.getAbsolutePath());
                stmt.setString(3, file.getName());
                stmt.executeUpdate();
                loadAttachments(selectedTask.getId());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Błąd dodawania załącznika", e);
            }
        }
    }

    private void loadTaskHistory() {
        historyList.getItems().clear();
        String sql = """
        SELECT z.nazwa FROM zadania z 
        JOIN statusy s ON z.id_statusu = s.id_statusu
        WHERE z.id_pracownika = ? AND s.nazwa = 'Zakończone'
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyList.getItems().add(rs.getString("nazwa"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania historii", e);
        }
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCurrentUser(UserSession.getInstance().getUser());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Błąd powrotu do dashboardu", e);
        }
    }
}
