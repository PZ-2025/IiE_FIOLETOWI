package com.example.projekt;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SidebarController {

    @FXML
    private Button userTaskPanelButton;

    @FXML
    private Button taskManagerButton;

    @FXML
    private Button reportButton;

    @FXML
    private Button adminButton;

    @FXML
    private Button productCrudButton;

    @FXML
    private void toggleSidebar() {
        sidebar.setVisible(!sidebar.isVisible());
        sidebar.setManaged(sidebar.isVisible());
    }

    @FXML
    private VBox sidebar;

    private MainController mainController;

    // Przechowujemy wszystkie przyciski, aby łatwo resetować style
    private final Map<String, Button> buttonMap = new HashMap<>();

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

        // Przeładuj styl dla sidebaru i całej sceny
        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }


    @FXML
    private void initialize() {
        // Mapowanie przycisków
        buttonMap.put("userTaskPanelButton", userTaskPanelButton);
        buttonMap.put("taskManagerButton", taskManagerButton);
        buttonMap.put("reportButton", reportButton);
        buttonMap.put("adminButton", adminButton);
        buttonMap.put("productCrudButton", productCrudButton);

        // Ograniczenia widoczności według roli
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
                // Dla nieznanych ról – schowaj wszystko
                buttonMap.values().forEach(btn -> {
                    btn.setVisible(false);
                    btn.setManaged(false);
                });
                break;
        }
        applySidebarStyles();

    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Ustawia aktywny przycisk po fx:id
    public void setActive(String buttonId) {
        buttonMap.forEach((id, button) -> {
            button.getStyleClass().remove("active"); // usuń aktywny z każdego
            if (id.equals(buttonId)) {
                if (!button.getStyleClass().contains("active")) {
                    button.getStyleClass().add("active");
                }
            }
        });
    }




    @FXML
    private void openUserTaskPanel() {
        mainController.loadView("/com/example/projekt/usertaskpanel.fxml", "userTaskPanelButton");
    }

    @FXML
    private void openTaskManager() {
        mainController.loadView("/com/example/projekt/task.fxml", "taskManagerButton");
    }

    @FXML
    private void openReports() {
        mainController.loadView("/com/example/projekt/reports.fxml", "reportButton");
    }

    @FXML
    private void openUserManagement() {
        mainController.loadView("/com/example/projekt/userManagement.fxml", "adminButton");
    }

    @FXML
    private void openProductManager() {
        mainController.loadView("/com/example/projekt/Product_Management.fxml", "productCrudButton");
    }


}
