package com.example.projekt;

import com.example.projekt.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Główny kontroler panelu nawigacyjnego aplikacji.
 * Obsługuje nawigację między różnymi modułami systemu oraz wyświetla podstawowe informacje o zalogowanym użytkowniku.
 * Kontroler zarządza również uprawnieniami użytkownika i udostępnia odpowiednie funkcje w zależności od roli.
 */
public class DashboardController {
    /** Logger do zapisywania zdarzeń i błędów */
    static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    /** Ścieżka do pliku FXML menedżera zadań */
    private static final String TASK_VIEW_PATH = "/com/example/projekt/task.fxml";

    /** Tytuł okna menedżera zadań */
    private static final String TASK_WINDOW_TITLE = "Zarządzanie zadaniami";

    /** Domyślna szerokość okna */
    private static final int WINDOW_WIDTH = 1000;

    /** Domyślna wysokość okna */
    private static final int WINDOW_HEIGHT = 600;

    /** Aktualnie zalogowany użytkownik */
    private User currentUser;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button adminButton;
    @FXML
    private Button reportButton;

    /**
     * Metoda inicjalizująca kontroler.
     * Ustawia widoczność przycisków w zależności od roli użytkownika i wyświetla informacje o zalogowanym użytkowniku.
     */
    @FXML
    public void initialize() {
        adminButton.setVisible(false);

        UserSession userSession = UserSession.getInstance();
        if (userSession != null && userSession.getUser() != null) {
            setCurrentUser(userSession.getUser());
        }
    }

    /**
     * Przechodzi do modułu zarządzania zadaniami.
     * @param event Zdarzenie wywołujące akcję
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
     * @param stage Scena, na której ma być wyświetlony menedżer zadań
     * @param root Główny element sceny
     */
    private void configureTaskManagerStage(Stage stage, Parent root) {
        stage.setTitle(TASK_WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    /**
     * Obsługuje błędy ładowania widoku.
     * @param e Wyjątek, który wystąpił podczas ładowania widoku
     */
    private void handleViewLoadingError(Exception e) {
        LOGGER.log(Level.SEVERE, "Błąd ładowania widoku menedżera zadań", e);
        System.err.println("Krytyczny błąd aplikacji: Nie można załadować widoku menedżera zadań");
    }

    /**
     * Przechodzi do modułu zarządzania użytkownikami (dostępne tylko dla administratorów i menedżerów).
     * @param event Zdarzenie wywołujące akcję
     * @throws IOException Jeśli wystąpi błąd podczas ładowania widoku
     */
    @FXML
    private void goToUserManagement(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/projekt/userManagement.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /**
     * Przechodzi do modułu raportów.
     */
    @FXML
    private void goToReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/reports.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) reportButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd ładowania widoku raportów: " + e.getMessage());
        }
    }

    /**
     * Przechodzi do panelu zadań użytkownika.
     * @param event Zdarzenie wywołujące akcję
     */
    @FXML
    private void goToUserTaskPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/userTaskPanel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Moje zadania");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Otwiera moduł zarządzania produktami.
     * @param event Zdarzenie wywołujące akcję
     */
    @FXML
    private void openProductManager(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/projekt/Product_Management.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Zarządzanie produktami");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd ładowania widoku zarządzania produktami: " + e.getMessage());
        }
    }

    /**
     * Ustawia aktualnego użytkownika i aktualizuje interfejs.
     * @param user Obiekt użytkownika do ustawienia
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (usernameLabel != null) {
            usernameLabel.setText("Witaj, " + user.getImie() + " " + user.getNazwisko());
        }

        if (roleLabel != null) {
            roleLabel.setText("Rola: " + user.getRole().toString());
        }

        if (user.isAdmin() || user.isManager()) {
            adminButton.setVisible(true);
        }

        System.out.println("Zalogowano jako: " + user.getLogin() + " (" + user.getRole() + ")");
    }

    /**
     * Otwiera okno ustawień aplikacji.
     * @param event Zdarzenie wywołujące akcję
     */
    @FXML
    private void openSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/settings.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Ustawienia");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd ładowania widoku ustawień: " + e.getMessage());
        }
    }

    /**
     * Wylogowuje użytkownika i przekierowuje do ekranu logowania.
     * @param event Zdarzenie wywołujące akcję
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // 1. Wyczyszczenie sesji użytkownika
        UserSession.clearSession();

        // 2. Przełączenie sceny na ekran logowania
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/projekt/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}