package com.example.projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontroler dla widoku dashboardu (pulpitu nawigacyjnego).
 * Obsługuje przejście do różnych modułów aplikacji, w tym do menedżera zadań.
 */
public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private static final String TASK_VIEW_PATH = "/com/example/projekt/task.fxml";
    private static final String TASK_WINDOW_TITLE = "Zarządzanie zadaniami";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    /**
     * Przechodzi do widoku menedżera zadań.
     * Ładuje plik FXML z definicją interfejsu menedżera zadań i zastępuje obecną scenę.
     *
     * @param event zdarzenie akcji wywołujące przejście do menedżera zadań
     * @throws IllegalStateException jeśli nie uda się załadować pliku FXML
     */
    @FXML
    private void goToTaskManager(ActionEvent event) {
        try {
            Parent taskManagerRoot = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(TASK_VIEW_PATH)));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            configureTaskManagerStage(stage, taskManagerRoot);

        } catch (IOException | NullPointerException e) {
            handleViewLoadingError(e);
        }
    }

    /**
     * Konfiguruje okno menedżera zadań.
     *
     * @param stage referencja do obiektu Stage
     * @param root załadowany graf sceny z pliku FXML
     */
    private void configureTaskManagerStage(Stage stage, Parent root) {
        stage.setTitle(TASK_WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    /**
     * Obsługuje błędy ładowania widoku.
     * Loguje szczegóły błędu i wyświetla komunikat w konsoli.
     *
     * @param e wyjątek który wystąpił podczas ładowania widoku
     */
    private void handleViewLoadingError(Exception e) {
        LOGGER.log(Level.SEVERE, "Błąd ładowania widoku menedżera zadań", e);
        System.err.println("Krytyczny błąd aplikacji: Nie można załadować widoku menedżera zadań");
        // W pełnej aplikacji warto dodać tu wyświetlenie komunikatu użytkownikowi
    }
}