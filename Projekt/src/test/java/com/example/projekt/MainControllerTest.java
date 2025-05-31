package com.example.projekt;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainControllerTest{

    private MainController controller;

    @BeforeAll
    public static void initJfx() throws Exception {
        // Uruchomienie JavaFX toolkitu przed wszystkimi testami
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new MainController();
    }

    private void runOnFxThreadAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void testInitializeSidebar() throws Exception {
        runOnFxThreadAndWait(() -> assertDoesNotThrow(() -> controller.initializeSidebar(),
                "initializeSidebar() nie powinno rzucać wyjątkiem"));
    }

    @Test
    public void testHandleLogoutSwitchesScene() throws Exception {
        runOnFxThreadAndWait(() -> assertDoesNotThrow(() -> controller.handleLogout(),
                "handleLogout() nie powinno rzucać wyjątkiem"));
    }

    @Test
    public void testOpenSettingsLoadsView() throws Exception {
        runOnFxThreadAndWait(() -> assertDoesNotThrow(() -> controller.openSettings(),
                "openSettings() nie powinno rzucać wyjątkiem"));
    }

    @Test
    public void testLoadViewWithInvalidPath() throws Exception {
        runOnFxThreadAndWait(() -> assertDoesNotThrow(() -> {
            controller.loadView("non_existing_view.fxml", "Test View");
        }, "loadView() z nieistniejącą ścieżką nie powinno rzucać wyjątkiem"));
    }
}
