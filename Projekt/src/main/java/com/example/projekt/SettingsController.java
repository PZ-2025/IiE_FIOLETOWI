package com.example.projekt;

import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;

/**
 * Kontroler zarządzający ustawieniami aplikacji, takimi jak zmiana motywu,
 * rozmiaru czcionki oraz hasła użytkownika.
 *
 * <p>Obsługuje interakcje użytkownika w widoku ustawień, zarządza zapisem i
 * odczytem ustawień z sesji oraz aktualizacją wyglądu aplikacji.
 *
 * <p>Umożliwia przejście do panelu głównego oraz stosowanie stylów CSS zgodnie
 * z wybranymi preferencjami użytkownika.
 */
public class SettingsController {

    /**
     * ComboBox wyboru motywu kolorystycznego aplikacji.
     * Dostępne opcje: "Jasny", "Ciemny", "Domyślny".
     */
    @FXML
    private ComboBox<String> themeChoiceBox;

    /**
     * ComboBox wyboru rozmiaru czcionki.
     * Dostępne opcje: "Mała", "Średnia", "Duża".
     */
    @FXML
    private ComboBox<String> fontSizeChoiceBox;

    /**
     * Pole do wprowadzania starego hasła przy zmianie hasła.
     */
    @FXML
    private PasswordField oldPasswordField;

    /**
     * Pole do wprowadzania nowego hasła przy zmianie hasła.
     */
    @FXML
    private PasswordField newPasswordField;

    /**
     * Pole do potwierdzania nowego hasła przy zmianie hasła.
     */
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Label wyświetlający komunikaty dotyczące zmiany hasła.
     */
    @FXML
    private Label passwordMessageLabel;

    /**
     * Główny kontener VBox z pliku FXML, do którego przypisane są style CSS.
     */
    @FXML
    private VBox rootVBox;

    /**
     * Referencja do kontrolera głównego (MainController) aplikacji.
     * Umożliwia odświeżenie stylów i widoków w głównym panelu.
     */
    private MainController mainController;

