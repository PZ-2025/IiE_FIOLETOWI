package com.example.projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private TableView<?> reportsTable;
    @FXML
    private TableColumn<?, ?> dateColumn;
    @FXML
    private TableColumn<?, ?> typeColumn;
    @FXML
    private TableColumn<?, ?> detailsColumn;
    @FXML
    private TableColumn<?, ?> statusColumn;

    @FXML
    public void initialize() {
        reportTypeComboBox.getItems().addAll("Transakcje", "Zadania", "Produkty");
    }

    @FXML
    public void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String reportType = reportTypeComboBox.getValue();

        if (startDate == null || endDate == null || reportType == null) {
            showAlert("Proszę wybrać daty początkową i końcową oraz typ raportu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            generatePdfReport(file, startDate, endDate, reportType);
        }
    }

    private void generatePdfReport(File file, LocalDate startDate, LocalDate endDate, String reportType) {
        try {
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            try {
                Image logo = new Image(ImageDataFactory.create("logo_hurtpolsan.png"));
                logo.setAutoScale(true);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Nie można załadować logo: " + e.getMessage());
            }

            document.add(new Paragraph("Raport HurtPolSan").setFontSize(16).setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("Data: " + LocalDate.now()));
            document.add(new Paragraph("Okres: " + startDate + " do " + endDate));
            document.add(new Paragraph("Typ raportu: " + reportType));
            document.add(new Paragraph(" "));

            List<String[]> reportData = fetchDataForPeriod(startDate, endDate, reportType);

            if (reportData.isEmpty()) {
                document.add(new Paragraph("Brak danych do wyświetlenia."));
            } else {
                Table table = new Table(3);
                table.addHeaderCell("Data");
                table.addHeaderCell("Opis");
                table.addHeaderCell("Wartość");

                for (String[] row : reportData) {
                    table.addCell(new Cell().add(new Paragraph(row[0])));
                    table.addCell(new Cell().add(new Paragraph(row[1])));
                    table.addCell(new Cell().add(new Paragraph(row[2])));
                }

                document.add(table);
            }

            document.add(new Paragraph("\nDokument wygenerowany automatycznie."));
            document.close();

            showAlert("Raport został zapisany.");
        } catch (FileNotFoundException e) {
            showAlert("Nie można zapisać pliku: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Wystąpił błąd podczas generowania raportu.");
        }
    }

    private List<String[]> fetchDataForPeriod(LocalDate startDate, LocalDate endDate, String reportType) {
        if (reportType.equals("Transakcje")) {
            return List.of(
                    new String[]{"2025-05-01", "Sprzedaż A", "1000"},
                    new String[]{"2025-05-02", "Sprzedaż B", "2000"}
            );
        } else if (reportType.equals("Zadania")) {
            return List.of(
                    new String[]{"2025-05-01", "Zadanie A", "500"},
                    new String[]{"2025-05-03", "Zadanie B", "750"}
            );
        } else if (reportType.equals("Produkty")) {
            return List.of(
                    new String[]{"2025-05-01", "Produkt X", "2000"},
                    new String[]{"2025-05-02", "Produkt Y", "1000"}
            );
        }
        return new ArrayList<>();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        showAlert("Przejście do panelu głównego (dashboard)...");
        // Tu możesz dodać logikę przejścia do innej sceny.
    }
}
