package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserTaskPanelController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, Integer> productStockColumn;
    @FXML private TableColumn<Product, Integer> productLimitColumn;
    @FXML private TableColumn<Product, Double> productPriceColumn;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dateColumn;

    @FXML private ComboBox<String> statusComboBox;
    @FXML private ListView<String> historyList;
    @FXML private TextArea commentTextArea;

    private static final Logger LOGGER = Logger.getLogger(UserTaskPanelController.class.getName());
    private Task selectedTask;

    @FXML
    public void initialize() {
        productNameColumn.setCellValueFactory(data -> data.getValue().nazwaProperty());
        productStockColumn.setCellValueFactory(data -> data.getValue().stanProperty().asObject());
        productLimitColumn.setCellValueFactory(data -> data.getValue().limitStanowProperty().asObject());
        productPriceColumn.setCellValueFactory(data -> data.getValue().cenaProperty().asObject());

        loadProducts();

        nameColumn.setCellValueFactory(data -> data.getValue().nazwaProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        priorityColumn.setCellValueFactory(data -> data.getValue().priorytetProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dataProperty());

        loadTasks();
        loadStatuses();
        loadTaskHistory();

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedTask = newSel;
            if (newSel != null) {
                loadTaskComment(newSel.getId());
            } else {
                commentTextArea.clear();
            }
        });

        checkForNotifications(); // 🟢 Dodane powiadomienia
    }

    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.id_produktu, p.nazwa, p.stan, p.cena, p.limit_stanow, p.id_typu_produktu, t.nazwa AS typ_nazwa " +
                "FROM produkty p " +
                "LEFT JOIN typ_produktu t ON p.id_typu_produktu = t.id_typu_produktu";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id_produktu"),
                        rs.getString("nazwa"),
                        rs.getInt("stan"),
                        rs.getDouble("cena"),
                        rs.getInt("limit_stanow"),
                        rs.getInt("id_typu_produktu"),
                        rs.getString("typ_nazwa")
                ));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania produktów", e);
        }

        productTable.setItems(products);
    }

    private void loadTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        String sql = """
            SELECT z.id_zadania, z.nazwa,
                   COALESCE(s.nazwa, 'Brak') AS status,
                   COALESCE(p.nazwa, 'Brak') AS priorytet,
                   z.data_rozpoczecia
            FROM zadania z
            LEFT JOIN statusy s ON z.id_statusu = s.id_statusu
            LEFT JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
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
                        rs.getDate("data_rozpoczecia") != null ? rs.getDate("data_rozpoczecia").toString() : ""
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

    private void loadTaskComment(int taskId) {
        String sql = "SELECT komentarz FROM zadania WHERE id_zadania = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                commentTextArea.setText(rs.getString("komentarz"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania komentarza", e);
        }
    }

    @FXML
    private void saveTaskComment() {
        if (selectedTask == null) return;

        String sql = "UPDATE zadania SET komentarz = ? WHERE id_zadania = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, commentTextArea.getText());
            stmt.setInt(2, selectedTask.getId());
            stmt.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Komentarz zapisany");
            alert.setHeaderText(null);
            alert.setContentText("Komentarz został zapisany.");
            alert.showAndWait();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd zapisu komentarza", e);
        }
    }

    @FXML
    private void changeTaskStatus() {
        if (selectedTask == null || statusComboBox.getValue() == null) return;

        String sql = """
            UPDATE zadania SET id_statusu =
            (SELECT id_statusu FROM statusy WHERE nazwa = ?),
            powiadomienia = 1
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

    private void checkForNotifications() {
        String sql = """
            SELECT nazwa FROM zadania
            WHERE id_pracownika = ? AND powiadomienia = 1
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();

            StringBuilder message = new StringBuilder();
            while (rs.next()) {
                message.append("- ").append(rs.getString("nazwa")).append("\n");
            }

            if (!message.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nowe powiadomienia");
                alert.setHeaderText("Masz nowe przypisane zadania lub zmiany:");
                alert.setContentText(message.toString());
                alert.showAndWait();

                try (PreparedStatement clear = conn.prepareStatement(
                        "UPDATE zadania SET powiadomienia = 0 WHERE id_pracownika = ?")) {
                    clear.setInt(1, UserSession.getInstance().getUser().getId());
                    clear.executeUpdate();
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd sprawdzania powiadomień", e);
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Błąd powrotu do dashboardu", e);
        }
    }
}
