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
    private ComboBox<String> fontSizeChoiceBox;

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
        themeChoiceBox.getItems().addAll("Jasny", "Ciemny", "Domyślny");
        themeChoiceBox.setValue(AppSettings.getTheme());

        fontSizeChoiceBox.getItems().addAll("Mała", "Średnia", "Duża");
        fontSizeChoiceBox.setValue(AppSettings.getFontSizeLabel());

        themeChoiceBox.setOnAction(e -> {
            String selected = themeChoiceBox.getValue();
            AppSettings.setTheme(selected); // zapis + ustawienie ścieżki do CSS w UserSession
            reloadSettingsScene();          // przeładuj widok
        });

        fontSizeChoiceBox.setOnAction(e -> {
            String selected = fontSizeChoiceBox.getValue();
            AppSettings.setFontSizeLabel(selected); // zapis + ścieżka CSS
            reloadSettingsScene();                 // przeładuj widok
        });

        // Po starcie zastosuj aktualne
        rootVBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyCurrentStyles();
            }
        });
    }


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

    @FXML
    private void handleThemeChange() {
        String selectedTheme = themeChoiceBox.getValue(); // np. "Dark", "Light", "Default"

        String themePath = switch (selectedTheme.toLowerCase()) {
            case "dark" -> "/com/example/projekt/styles/themes/dark.css";
            case "light" -> "/com/example/projekt/styles/themes/light.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };

        // Zapisz nowy motyw w sesji
        UserSession.setCurrentTheme(themePath);

        // Zastosuj do głównej sceny
        Scene scene = themeChoiceBox.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(themePath).toExternalForm());
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
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }


    @FXML
    private void handleApplySettings() {
        // Zastosuj motyw i rozmiar czcionki z ComboBoxów
        String selectedTheme = themeChoiceBox.getValue();
        String selectedFontLabel = fontSizeChoiceBox.getValue();

        // Zapisz do pliku
        AppSettings.setTheme(selectedTheme);
        AppSettings.setFontSizeLabel(selectedFontLabel);

        // Zaktualizuj UserSession
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

        // Odśwież style sceny
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

        // Zastosuj do MainController
        if (mainController != null) {
            mainController.applyCurrentStyles(); // odświeża całą scenę, w tym sidebar
        }
    }
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
            r.layout(); // tylko jeśli root to np. VBox, BorderPane itp.
        }
    }
    private void reloadSettingsScene() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/example/projekt/MainLayout.fxml"));
            Parent mainRoot = mainLoader.load();

            // Pobierz scenę i ustaw nowy root
            Scene scene = rootVBox.getScene();
            if (scene != null) {
                scene.setRoot(mainRoot);
            }

            // Załaduj widok ustawień jako zawartość centralną
            MainController mainController = mainLoader.getController();
            mainController.initializeSidebar(); // ponowne załadowanie sidebaru
            mainController.loadView("/com/example/projekt/settings.fxml", null);

            // Ustaw style
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
