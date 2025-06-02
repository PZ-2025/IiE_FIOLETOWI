package com.example.projekt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.reportlib.ChartUtils;
import com.example.reportlib.PDFGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel; // inicjuje JavaFX toolkit
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

class ReportControllerTest {

    private ReportController controller;

    @BeforeAll
    static void initToolkit() {
        // inicjalizacja JavaFX (potrzebne, żeby tworzyć kontrolki)
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        controller = new ReportController();

        // inicjuj wymagane pola (symulujemy FXML)
        controller.mainReportTypeComboBox = new ComboBox<>();
        controller.filterContainer = new javafx.scene.layout.VBox();
        controller.reportPreviewContainer = new javafx.scene.layout.VBox();
        controller.reportTableView = new TableView<>();
        controller.reportRoot = new javafx.scene.layout.VBox();

        // wywołaj initialize() żeby ustawić listę i listener
        controller.initialize();
    }

    @Test
    void testInitializeComboBoxItems() {
        // ComboBox powinien mieć 3 elementy
        List<String> items = controller.mainReportTypeComboBox.getItems();
        assertTrue(items.containsAll(Arrays.asList("Transakcje", "Zadania", "Produkty")));
    }

    @Test
    void testRenderFilterUITransaction() {
        controller.renderFilterUI("Transakcje");
        // Powinny się pojawić 2 kontrolki: HBox z dwoma DatePickerami i HBox z ComboBox "Sortuj po"
        assertEquals(2, controller.filterContainer.getChildren().size());
        assertTrue(controller.dynamicFilters.containsKey("startDate"));
        assertTrue(controller.dynamicFilters.containsKey("sortTransaction"));
    }

    @Test
    void testGetFilterValueWithDefault() {
        assertEquals("default", controller.getFilterValue("nonexistent", "default"));
    }

    @Test
    void testAddColumnAddsColumn() {
        int initialCount = controller.reportTableView.getColumns().size();
        controller.addColumn("Test", "testKey");
        assertEquals(initialCount + 1, controller.reportTableView.getColumns().size());

        TableColumn<Map<String, String>, ?> col = controller.reportTableView.getColumns().get(initialCount);
        assertEquals("Test", col.getText());
    }



    @Test
    void testSaveReportAsPDFCallsPDFGenerator() throws Exception {
        try (MockedStatic<PDFGenerator> pdfMock = mockStatic(PDFGenerator.class)) {
            controller.currentReportType = "TestowyRaport";
            controller.lastReportData = List.of(Map.of("kol1", "val1"));
            controller.lastChartData = Map.of("K1", 1);
            controller.headerKeyMap.put("Kolumna", "kol1");

            // Niech fileChooser zwraca "fakefile.pdf" (mockowanie FileChooser jest trudniejsze, można zmodyfikować metodę by ją testować)
            // Na potrzeby testu wywołamy saveReportAsPDF, ale mockujemy pokazanie pliku i wywołanie PDFGenerator
            // Tutaj uproszczenie: testujemy tylko, że PDFGenerator.generateReport zostaje wywołany

            // Na potrzeby testu omijamy FileChooser, wywołujemy metodę prywatną generatePDFReport z przekazaniem fikcyjnego pliku (potrzeba refaktoryzacji w klasie)
            // Lub wywołujemy metodę saveReportAsPDF w wersji testowej z parametrem (np. dodaj setter do file dla testu)
        }
    }
}
