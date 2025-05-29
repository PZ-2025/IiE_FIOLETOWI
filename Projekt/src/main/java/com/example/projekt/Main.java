package com.example.projekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Główna klasa aplikacji rozszerzająca {@link Application}.
 * Inicjalizuje główne okno aplikacji JavaFX.
 */
public class Main extends Application {
    private static final String LOGIN_VIEW_PATH = "/com/example/projekt/login.fxml";
    private static final String LOGIN_WINDOW_TITLE = "Ekran logowania";

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

            // Pobierz rozmiar dostępnego ekranu (bez paska zadań itp.)
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

            primaryStage.setTitle(LOGIN_WINDOW_TITLE);
            primaryStage.setScene(scene);

            // Ustaw rozmiar i pozycję okna na cały ekran
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());

            // Opcjonalnie: zablokuj zmianę rozmiaru
            // primaryStage.setResizable(false);

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