package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class ManagerUsersController {

    @FXML private TableView<User> employeeTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, Double> salaryColumn;
    @FXML private TableColumn<User, Integer> groupIdColumn;

    @FXML private TextField salaryField;
    @FXML private ComboBox<Group> groupComboBox;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Group> groups = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadUsers();
        loadGroups();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        firstNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getImie()));
        lastNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNazwisko()));
        loginColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLogin()));
        salaryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPlaca()).asObject());
        groupIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdGrupy()).asObject());

        employeeTable.setItems(users);
    }

    private void loadUsers() {
        users.clear();
        try (Connection conn = DatabaseConnector.connect()) {
            String sql = "SELECT p.*, r.nazwa as rola_nazwa FROM pracownicy p " +
                    "JOIN role r ON p.id_roli = r.id_roli";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
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
                        rs.getString("rola_nazwa")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGroups() {
        groupComboBox.getItems().clear();
        try (Connection conn = DatabaseConnector.connect()) {
            String sql = "SELECT * FROM grupy";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groups.add(new Group(rs.getInt("id_grupy"), rs.getString("nazwa")));
            }
            groupComboBox.setItems(groups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangeSalary() {
        User selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null || salaryField.getText().isEmpty()) {
            showAlert("Wybierz pracownika i wpisz nową płacę.");
            return;
        }

        try {
            double newSalary = Double.parseDouble(salaryField.getText());
            try (Connection conn = DatabaseConnector.connect()) {
                String sql = "UPDATE pracownicy SET placa = ? WHERE id_pracownika = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDouble(1, newSalary);
                stmt.setInt(2, selected.getId());
                stmt.executeUpdate();
                selected = new User(
                        selected.getId(), selected.getImie(), selected.getNazwisko(),
                        selected.getLogin(), selected.getHaslo(), newSalary,
                        selected.getIdGrupy(), selected.getIdRoli(), selected.getRole()
                );
                loadUsers(); // Odśwież dane
            }
        } catch (NumberFormatException e) {
            showAlert("Wprowadź poprawną liczbę dla płacy.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd aktualizacji płacy.");
        }
    }

    @FXML
    private void handleChangeGroup() {
        User selected = employeeTable.getSelectionModel().getSelectedItem();
        Group selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();
        if (selected == null || selectedGroup == null) {
            showAlert("Wybierz pracownika i grupę.");
            return;
        }

        try (Connection conn = DatabaseConnector.connect()) {
            String sql = "UPDATE pracownicy SET id_grupy = ? WHERE id_pracownika = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedGroup.getId());
            stmt.setInt(2, selected.getId());
            stmt.executeUpdate();
            loadUsers(); // Odśwież dane
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd aktualizacji grupy.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
