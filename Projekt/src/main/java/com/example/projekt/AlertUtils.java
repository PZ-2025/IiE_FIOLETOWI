package com.example.projekt;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Klasa pomocnicza służąca do wyświetlania okien dialogowych typu alert
 * w aplikacji JavaFX.
 * Udostępnia metody do wyświetlania komunikatów informacyjnych oraz błędów.
 */
public class AlertUtils {

    /**
     * Wyświetla okno dialogowe z komunikatem informacyjnym.
     *
     * @param message treść komunikatu do wyświetlenia
     */
    public static void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informacja");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Wyświetla okno dialogowe z komunikatem o błędzie.
     *
     * @param message treść komunikatu o błędzie do wyświetlenia
     */
    public static void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
