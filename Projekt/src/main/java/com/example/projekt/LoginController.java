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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // Dodanie loggera
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Prosta weryfikacja danych
        if (username.equals("admin") && password.equals("admin")) {
            try {
                // Spróbuj załadować plik FXML
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/Projekt/dashboard.fxml"));

                // Jeśli zasób jest null, wyświetl komunikat o błędzie
                if (dashboardRoot == null) {
                    messageLabel.setText("Nie udało się załadować pulpitu!");
                    LOGGER.log(Level.SEVERE, "Nie udało się załadować pliku task.fxml");
                    return;
                }

                // Pobranie obecnej sceny i ustawienie nowej
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Pulpit");
                stage.setScene(new Scene(dashboardRoot, 1000, 600));
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Błąd ładowania pulpitu", e);
                messageLabel.setText("Błąd ładowania pulpitu!");
            }
        } else {
            messageLabel.setText("Błędne dane logowania!");
        }
    }
}