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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;

import static com.example.projekt.DashboardController.LOGGER;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> startDateColumn;
    @FXML private TableColumn<Task, String> endDateColumn;
    @FXML private TableColumn<Task, String> assignedColumn;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> employeeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private ObservableList<String> statusList = FXCollections.observableArrayList();
    private ObservableList<String> priorityList = FXCollections.observableArrayList();
    private ObservableList<String> employeeList = FXCollections.observableArrayList();

    private final String URL = "jdbc:mysql://localhost:3306/HurtPolSan";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorytet"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("koniec"));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<>("pracownik"));

        loadData();
        loadComboBoxes();

        taskTable.setOnMouseClicked(event -> {
            Task selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getNazwa());
                statusBox.setValue(selected.getStatus());
                priorityBox.setValue(selected.getPriorytet());
                employeeBox.getItems().stream()
                        .filter(emp -> emp.contains(selected.getPracownik()))
                        .findFirst()
                        .ifPresent(employeeBox::setValue);
                startDatePicker.setValue(LocalDate.parse(selected.getData()));
                endDatePicker.setValue(LocalDate.parse(selected.getKoniec()));
            }
        });
    }

    private void loadComboBoxes() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT nazwa FROM statusy");
            while (rs.next()) statusList.add(rs.getString("nazwa"));
            statusBox.setItems(statusList);

            rs = conn.createStatement().executeQuery("SELECT nazwa FROM priorytety");
            while (rs.next()) priorityList.add(rs.getString("nazwa"));
            priorityBox.setItems(priorityList);

            rs = conn.createStatement().executeQuery("SELECT id_pracownika, imie, nazwisko FROM pracownicy");
            while (rs.next()) {
                String emp = rs.getInt("id_pracownika") + ": " + rs.getString("imie") + " " + rs.getString("nazwisko");
                employeeList.add(emp);
            }
            employeeBox.setItems(employeeList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        taskList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = """
                SELECT z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet,
                       z.data_rozpoczecia, z.data_zakonczenia,
                       CONCAT(pr.imie, ' ', pr.nazwisko) AS pracownik
                FROM zadania z
                JOIN statusy s ON z.id_statusu = s.id_statusu
                JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
                JOIN pracownicy pr ON z.id_pracownika = pr.id_pracownika
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id_zadania"),
                        rs.getString("nazwa"),
                        rs.getString("status"),
                        rs.getString("priorytet"),
                        rs.getString("data_rozpoczecia")
                );
                task.setEndDate(rs.getString("data_zakonczenia"));
                task.setAssignedTo(rs.getString("pracownik"));
                taskList.add(task);
            }
            taskTable.setItems(taskList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTask() {
        String name = nameField.getText();
        String status = statusBox.getValue();
        String priority = priorityBox.getValue();
        String employee = employeeBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (name.isEmpty() || status == null || priority == null || employee == null || startDate == null || endDate == null) {
            showAlert("Uzupełnij wszystkie pola!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int statusId = getIdFromTable(conn, "statusy", status);
            int priorityId = getIdFromTable(conn, "priorytety", priority);
            int employeeId = Integer.parseInt(employee.split(":")[0]);

            String sql = """
                INSERT INTO zadania (id_pracownika, nazwa, id_statusu, id_priorytetu, data_rozpoczecia, data_zakonczenia)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.setString(2, name);
            stmt.setInt(3, statusId);
            stmt.setInt(4, priorityId);
            stmt.setDate(5, Date.valueOf(startDate));
            stmt.setDate(6, Date.valueOf(endDate));
            stmt.executeUpdate();
            loadData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Wybierz zadanie do edycji.");
            return;
        }

        String name = nameField.getText();
        String status = statusBox.getValue();
        String priority = priorityBox.getValue();
        String employee = employeeBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (name.isEmpty() || status == null || priority == null || employee == null || startDate == null || endDate == null) {
            showAlert("Uzupełnij wszystkie pola!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int statusId = getIdFromTable(conn, "statusy", status);
            int priorityId = getIdFromTable(conn, "priorytety", priority);
            int employeeId = Integer.parseInt(employee.split(":")[0]);

            String sql = """
                UPDATE zadania
                SET nazwa = ?, id_statusu = ?, id_priorytetu = ?, data_rozpoczecia = ?, data_zakonczenia = ?, id_pracownika = ?
                WHERE id_zadania = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, statusId);
            stmt.setInt(3, priorityId);
            stmt.setDate(4, Date.valueOf(startDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setInt(6, employeeId);
            stmt.setInt(7, selected.getId());
            stmt.executeUpdate();
            loadData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Wybierz zadanie do usunięcia.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM zadania WHERE id_zadania = ?");
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIdFromTable(Connection conn, String table, String name) throws SQLException {
        String baseName = table.endsWith("y") ? table.substring(0, table.length() - 1) + "u" : table;
        String columnName = "id_" + baseName;

        String sql = "SELECT " + columnName + " FROM " + table + " WHERE nazwa = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
        throw new SQLException("Nie znaleziono: " + name);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        statusBox.getSelectionModel().clearSelection();
        priorityBox.getSelectionModel().clearSelection();
        employeeBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
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
