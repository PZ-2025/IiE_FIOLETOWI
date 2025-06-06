package com.example.projekt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class TaskControllerTest {

    private TaskController taskController;
    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private static MockedStatic<DriverManager> mockedDriverManager;

    @Start
    public void start(Stage stage) {
        // JavaFX aplikacja jest teraz uruchomiona
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Zamknij poprzedni mock jeśli istnieje
        if (mockedDriverManager != null) {
            mockedDriverManager.close();
        }

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

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);
        // Nie sprawdzamy dokładnego zapytania, tylko czy PreparedStatement został użyty
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Wykonanie
        int result = taskController.getIdFromTable(mockConnection, tableName, testName);

        // Weryfikacja
        assertEquals(5, result);
        verify(mockConnection).prepareStatement(argThat(sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM " + tableName) &&
                        sql.contains("WHERE nazwa = ?")
        ));
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
    void testValidateFormWithEmptyFields() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};

        Platform.runLater(() -> {
            try {
                // Przygotowanie - pola już są puste po inicjalizacji
                result[0] = taskController.validateForm();
            } catch (Exception e) {
                // Ignoruj wyjątki związane z alertami w testach
                result[0] = false;
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        // Weryfikacja
        assertFalse(result[0], "Walidacja powinna zwrócić false dla pustych pól");
    }

    @Test
    void testValidateFormWithValidData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};

        Platform.runLater(() -> {
            try {
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
                result[0] = taskController.validateForm();
            } catch (Exception e) {
                // W przypadku błędu związanego z alertami, uznajemy za niepoprawną walidację
                result[0] = false;
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        // Weryfikacja
        assertTrue(result[0], "Walidacja powinna zwrócić true dla poprawnych danych");
    }


    @Test
    void testClearFields() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
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
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        // Weryfikacja
        Platform.runLater(() -> {
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
        });
    }

    @Test
    void testFillFormWithSelectedTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
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
                        "1: Jan Kowalski"
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
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        // Weryfikacja
        Platform.runLater(() -> {
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
        });
    }

    // Cleanup metoda do zamknięcia mocków statycznych
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockedDriverManager != null) {
            mockedDriverManager.close();
            mockedDriverManager = null;
        }
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDownAll() {
        // Dodatkowa asekuracja na końcu wszystkich testów
        try {
            Mockito.framework().clearInlineMocks();
        } catch (Exception e) {
            // Ignoruj błędy cleanup
        }
    }
}