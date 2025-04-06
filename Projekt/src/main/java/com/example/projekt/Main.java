package com.example.projekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Główna klasa aplikacji rozszerzająca {@link Application}.
 * Inicjalizuje główne okno aplikacji JavaFX.
 */
public class Main extends Application {
    private static final String LOGIN_VIEW_PATH = "/com/example/projekt/login.fxml";
    private static final String LOGIN_WINDOW_TITLE = "Ekran logowania";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    /**
     * Punkt wejścia dla aplikacji JavaFX.
     *
     * @param primaryStage główne okno aplikacji
     * @throws Exception jeśli wystąpi błąd podczas ładowania widoku
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(LOGIN_VIEW_PATH)));

            primaryStage.setTitle(LOGIN_WINDOW_TITLE);
            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
            primaryStage.setMinWidth(WINDOW_WIDTH);
            primaryStage.setMinHeight(WINDOW_HEIGHT);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania ekranu logowania: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch(args);
    }
}