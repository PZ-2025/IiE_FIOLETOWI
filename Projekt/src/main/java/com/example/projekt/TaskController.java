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
    @FXML private TableColumn<Task, String> startDateColumn;
    @FXML private TableColumn<Task, String> endDateColumn;
    @FXML private TableColumn<Task, String> assignedColumn;
    @FXML private TableColumn<Task, String> commentColumn;
    @FXML private TableColumn<Task, String> quantityColumn;
    @FXML private TableColumn<Task, String> assignedproductColumn;
    @FXML private TableColumn<Task, String> directionColumn;

    @FXML private TextField nameField;
    @FXML private TextField commentField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> productBox;
    @FXML private ComboBox<String> directionBox;
    @FXML private ComboBox<String> employeeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private BorderPane taskRoot;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private ObservableList<String> statusList = FXCollections.observableArrayList();
    private ObservableList<String> priorityList = FXCollections.observableArrayList();
    private ObservableList<String> productList = FXCollections.observableArrayList();
    private ObservableList<String> directionList = FXCollections.observableArrayList();
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
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("komentarz"));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<>("pracownik"));
        assignedproductColumn.setCellValueFactory(new PropertyValueFactory<>("produkt"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("ilosc"));
        directionColumn.setCellValueFactory(new PropertyValueFactory<>("kierunek"));

        // Ustawienie proporcjonalnego rozkładu szerokości kolumn
        nameColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.25));
        statusColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        priorityColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        startDateColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        endDateColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        assignedColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        commentColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        assignedproductColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        quantityColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        directionColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
    }

    private void fillFormWithSelectedTask(Task task) {
        nameField.setText(task.getNazwa());
        commentField.setText(task.getKomentarz());
        statusBox.setValue(task.getStatus());
        priorityBox.setValue(task.getPriorytet());
        productBox.setValue(task.getProdukt());
        directionBox.setValue(task.getKierunek());
        quantityField.setText(task.getIlosc()); // Naprawiono - było getKierunek() zamiast getIlosc()

        // Znalezienie i ustawienie pracownika
        employeeBox.getItems().stream()
                .filter(emp -> emp.contains(task.getPracownik()))
                .findFirst()
                .ifPresent(employeeBox::setValue);

        // Ustawienie dat z obsługą błędów
        try {
            if (task.getData() != null && !task.getData().isEmpty()) {
                startDatePicker.setValue(LocalDate.parse(task.getData()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Błąd parsowania daty rozpoczęcia: " + task.getData(), e);
        }

        try {
            if (task.getKoniec() != null && !task.getKoniec().isEmpty()) {
                endDatePicker.setValue(LocalDate.parse(task.getKoniec()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Błąd parsowania daty zakończenia: " + task.getKoniec(), e);
        }
    }

    private void loadComboBoxes() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            loadStatuses(conn);
            loadPriorities(conn);
            loadProducts(conn);
            loadDirections(conn);
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

    private void loadDirections(Connection conn) throws SQLException {
        directionList.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT nazwa FROM kierunki");
        while (rs.next()) {
            directionList.add(rs.getString("nazwa"));
        }
        directionBox.setItems(directionList);
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
        String query = """
        SELECT z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet,
               z.data_rozpoczecia, z.data_zakonczenia, z.komentarz, z.ilosc,
               pk.nazwa AS produkt, k.nazwa AS kierunek,
               CONCAT(pr.imie, ' ', pr.nazwisko) AS pracownik
        FROM zadania z
        LEFT JOIN statusy s ON z.id_statusu = s.id_statusu
        LEFT JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
        LEFT JOIN pracownicy pr ON z.id_pracownika = pr.id_pracownika
        LEFT JOIN produkty pk ON z.id_produktu = pk.id_produktu
        LEFT JOIN kierunki k ON z.id_kierunku = k.id_kierunku
    """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id_zadania");
                String nazwa = rs.getString("nazwa");
                String status = rs.getString("status");
                String priorytet = rs.getString("priorytet");
                String dataRozpoczecia = rs.getString("data_rozpoczecia");
                String komentarz = rs.getString("komentarz");
                String ilosc = rs.getString("ilosc");
                String produkt = rs.getString("produkt");
                String kierunek = rs.getString("kierunek");
                String pracownik = rs.getString("pracownik");
                String dataZakonczenia = rs.getString("data_zakonczenia");

                Task task = new Task(
                        id,
                        nazwa,
                        status,
                        priorytet,
                        dataRozpoczecia,
                        produkt,
                        kierunek,
                        komentarz,
                        ilosc,
                        pracownik
                );

                task.setEndDate(dataZakonczenia);

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
            int directionId = getIdFromTable(conn, "kierunki", directionBox.getValue());
            int employeeId = Integer.parseInt(employeeBox.getValue().split(":")[0]);

            String sql = """
                INSERT INTO zadania (id_pracownika, nazwa, id_statusu, id_priorytetu, data_rozpoczecia, data_zakonczenia, komentarz, id_produktu, ilosc, id_kierunku)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.setString(2, nameField.getText());
            stmt.setInt(3, statusId);
            stmt.setInt(4, priorityId);
            stmt.setDate(5, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(6, Date.valueOf(endDatePicker.getValue()));
            stmt.setString(7, commentField.getText());
            stmt.setInt(8, productId);
            stmt.setString(9, quantityField.getText());
            stmt.setInt(10, directionId);
            stmt.executeUpdate();

            loadData();
            clearFields();
            showAlert("Sukces", "Zadanie zostało dodane");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd dodawania zadania", e);
            showAlert("Błąd", "Nie udało się dodać zadania: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Nieoczekiwany błąd podczas dodawania zadania", e);
            showAlert("Błąd", "Nieoczekiwany błąd: " + e.getMessage());
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
            // Dodano sprawdzanie null przed wywołaniem getIdFromTable
            Integer statusId = null, priorityId = null, productId = null, directionId = null, employeeId = null;

            if (statusBox.getValue() != null) {
                statusId = getIdFromTable(conn, "statusy", statusBox.getValue());
            } else {
                showAlert("Błąd", "Wybierz status zadania");
                return;
            }

            if (priorityBox.getValue() != null) {
                priorityId = getIdFromTable(conn, "priorytety", priorityBox.getValue());
            } else {
                showAlert("Błąd", "Wybierz priorytet zadania");
                return;
            }

            if (productBox.getValue() != null) {
                productId = getIdFromTable(conn, "produkty", productBox.getValue());
            } else {
                showAlert("Błąd", "Wybierz produkt");
                return;
            }

            if (directionBox.getValue() != null) {
                directionId = getIdFromTable(conn, "kierunki", directionBox.getValue());
            } else {
                showAlert("Błąd", "Wybierz kierunek");
                return;
            }

            if (employeeBox.getValue() != null) {
                employeeId = Integer.parseInt(employeeBox.getValue().split(":")[0]);
            } else {
                showAlert("Błąd", "Wybierz pracownika");
                return;
            }

            // Naprawiono SQL - dodano brakujący przecinek
            String sql = """
                UPDATE zadania
                SET nazwa = ?, id_statusu = ?, id_priorytetu = ?, 
                    data_rozpoczecia = ?, data_zakonczenia = ?, komentarz = ?, id_pracownika = ?, id_produktu = ?, ilosc = ?, id_kierunku = ?
                WHERE id_zadania = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setInt(2, statusId);
            stmt.setInt(3, priorityId);
            stmt.setDate(4, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(5, Date.valueOf(endDatePicker.getValue()));
            stmt.setString(6, commentField.getText());
            stmt.setInt(7, employeeId);
            stmt.setInt(8, productId);
            stmt.setString(9, quantityField.getText());
            stmt.setInt(10, directionId);
            stmt.setInt(11, selected.getId());
            stmt.executeUpdate();

            loadData();
            clearFields();
            showAlert("Sukces", "Zadanie zostało zaktualizowane");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd edycji zadania", e);
            showAlert("Błąd", "Nie udało się zaktualizować zadania: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Nieoczekiwany błąd podczas edycji zadania", e);
            showAlert("Błąd", "Nieoczekiwany błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Błąd", "Wybierz zadanie do usunięcia");
            return;
        }

        // Dodano potwierdzenie usunięcia
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Potwierdzenie");
        confirmAlert.setHeaderText("Czy na pewno chcesz usunąć to zadanie?");
        confirmAlert.setContentText("Zadanie: " + selected.getNazwa());

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
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
            showAlert("Błąd", "Nie udało się usunąć zadania: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().isEmpty()) {
            showAlert("Błąd", "Nazwa zadania nie może być pusta");
            return false;
        }

        if (statusBox.getValue() == null) {
            showAlert("Błąd", "Wybierz status zadania");
            return false;
        }

        if (priorityBox.getValue() == null) {
            showAlert("Błąd", "Wybierz priorytet zadania");
            return false;
        }

        if (productBox.getValue() == null) {
            showAlert("Błąd", "Wybierz produkt");
            return false;
        }

        if (directionBox.getValue() == null) {
            showAlert("Błąd", "Wybierz kierunek");
            return false;
        }

        if (employeeBox.getValue() == null) {
            showAlert("Błąd", "Wybierz pracownika");
            return false;
        }

        if (startDatePicker.getValue() == null) {
            showAlert("Błąd", "Wybierz datę rozpoczęcia");
            return false;
        }

        if (endDatePicker.getValue() == null) {
            showAlert("Błąd", "Wybierz datę zakończenia");
            return false;
        }

        return true;
    }

    private int getIdFromTable(Connection conn, String table, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new SQLException("Nazwa nie może być pusta dla tabeli: " + table);
        }

        String baseName = table.endsWith("y") || table.endsWith("i") ? table.substring(0, table.length() - 1) + "u" : table;
        String columnName = "id_" + baseName;

        String sql = "SELECT " + columnName + " FROM " + table + " WHERE nazwa = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name.trim());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
        throw new SQLException("Nie znaleziono: " + name + " w tabeli: " + table);
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
        commentField.clear();
        quantityField.clear();
        statusBox.getSelectionModel().clearSelection();
        priorityBox.getSelectionModel().clearSelection();
        productBox.getSelectionModel().clearSelection();
        directionBox.getSelectionModel().clearSelection();
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
            case "Jasny" -> "/com/example/projekt/styles/themes/light.css";
            case "Ciemny" -> "/com/example/projekt/styles/themes/dark.css";
            default -> "/com/example/projekt/styles/themes/default.css";
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