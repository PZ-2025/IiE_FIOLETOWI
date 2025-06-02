package com.example.projekt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class TaskControllerTest {

    private TaskController taskController;
    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        taskController = new TaskController();

        // Mockowanie obiektów JDBC
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Ustawienie mocka dla DriverManager
        try {
            DriverManager.registerDriver(mock(Driver.class));
        } catch (SQLException e) {
            // Driver już zarejestrowany
        }

        // Fix: Use eq() matchers for all String parameters
        when(DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/HurtPolSan"),
                eq("root"),
                eq("")))
                .thenReturn(mockConnection);
    }


    @Test
    void testGetIdFromTable() throws SQLException {
        // Przygotowanie
        String testName = "Test Status";
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        // Wykonanie
        int result = taskController.getIdFromTable(mockConnection, "statusy", testName);

        // Weryfikacja
        assertEquals(5, result);
        verify(mockPreparedStatement).setString(1, testName);
    }

    @Test
    void testGetIdFromTableThrowsExceptionWhenNotFound() throws SQLException {
        // Przygotowanie
        String testName = "Nieistniejący Status";
        when(mockResultSet.next()).thenReturn(false);

        // Wykonanie i Weryfikacja
        assertThrows(SQLException.class, () -> {
            taskController.getIdFromTable(mockConnection, "statusy", testName);
        });
    }

    @Test
    void testValidateFormWithEmptyFields() {
        // Przygotowanie
        taskController.nameField = new javafx.scene.control.TextField("");
        taskController.statusBox = new javafx.scene.control.ComboBox<>();
        taskController.priorityBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox = new javafx.scene.control.ComboBox<>();
        taskController.startDatePicker = new javafx.scene.control.DatePicker();
        taskController.endDatePicker = new javafx.scene.control.DatePicker();

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertFalse(result);
    }

    @Test
    void testValidateFormWithValidData() {
        // Przygotowanie
        taskController.nameField = new javafx.scene.control.TextField("Test Task");
        taskController.statusBox = new javafx.scene.control.ComboBox<>();
        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");
        taskController.priorityBox = new javafx.scene.control.ComboBox<>();
        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");
        taskController.employeeBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");
        taskController.startDatePicker = new javafx.scene.control.DatePicker(LocalDate.now());
        taskController.endDatePicker = new javafx.scene.control.DatePicker(LocalDate.now().plusDays(1));

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertTrue(result);
    }

    @Test
    void testValidateFormWithInvalidDates() {
        // Przygotowanie
        taskController.nameField = new javafx.scene.control.TextField("Test Task");
        taskController.statusBox = new javafx.scene.control.ComboBox<>();
        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");
        taskController.priorityBox = new javafx.scene.control.ComboBox<>();
        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");
        taskController.employeeBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");
        taskController.startDatePicker = new javafx.scene.control.DatePicker(LocalDate.now());
        taskController.endDatePicker = new javafx.scene.control.DatePicker(LocalDate.now().minusDays(1)); // Data końca przed datą początku

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertFalse(result);
    }

    @Test
    void testClearFields() {
        // Przygotowanie
        taskController.nameField = new javafx.scene.control.TextField("Test");
        taskController.commentField = new javafx.scene.control.TextField("Komentarz");
        taskController.quantityField = new javafx.scene.control.TextField("10");
        taskController.statusBox = new javafx.scene.control.ComboBox<>();
        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");
        taskController.priorityBox = new javafx.scene.control.ComboBox<>();
        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");
        taskController.productBox = new javafx.scene.control.ComboBox<>();
        taskController.productBox.getItems().add("Product");
        taskController.productBox.setValue("Product");
        taskController.directionBox = new javafx.scene.control.ComboBox<>();
        taskController.directionBox.getItems().add("Direction");
        taskController.directionBox.setValue("Direction");
        taskController.employeeBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");
        taskController.startDatePicker = new javafx.scene.control.DatePicker(LocalDate.now());
        taskController.endDatePicker = new javafx.scene.control.DatePicker(LocalDate.now());

        // Wykonanie
        taskController.clearFields();

        // Weryfikacja
        assertTrue(taskController.nameField.getText().isEmpty());
        assertTrue(taskController.commentField.getText().isEmpty());
        assertTrue(taskController.quantityField.getText().isEmpty());
        assertNull(taskController.statusBox.getValue());
        assertNull(taskController.priorityBox.getValue());
        assertNull(taskController.productBox.getValue());
        assertNull(taskController.directionBox.getValue());
        assertNull(taskController.employeeBox.getValue());
        assertNull(taskController.startDatePicker.getValue());
        assertNull(taskController.endDatePicker.getValue());
    }

    @Test
    void testFillFormWithSelectedTask() {
        // Przygotowanie
        Task testTask = new Task(
                1,
                "Test Task",
                "Status",
                "Priority",
                "2023-01-01",
                "Product",
                "Direction",
                "Komentarz",
                "10",
                "Jan Kowalski"
        );
        testTask.setEndDate("2023-01-02");

        taskController.nameField = new javafx.scene.control.TextField();
        taskController.commentField = new javafx.scene.control.TextField();
        taskController.quantityField = new javafx.scene.control.TextField();
        taskController.statusBox = new javafx.scene.control.ComboBox<>();
        taskController.priorityBox = new javafx.scene.control.ComboBox<>();
        taskController.productBox = new javafx.scene.control.ComboBox<>();
        taskController.directionBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox = new javafx.scene.control.ComboBox<>();
        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.startDatePicker = new javafx.scene.control.DatePicker();
        taskController.endDatePicker = new javafx.scene.control.DatePicker();

        // Wykonanie
        taskController.fillFormWithSelectedTask(testTask);

        // Weryfikacja
        assertEquals("Test Task", taskController.nameField.getText());
        assertEquals("Komentarz", taskController.commentField.getText());
        assertEquals("10", taskController.quantityField.getText());
        assertEquals("Status", taskController.statusBox.getValue());
        assertEquals("Priority", taskController.priorityBox.getValue());
        assertEquals("Product", taskController.productBox.getValue());
        assertEquals("Direction", taskController.directionBox.getValue());
        assertEquals("1: Jan Kowalski", taskController.employeeBox.getValue());
        assertEquals(LocalDate.parse("2023-01-01"), taskController.startDatePicker.getValue());
        assertEquals(LocalDate.parse("2023-01-02"), taskController.endDatePicker.getValue());
    }
}