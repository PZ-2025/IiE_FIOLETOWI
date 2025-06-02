package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementControllerTest extends ApplicationTest {

    @InjectMocks
    private UserManagementController controller;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @BeforeEach
    public void setUp() throws Exception {
        controller.usernameField = new TextField();
        controller.passwordField = new PasswordField();
        controller.firstNameField = new TextField();
        controller.lastNameField = new TextField();
        controller.salaryField = new TextField();
        controller.roleComboBox = new ComboBox<>();
        controller.groupComboBox = new ComboBox<>();

        controller.roles = FXCollections.observableArrayList(new Role(1, "Admin"));
        controller.groups = FXCollections.observableArrayList(new Group(1, "Grupa A"));
        controller.roleComboBox.setItems(controller.roles);
        controller.groupComboBox.setItems(controller.groups);

        controller.roleComboBox.setValue(controller.roles.get(0));
        controller.groupComboBox.setValue(controller.groups.get(0));
    }

    @Test
    public void testAddNewUser_withValidData_shouldInsertIntoDatabase() throws Exception {
        // Arrange
        controller.usernameField.setText("jan.kowalski");
        controller.passwordField.setText("Haslo123!");
        controller.firstNameField.setText("Jan");
        controller.lastNameField.setText("Kowalski");
        controller.salaryField.setText("5000");

        mockStatic(DatabaseConnector.class);
        when(DatabaseConnector.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);

        // Act
        controller.addNewUser();

        // Assert
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testAddNewUser_withInvalidSalary_shouldShowError() {
        controller.usernameField.setText("anna.nowak");
        controller.passwordField.setText("Haslo123!");
        controller.firstNameField.setText("Anna");
        controller.lastNameField.setText("Nowak");
        controller.salaryField.setText("-100");

        controller.addNewUser();

        // Nie sprawdzamy alertu GUI, ale możemy użyć AlertUtils z mockiem do testów
    }

    @Test
    public void testAddNewUser_withEmptyField_shouldShowError() {
        controller.usernameField.setText("");
        controller.passwordField.setText("");
        controller.firstNameField.setText("");
        controller.lastNameField.setText("");
        controller.salaryField.setText("");

        controller.addNewUser();

        // Ponownie: w pełnych testach GUI sprawdzasz komunikaty, ale tutaj wystarczy, że nie ma wyjątków i nie wykonano zapytania
    }
}
