package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.*;

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

    @Mock
    private Statement mockPlainStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws Exception {
        controller = new UserManagementController(); // ensure default constructor

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

    @Override
    public void start(Stage stage) {
        // required by ApplicationTest
    }


    @Test
    public void testAddNewUser_withEmptyField_shouldNotInsert() throws Exception {
        controller.usernameField.setText("");
        controller.passwordField.setText("");
        controller.firstNameField.setText("");
        controller.lastNameField.setText("");
        controller.salaryField.setText("");

        try (MockedStatic<DatabaseConnector> mockedStatic = mockStatic(DatabaseConnector.class)) {
            mockedStatic.when(DatabaseConnector::connect).thenReturn(mockConnection);

            controller.addNewUser();

            verify(mockConnection, never()).prepareStatement(any());
        }
    }
}
