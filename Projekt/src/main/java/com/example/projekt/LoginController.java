package com.example.projekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;

/**
 * Kontroler dla widoku logowania do systemu.
 * Obsługuje proces uwierzytelniania użytkowników i przekierowanie do odpowiedniego panelu.
 *
 * @author KrzysztofDrozda
 * @version 1.0
 * @since 2025-04-25
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * Obsługuje zdarzenie kliknięcia przycisku logowania.
     * Weryfikuje dane logowania i przekierowuje do odpowiedniego panelu.
     *
     * @param event zdarzenie wywołane kliknięciem przycisku
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Wprowadź login i hasło!");
            return;
        }

        try {
            User authenticatedUser = authenticateUser(username, password);

            if (authenticatedUser != null) {
                // Ustawiamy użytkownika w sesji
                UserSession.init(authenticatedUser);
                redirectToDashboard(event, authenticatedUser);
            } else {
                showErrorMessage("Błędne dane logowania!");
            }
        } catch (IOException e) {
            showErrorMessage("Błąd aplikacji!");
            e.printStackTrace();
        }
    }



    /**
     * Uwierzytelnia użytkownika na podstawie loginu i hasła w bazie danych.
     *
     * @param username login użytkownika
     * @param password hasło użytkownika
     * @return obiekt User jeśli uwierzytelnienie powiodło się, null w przeciwnym wypadku
     */
    private User authenticateUser(String username, String password) {
        String sql = """
        SELECT p.id_pracownika, p.imie, p.nazwisko, p.login, p.haslo, p.placa, 
               p.id_grupy, p.id_roli, r.nazwa AS nazwa_roli
        FROM pracownicy p
        JOIN role r ON p.id_roli = r.id_roli
        WHERE p.login = ?
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("haslo");

                    // Weryfikujemy hasło
                    if (PasswordHasher.verifyPassword(password, storedHashedPassword)) {
                        int id = rs.getInt("id_pracownika");
                        String imie = rs.getString("imie");
                        String nazwisko = rs.getString("nazwisko");
                        String login = rs.getString("login");
                        double placa = rs.getDouble("placa");
                        int id_grupy = rs.getInt("id_grupy");
                        int id_roli = rs.getInt("id_roli");
                        String nazwa_roli = rs.getString("nazwa_roli");

                        return new User(id, imie, nazwisko, login, storedHashedPassword, placa, id_grupy, id_roli, nazwa_roli);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * Przekierowuje zalogowanego użytkownika do panelu głównego.
     *
     * @param event zdarzenie wywołane kliknięciem przycisku
     * @param user obiekt reprezentujący zalogowanego użytkownika
     * @throws IOException jeśli wystąpi błąd podczas ładowania widoku dashboardu
     */
    private void redirectToDashboard(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
        Parent root = loader.load();

        DashboardController dashboardController = loader.getController();
        dashboardController.setCurrentUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard - " + user.getLogin());
    }

    /**
     * Wyświetla komunikat o błędzie w interfejsie użytkownika.
     *
     * @param message treść komunikatu do wyświetlenia
     */
    private void showErrorMessage(String message) {
        messageLabel.setText(message);
    }
}
