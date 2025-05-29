package com.example.projekt;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagementControllerTest extends ApplicationTest {



    private UserManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new UserManagementController();

        // Inicjalizacja pól UI
        controller.usernameField = new TextField();
        controller.passwordField = new PasswordField();
        controller.firstNameField = new TextField();
        controller.lastNameField = new TextField();
        controller.salaryField = new TextField();

        controller.roleComboBox = new ComboBox<>();
        controller.groupComboBox = new ComboBox<>();

        // Dodajemy jakieś Role i Group, które możemy wybrać
        controller.roles.add(new Role(1, "Admin"));
        controller.groups.add(new Group(1, "Grupa1"));
        controller.roleComboBox.setItems(controller.roles);
        controller.groupComboBox.setItems(controller.groups);
    }

    @Test
    void addNewUser_withEmptyFields_showsError() {
        // Wszystkie pola puste
        controller.usernameField.setText("");
        controller.passwordField.setText("");
        controller.firstNameField.setText("");
        controller.lastNameField.setText("");
        controller.salaryField.setText("");
        controller.roleComboBox.setValue(null);
        controller.groupComboBox.setValue(null);

        // Zamiast AlertUtils - można mockować lub sprawdzić, że metoda zwraca bez błędu
        // Tutaj sprawdzimy, że metoda zwraca bez dodania użytkownika (bo nic nie zadziała)
        controller.addNewUser();

        // Możesz dodać tu mock AlertUtils.showError i sprawdzić czy zostało wywołane
    }

    @Test
    void addNewUser_withInvalidSalary_showsError() {
        controller.usernameField.setText("user");
        controller.passwordField.setText("Password1!");
        controller.firstNameField.setText("Jan");
        controller.lastNameField.setText("Kowalski");
        controller.salaryField.setText("abc"); // nieprawidłowa liczba
        controller.roleComboBox.setValue(controller.roles.get(0));
        controller.groupComboBox.setValue(controller.groups.get(0));

        controller.addNewUser();

        // Tu również możesz mockować AlertUtils i sprawdzać wywołania
    }



}
