    package com.example.projekt;
    import javafx.application.Platform;
    import javafx.embed.swing.JFXPanel;
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    class AlertUtilsTest {


        @BeforeAll
        public static void initJFX() {
            // Inicjalizacja JavaFX (wymagana dla testów komponentów GUI)
            new JFXPanel(); // inicjuje toolkit
        }

        @Test
        public void testShowAlertDoesNotThrow() {
            Platform.runLater(() -> assertDoesNotThrow(() ->
                    AlertUtils.showAlert("To jest testowy komunikat informacyjny.")
            ));
        }

        @Test
        public void testShowErrorDoesNotThrow() {
            Platform.runLater(() -> assertDoesNotThrow(() ->
                    AlertUtils.showError("To jest testowy komunikat błędu.")
            ));
        }
    }