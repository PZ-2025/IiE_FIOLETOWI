package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static javafx.beans.binding.IntegerExpression.integerExpression;

public class ManagerUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> imieColumn;
    @FXML private TableColumn<User, String> nazwiskoColumn;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mapowanie kolumn na pola w klasie User
        imieColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNazwisko()));
        loginColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLogin()));
        roleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));

        // Tutaj wstaw przykładowe dane, albo później ładowanie z bazy/danych
        userList.addAll(
                new User(1, "Anna", "Nowak", "anna", "haslo", 3000, 1, 2, "Pracownik"),
                new User(2, "Jan", "Kowalski", "jan", "haslo", 3500, 1, 3, "Kierownik"),
                new User(3, "Piotr", "Wiśniewski", "piotr", "haslo", 4000, 2, 1, "Admin")
        );

        userTable.setItems(userList);
    }

    @FXML
    private void changeRole() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Przykładowa zmiana roli na potrzeby demo
            String currentRole = selected.getRole();
            if ("Pracownik".equalsIgnoreCase(currentRole)) {
                selected.setRole("Kierownik");
            } else if ("Kierownik".equalsIgnoreCase(currentRole)) {
                selected.setRole("Admin");
            } else {
                selected.setRole("Pracownik");
            }
            userTable.refresh();
        }
    }

    @FXML
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userList.remove(selected);
        }
    }
}
