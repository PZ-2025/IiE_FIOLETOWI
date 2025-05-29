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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;

import static com.example.projekt.DashboardController.LOGGER;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> productColumn;
    @FXML private TableColumn<Task, String> startDateColumn;
    @FXML private TableColumn<Task, String> endDateColumn;
    @FXML private TableColumn<Task, String> assignedColumn;
    @FXML private TableColumn<Task, String> assignedproductColumn;
    @FXML private TableColumn<Task, String> directionColumn;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> productBox;
    @FXML private ComboBox<String> employeeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private BorderPane taskRoot;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private ObservableList<String> statusList = FXCollections.observableArrayList();
    private ObservableList<String> priorityList = FXCollections.observableArrayList();
    private ObservableList<String> productList = FXCollections.observableArrayList();
    private ObservableList<String> employeeList = FXCollections.observableArrayList();

    private final String URL = "jdbc:mysql://localhost:3306/HurtPolSan";
    private final String USER = "root";
    private final String PASSWORD = "";

    @FXML
    public void initialize() {

        taskRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSize());
            }
        });

        // Konfiguracja tabeli
        configureTableColumns();

        // Ustawienie polityki zmiany rozmiaru kolumn
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadData();
        loadComboBoxes();

        taskTable.setOnMouseClicked(event -> {
            Task selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                fillFormWithSelectedTask(selected);
            }
        });
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorytet"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("koniec"));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<>("pracownik"));
        assignedproductColumn.setCellValueFactory(new PropertyValueFactory<>("produkt"));
        directionColumn.setCellValueFactory(new PropertyValueFactory<>("kierunek"));

        // Ustawienie proporcjonalnego rozkładu szerokości kolumn
        nameColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.25));
        statusColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        priorityColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        startDateColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        endDateColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        assignedColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        assignedproductColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        directionColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
    }

    private void fillFormWithSelectedTask(Task task) {
        nameField.setText(task.getNazwa());
        statusBox.setValue(task.getStatus());
        priorityBox.setValue(task.getPriorytet());
        productBox.setValue(task.getProdukt());
        employeeBox.getItems().stream()
                .filter(emp -> emp.contains(task.getPracownik()))
                .findFirst()
                .ifPresent(employeeBox::setValue);
        startDatePicker.setValue(LocalDate.parse(task.getData()));
        endDatePicker.setValue(LocalDate.parse(task.getKoniec()));
    }

    private void loadComboBoxes() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            loadStatuses(conn);
            loadPriorities(conn);
            loadProducts(conn);
            loadEmployees(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania danych do comboboxów", e);
            showAlert("Błąd ładowania danych", "Nie udało się załadować danych do formularza");
        }
    }

    private void loadStatuses(Connection conn) throws SQLException {
        statusList.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT nazwa FROM statusy");
        while (rs.next()) {
            statusList.add(rs.getString("nazwa"));
        }
        statusBox.setItems(statusList);
    }
    private void loadProducts(Connection conn) throws SQLException {
        productList.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT nazwa FROM produkty");
        while (rs.next()) {
            productList.add(rs.getString("nazwa"));
        }
        productBox.setItems(productList);
    }

    private void loadPriorities(Connection conn) throws SQLException {
        priorityList.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT nazwa FROM priorytety");
        while (rs.next()) {
            priorityList.add(rs.getString("nazwa"));
        }
        priorityBox.setItems(priorityList);
    }

    private void loadEmployees(Connection conn) throws SQLException {
        employeeList.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT id_pracownika, imie, nazwisko FROM pracownicy");
        while (rs.next()) {
            String emp = rs.getInt("id_pracownika") + ": " + rs.getString("imie") + " " + rs.getString("nazwisko");
            employeeList.add(emp);
        }
        employeeBox.setItems(employeeList);
    }

    private void loadData() {
        taskList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = """
                SELECT z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet,
                       z.data_rozpoczecia, z.data_zakonczenia, pk.nazwa AS produkt, z.kierunek,
                       CONCAT(pr.imie, ' ', pr.nazwisko) AS pracownik
                FROM zadania z
                JOIN statusy s ON z.id_statusu = s.id_statusu
                JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
                JOIN pracownicy pr ON z.id_pracownika = pr.id_pracownika
                JOIN produkty pk ON z.id_produktu = pk.id_produktu
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id_zadania"),
                        rs.getString("nazwa"),
                        rs.getString("status"),
                        rs.getString("priorytet"),
                        rs.getString("data_rozpoczecia"),
                        rs.getString("produkt"),
                        rs.getString("kierunek")
                );
                task.setEndDate(rs.getString("data_zakonczenia"));
                task.setAssignedTo(rs.getString("pracownik"));
                taskList.add(task);
            }
            taskTable.setItems(taskList);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania zadań", e);
            showAlert("Błąd ładowania danych", "Nie udało się załadować listy zadań");
        }
    }

    @FXML
    private void handleAddTask() {
        if (!validateForm()) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int statusId = getIdFromTable(conn, "statusy", statusBox.getValue());
            int priorityId = getIdFromTable(conn, "priorytety", priorityBox.getValue());
            int productId = getIdFromTable(conn, "produkty", productBox.getValue());
            int employeeId = Integer.parseInt(employeeBox.getValue().split(":")[0]);

            String sql = """
                INSERT INTO zadania (id_pracownika, nazwa, id_statusu, id_priorytetu, data_rozpoczecia, data_zakonczenia, id_produktu, kierunek)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.setString(2, nameField.getText());
            stmt.setInt(3, statusId);
            stmt.setInt(4, priorityId);
            stmt.setInt(5, productId);
            stmt.setDate(6, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(7, Date.valueOf(endDatePicker.getValue()));
            stmt.executeUpdate();

            loadData();
            clearFields();
            showAlert("Sukces", "Zadanie zostało dodane");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd dodawania zadania", e);
            showAlert("Błąd", "Nie udało się dodać zadania");
        }
    }

    @FXML
    private void handleEditTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Błąd", "Wybierz zadanie do edycji");
            return;
        }

        if (!validateForm()) return;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int statusId = getIdFromTable(conn, "statusy", statusBox.getValue());
            int priorityId = getIdFromTable(conn, "priorytety", priorityBox.getValue());
            int productId = getIdFromTable(conn, "produkty", productBox.getValue());
            int employeeId = Integer.parseInt(employeeBox.getValue().split(":")[0]);

            String sql = """
                UPDATE zadania
                SET nazwa = ?, id_statusu = ?, id_priorytetu = ?, 
                    data_rozpoczecia = ?, data_zakonczenia = ?, id_pracownika = ?, id_produktu = ?, kierunek = ?
                WHERE id_zadania = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setInt(2, statusId);
            stmt.setInt(3, priorityId);
            stmt.setInt(4, productId);
            stmt.setDate(5, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(6, Date.valueOf(endDatePicker.getValue()));
            stmt.setInt(7, employeeId);
            stmt.setInt(8, selected.getId());
            stmt.executeUpdate();

            loadData();
            clearFields();
            showAlert("Sukces", "Zadanie zostało zaktualizowane");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd edycji zadania", e);
            showAlert("Błąd", "Nie udało się zaktualizować zadania");
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Błąd", "Wybierz zadanie do usunięcia");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM zadania WHERE id_zadania = ?");
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();

            loadData();
            clearFields();
            showAlert("Sukces", "Zadanie zostało usunięte");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd usuwania zadania", e);
            showAlert("Błąd", "Nie udało się usunąć zadania");
        }
    }

    private boolean validateForm() {
        if (nameField.getText().isEmpty() || statusBox.getValue() == null ||
                priorityBox.getValue() == null || employeeBox.getValue() == null ||
                startDatePicker.getValue() == null || endDatePicker.getValue() == null) {

            showAlert("Błąd", "Wypełnij wszystkie pola formularza");
            return false;
        }
        return true;
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

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        statusBox.getSelectionModel().clearSelection();
        priorityBox.getSelectionModel().clearSelection();
        productBox.getSelectionModel().clearSelection();
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
            showAlert("Błąd", "Nie udało się przejść do dashboardu");
        }
    }
    private void applyTheme(String theme) {
        Scene scene = taskRoot.getScene();
        if (scene == null) return;

        scene.getStylesheets().clear();

        String cssFile = switch (theme) {
            case "Jasny" -> "/styles/themes/light.css";
            case "Ciemny" -> "/styles/themes/dark.css";
            default -> "/styles/themes/default.css";
        };

        URL cssUrl = getClass().getResource(cssFile);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private void applyFontSize(double size) {
        taskRoot.getScene().getRoot().setStyle("-fx-font-size: " + (int) size + "px;");
    }
}