    /**
     * Inicjalizuje kontroler, ustawia domyślne wartości ComboBoxów oraz
     * konfiguruje nasłuchiwanie zmian wyboru motywu i rozmiaru czcionki.
     * Zastosowuje aktualne style po załadowaniu sceny.
     */
    @FXML
    public void initialize() {
        themeChoiceBox.getItems().addAll("Jasny", "Ciemny", "Domyślny");
        themeChoiceBox.setValue(AppSettings.getTheme());

        fontSizeChoiceBox.getItems().addAll("Mała", "Średnia", "Duża");
        fontSizeChoiceBox.setValue(AppSettings.getFontSizeLabel());

        themeChoiceBox.setOnAction(e -> {
            String selected = themeChoiceBox.getValue();
            AppSettings.setTheme(selected);
            reloadSettingsScene();
        });

        fontSizeChoiceBox.setOnAction(e -> {
            String selected = fontSizeChoiceBox.getValue();
            AppSettings.setFontSizeLabel(selected);
            reloadSettingsScene();
        });

        rootVBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyCurrentStyles();
            }
        });
    }

    /**
     * Ustawia wybrany motyw kolorystyczny do sceny.
     * @param theme Nazwa motywu ("Jasny", "Ciemny", "Domyślny").
     */
    private void applyTheme(String theme) {
        Scene scene = themeChoiceBox.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css -> css.contains("/styles/themes/"));

        String cssFile = switch (theme) {
            case "Jasny" -> "/com/example/projekt/styles/themes/light.css";
            case "Ciemny" -> "/com/example/projekt/styles/themes/dark.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };

        URL cssUrl = getClass().getResource(cssFile);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Nie znaleziono pliku stylu: " + cssFile);
        }
    }

    /**
     * Ustawia rozmiar czcionki w scenie na podstawie wybranej etykiety.
     * @param label Etykieta rozmiaru czcionki ("Mała", "Średnia", "Duża").
     */
    private void applyFontSize(String label) {
        String fontCss = switch (label.toLowerCase()) {
            case "mała" -> "/com/example/projekt/styles/fonts/small.css";
            case "duża" -> "/com/example/projekt/styles/fonts/large.css";
            default -> "/com/example/projekt/styles/fonts/medium.css";
        };

        UserSession.setCurrentFontSize(fontCss);

        Scene scene = rootVBox.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css ->
                css.contains("/styles/fonts/") || css.contains("/styles/themes/"));

        URL themeUrl = getClass().getResource(UserSession.getCurrentTheme());
        URL fontUrl = getClass().getResource(UserSession.getCurrentFontSize());

        if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
        if (fontUrl != null) scene.getStylesheets().add(fontUrl.toExternalForm());
    }

    /**
     * Obsługuje zdarzenie zmiany hasła użytkownika.
     * Sprawdza poprawność i zgodność haseł, waliduje nowe hasło,
     * aktualizuje je w bazie danych oraz wyświetla odpowiednie komunikaty.
     */
    @FXML
    private void handleChangePassword() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            AlertUtils.showError("Wypełnij wszystkie pola.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            AlertUtils.showError("Nowe hasła nie są zgodne.");
            return;
        }

        if (oldPass.equals(newPass)) {
            AlertUtils.showError("Nowe hasło musi być inne niż stare.");
            return;
        }

        if (!PasswordValidator.isPasswordValid(newPass)) {
            AlertUtils.showError(PasswordValidator.getPasswordRequirementsMessage());
            return;
        }

        User currentUser = UserSession.getInstance().getUser();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT haslo FROM pracownicy WHERE id_pracownika = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("haslo");

                if (!PasswordHasher.verifyPassword(oldPass, storedHashedPassword)) {
                    AlertUtils.showError("Stare hasło jest nieprawidłowe.");
                    return;
                }
            } else {
                AlertUtils.showError("Nie znaleziono użytkownika.");
                return;
            }

            byte[] newSalt = PasswordHasher.generateSalt();
            String hashedNewPass = PasswordHasher.hashPassword(newPass, newSalt);

            String update = "UPDATE pracownicy SET haslo = ? WHERE id_pracownika = ?";
            PreparedStatement updateStmt = conn.prepareStatement(update);
            updateStmt.setString(1, hashedNewPass);
            updateStmt.setInt(2, currentUser.getId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                AlertUtils.showAlert("Hasło zmienione pomyślnie.");
                oldPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                AlertUtils.showError("Błąd podczas aktualizacji hasła.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showError("Błąd połączenia z bazą danych.");
        }
    }

    /**
     * Obsługuje zmianę motywu na podstawie wyboru użytkownika
     * i aktualizuje sesję oraz styl sceny.
     */
    @FXML
    private void handleThemeChange() {
        String selectedTheme = themeChoiceBox.getValue();

        String themePath = switch (selectedTheme.toLowerCase()) {
            case "dark" -> "/com/example/projekt/styles/themes/dark.css";
            case "light" -> "/com/example/projekt/styles/themes/light.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };

        UserSession.setCurrentTheme(themePath);

        Scene scene = themeChoiceBox.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(themePath).toExternalForm());
        }
    }

    /**
     * Przechodzi do głównego menu aplikacji.
     */
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

    /**
     * Ustawia referencję do kontrolera głównego.
     * @param controller Instancja MainController
     */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    /**
     * Zatwierdza zmiany ustawień motywu i rozmiaru czcionki.
     * Zapisuje je w pliku i sesji, oraz odświeża style na scenie i w MainController.
     */
    @FXML
    private void handleApplySettings() {
        String selectedTheme = themeChoiceBox.getValue();
        String selectedFontLabel = fontSizeChoiceBox.getValue();

        AppSettings.setTheme(selectedTheme);
        AppSettings.setFontSizeLabel(selectedFontLabel);

        String themePath = switch (selectedTheme.toLowerCase()) {
            case "ciemny" -> "/com/example/projekt/styles/themes/dark.css";
            case "jasny" -> "/com/example/projekt/styles/themes/light.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };
        String fontPath = switch (selectedFontLabel.toLowerCase()) {
            case "mała" -> "/com/example/projekt/styles/fonts/small.css";
            case "duża" -> "/com/example/projekt/styles/fonts/large.css";
            default -> "/com/example/projekt/styles/fonts/medium.css";
        };

        UserSession.setCurrentTheme(themePath);
        UserSession.setCurrentFontSize(fontPath);

        Scene scene = rootVBox.getScene();
        if (scene != null) {
            scene.getStylesheets().removeIf(css ->
                    css.contains("/styles/themes/") || css.contains("/styles/fonts/"));

            URL themeUrl = getClass().getResource(themePath);
            URL fontUrl = getClass().getResource(fontPath);
            if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
            if (fontUrl != null) scene.getStylesheets().add(fontUrl.toExternalForm());

            scene.getRoot().applyCss();
        }

        if (mainController != null) {
            mainController.applyCurrentStyles();
        }
    }

    /**
     * Zastosowuje aktualne style motywu i czcionki zapisane w sesji do sceny.
     */
    private void applyCurrentStyles() {
        Scene scene = rootVBox.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css ->
                css.contains("/styles/themes/") || css.contains("/styles/fonts/"));

        String theme = UserSession.getCurrentTheme();
        String font = UserSession.getCurrentFontSize();

        if (theme != null) {
            URL themeUrl = getClass().getResource(theme);
            if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
        }

        if (font != null) {
            URL fontUrl = getClass().getResource(font);
            if (fontUrl != null) scene.getStylesheets().add(fontUrl.toExternalForm());
        }

        Node root = scene.getRoot();
        root.applyCss();
        if (root instanceof Region r) {
            r.layout();
        }
    }

    /**
     * Przeładowuje widok ustawień, stosując aktualne style oraz ładując
     * sidebar i widok ustawień w MainController.
     */
    private void reloadSettingsScene() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/example/projekt/MainLayout.fxml"));
            Parent mainRoot = mainLoader.load();

            Scene scene = rootVBox.getScene();
            if (scene != null) {
                scene.setRoot(mainRoot);
            }

            MainController mainController = mainLoader.getController();
            mainController.initializeSidebar();
            mainController.loadView("/com/example/projekt/settings.fxml", null);

            scene.getStylesheets().clear();

            String theme = UserSession.getCurrentTheme();
            String font = UserSession.getCurrentFontSize();

            if (theme != null) {
                URL themeUrl = getClass().getResource(theme);
                if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
            }

            if (font != null) {
                URL fontUrl = getClass().getResource(font);
                if (fontUrl != null) scene.getStylesheets().add(fontUrl.toExternalForm());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
