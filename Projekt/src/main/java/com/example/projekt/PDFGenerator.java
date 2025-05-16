package com.example.projekt;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public static void generateReport(String reportType, List<Map<String, String>> tableData,
                                      String[] headers, File outputFile, Map<String, Integer> chartData) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Dodaj logo firmy
            InputStream logoStream = PDFGenerator.class.getClassLoader().getResourceAsStream("HurtPolSan.png");
            if (logoStream != null) {
                File tempLogoFile = new File("HurtPolSan_temp.png");
                Files.copy(logoStream, tempLogoFile.toPath());
                Image logo = new Image(ImageDataFactory.create(tempLogoFile.getAbsolutePath()));
                logo.setWidth(100);
                document.add(logo);
                tempLogoFile.delete();
            }

            // Dodaj tytuł i datę
            document.add(new Paragraph("Raport: " + reportType).setFontSize(18).setBold());
            document.add(new Paragraph("Data: " + LocalDate.now()).setFontSize(10));

            // Dodaj wykres, jeśli dostępny
            if (chartData != null && !chartData.isEmpty()) {
                document.add(new Paragraph("Wykres:").setFontSize(14).setMarginTop(20));
                String chartPath = "chart.png";
                ChartUtils.saveChartAsPNG(chartData, "", chartPath);
                document.add(new Image(ImageDataFactory.create(chartPath)).scaleToFit(500, 300));
            }

            // Tworzenie tabeli
            Table table = new Table(headers.length);
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header)));
            }

            // Dodawanie danych do tabeli
            for (Map<String, String> row : tableData) {
                for (String key : headers) {
                    String value = row.get(key);
                    if (value == null) {
                        value = "";
                    }
                    table.addCell(value);
                }
            }


            // Dodanie tabeli do dokumentu
            document.add(table);

            // Zamknięcie dokumentu
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
