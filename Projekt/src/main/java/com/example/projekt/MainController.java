package com.example.projekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główny kontroler aplikacji zarządzający podstawowym układem interfejsu.
 * Odpowiada za:
 * - Ładowanie i przełączanie widoków w głównym obszarze zawartości
 * - Zarządzanie panelem bocznym (sidebar)
 * - Obsługę logiki wylogowania
 * - Stosowanie aktualnych stylów i motywów
 * - Przełączanie widoczności panelu bocznego
 */
public class MainController {

    /** Kontener dla panelu bocznego (musi odpowiadać fx:id z MainLayout.fxml) */
    @FXML
    protected Pane sidebarContainer;

    /** Główny układ aplikacji */
    @FXML
    protected BorderPane mainLayout;

    /** Przycisk do przełączania widoczności panelu bocznego */
    @FXML
    private Button toggleSidebarButton;

    /** Główny obszar zawartości aplikacji */
    @FXML
    protected StackPane contentArea;

    /** Kontroler panelu bocznego */
    private SidebarController sidebarController;

    /**
     * Otwiera widok ustawień aplikacji.
     * Ładuje i wyświetla widok ustawień w głównym obszarze zawartości.
     */
    @FXML
    protected void openSettings() {
        loadView("/com/example/projekt/settings.fxml", "ustawienia");
    }

    /**
     * Obsługuje proces wylogowania użytkownika.
     * Czyści sesję użytkownika i przekierowuje do ekranu logowania.
     */
    @FXML
    protected void handleLogout() {
        UserSession.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicjalizuje panel boczny aplikacji.
     * Ładuje widok panelu bocznego, konfiguruje jego kontroler i stosuje style.
     */
    public void initializeSidebar() {
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/example/projekt/Sidebar.fxml"));
            Node sidebar = sidebarLoader.load();

            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidebar);

            sidebarController = sidebarLoader.getController();
            sidebarController.setMainController(this);
            sidebarController.applySidebarStyles();

            // Opóźnione wykonanie: tylko gdy scena już istnieje
            mainLayout.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    applyCurrentStyles();
                }
            });

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania paska bocznego (Sidebar.fxml): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ładuje i wyświetla określony widok w głównym obszarze zawartości.
     * @param fxmlPath Ścieżka do pliku FXML z definicją widoku
     * @param buttonId Identyfikator przycisku w panelu bocznym (dla zaznaczenia aktywnego elementu)
     */
    public void loadView(String fxmlPath, String buttonId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Zastosuj style do ładowanego widoku
            Scene scene = mainLayout.getScene();
            if (scene != null) {
                content.getStylesheets().clear();
                if (UserSession.getCurrentTheme() != null)
                    content.getStylesheets().add(getClass().getResource(UserSession.getCurrentTheme()).toExternalForm());
                if (UserSession.getCurrentFontSize() != null)
                    content.getStylesheets().add(getClass().getResource(UserSession.getCurrentFontSize()).toExternalForm());
            }

            // Ustaw widok w contentArea
            contentArea.getChildren().setAll(content);

            // Przekaż controllerowi dostęp do MainController, jeśli to SettingsController
            Object controller = loader.getController();
            if (controller instanceof SettingsController settingsController) {
                settingsController.setMainController(this);
            }

            // Ustaw aktywny przycisk w sidebarze
            if (sidebarController != null) {
                sidebarController.setActive(buttonId);
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania widoku: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Stosuje aktualne style i motywy do całej sceny.
     * Czyści poprzednie style i stosuje aktualnie wybrane w UserSession.
     */
    public void applyCurrentStyles() {
        Scene scene = mainLayout.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css ->
                css.contains("/styles/themes/") || css.contains("/styles/fonts/"));

        if (UserSession.getCurrentTheme() != null)
            scene.getStylesheets().add(getClass().getResource(UserSession.getCurrentTheme()).toExternalForm());

        if (UserSession.getCurrentFontSize() != null)
            scene.getStylesheets().add(getClass().getResource(UserSession.getCurrentFontSize()).toExternalForm());

        scene.getRoot().applyCss();
        debugStyles(mainLayout.getScene(), (VBox) sidebarContainer.getChildren().get(0));
    }

    /**
     * Przełącza widoczność panelu bocznego.
     * Zmienia zarówno widoczność jak i flagę zarządzania panelem bocznym.
     */
    @FXML
    private void toggleSidebar() {
        boolean visible = sidebarContainer.isVisible();
        sidebarContainer.setVisible(!visible);
        sidebarContainer.setManaged(!visible);
    }

    /**
     * Metoda pomocnicza do debugowania zastosowanych stylów.
     * @param scene Scena do sprawdzenia
     * @param sidebar Panel boczny do sprawdzenia
     */
    public void debugStyles(Scene scene, VBox sidebar) {
        // Metoda przeznaczona do debugowania - obecnie pusta implementacja
        // Można dodać logikę diagnostyczną w przyszłości
    }
}