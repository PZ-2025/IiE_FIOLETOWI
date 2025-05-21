package com.example.projekt;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserManagementControllerTest {
    public static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    public static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private UserManagementController controller;

    @BeforeEach
    void setUp() throws Exception {
        new JFXPanel();

        controller = new UserManagementController();

        setPrivateField(controller, "usernameField", new TextField("testUser"));
        setPrivateField(controller, "passwordField", new PasswordField());
        setPrivateField(controller, "firstNameField", new TextField("Jan"));
        setPrivateField(controller, "lastNameField", new TextField("Kowalski"));
        setPrivateField(controller, "salaryField", new TextField("5500"));
        setPrivateField(controller, "roleComboBox", new ComboBox<Role>());
        setPrivateField(controller, "groupComboBox", new ComboBox<Group>());

        ComboBox<Role> roleComboBox = (ComboBox<Role>) getPrivateField(controller, "roleComboBox");
        ComboBox<Group> groupComboBox = (ComboBox<Group>) getPrivateField(controller, "groupComboBox");

        Role testRole = new Role(1, "Admin");
        Group testGroup = new Group(2, "IT");

        roleComboBox.getItems().add(testRole);
        groupComboBox.getItems().add(testGroup);
        roleComboBox.setValue(testRole);
        groupComboBox.setValue(testGroup);
    }

    @Test
    void testCreateUser_ValidData_ShouldInsertUser() {
        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());
        doNothing().when(controller).loadUsersFromDatabase();

        controller.createUser();

        verify(controller, never()).showAlert("Błąd podczas dodawania użytkownika.");
    }

    @Test
    void testCreateUser_InvalidSalary_ShouldShowError() throws Exception {
        TextField salaryField = (TextField) getPrivateField(controller, "salaryField");
        salaryField.setText("nie liczba");

        ((TextField) getPrivateField(controller, "usernameField")).setText("testUser");
        ((PasswordField) getPrivateField(controller, "passwordField")).setText("password123");
        ((TextField) getPrivateField(controller, "firstNameField")).setText("Jan");
        ((TextField) getPrivateField(controller, "lastNameField")).setText("Kowalski");
        ((ComboBox<Role>) getPrivateField(controller, "roleComboBox")).setValue(new Role(1, "Admin"));
        ((ComboBox<Group>) getPrivateField(controller, "groupComboBox")).setValue(new Group(2, "IT"));

        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());

        controller.createUser();

        verify(controller).showAlert("Nieprawidłowa wartość płacy.");
    }

    @Test
    void testCreateUser_MissingFields_ShouldShowError() throws Exception {
        TextField firstNameField = (TextField) getPrivateField(controller, "firstNameField");
        firstNameField.setText("");

        controller = spy(controller);
        doNothing().when(controller).showAlert(anyString());

        controller.createUser();

        verify(controller).showAlert("Wszystkie pola muszą być wypełnione!");
    }

}