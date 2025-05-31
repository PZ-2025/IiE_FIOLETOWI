package com.example.projekt;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private TableView<Task> mockTaskTable;
    @Mock
    private TableColumn<Task, String> mockNameColumn;
    @Mock
    private BorderPane mockTaskRoot;

    @BeforeAll
    static void initJfx() throws InterruptedException {
        if (!Platform.isFxApplicationThread()) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        }
    }



    @AfterAll
    static void tearDownJfx() {
        Platform.exit();
    }

    @BeforeEach
    void setUp() {
        // Inicjalizacja kontrolek (upewnij się, że TaskController ma je publiczne lub dostępne)
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
        taskController.TaskCheckBox = new CheckBox();
        taskController.taskRoot = mockTaskRoot;
        taskController.taskTable = mockTaskTable;

        taskController.nameColumn = mockNameColumn;
    }



    @Test
    void testToggleCustomTaskFields_Enable() {
        taskController.TaskCheckBox.setSelected(true);

        taskController.toggleCustomTaskFields();

        assertFalse(taskController.productBox.isDisabled());
        assertFalse(taskController.directionBox.isDisabled());
        assertFalse(taskController.quantityField.isDisabled());
    }

    @Test
    void testToggleCustomTaskFields_Disable() {
        taskController.TaskCheckBox.setSelected(false);

        taskController.toggleCustomTaskFields();

        assertTrue(taskController.productBox.isDisabled());
        assertTrue(taskController.directionBox.isDisabled());
        assertTrue(taskController.quantityField.isDisabled());
    }

    @Test
    void testClearForm() {
        taskController.nameField.setText("Test");
        taskController.commentField.setText("Comment");
        taskController.quantityField.setText("5");
        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");
        taskController.startDatePicker.setValue(LocalDate.now());

        taskController.clearForm();

        assertEquals("", taskController.nameField.getText());
        assertEquals("", taskController.commentField.getText());
        assertEquals("", taskController.quantityField.getText());
        assertNull(taskController.statusBox.getValue());
        assertNull(taskController.startDatePicker.getValue());
    }

    @Test
    void testFillFormWithSelectedTask() {
        Task testTask = new Task(
                1, "Test Task", "Status", "High", "2023-01-01",
                "Product", "Direction", "Comment", "5", "John Doe"
        );
        testTask.setEndDate("2023-01-10");

        taskController.employeeBox.getItems().add("1: John Doe");

        taskController.fillFormWithSelectedTask(testTask);

        assertEquals("Test Task", taskController.nameField.getText());
        assertEquals("Comment", taskController.commentField.getText());
        assertEquals("5", taskController.quantityField.getText());
        assertEquals("1: John Doe", taskController.employeeBox.getValue());
        assertEquals(LocalDate.parse("2023-01-01"), taskController.startDatePicker.getValue());
        assertEquals(LocalDate.parse("2023-01-10"), taskController.endDatePicker.getValue());
    }

    @Test
    void testLoadData() throws SQLException {
        // Mockowanie połączenia i zapytań
        lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);
        lenient().when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        lenient().when(mockResultSet.next()).thenReturn(true, false);
        lenient().when(mockResultSet.getInt("id_zadania")).thenReturn(1);
        lenient().when(mockResultSet.getString(anyString())).thenReturn("Test Value");

        // Podstawowy mock TableView.setItems()
        doNothing().when(mockTaskTable).setItems(any());

        taskController.loadData();

        verify(mockTaskTable).setItems(any());
    }

    @Test
    void testValidateForm_ValidData() {
        taskController.nameField.setText("Test Task");
        taskController.statusBox.getItems().add("Status");
        taskController.statusBox.setValue("Status");
        taskController.priorityBox.getItems().add("High");
        taskController.priorityBox.setValue("High");
        taskController.employeeBox.getItems().add("1: John Doe");
        taskController.employeeBox.setValue("1: John Doe");
        taskController.startDatePicker.setValue(LocalDate.now());
        taskController.endDatePicker.setValue(LocalDate.now().plusDays(1));

        assertTrue(taskController.validateForm());
    }





    @Test
    void testHandleAddTask_InvalidForm() throws InterruptedException {
        Platform.runLater(() -> {
            taskController.nameField.setText(""); // puste pole
            // inne pola puste lub niepełne
        });
        WaitForAsyncUtils.waitForFxEvents();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                taskController.handleAddTask();
                // tutaj możesz sprawdzić, że nie wywołano executeUpdate()
                verify(mockPreparedStatement, never()).executeUpdate();
            } catch (Exception e) {
                fail("Exception during handleAddTask with invalid form: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout waiting for FX thread");
        }
    }






    @Test
    void testGetIdFromTable_Success() throws SQLException {
        String expectedSql = "SELECT id_statusu FROM statusy WHERE nazwa = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int result = taskController.getIdFromTable(mockConnection, "statusy", "Test Status");

        assertEquals(5, result);
        verify(mockPreparedStatement).setString(1, "Test Status");
    }

    @Test
    void testShowAlert() {
        String title = "Test Title";
        String message = "Test Message";

        try (MockedConstruction<Alert> mockedAlert = mockConstruction(Alert.class, (mock, context) -> {
            when(mock.showAndWait()).thenReturn(Optional.empty());
        })) {
            taskController.showAlert(title, message);

            assertEquals(1, mockedAlert.constructed().size());
            Alert alert = mockedAlert.constructed().get(0);
            verify(alert).setTitle(title);
            verify(alert).setHeaderText(null);
            verify(alert).setContentText(message);
            verify(alert).showAndWait();
        }
    }
}
