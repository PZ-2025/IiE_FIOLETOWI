package com.example.projekt;

import javafx.application.Platform;
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
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontroler zarządzający interfejsem użytkownika do zarządzania użytkownikami systemu.
 * Obsługuje operacje CRUD na użytkownikach, w tym tworzenie, edycję i wyświetlanie listy użytkowników.
 */
public class UserManagementController {

    private static final Logger LOGGER = Logger.getLogger(UserManagementController.class.getName());
    private static final String DASHBOARD_VIEW_PATH = "/com/example/projekt/dashboard.fxml";
    private static final String DASHBOARD_TITLE = "Dashboard";

    @FXML private Button createUserButton;
    @FXML private Label adminLabel;
    @FXML private Label managerLabel;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> imieColumn;
    @FXML private TableColumn<User, String> nazwiskoColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, Double> placaColumn;
    @FXML private TableColumn<User, String> rolaColumn;
    @FXML private TableColumn<User, String> grupaColumn;
    @FXML protected TextField usernameField;
    @FXML protected PasswordField passwordField;
    @FXML public ComboBox<Role> roleComboBox;
    @FXML public ComboBox<Group> groupComboBox;
    @FXML protected TextField firstNameField;
    @FXML protected TextField lastNameField;
    @FXML protected TextField salaryField;
    @FXML private Button deleteUserButton;
    @FXML private VBox userRoot;

    public ObservableList<Role> roles = FXCollections.observableArrayList();
    public ObservableList<Group> groups = FXCollections.observableArrayList();
    private User selectedUserToEdit = null;

