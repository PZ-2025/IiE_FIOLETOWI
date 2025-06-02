package com.example.projekt;
import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class UserTaskPanelControllerTest {

    private UserTaskPanelController controller;

    @BeforeEach
    void setUp() {
        controller = new UserTaskPanelController();
        controller.initializeStatusOrder(); // ręczne wywołanie bo @FXML nie działa w teście
    }

    @Test
    void testInitializeStatusOrder_containsAllStatuses() {
        var statusOrder = controller.statusOrder;
        assertEquals(4, statusOrder.size());
        assertTrue(statusOrder.containsKey("Oczekujące"));
        assertTrue(statusOrder.containsKey("Rozpoczęte"));
        assertTrue(statusOrder.containsKey("W trakcie"));
        assertTrue(statusOrder.containsKey("Zakończone"));
    }

    @Test
    void testUpdateAvailableStatuses_addsOnlyNextStatuses() {
        // Musimy przygotować ComboBox mock lub stub, tutaj uproszczenie: zamienimy ComboBox na testową listę
        class TestComboBox {
            List<String> items = new java.util.ArrayList<>();
            void getItemsClear() { items.clear(); }
            void getItemsAdd(String s) { items.add(s); }
        }

        // Przypiszemy ComboBox ręcznie w teście
        var testComboBox = new TestComboBox();
        // Zamień w kontrolerze statusComboBox na testComboBox (załóżmy, że mamy setter lub dostęp)
        // W realnym teście trzeba to zrobić inaczej, tutaj pokazuję ideę.

        // Ustawienie ręczne pola statusComboBox
        // Tu zamiana pola ComboBox na zwykłą listę (symulacja):
        controller.statusComboBox = new ComboBox<>();
        // updateAvailableStatuses dodaje do statusComboBox.getItems()
        controller.updateAvailableStatuses("Oczekujące");
        var items = controller.statusComboBox.getItems();

        assertTrue(items.contains("Rozpoczęte"));
        assertTrue(items.contains("W trakcie"));
        assertTrue(items.contains("Zakończone"));
        assertFalse(items.contains("Oczekujące"));

        controller.updateAvailableStatuses("W trakcie");
        items = controller.statusComboBox.getItems();
        assertTrue(items.contains("Zakończone"));
        assertFalse(items.contains("Rozpoczęte"));
        assertFalse(items.contains("Oczekujące"));
    }
}
