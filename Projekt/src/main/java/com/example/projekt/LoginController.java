package com.example.projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontroler dla widoku logowania.
 * Obsługuje proces uwierzytelniania użytkownika i przejście do dashboardu.
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String DASHBOARD_VIEW_PATH = "/com/example/projekt/dashboard.fxml";
    private static final String DASHBOARD_TITLE = "Pulpit";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    /**
     * Pole tekstowe do wprowadzenia nazwy użytkownika
     */
    @FXML
    private TextField usernameField;

    /**
     * Pole do wprowadzenia hasła
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Etykieta wyświetlająca komunikaty o błędach
     */
    @FXML
    private Label messageLabel;

    /**
     * Obsługuje zdarzenie kliknięcia przycisku logowania.
     * Weryfikuje dane logowania i w przypadku sukcesu przechodzi do dashboardu.
     *
     * @param event zdarzenie akcji przycisku
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticate(username, password)) {
            loadDashboardView(event);
        } else {
            showErrorMessage("Błędne dane logowania!");
        }
    }

    /**
     * Weryfikuje dane logowania użytkownika.
     *
     * @param username nazwa użytkownika
     * @param password hasło
     * @return true jeśli dane są poprawne, false w przeciwnym wypadku
     */
    private boolean authenticate(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    /**
     * Ładuje i wyświetla widok dashboardu.
     *
     * @param event zdarzenie wywołujące zmianę widoku
     */
    private void loadDashboardView(ActionEvent event) {
        try {
            Parent dashboardRoot = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(DASHBOARD_VIEW_PATH)));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(DASHBOARD_TITLE);
            stage.setScene(new Scene(dashboardRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
            stage.show();

        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania pulpitu", e);
            showErrorMessage("Błąd ładowania pulpitu!");
        }
    }

    /**
     * Wyświetla komunikat o błędzie w interfejsie użytkownika.
     *
     * @param message treść komunikatu błędu
     */
    private void showErrorMessage(String message) {
        messageLabel.setText(message);
    }
}