    /**
     * Metoda inicjalizująca kontroler. Konfiguruje tabelę użytkowników, ładuje dane
     * i ustawia podstawowe właściwości interfejsu użytkownika.
     */
    @FXML
    public void initialize() {
        userRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSizeLabel());
            }
        });

        // Konfiguracja wiązań danych w kolumnach tabeli
        imieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNazwisko()));
        loginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        placaColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPlaca()).asObject());
        rolaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        grupaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroup()));

        // Formatowanie wyświetlania wartości w kolumnie płacy
        placaColumn.setCellFactory(column -> new TableCell<User, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f zł", item));
                }
            }
        });

        roles = loadRolesFromDatabase();
        groups = loadGroupsFromDatabase();
        roleComboBox.setItems(roles);
        groupComboBox.setItems(groups);
        loadUsersFromDatabase();

        // Ustawienie proporcjonalnych szerokości kolumn
        double colWidth = 1.0 / 6;
        imieColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        nazwiskoColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        loginColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        placaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        rolaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        grupaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));

        // Konfiguracja widoczności pól w zależności od roli użytkownika
        configureFieldsByRole();

        // Obsługa kliknięcia wiersza tabeli
        usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectedUserToEdit = row.getItem();
                    fillFormForEditing(selectedUserToEdit);
                }
            });
            return row;
        });

        // Ukrycie przycisku tworzenia użytkownika dla menedżerów
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser.isManager()) {
            createUserButton.setVisible(false);
        }

        // Ustawienie focusa na głównym panelu
        Platform.runLater(() -> {
            userRoot.requestFocus();
        });
    }

    /**
     * Wypełnia formularz danymi wybranego użytkownika do edycji.
     *
     * @param user Użytkownik, którego dane mają zostać wypełnione w formularzu
     */
    private void fillFormForEditing(User user) {
        usernameField.setText(user.getLogin());
        firstNameField.setText(user.getImie());
        lastNameField.setText(user.getNazwisko());
        salaryField.setText(String.format("%.2f", user.getPlaca()));

        roleComboBox.getSelectionModel().select(
                roles.stream().filter(r -> r.getId() == user.getIdRoli()).findFirst().orElse(null)
        );
        groupComboBox.getSelectionModel().select(
                groups.stream().filter(g -> g.getId() == user.getIdGrupy()).findFirst().orElse(null)
        );
    }

    /**
     * Ładuje listę użytkowników z bazy danych i wyświetla ją w tabeli.
     */
    public void loadUsersFromDatabase() {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id_pracownika, p.imie, p.nazwisko, p.login, p.haslo, p.placa, p.id_grupy, p.id_roli,
                 r.nazwa AS nazwa_roli,
                 g.nazwa AS nazwa_grupy
                 FROM pracownicy p
                 JOIN role r ON p.id_roli = r.id_roli
                 JOIN grupy g ON p.id_grupy = g.id_grupy
        """;

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_pracownika"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("login"),
                        rs.getString("haslo"),
                        rs.getDouble("placa"),
                        rs.getInt("id_grupy"),
                        rs.getInt("id_roli"),
                        rs.getString("nazwa_roli"),
                        rs.getString("nazwa_grupy")
                );
                usersList.add(user);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania danych z bazy", e);
            AlertUtils.showError("Błąd ładowania danych z bazy danych.");
        }

        usersTable.setItems(usersList);
    }

    /**
     * Ładuje listę ról z bazy danych.
     *
     * @return Lista ról dostępnych w systemie
     */
    private ObservableList<Role> loadRolesFromDatabase() {
        ObservableList<Role> roles = FXCollections.observableArrayList();
        String sql = "SELECT id_roli, nazwa FROM role";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(new Role(rs.getInt("id_roli"), rs.getString("nazwa")));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania ról", e);
            AlertUtils.showError("Nie udało się załadować ról.");
        }

        return roles;
    }

    /**
     * Ładuje listę grup z bazy danych.
     *
     * @return Lista grup dostępnych w systemie
     */
    private ObservableList<Group> loadGroupsFromDatabase() {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String sql = "SELECT id_grupy, nazwa FROM grupy";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groups.add(new Group(rs.getInt("id_grupy"), rs.getString("nazwa")));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania grup", e);
            AlertUtils.showError("Nie udało się załadować grup.");
        }

        return groups;
    }

    /**
     * Konfiguruje widoczność pól formularza w zależności od roli aktualnie zalogowanego użytkownika.
     */
    private void configureFieldsByRole() {
        User currentUser = UserSession.getInstance().getUser();

        boolean isAdmin = currentUser.isAdmin();
        boolean isManager = currentUser.isManager();

        usernameField.setVisible(isAdmin);
        passwordField.setVisible(isAdmin);
        firstNameField.setVisible(isAdmin);
        lastNameField.setVisible(isAdmin);
        adminLabel.setVisible(isAdmin);

        roleComboBox.setVisible(true);
        salaryField.setVisible(true);
        groupComboBox.setVisible(true);
        managerLabel.setVisible(isManager);
    }

    /**
     * Obsługuje zdarzenie tworzenia nowego użytkownika lub aktualizacji istniejącego.
     */
    @FXML
    void createUser() {
        addNewUser();
        selectedUserToEdit = null;
    }

    @FXML
    void editUser() {
        if (selectedUserToEdit != null) {
            updateUser();
            selectedUserToEdit = null;
        }
    }


    /**
     * Dodaje nowego użytkownika do bazy danych na podstawie danych z formularza.
     */
    protected void addNewUser() {
        String login = usernameField.getText();
        String haslo = passwordField.getText();
        String imie = firstNameField.getText();
        String nazwisko = lastNameField.getText();
        String placaStr = salaryField.getText();
        Role selectedRole = roleComboBox.getValue();
        Group selectedGroup = groupComboBox.getValue();

        if (login.isEmpty() || haslo.isEmpty() || imie.isEmpty() || nazwisko.isEmpty() || placaStr.isEmpty() || selectedRole == null || selectedGroup == null) {
            AlertUtils.showError("Wszystkie pola muszą być wypełnione!");
            return;
        }

        if (!PasswordValidator.isPasswordValid(haslo)) {
            AlertUtils.showError(PasswordValidator.getPasswordRequirementsMessage());
            return;
        }
        if (isLoginTaken(login, null)) {
            AlertUtils.showError("Login jest już w użyciu przez innego użytkownika.");
            return;
        }

        double placa;
        try {
            placa = Double.parseDouble(placaStr.replace(",", "."));
            if (placa < 0) {
                AlertUtils.showError("Płaca nie może być mniejsza niż 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Nieprawidłowa wartość płacy.");
            return;
        }


        String hashedPassword = PasswordHasher.hashPassword(haslo, PasswordHasher.generateSalt());

        String sql = "INSERT INTO pracownicy (imie, nazwisko, login, haslo, placa, id_roli, id_grupy) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setString(3, login);
            stmt.setString(4, hashedPassword);
            stmt.setDouble(5, placa);
            stmt.setInt(6, selectedRole.getId());
            stmt.setInt(7, selectedGroup.getId());

            stmt.executeUpdate();
            AlertUtils.showAlert("Użytkownik został dodany!");
            clearForm();
            loadUsersFromDatabase();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy dodawaniu użytkownika", e);
            AlertUtils.showError("Błąd podczas dodawania użytkownika.");
        }
    }

    /**
     * Aktualizuje dane istniejącego użytkownika w bazie danych.
     */
    private void updateUser() {
        String login = usernameField.getText();
        String haslo = passwordField.getText();
        String imie = firstNameField.getText();
        String nazwisko = lastNameField.getText();
        String placaStr = salaryField.getText();
        Role selectedRole = roleComboBox.getValue();
        Group selectedGroup = groupComboBox.getValue();

        if (login.isEmpty() || imie.isEmpty() || nazwisko.isEmpty() || placaStr.isEmpty() || selectedRole == null || selectedGroup == null) {
            AlertUtils.showError("Wszystkie pola muszą być wypełnione!");
            return;
        }

        if (!haslo.isEmpty()) {
            if (!PasswordValidator.isPasswordValid(haslo)) {
                AlertUtils.showError(PasswordValidator.getPasswordRequirementsMessage());
                return;
            }
            haslo = PasswordHasher.hashPassword(haslo, PasswordHasher.generateSalt());
        } else {
            haslo = selectedUserToEdit.getHaslo();
        }

        double placa;
        try {
            placa = Double.parseDouble(placaStr.replace(",", "."));
            if (placa < 0) {
                AlertUtils.showError("Płaca nie może być mniejsza niż 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Nieprawidłowa wartość płacy.");
            return;
        }

        if (isLoginTaken(login, selectedUserToEdit.getId())) {
            AlertUtils.showError("Login jest już w użyciu przez innego użytkownika.");
            return;
        }

        String sql = """
            UPDATE pracownicy
            SET imie = ?, nazwisko = ?, login = ?, haslo = ?, placa = ?, id_roli = ?, id_grupy = ?
            WHERE id_pracownika = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setString(3, login);
            stmt.setString(4, haslo);
            stmt.setDouble(5, placa);
            stmt.setInt(6, selectedRole.getId());
            stmt.setInt(7, selectedGroup.getId());
            stmt.setInt(8, selectedUserToEdit.getId());

            stmt.executeUpdate();
            AlertUtils.showAlert("Dane użytkownika zostały zaktualizowane!");
            clearForm();
            loadUsersFromDatabase();
            selectedUserToEdit = null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy aktualizacji użytkownika", e);
            AlertUtils.showError("Błąd podczas aktualizacji użytkownika.");
        }
    }

    private boolean isLoginTaken(String login, Integer excludeUserId) {
        String sql = "SELECT COUNT(*) FROM pracownicy WHERE login = ?";

        if (excludeUserId != null) {
            sql += " AND id_pracownika != ?";
        }

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            if (excludeUserId != null) {
                stmt.setInt(2, excludeUserId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy sprawdzaniu loginu", e);
        }

        return false;
    }


    /**
     * Czyści formularz zarządzania użytkownikami.
     */
    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        salaryField.clear();
        roleComboBox.setValue(null);
        groupComboBox.setValue(null);
        selectedUserToEdit = null;
    }

    /**
     * Przechodzi do widoku dashboardu.
     *
     * @param event Zdarzenie wywołujące metodę
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
            stage.setScene(new Scene(dashboardRoot));
            stage.show();

        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania dashboardu", e);
            AlertUtils.showError("Nie można załadować dashboardu.");
        }
    }

    /**
     * Obsługuje aktualizację danych użytkownika.
     */
    @FXML
    private void handleUpdateUser() {
        if (selectedUserToEdit == null) {
            AlertUtils.showError("Wybierz użytkownika z tabeli do edycji!");
            return;
        }
        updateUser();
    }

    /**
     * Stosuje wybrany motyw do interfejsu użytkownika.
     *
     * @param theme Nazwa motywu do zastosowania
     */
    private void applyTheme(String theme) {
        Scene scene = userRoot.getScene();
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

    /**
     * Stosuje wybrany rozmiar czcionki do interfejsu użytkownika.
     *
     * @param label Etykieta określająca rozmiar czcionki
     */
    private void applyFontSize(String label) {
        Scene scene = userRoot.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css -> css.contains("/styles/fonts/"));

        String fontCss = switch (label.toLowerCase()) {
            case "mała" -> "/com/example/projekt/styles/fonts/small.css";
            case "duża" -> "/com/example/projekt/styles/fonts/large.css";
            default -> "/com/example/projekt/styles/fonts/medium.css";
        };

        UserSession.setCurrentFontSize(fontCss); // aktualizacja sesji

        URL fontUrl = getClass().getResource(fontCss);
        if (fontUrl != null) {
            scene.getStylesheets().add(fontUrl.toExternalForm());
        }
    }
    @FXML
    private void handleDeleteUser() {
        if (selectedUserToEdit == null) {
            AlertUtils.showError("Wybierz użytkownika do usunięcia.");
            return;
        }

        int userId = selectedUserToEdit.getId();

        try (Connection conn = DatabaseConnector.connect()) {

            String checkTasksSql = "SELECT COUNT(*) FROM zadania WHERE id_pracownika = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkTasksSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int taskCount = rs.getInt(1);

                if (taskCount > 0) {
                    showTaskReassignmentDialog(conn, userId);
                } else {
                    deleteUserFromDatabase(conn, userId);
                    AlertUtils.showAlert("Użytkownik został usunięty.");
                    loadUsersFromDatabase();
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy usuwaniu użytkownika", e);
            AlertUtils.showError("Błąd podczas usuwania użytkownika.");
        }
    }
    private void showTaskReassignmentDialog(Connection conn, int userId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Użytkownik ma przypisane zadania");
        alert.setHeaderText("Co chcesz zrobić z zadaniami przypisanymi do tego użytkownika?");
        alert.setContentText("Wybierz jedną z opcji:");

        ButtonType deleteTasks = new ButtonType("Usuń zadania");
        ButtonType reassignTasks = new ButtonType("Przypisz innemu pracownikowi");
        ButtonType cancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(deleteTasks, reassignTasks, cancel);

        alert.showAndWait().ifPresent(response -> {
            try {
                if (response == deleteTasks) {
                    deleteTasksForUser(conn, userId);
                    deleteUserFromDatabase(conn, userId);
                    AlertUtils.showAlert("Użytkownik i jego zadania zostały usunięte.");
                } else if (response == reassignTasks) {
                    reassignTasksToAnotherUser(conn, userId);
                }
                loadUsersFromDatabase();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Błąd podczas operacji na zadaniach", e);
                AlertUtils.showError("Wystąpił błąd podczas usuwania lub przypisywania zadań.");
            }
        });
    }
    private void deleteTasksForUser(Connection conn, int userId) throws SQLException {
        String deleteTasksSql = "DELETE FROM zadania WHERE id_pracownika = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteTasksSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    private void deleteUserFromDatabase(Connection conn, int userId) throws SQLException {
        String deleteUserSql = "DELETE FROM pracownicy WHERE id_pracownika = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private void reassignTasksToAnotherUser(Connection conn, int oldUserId) throws SQLException {
        List<Task> tasks = getTasksByUserId(conn, oldUserId);
        List<User> availableUsers = usersTable.getItems().filtered(u -> u.getId() != oldUserId);

        if (tasks.isEmpty()) {
            deleteUserFromDatabase(conn, oldUserId);
            AlertUtils.showAlert("Użytkownik usunięty. Nie miał przypisanych zadań.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Przypisz zadania");
        dialog.setHeaderText("Wybierz nowego pracownika dla każdego zadania");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));

        List<ComboBox<User>> comboBoxes = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Label taskLabel = new Label(task.getNazwa());

            ComboBox<User> comboBox = new ComboBox<>(FXCollections.observableArrayList(availableUsers));
            comboBox.setPromptText("Wybierz pracownika");

            comboBoxes.add(comboBox);
            grid.addRow(i, taskLabel, comboBox);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    for (int i = 0; i < tasks.size(); i++) {
                        User selectedUser = comboBoxes.get(i).getValue();
                        if (selectedUser == null) {
                            AlertUtils.showError("Nie przypisałeś pracownika do zadania: " + tasks.get(i).getNazwa());
                            return;
                        }

                        try (PreparedStatement stmt = conn.prepareStatement(
                                "UPDATE zadania SET id_pracownika = ? WHERE id_zadania = ?")) {
                            stmt.setInt(1, selectedUser.getId());
                            stmt.setInt(2, tasks.get(i).getId());
                            stmt.executeUpdate();
                        }
                    }

                    deleteUserFromDatabase(conn, oldUserId);
                    AlertUtils.showAlert("Zadania zostały przypisane, a użytkownik usunięty.");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Błąd przy przypisywaniu zadań", e);
                    AlertUtils.showError("Nie udało się przypisać zadań.");
                }
            }
        });
    }

    private List<Task> getTasksByUserId(Connection conn, int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT id_zadania, nazwa FROM zadania WHERE id_pracownika = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_zadania");
                    String nazwa = rs.getString("nazwa");

                    tasks.add(new Task(id, nazwa, "", "", "", "", "", "", "", ""));
                }
            }
        }

        return tasks;
    }
}