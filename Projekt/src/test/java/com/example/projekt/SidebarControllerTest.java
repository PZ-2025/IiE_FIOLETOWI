package com.example.projekt;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe klasy SidebarController.
 * Mockuje UserSession, by uniknąć NullPointerException podczas testów.
 */
public class SidebarControllerTest {

    private SidebarController controller;
    private MainController mockMainController;

    private MockedStatic<UserSession> mockedUserSessionStatic;

    @BeforeEach
    public void setup() {
        controller = new SidebarController();

        // Inicjalizacja przycisków i VBox (zazwyczaj w FXML, tutaj ręcznie)
        controller.userTaskPanelButton = new Button();
        controller.taskManagerButton = new Button();
        controller.reportButton = new Button();
        controller.adminButton = new Button();
        controller.productCrudButton = new Button();
        controller.sidebar = new VBox();

        // Mock głównego kontrolera aplikacji
        mockMainController = mock(MainController.class);
        controller.setMainController(mockMainController);

        // Mock statycznych metod UserSession
        mockedUserSessionStatic = mockStatic(UserSession.class);

        // Tworzymy mock UserSession i User z rolą "admin"
        UserSession mockUserSession = mock(UserSession.class);
        User mockUser = mock(User.class);
        when(mockUser.getRole()).thenReturn("admin");
        when(mockUserSession.getUser()).thenReturn(mockUser);

        // Ustawiamy, aby getInstance() zwracało nasz mock UserSession
        mockedUserSessionStatic.when(UserSession::getInstance).thenReturn(mockUserSession);

        // Mockujemy również metody statyczne getCurrentTheme i getCurrentFontSize
        mockedUserSessionStatic.when(UserSession::getCurrentTheme).thenReturn("/styles/themes/default.css");
        mockedUserSessionStatic.when(UserSession::getCurrentFontSize).thenReturn("/styles/fonts/medium.css");
    }

    @AfterEach
    public void tearDown() {
        mockedUserSessionStatic.close();
    }

    @Test
    public void testInitializeSetsButtonVisibilityForAdmin() {
        controller.initialize();

        // Admin widzi wszystkie przyciski
        assertTrue(controller.adminButton.isVisible());
        assertTrue(controller.productCrudButton.isVisible());
    }

    @Test
    public void testSetActiveAddsActiveClass() {
        controller.initialize();

        controller.setActive("userTaskPanelButton");

        assertTrue(controller.userTaskPanelButton.getStyleClass().contains("active"));
        assertFalse(controller.taskManagerButton.getStyleClass().contains("active"));
    }

    @Test
    public void testToggleSidebar() {
        controller.sidebar.setVisible(true);
        controller.sidebar.setManaged(true);

        controller.toggleSidebar();

        assertFalse(controller.sidebar.isVisible());
        assertFalse(controller.sidebar.isManaged());

        controller.toggleSidebar();

        assertTrue(controller.sidebar.isVisible());
        assertTrue(controller.sidebar.isManaged());
    }

    @Test
    public void testButtonActionsCallMainControllerLoadView() {
        controller.initialize();

        controller.openUserTaskPanel();
        verify(mockMainController).loadView("/com/example/projekt/usertaskpanel.fxml", "userTaskPanelButton");

        controller.openTaskManager();
        verify(mockMainController).loadView("/com/example/projekt/task.fxml", "taskManagerButton");

        controller.openReports();
        verify(mockMainController).loadView("/com/example/projekt/reports.fxml", "reportButton");

        controller.openUserManagement();
        verify(mockMainController).loadView("/com/example/projekt/userManagement.fxml", "adminButton");

        controller.openProductManager();
        verify(mockMainController).loadView("/com/example/projekt/Product_Management.fxml", "productCrudButton");
    }
}
