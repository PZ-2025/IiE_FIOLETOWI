package com.example.projekt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class TaskControllerTest {

    private TaskController taskController;
    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DriverManager> mockedDriverManager;

    @BeforeEach
    void setUp() throws SQLException {
        taskController = new TaskController();

        // Mockowanie obiektów JDBC
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Konfiguracja mocków
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mockowanie DriverManager
        mockedDriverManager = Mockito.mockStatic(DriverManager.class);
        mockedDriverManager.when(() -> DriverManager.getConnection(
                        eq("jdbc:mysql://localhost:3306/HurtPolSan"),
                        eq("root"),
                        eq("")))
                .thenReturn(mockConnection);

        // Inicjalizacja pól UI kontrolera
        initializeUIFields();
    }

    private void initializeUIFields() {
        taskController.nameField = new TextField();
        taskController.commentField = new TextField();
        taskController.quantityField = new TextField();
        taskController.statusBox = new ComboBox<>();
        taskController.priorityBox = new ComboBox<>();
        taskController.productBox = new ComboBox<>();
        taskController.directionBox = new ComboBox<>();
        taskController.employeeBox = new ComboBox<>();
        taskController.startDatePicker = new DatePicker();
        taskController.endDatePicker = new DatePicker();
    }

    @Test
    void testGetIdFromTable() throws SQLException {
        // Przygotowanie
        String testName = "Test Status";
        String tableName = "statusy";
        String expectedQuery = "SELECT id FROM " + tableName + " WHERE nazwa = ?";

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);
        when(mockConnection.prepareStatement(expectedQuery)).thenReturn(mockPreparedStatement);

        // Wykonanie
        int result = taskController.getIdFromTable(mockConnection, tableName, testName);

        // Weryfikacja
        assertEquals(5, result);
        verify(mockConnection).prepareStatement(expectedQuery);
        verify(mockPreparedStatement).setString(1, testName);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetIdFromTableThrowsExceptionWhenNotFound() throws SQLException {
        // Przygotowanie
        String testName = "Nieistniejący Status";
        String tableName = "statusy";
        when(mockResultSet.next()).thenReturn(false);

        // Wykonanie i Weryfikacja
        SQLException exception = assertThrows(SQLException.class, () -> {
            taskController.getIdFromTable(mockConnection, tableName, testName);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testValidateFormWithEmptyFields() {
        // Przygotowanie - pola już są puste po inicjalizacji

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertFalse(result, "Walidacja powinna zwrócić false dla pustych pól");
    }

    @Test
    void testValidateFormWithValidData() {
        // Przygotowanie
        taskController.nameField.setText("Test Task");

        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");

        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");

        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");

        taskController.startDatePicker.setValue(LocalDate.now());
        taskController.endDatePicker.setValue(LocalDate.now().plusDays(1));

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertTrue(result, "Walidacja powinna zwrócić true dla poprawnych danych");
    }

    @Test
    void testValidateFormWithInvalidDates() {
        // Przygotowanie
        taskController.nameField.setText("Test Task");

        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");

        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");

        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");

        // Data końca przed datą początku
        taskController.startDatePicker.setValue(LocalDate.now());
        taskController.endDatePicker.setValue(LocalDate.now().minusDays(1));

        // Wykonanie
        boolean result = taskController.validateForm();

        // Weryfikacja
        assertFalse(result, "Walidacja powinna zwrócić false gdy data końca jest przed datą początku");
    }

    @Test
    void testClearFields() {
        // Przygotowanie - wypełnienie pól
        taskController.nameField.setText("Test");
        taskController.commentField.setText("Komentarz");
        taskController.quantityField.setText("10");

        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");

        taskController.priorityBox.getItems().add("Priority");
        taskController.priorityBox.setValue("Priority");

        taskController.productBox.getItems().add("Product");
        taskController.productBox.setValue("Product");

        taskController.directionBox.getItems().add("Direction");
        taskController.directionBox.setValue("Direction");

        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.employeeBox.setValue("1: Jan Kowalski");

        taskController.startDatePicker.setValue(LocalDate.now());
        taskController.endDatePicker.setValue(LocalDate.now());

        // Wykonanie
        taskController.clearFields();

        // Weryfikacja
        assertTrue(taskController.nameField.getText().isEmpty(), "Pole nazwy powinno być puste");
        assertTrue(taskController.commentField.getText().isEmpty(), "Pole komentarza powinno być puste");
        assertTrue(taskController.quantityField.getText().isEmpty(), "Pole ilości powinno być puste");
        assertNull(taskController.statusBox.getValue(), "ComboBox statusu powinien być pusty");
        assertNull(taskController.priorityBox.getValue(), "ComboBox priorytetu powinien być pusty");
        assertNull(taskController.productBox.getValue(), "ComboBox produktu powinien być pusty");
        assertNull(taskController.directionBox.getValue(), "ComboBox kierunku powinien być pusty");
        assertNull(taskController.employeeBox.getValue(), "ComboBox pracownika powinien być pusty");
        assertNull(taskController.startDatePicker.getValue(), "DatePicker daty początku powinien być pusty");
        assertNull(taskController.endDatePicker.getValue(), "DatePicker daty końca powinien być pusty");
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

        // Dodanie opcji do ComboBoxów (symulacja załadowanych danych)
        taskController.employeeBox.getItems().add("1: Jan Kowalski");
        taskController.statusBox.getItems().add("Status");
        taskController.priorityBox.getItems().add("Priority");
        taskController.productBox.getItems().add("Product");
        taskController.directionBox.getItems().add("Direction");

        // Wykonanie
        taskController.fillFormWithSelectedTask(testTask);

        // Weryfikacja
        assertEquals("Test Task", taskController.nameField.getText(), "Nazwa zadania powinna być wypełniona");
        assertEquals("Komentarz", taskController.commentField.getText(), "Komentarz powinien być wypełniony");
        assertEquals("10", taskController.quantityField.getText(), "Ilość powinna być wypełniona");
        assertEquals("Status", taskController.statusBox.getValue(), "Status powinien być wybrany");
        assertEquals("Priority", taskController.priorityBox.getValue(), "Priorytet powinien być wybrany");
        assertEquals("Product", taskController.productBox.getValue(), "Produkt powinien być wybrany");
        assertEquals("Direction", taskController.directionBox.getValue(), "Kierunek powinien być wybrany");
        assertEquals("1: Jan Kowalski", taskController.employeeBox.getValue(), "Pracownik powinien być wybrany");
        assertEquals(LocalDate.parse("2023-01-01"), taskController.startDatePicker.getValue(), "Data początku powinna być ustawiona");
        assertEquals(LocalDate.parse("2023-01-02"), taskController.endDatePicker.getValue(), "Data końca powinna być ustawiona");
    }

    // Cleanup metoda do zamknięcia mocków statycznych
    void tearDown() {
        if (mockedDriverManager != null) {
            mockedDriverManager.close();
        }
    }
}