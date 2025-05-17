package com.example.projekt;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementController {

    /**
     * Kontroler odpowiedzialny za zarządzanie użytkownikami w aplikacji przez administratora.
     * <p>
     * Obsługuje wyświetlanie tabeli użytkowników, dodawanie nowych użytkowników do bazy danych,
     * oraz inicjalizację pól wyboru (ComboBox) dla ról i grup.
     * @author KrzysztofDrozda
     * @version 1.0
     * @since 2025-04-25
     */

    private static final Logger LOGGER = Logger.getLogger(UserManagementController.class.getName());
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;
    private static final String DASHBOARD_VIEW_PATH = "/com/example/projekt/dashboard.fxml";
    private static final String DASHBOARD_TITLE = "Dashboard";

    @FXML
    TableView<User> usersTable;
    @FXML
    TableColumn<User, String> imieColumn;
    @FXML
    TableColumn<User, String> nazwiskoColumn;
    @FXML
    TableColumn<User, String> loginColumn;
    @FXML
    TableColumn<User, Double> placaColumn;
    @FXML
    TableColumn<User, String> rolaColumn;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    ComboBox<Role> roleComboBox;
    @FXML
    ComboBox<Group> groupComboBox;
    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField salaryField;
    /**
     * Inicjalizuje kontroler zarządzania użytkownikami.
     * Konfiguruje kolumny tabeli, ładuje role, grupy i użytkowników z bazy danych.
     */
    @FXML
    public void initialize() {
        imieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNazwisko()));
        loginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        placaColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPlaca()).asObject());
        rolaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        roleComboBox.setItems(loadRolesFromDatabase());
        groupComboBox.setItems(loadGroupsFromDatabase());
        loadUsersFromDatabase();

        double colWidth = 1.0 / 5;
        imieColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        nazwiskoColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        loginColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        placaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        rolaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
    }
    /**
     * Ładuje listę użytkowników z bazy danych i wyświetla ich w tabeli.
     * W przypadku błędu połączenia z bazą, wyświetla komunikat o błędzie.
     */
    public void loadUsersFromDatabase() {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id_pracownika, p.imie, p.nazwisko, p.login, p.haslo, p.placa, p.id_grupy, p.id_roli,
                   r.nazwa AS nazwa_roli
            FROM pracownicy p
            JOIN role r ON p.id_roli = r.id_roli
        """;

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_pracownika");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                String login = rs.getString("login");
                String haslo = rs.getString("haslo");
                double placa = rs.getDouble("placa");
                int idGrupy = rs.getInt("id_grupy");
                int idRoli = rs.getInt("id_roli");
                String nazwaRoli = rs.getString("nazwa_roli");

                User user = new User(id, imie, nazwisko, login, haslo, placa, idGrupy, idRoli, nazwaRoli);
                usersList.add(user);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania danych z bazy", e);
            showAlert("Błąd ładowania danych z bazy danych.");
        }

        usersTable.setItems(usersList);
    }
    /**
     * Ładuje role z bazy danych do ComboBoxa.
     *
     * @return lista ról dostępnych w systemie
     */
    private ObservableList<Role> loadRolesFromDatabase() {
        ObservableList<Role> roles = FXCollections.observableArrayList();
        String sql = "SELECT id_roli, nazwa FROM role";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_roli");
                String nazwa = rs.getString("nazwa");
                roles.add(new Role(id, nazwa));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania ról", e);
        }

        return roles;
    }
    /**
     * Ładuje grupy z bazy danych do ComboBoxa.
     *
     * @return lista grup dostępnych w systemie
     */
    private ObservableList<Group> loadGroupsFromDatabase() {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String sql = "SELECT id_grupy, nazwa FROM grupy";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_grupy");
                String nazwa = rs.getString("nazwa");
                groups.add(new Group(id, nazwa));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania grup", e);
        }

        return groups;
    }
    /**
     * Obsługuje zdarzenie utworzenia nowego użytkownika.
     * Waliduje dane formularza, zapisuje użytkownika do bazy danych
     * i odświeża widok tabeli. W razie błędów wyświetla odpowiedni alert.
     */
    @FXML
    void createUser() {
        String login = usernameField.getText();
        String haslo = passwordField.getText();
        String imie = firstNameField.getText();
        String nazwisko = lastNameField.getText();
        String placaStr = salaryField.getText();
        Role selectedRole = roleComboBox.getValue();
        Group selectedGroup = groupComboBox.getValue();

        if (login.isEmpty() || haslo.isEmpty() || imie.isEmpty() || nazwisko.isEmpty() || placaStr.isEmpty() || selectedRole == null || selectedGroup == null) {
            showAlert("Wszystkie pola muszą być wypełnione!");
            return;
        }

        double placa;
        try {
            placa = Double.parseDouble(placaStr);
        } catch (NumberFormatException e) {
            showAlert("Nieprawidłowa wartość płacy.");
            return;
        }

        String sql = "INSERT INTO pracownicy (imie, nazwisko, login, haslo, placa, id_roli, id_grupy) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setString(3, login);
            stmt.setString(4, haslo);
            stmt.setDouble(5, placa);
            stmt.setInt(6, selectedRole.getId());
            stmt.setInt(7, selectedGroup.getId());

            stmt.executeUpdate();
            showAlert("Użytkownik został dodany!");
            clearForm();
            loadUsersFromDatabase();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy dodawaniu użytkownika", e);
            showAlert("Błąd podczas dodawania użytkownika.");
        }
    }
    /**
     * Czyści wszystkie pola formularza dodawania użytkownika.
     * Resetuje pola tekstowe oraz wybory ComboBoxów.
     */
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        salaryField.clear();
        roleComboBox.setValue(null);
        groupComboBox.setValue(null);
    }
    /**
     * Przełącza widok do ekranu głównego (dashboard).
     * Przekazuje dane zalogowanego użytkownika do kontrolera dashboardu.
     *
     * @param event zdarzenie wywołane przez użytkownika (np. kliknięcie przycisku)
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DASHBOARD_VIEW_PATH));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(UserSession.getInstance().getUser());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(DASHBOARD_TITLE);
            stage.setScene(new Scene(dashboardRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
            stage.show();

        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania dashboardu", e);
            showAlert("Nie można załadować dashboardu.");
        }
    }

    /**
     * Wyświetla prosty alert informacyjny z podaną wiadomością.
     *
     * @param message treść komunikatu do wyświetlenia
     */
    void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}