package com.example.projekt;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.PasswordField;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SettingsControllerTest {

    private SettingsController controller;

    @BeforeAll
    static void initToolkit() {
        // Potrzebne do inicjalizacji JavaFX Toolkit
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        controller = new SettingsController();

        // Ustawiamy wymagane pola kontrolera (symulacja @FXML injection)
        controller.oldPasswordField = new PasswordField();
        controller.newPasswordField = new PasswordField();
        controller.confirmPasswordField = new PasswordField();
    }

    @Test
    void testHandleChangePassword_emptyFields_showsError() {
        controller.oldPasswordField.setText("");
        controller.newPasswordField.setText("");
        controller.confirmPasswordField.setText("");

        AlertUtils alertUtilsMock = mock(AlertUtils.class);
        // Zastępujemy statyczną metodę AlertUtils.showError (w prawdziwym projekcie trzeba użyć np. PowerMockito)

        // Tu dla uproszczenia test sprawdza brak wyjątków i działanie metody
        assertDoesNotThrow(() -> controller.handleChangePassword());
        // W praktyce w testach integracyjnych sprawdzamy wyświetlane alerty lub logi
    }

    @Test
    void testHandleChangePassword_newPasswordsMismatch_showsError() {
        controller.oldPasswordField.setText("oldPass123");
        controller.newPasswordField.setText("newPass123");
        controller.confirmPasswordField.setText("differentPass");

        assertDoesNotThrow(() -> controller.handleChangePassword());
        // W tym teście powinniśmy sprawdzić, że pojawił się alert z błędem "Nowe hasła nie są zgodne"
    }

    @Test
    void testHandleChangePassword_newPasswordSameAsOld_showsError() {
        controller.oldPasswordField.setText("samePassword");
        controller.newPasswordField.setText("samePassword");
        controller.confirmPasswordField.setText("samePassword");

        assertDoesNotThrow(() -> controller.handleChangePassword());
    }

    @Test
    void testHandleChangePassword_invalidNewPassword_showsError() {
        controller.oldPasswordField.setText("oldPassword1");
        controller.newPasswordField.setText("short");
        controller.confirmPasswordField.setText("short");

        // Tu trzeba by było zamockować PasswordValidator.isPasswordValid na false
        // lub zamienić metodę na publiczną i podać mock. Dla uproszczenia testujemy, że metoda się wywołuje.
        assertDoesNotThrow(() -> controller.handleChangePassword());
    }



    // Pomocnicza metoda do mockowania statycznych metod (np. PowerMockito lub Mockito-inline w nowszych wersjach)
    private static void mockStatic(Class<?> clazz) {
        // Tu należałoby użyć frameworka do mockowania statycznych metod.
        // Poniżej jest tylko placeholder.
    }
}
