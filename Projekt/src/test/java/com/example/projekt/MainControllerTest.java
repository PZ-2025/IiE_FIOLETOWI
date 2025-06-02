package com.example.projekt;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainControllerTest extends ApplicationTest {

    private MainController controller;

    private Pane sidebarContainerMock;
    private StackPane contentAreaMock;

    @Override
    public void start(Stage stage) {
        // TestFX wymaga tej metody nawet jeśli nie jest używana
    }

    @BeforeEach
    public void setUp() {
        controller = new MainController();

        sidebarContainerMock = Mockito.mock(Pane.class);
        contentAreaMock = Mockito.mock(StackPane.class);

        controller.sidebarContainer = sidebarContainerMock;
        controller.contentArea = contentAreaMock;

        when(sidebarContainerMock.isVisible()).thenReturn(true);
        doNothing().when(sidebarContainerMock).setVisible(anyBoolean());
        doNothing().when(sidebarContainerMock).setManaged(anyBoolean());
    }

    @Test
    public void testToggleSidebar_hidesSidebarWhenVisible() {
        // Act
        controller.toggleSidebar();

        // Assert
        verify(sidebarContainerMock).setVisible(false);
        verify(sidebarContainerMock).setManaged(false);
    }
}
