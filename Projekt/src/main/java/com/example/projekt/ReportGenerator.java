package com.example.projekt;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class ReportGenerator {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String DB_USER = "your_database_user";
    private static final String DB_PASSWORD = "your_database_password";

    // Pobieranie danych z bazy
    public static List<Map<String, Object>> fetchDataFromDatabase(java.sql.Date startDate, java.sql.Date endDate, String reportType) {
        String query = "";

        if (reportType.equals("Transakcje")) {
            query = "SELECT t.data_transakcji, p.nazwa AS produkt, t.ilosc, pr.imie, pr.nazwisko "
                    + "FROM transakcje t "
                    + "JOIN produkty p ON t.id_produktu = p.id_produktu "
                    + "JOIN pracownicy pr ON t.id_pracownika = pr.id_pracownika "
                    + "WHERE t.data_transakcji BETWEEN ? AND ? ORDER BY t.data_transakcji";
        } else if (reportType.equals("Zadania")) {
            query = "SELECT z.nazwa AS zadanie, pr.imie, pr.nazwisko, s.nazwa AS status, p.nazwa AS priorytet, z.data_rozpoczęcia "
                    + "FROM zadania z "
                    + "JOIN pracownicy pr ON z.id_pracownika = pr.id_pracownika "
                    + "JOIN statusy s ON z.id_statusu = s.id_statusu "
                    + "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu "
                    + "WHERE z.data_rozpoczęcia BETWEEN ? AND ? ORDER BY z.data_rozpoczęcia";
        } else if (reportType.equals("Produkty")) {
            query = "SELECT p.nazwa AS produkt, p.stan, p.limit_stanow "
                    + "FROM produkty p "
                    + "WHERE p.stan < p.limit_stanow ORDER BY p.nazwa";
        }

        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Generowanie raportu PDF
    public static void generatePdfReport(List<Map<String, Object>> reportData, String reportType) {
        try {
            // Wybór lokalizacji zapisu pliku PDF
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Wybierz lokalizację do zapisania raportu");
            fileChooser.setSelectedFile(new File(reportType + "_raport.pdf"));
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Tworzenie dokumentu PDF
                PdfWriter writer = new PdfWriter(fileToSave);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Dodanie tytułu
                document.add(new Paragraph(reportType + " - Raport"));
                document.add(new Paragraph("Generowane dane:"));

                // Tworzenie tabeli
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 1})).useAllAvailableWidth();

                // Dodanie nagłówków
                table.addCell(new Cell().add(new Paragraph("Data")));
                table.addCell(new Cell().add(new Paragraph("Produkt/Zadanie")));
                table.addCell(new Cell().add(new Paragraph("Ilość/Status")));
                table.addCell(new Cell().add(new Paragraph("Pracownik")));

                // Dodanie danych
                for (Map<String, Object> row : reportData) {
                    for (String column : row.keySet()) {
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(row.get(column)))));
                    }
                }

                document.add(table);
                document.close();

                System.out.println("Raport zapisany do pliku: " + fileToSave.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metoda generująca raport
    public static void generateReport(java.util.Date startDate, java.util.Date endDate, String reportType) {
        // Convert java.util.Date to java.sql.Date
        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

        // Fetch data using the converted dates
        List<Map<String, Object>> reportData = fetchDataFromDatabase(sqlStartDate, sqlEndDate, reportType);
        generatePdfReport(reportData, reportType);
    }

    public static void main(String[] args) {
        // Możesz dodać UI do wprowadzania dat lub ustawienia domyślne daty
        java.util.Date startDate = new java.util.Date(); // Przykładowa data
        java.util.Date endDate = new java.util.Date(); // Przykładowa data
        String reportType = "Transakcje"; // Typ raportu

        generateReport(startDate, endDate, reportType);
    }
}
