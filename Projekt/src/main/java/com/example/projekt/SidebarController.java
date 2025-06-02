package com.example.projekt;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Kontroler dla panelu bocznego (sidebar) aplikacji.
 * Odpowiada za obsługę przycisków nawigacyjnych, widoczność przycisków w zależności od roli użytkownika
 * oraz stosowanie odpowiednich styli CSS dla sidebaru i całej sceny.
 */
public class SidebarController {

    /** Przycisk do panelu zadań użytkownika. */
    @FXML
    Button userTaskPanelButton;

    /** Przycisk do menedżera zadań. */
    @FXML
    Button taskManagerButton;

    /** Przycisk do raportów. */
    @FXML
    Button reportButton;

    /** Przycisk do zarządzania użytkownikami (panel admina). */
    @FXML
    Button adminButton;

    /** Przycisk do zarządzania produktami. */
    @FXML
    Button productCrudButton;

    /** Kontener VBox zawierający cały sidebar. */
    @FXML
    VBox sidebar;

    /** Referencja do głównego kontrolera aplikacji. */
    private MainController mainController;

    /** Mapa przycisków, gdzie kluczem jest fx:id przycisku, a wartością sam przycisk. */
    private final Map<String, Button> buttonMap = new HashMap<>();

    /**
     * Inicjalizuje kontroler po załadowaniu FXML.
     * Mapuje przyciski do mapy, ustawia widoczność przycisków na podstawie roli użytkownika
     * oraz stosuje aktualne style CSS do sidebaru.
     */
    @FXML
    void initialize() {
        buttonMap.put("userTaskPanelButton", userTaskPanelButton);
        buttonMap.put("taskManagerButton", taskManagerButton);
        buttonMap.put("reportButton", reportButton);
        buttonMap.put("adminButton", adminButton);
        buttonMap.put("productCrudButton", productCrudButton);

        String rola = UserSession.getInstance().getUser().getRole();

        switch (rola.toLowerCase()) {
            case "pracownik":
                adminButton.setManaged(false);
                adminButton.setVisible(false);
                productCrudButton.setManaged(false);
                productCrudButton.setVisible(false);
                break;
            case "kierownik":
                adminButton.setManaged(false);
                adminButton.setVisible(false);
                break;
            case "admin":
                // Widzi wszystko
                break;
            default:
                // Ukryj wszystkie przyciski dla nieznanych ról
                buttonMap.values().forEach(btn -> {
                    btn.setVisible(false);
                    btn.setManaged(false);
                });
                break;
        }
        applySidebarStyles();
    }

    /**
     * Ustawia referencję do głównego kontrolera.
     * @param mainController instancja klasy MainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Stosuje aktualne style CSS (motyw i rozmiar czcionki) do sidebaru oraz całej sceny.
     * Usuwa poprzednie style i dodaje nowe na podstawie danych z UserSession.
     */
    public void applySidebarStyles() {
        Scene scene = sidebar.getScene();
        if (scene == null) {
            return;
        }

        scene.getStylesheets().removeIf(css ->
                css.contains("/styles/themes/") || css.contains("/styles/fonts/"));

        URL themeUrl = getClass().getResource(UserSession.getCurrentTheme());
        URL fontUrl = getClass().getResource(UserSession.getCurrentFontSize());

        if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
        if (fontUrl != null) scene.getStylesheets().add(fontUrl.toExternalForm());

        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }

    /**
     * Ustawia przycisk o podanym fx:id jako aktywny,
     * czyli dodaje mu klasę CSS "active" oraz usuwa ją z pozostałych przycisków.
     * @param buttonId fx:id przycisku, który ma być aktywny
     */
    public void setActive(String buttonId) {
        buttonMap.forEach((id, button) -> {
            button.getStyleClass().remove("active");
            if (id.equals(buttonId)) {
                if (!button.getStyleClass().contains("active")) {
                    button.getStyleClass().add("active");
                }
            }
        });
    }

    /**
     * Przełącza widoczność sidebaru (pokazuje/ukrywa).
     */
    @FXML
    void toggleSidebar() {
        sidebar.setVisible(!sidebar.isVisible());
        sidebar.setManaged(sidebar.isVisible());
    }

    /**
     * Akcja wywoływana po kliknięciu przycisku "Panel zadań użytkownika".
     * Przełącza widok na panel zadań użytkownika.
     */
    @FXML
    void openUserTaskPanel() {
        mainController.loadView("/com/example/projekt/usertaskpanel.fxml", "userTaskPanelButton");
    }

    /**
     * Akcja wywoływana po kliknięciu przycisku "Menedżer zadań".
     * Przełącza widok na menedżera zadań.
     */
    @FXML
    void openTaskManager() {
        mainController.loadView("/com/example/projekt/task.fxml", "taskManagerButton");
    }

    /**
     * Akcja wywoływana po kliknięciu przycisku "Raporty".
     * Przełącza widok na raporty.
     */
    @FXML
    void openReports() {
        mainController.loadView("/com/example/projekt/reports.fxml", "reportButton");
    }

    /**
     * Akcja wywoływana po kliknięciu przycisku "Zarządzanie użytkownikami".
     * Przełącza widok na panel zarządzania użytkownikami (tylko dla admina).
     */
    @FXML
    void openUserManagement() {
        mainController.loadView("/com/example/projekt/userManagement.fxml", "adminButton");
    }

    /**
     * Akcja wywoływana po kliknięciu przycisku "Zarządzanie produktami".
     * Przełącza widok na panel zarządzania produktami.
     */
    @FXML
    void openProductManager() {
        mainController.loadView("/com/example/projekt/Product_Management.fxml", "productCrudButton");
    }
}
