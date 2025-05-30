package com.example.projekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Pane sidebarContainer;  // musi odpowiadać fx:id z MainLayout.fxml

    @FXML
    private BorderPane mainLayout;


    @FXML
    private StackPane contentArea;

    @FXML
    private void openSettings() {
        loadView("/com/example/projekt/settings.fxml", "ustawienia");
    }

    @FXML
    private void handleLogout() {
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

            // Dodajemy sidebar do kontenera w MainLayout.fxml
            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidebar);

            // Ustawiamy referencję do MainController
            sidebarController = sidebarLoader.getController();
            sidebarController.setMainController(this);

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania paska bocznego (Sidebar.fxml): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Wywoływana z SidebarController do załadowania widoku i ustawienia aktywnego przycisku.
     */
    public void loadView(String fxmlPath, String buttonId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentArea.getChildren().setAll(content);// fx:id="contentContainer" w MainLayout.fxml

            if (sidebarController != null) {
                sidebarController.setActive(buttonId);
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania widoku: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
