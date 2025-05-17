package com.example.projekt;

import com.example.projekt.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserManagementControllerTest {

    private UserManagementController controller;

    @BeforeEach
    void setUp() {
        new JFXPanel();

        controller = new UserManagementController();

        controller.usernameField = new TextField("testUser");
        controller.passwordField = new PasswordField();
        controller.passwordField.setText("password123");
        controller.firstNameField = new TextField("Jan");
        controller.lastNameField = new TextField("Kowalski");
        controller.salaryField = new TextField("5500");

        controller.roleComboBox = new ComboBox<>();
        controller.groupComboBox = new ComboBox<>();

        Role testRole = new Role(1, "Admin");
        Group testGroup = new Group(2, "IT");

        controller.roleComboBox.getItems().add(testRole);
        controller.groupComboBox.getItems().add(testGroup);
        controller.roleComboBox.setValue(testRole);
        controller.groupComboBox.setValue(testGroup);
    }

    @Test
    void testCreateUser_ValidData_ShouldInsertUser() {
        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());
        doNothing().when(controller).loadUsersFromDatabase();

        controller.createUser();

        // Sprawdzenie że nie było błędu
        verify(controller, never()).showAlert("Błąd podczas dodawania użytkownika.");
    }

    @Test
    void testCreateUser_InvalidSalary_ShouldShowError() {
        controller.salaryField.setText("nie liczba");

        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());

        controller.createUser();

        verify(controller).showAlert("Nieprawidłowa wartość płacy.");
    }

    @Test
    void testCreateUser_MissingFields_ShouldShowError() {
        controller.firstNameField.setText("");

        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());

        controller.createUser();

        verify(controller).showAlert("Wszystkie pola muszą być wypełnione!");
    }
}
