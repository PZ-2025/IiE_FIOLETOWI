package com.example.projekt;

import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;

public class SettingsController {

    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot, 1000, 600));  // dopasuj rozmiar jeśli trzeba
            stage.setTitle("Menu główne");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd podczas powrotu do menu: " + e.getMessage());
        }
    }

    @FXML
    private ComboBox<String> themeChoiceBox;

    @FXML
    private ComboBox<Integer> fontSizeChoiceBox;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordMessageLabel;

    @FXML
    private VBox rootVBox; // lub inny główny kontener z pliku FXML

    @FXML
    public void initialize() {
        // Inicjalizacja motywów
        themeChoiceBox.getItems().addAll("Jasny", "Ciemny", "Domyślny");
        themeChoiceBox.setValue("Domyślny");
        themeChoiceBox.setOnAction(e -> applyTheme(themeChoiceBox.getValue()));

        // Konfiguracja suwaka czcionki - tylko liczby całkowite
        fontSizeChoiceBox.getItems().addAll(10, 12, 14, 16, 18, 20, 24);
        fontSizeChoiceBox.setValue(14); // domyślny rozmiar

        fontSizeChoiceBox.setOnAction(e -> {
            int fontSize = fontSizeChoiceBox.getValue();
            applyFontSize(fontSize);
        });


    }


    private void applyTheme(String theme) {
        // Placeholder - tutaj można zmieniać style CSS
        System.out.println("Wybrano motyw: " + theme);
    }

    private void applyFontSize(double size) {
        Scene scene = themeChoiceBox.getScene();
        scene.getRoot().setStyle("-fx-font-size: " + (int) size + "px;");
    }



    @FXML
    private void handleChangePassword() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            passwordMessageLabel.setText("Wypełnij wszystkie pola.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            passwordMessageLabel.setText("Nowe hasła nie są zgodne.");
            return;
        }

        if (oldPass.equals(newPass)) {
            passwordMessageLabel.setText("Nowe hasło musi być inne niż stare.");
            return;
        }

        // Pobieranie aktualnego użytkownika z sesji
        User currentUser = UserSession.getInstance().getUser();

        try (Connection conn = DatabaseConnector.connect()) {
            // Weryfikacja starego hasła
            String query = "SELECT haslo FROM pracownicy WHERE id_pracownika = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String actualPassword = rs.getString("haslo");

                if (!actualPassword.equals(oldPass)) {
                    passwordMessageLabel.setText("Stare hasło jest nieprawidłowe.");
                    return;
                }
            } else {
                passwordMessageLabel.setText("Nie znaleziono użytkownika.");
                return;
            }

            // Aktualizacja hasła
            String update = "UPDATE pracownicy SET haslo = ? WHERE id_pracownika = ?";
            PreparedStatement updateStmt = conn.prepareStatement(update);
            updateStmt.setString(1, newPass);
            updateStmt.setInt(2, currentUser.getId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                passwordMessageLabel.setText("Hasło zmienione pomyślnie.");
            } else {
                passwordMessageLabel.setText("Błąd podczas aktualizacji hasła.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            passwordMessageLabel.setText("Błąd połączenia z bazą danych.");
        }
    }


    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            VBox root = loader.load();
            Stage stage = (Stage) themeChoiceBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
