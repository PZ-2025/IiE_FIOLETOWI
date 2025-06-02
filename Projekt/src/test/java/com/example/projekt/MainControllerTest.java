package com.example.projekt;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainControllerTest {

    private MainController controller;

    private Pane sidebarContainerMock;
    private StackPane contentAreaMock;

    @BeforeEach
    public void setUp() {
        controller = new MainController();

        // Mockowanie sidebarContainer jako Pane
        sidebarContainerMock = Mockito.mock(Pane.class);
        // Mockowanie contentArea
        contentAreaMock = Mockito.mock(StackPane.class);

        // Ustawiamy pola w kontrolerze na mocki
        controller.sidebarContainer = sidebarContainerMock;
        controller.contentArea = contentAreaMock;

        // Przy metodzie toggleSidebar trzeba zdefiniować zachowanie isVisible() i setVisible()
        when(sidebarContainerMock.isVisible()).thenReturn(true);
        doNothing().when(sidebarContainerMock).setVisible(anyBoolean());
        doNothing().when(sidebarContainerMock).setManaged(anyBoolean());
    }
}