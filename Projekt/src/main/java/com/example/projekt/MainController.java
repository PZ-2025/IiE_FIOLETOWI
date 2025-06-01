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

public class MainController {

    @FXML
    protected Pane sidebarContainer;  // musi odpowiadać fx:id z MainLayout.fxml

    @FXML
    protected BorderPane mainLayout;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    protected StackPane contentArea;

    @FXML
    protected void openSettings() {
        loadView("/com/example/projekt/settings.fxml", "ustawienia");
    }

    @FXML
    protected void handleLogout() {
        // Przykładowo: wyczyść sesję i wróć do loginu
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

    private SidebarController sidebarController;

    /**
     * Inicjalizuje sidebar i przypisuje kontroler.
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
    @FXML
    private void toggleSidebar() {
        boolean visible = sidebarContainer.isVisible();
        sidebarContainer.setVisible(!visible);
        sidebarContainer.setManaged(!visible);
    }

    public void debugStyles(Scene scene, VBox sidebar) {
        System.out.println("🎨 === AKTUALNE STYLE SCENY ===");
        for (String css : scene.getStylesheets()) {
            System.out.println("SCENA: " + css);
        }

        System.out.println("\n🎨 === AKTUALNE STYLE SIDEBARA ===");
        for (String css : sidebar.getStylesheets()) {
            System.out.println("SIDEBAR: " + css);
        }

        System.out.println("\n🎯 UserSession:");
        System.out.println("Motyw: " + UserSession.getCurrentTheme());
        System.out.println("Czcionka: " + UserSession.getCurrentFontSize());
    }


}
