package com.example.projekt;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardControllerTest {

    private DashboardController controller;
    private Stage stage;
    private ActionEvent event;

    @BeforeAll
    public static void initToolkit() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // Inicjalizacja sesji użytkownika
        UserSession.init(new User(
                10, "Anna", "Nowak", "anowak", "haslo123",
                6000.0, 3, 2, "Administrator", "IT"
        ));

        Platform.runLater(() -> {
            try {
                controller = new DashboardController();

                // Inicjalizacja pól kontrolera
                controller.usernameLabel = new Label();
                controller.roleLabel = new Label();
                controller.adminButton = new Button();

                // Przygotowanie sceny i eventu
                stage = new Stage();
                Button button = new Button();
                Scene scene = new Scene(new StackPane(button), 800, 600);
                stage.setScene(scene);
                stage.show();

                event = new ActionEvent(button, null);

                controller.initialize();
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout podczas inicjalizacji");
        }
    }

    @Test
    public void testGoToTaskManager_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToTaskManager(event);
                assertEquals("Zarządzanie zadaniami", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToTaskManager");
        }
    }

    @Test
    public void testOpenSettings_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openSettings(event);
                assertEquals("Ustawienia", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testOpenSettings");
        }
    }

    @Test
    public void testHandleLogout_clearsSessionAndChangesScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.handleLogout(event);
                assertNull(UserSession.getInstance());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testHandleLogout");
        }
    }

    @Test
    public void testOpenProductManager_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openProductManager(event);
                assertEquals("Zarządzanie produktami", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testOpenProductManager");
        }
    }

    @Test
    public void testGoToUserManagement_changesScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToUserManagement(event);
                assertNotNull(stage.getScene());
            } catch (IOException e) {
                fail("Błąd ładowania userManagement.fxml", e);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToUserManagement");
        }
    }

    @Test
    public void testGoToUserTaskPanel_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToUserTaskPanel(event);
                assertEquals("Moje zadania", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToUserTaskPanel");
        }
    }
}package com.example.projekt;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardControllerTest {

    private DashboardController controller;
    private Stage stage;
    private ActionEvent event;

    @BeforeAll
    public static void initToolkit() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // Inicjalizacja sesji użytkownika
        UserSession.init(new User(
                10, "Anna", "Nowak", "anowak", "haslo123",
                6000.0, 3, 2, "Administrator", "IT"
        ));

        Platform.runLater(() -> {
            try {
                controller = new DashboardController();

                // Inicjalizacja pól kontrolera
                controller.usernameLabel = new Label();
                controller.roleLabel = new Label();
                controller.adminButton = new Button();

                // Przygotowanie sceny i eventu
                stage = new Stage();
                Button button = new Button();
                Scene scene = new Scene(new StackPane(button), 800, 600);
                stage.setScene(scene);
                stage.show();

                event = new ActionEvent(button, null);

                controller.initialize();
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout podczas inicjalizacji");
        }
    }

    @Test
    public void testGoToTaskManager_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToTaskManager(event);
                assertEquals("Zarządzanie zadaniami", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToTaskManager");
        }
    }

    @Test
    public void testOpenSettings_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openSettings(event);
                assertEquals("Ustawienia", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testOpenSettings");
        }
    }

    @Test
    public void testHandleLogout_clearsSessionAndChangesScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.handleLogout(event);
                assertNull(UserSession.getInstance());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testHandleLogout");
        }
    }

    @Test
    public void testOpenProductManager_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openProductManager(event);
                assertEquals("Zarządzanie produktami", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testOpenProductManager");
        }
    }

    @Test
    public void testGoToUserManagement_changesScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToUserManagement(event);
                assertNotNull(stage.getScene());
            } catch (IOException e) {
                fail("Błąd ładowania userManagement.fxml", e);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToUserManagement");
        }
    }

    @Test
    public void testGoToUserTaskPanel_changesSceneAndTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.goToUserTaskPanel(event);
                assertEquals("Moje zadania", stage.getTitle());
                assertNotNull(stage.getScene());
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout w testGoToUserTaskPanel");
        }
    }
}