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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    @FXML protected TextField usernameField;
    @FXML protected PasswordField passwordField;
    @FXML
    public ComboBox<Role> roleComboBox;
    @FXML
    public ComboBox<Group> groupComboBox;
    @FXML protected TextField firstNameField;
    @FXML protected TextField lastNameField;
    @FXML protected TextField salaryField;
    @FXML private VBox userRoot;

    public ObservableList<Role> roles = FXCollections.observableArrayList();
    public ObservableList<Group> groups = FXCollections.observableArrayList();
    private User selectedUserToEdit = null;

    @FXML
    public void initialize() {
        userRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSize());
            }
        });

        imieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNazwisko()));
        loginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        placaColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPlaca()).asObject());
        rolaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

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

        double colWidth = 1.0 / 5;
        imieColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        nazwiskoColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        loginColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        placaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));
        rolaColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(colWidth));

        configureFieldsByRole();

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

        User currentUser = UserSession.getInstance().getUser();
        if (currentUser.isManager()) {
            createUserButton.setVisible(false);
        }
        Platform.runLater(() -> {
            userRoot.requestFocus();
        });
    }

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
                User user = new User(
                        rs.getInt("id_pracownika"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("login"),
                        rs.getString("haslo"),
                        rs.getDouble("placa"),
                        rs.getInt("id_grupy"),
                        rs.getInt("id_roli"),
                        rs.getString("nazwa_roli")
                );
                usersList.add(user);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania danych z bazy", e);
            AlertUtils.showError("Błąd ładowania danych z bazy danych.");
        }

        usersTable.setItems(usersList);
    }

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

    @FXML
    void createUser() {
        if (selectedUserToEdit != null) {
            updateUser();
        } else {
            addNewUser();
        }
    }

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

        double placa;
        try {
            placa = Double.parseDouble(placaStr);
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
            placa = Double.parseDouble(placaStr);
            if (placa < 0) {
                AlertUtils.showError("Płaca nie może być mniejsza niż 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Nieprawidłowa wartość płacy.");
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

    @FXML
    private void handleUpdateUser() {
        if (selectedUserToEdit == null) {
            AlertUtils.showError("Wybierz użytkownika z tabeli do edycji!");
            return;
        }
        updateUser();
    }

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

    private void applyFontSize(double size) {
        userRoot.getScene().getRoot().setStyle("-fx-font-size: " + (int) size + "px;");
    }
}