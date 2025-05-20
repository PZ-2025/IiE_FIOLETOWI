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
 * Kontroler dla widoku dashboardu (pulpitu nawigacyjnego).
 * Obsługuje przejście do różnych modułów aplikacji, w tym do menedżera zadań.
 */
public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private static final String TASK_VIEW_PATH = "/com/example/projekt/task.fxml";
    private static final String TASK_WINDOW_TITLE = "Zarządzanie zadaniami";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    private User currentUser;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;

    @FXML
    private Button adminButton;

    @FXML
    public void initialize() {
        adminButton.setVisible(false);

        UserSession userSession = UserSession.getInstance();
        if (userSession != null && userSession.getUser() != null) {
            setCurrentUser(userSession.getUser());
        }
    }

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

    private void configureTaskManagerStage(Stage stage, Parent root) {
        stage.setTitle(TASK_WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    private void handleViewLoadingError(Exception e) {
        LOGGER.log(Level.SEVERE, "Błąd ładowania widoku menedżera zadań", e);
        System.err.println("Krytyczny błąd aplikacji: Nie można załadować widoku menedżera zadań");
    }

    @FXML
    private void goToUserManagement(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/projekt/userManagement.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private Button reportButton;  // Dodaj to pole, aby powiązać przycisk z plikiem FXML

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
    @FXML
    private void openManagerUsersWindow() {
        if (currentUser != null && currentUser.isManager()|| currentUser.isAdmin()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/ManagerUsers.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Zarządzaj użytkownikami");
                stage.setScene(new Scene(root, 800, 600));
                stage.initModality(Modality.APPLICATION_MODAL); // blokuje inne okna do zamknięcia
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Błąd otwierania okna zarządzania użytkownikami: " + e.getMessage());
            }
        } else {
            System.out.println("Brak dostępu - tylko kierownik może zarządzać użytkownikami.");
        }
    }


    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (usernameLabel != null) {
            usernameLabel.setText("Witaj, " + user.getImie() + " " + user.getNazwisko());
        }

        if (roleLabel != null) {
            roleLabel.setText("Rola: " + user.getRole().toString());
        }

        if (user.isAdmin()) {
            adminButton.setVisible(true);
        }

        System.out.println("Zalogowano jako: " + user.getLogin() + " (" + user.getRole() + ")");
    }
    @FXML
    private void openSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/settings.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600)); // dostosuj rozmiar jeśli potrzeba
            stage.setTitle("Ustawienia");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd ładowania widoku ustawień: " + e.getMessage());
        }
    }


}
