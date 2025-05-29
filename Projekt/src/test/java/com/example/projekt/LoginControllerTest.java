package com.example.projekt;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoginController controller;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;

    private MockedStatic<DatabaseConnector> databaseConnectorMock;
    private MockedStatic<PasswordHasher> passwordHasherMock;

    @BeforeAll
    static void initJFX() {
        new JFXPanel(); // inicjalizacja JavaFX
    }

    @BeforeEach
    void setUp() {
        controller = new LoginController();

        usernameField = new TextField();
        passwordField = new PasswordField();
        messageLabel = new Label();

        // Wstrzykujemy pola do kontrolera (symulacja @FXML)
        setField(controller, "usernameField", usernameField);
        setField(controller, "passwordField", passwordField);
        setField(controller, "messageLabel", messageLabel);

        databaseConnectorMock = Mockito.mockStatic(DatabaseConnector.class);
        passwordHasherMock = Mockito.mockStatic(PasswordHasher.class);
    }

    @AfterEach
    void tearDown() {
        databaseConnectorMock.close();
        passwordHasherMock.close();
    }

    // Metoda pomocnicza do wstrzykiwania prywatnych pól (reflection)
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmptyUsernameOrPasswordShowsError() {
        usernameField.setText("");
        passwordField.setText("password");
        controller.handleLogin(mock(ActionEvent.class));
        assertEquals("Wprowadź login i hasło!", messageLabel.getText());

        usernameField.setText("user");
        passwordField.setText("");
        controller.handleLogin(mock(ActionEvent.class));
        assertEquals("Wprowadź login i hasło!", messageLabel.getText());
    }

    @Test
    void testInvalidCredentialsShowsError() throws SQLException {
        usernameField.setText("user");
        passwordField.setText("wrongpass");

        // Mock DB connection, statement, resultset
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        databaseConnectorMock.when(DatabaseConnector::connect).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // brak użytkownika

        controller.handleLogin(mock(ActionEvent.class));
        assertEquals("Błędne dane logowania!", messageLabel.getText());

        // Zweryfikuj, że zasoby są zamykane
        verify(rs).close();
        verify(stmt).close();
        verify(conn).close();
    }


}
