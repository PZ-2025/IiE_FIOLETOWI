package com.example.projekt;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
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
    private void openSettings() {
        mainController.loadView("/com/example/projekt/settings.fxml", null);
    }

    @FXML
    private void handleLogout() {
        // logika wylogowania
        System.out.println("Wylogowywanie...");
    }

    @FXML
    private VBox sidebar;

    private MainController mainController;

    // Przechowujemy wszystkie przyciski, aby łatwo resetować style
    private final Map<String, Button> buttonMap = new HashMap<>();

    @FXML
    private void initialize() {
        // Mapujemy przyciski po ich fx:id
        buttonMap.put("userTaskPanelButton", userTaskPanelButton);
        buttonMap.put("taskManagerButton", taskManagerButton);
        buttonMap.put("reportButton", reportButton);
        buttonMap.put("adminButton", adminButton);
        buttonMap.put("productCrudButton", productCrudButton);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Ustawia aktywny przycisk po fx:id
    public void setActive(String buttonId) {
        buttonMap.forEach((id, button) -> {
            if (id.equals(buttonId)) {
                button.getStyleClass().add("active");
            } else {
                button.getStyleClass().remove("active");
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
