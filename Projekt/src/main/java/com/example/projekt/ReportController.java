package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.reportlib.ChartUtils;
import com.example.reportlib.PDFGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ReportController {

    @FXML private ComboBox<String> mainReportTypeComboBox;
    @FXML private VBox filterContainer;
    @FXML private VBox reportPreviewContainer;
    @FXML private TableView<Map<String, String>> reportTableView;

    private final Map<String, Control> dynamicFilters = new HashMap<>();
    private String currentReportType;

    @FXML
    public void initialize() {
        mainReportTypeComboBox.getItems().addAll("Transakcje", "Zadania", "Produkty");
        mainReportTypeComboBox.setOnAction(e -> {
            currentReportType = mainReportTypeComboBox.getValue();
            renderFilterUI(currentReportType);
        });
    }

    private void renderFilterUI(String type) {
        filterContainer.getChildren().clear();
        dynamicFilters.clear();

        switch (type) {
            case "Transakcje" -> setupTransactionFilters();
            case "Zadania" -> setupTaskFilters();
            case "Produkty" -> setupProductFilters();
        }
    }

    private void setupTransactionFilters() {
        addDateRangePickers();
        addComboBox("Sortuj po", "sortTransaction", List.of("Data", "Produkt", "Ilość"));
    }

    private void setupTaskFilters() {
        addDateRangePickers();
        addComboBox("Status", "status", getDataFromDatabase("SELECT nazwa FROM statusy"));
        addComboBox("Priorytet", "priority", getDataFromDatabase("SELECT nazwa FROM priorytety"));
        addComboBox("Sortuj po", "sortTask", List.of("Data rozpoczęcia", "Priorytet", "Status"));
    }

    private void setupProductFilters() {
        addComboBox("Stan magazynowy", "stockFilter", List.of("Wszystkie", "Tylko poniżej limitu"));
        addComboBox("Sortuj po", "sortProduct", List.of("Nazwa", "Stan", "Cena"));
    }

    private void addDateRangePickers() {
        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();
        start.setPromptText("Data od");
        end.setPromptText("Data do");
        dynamicFilters.put("startDate", start);
        dynamicFilters.put("endDate", end);
        filterContainer.getChildren().add(new HBox(10, start, end));
    }

    private void addComboBox(String label, String key, List<String> items) {
        Label lbl = new Label(label);
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(items));
        cb.setPrefWidth(200);
        filterContainer.getChildren().add(new HBox(10, lbl, cb));
        dynamicFilters.put(key, cb);
    }

    private List<String> getDataFromDatabase(String query) {
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @FXML
    public void generateReport() {
        reportTableView.getColumns().clear();
        reportTableView.getItems().clear();
        reportPreviewContainer.getChildren().clear();

        switch (currentReportType) {
            case "Transakcje" -> generateTransactionPreview();
            case "Zadania"     -> generateTaskPreview();
            case "Produkty"    -> generateProductPreview();
        }

        reportPreviewContainer.getChildren().add(reportTableView);
    }

    private void generateTransactionPreview() {
        String sortKey = getFilterValue("sortTransaction","Data");
        List<Map<String, String>> data = getTransactionData(sortKey);

        addColumn("Produkt", "Produkt");
        addColumn("Data", "Data");
        addColumn("Ilosc", "Ilosc");

        reportTableView.setItems(FXCollections.observableArrayList(data));

        Map<String, Integer> chartData = getTransactionChartData();
        ImageView chart = ChartUtils.createChartImage(chartData, "Sprzedaż dzienna");
        reportPreviewContainer.getChildren().add(chart);
    }

    private List<Map<String, String>> getTransactionData(String sortKey) {
        List<Map<String, String>> list = new ArrayList<>();
        String orderBy;
        switch (sortKey) {
            case "Produkt" -> orderBy = "p.nazwa";
            case "Data" -> orderBy = "t.data_transakcji";
            case "Ilość" -> orderBy = "t.ilosc";
            default -> orderBy = "t.data_transakcji";
        }

        String query = "SELECT p.nazwa AS produkt, t.data_transakcji AS data, t.ilosc AS ilosc " +
                "FROM transakcje t " +
                "JOIN produkty p ON t.id_produktu = p.id_produktu " +
                "ORDER BY " + orderBy;

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("Produkt", rs.getString("produkt"));
                row.put("Data", rs.getString("data"));
                row.put("Ilosc", rs.getString("ilosc"));
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Map<String, Integer> getTransactionChartData() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT t.data_transakcji, SUM(t.ilosc) AS total FROM transakcje t GROUP BY t.data_transakcji";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.put(rs.getString("data_transakcji"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


    private void generateTaskPreview() {
        String sortKey = Optional.ofNullable(getFilterValue("sortTask", "Data rozpoczęcia")).orElse("Data rozpoczęcia");
        String statusFilter = getFilterValue("status", null);
        String priorityFilter = getFilterValue("priority", null);

        LocalDate start = ((DatePicker) dynamicFilters.get("startDate")).getValue();
        LocalDate end = ((DatePicker) dynamicFilters.get("endDate")).getValue();

        System.out.println("Filtracja zadań: sortKey=" + sortKey + ", status=" + statusFilter + ", priority=" + priorityFilter);
        System.out.println("Zakres dat: start=" + start + ", end=" + end);

        List<Map<String, String>> data = getTaskData(sortKey, statusFilter, priorityFilter, start, end);

        // Logowanie pobranych danych
        System.out.println("Dane zadań: " + data);

        addColumn("Zadanie", "task");
        addColumn("Status", "status");
        addColumn("Priorytet", "priority");
        addColumn("Data rozpoczęcia", "start");

        reportTableView.setItems(FXCollections.observableArrayList(data));

        Map<String, Integer> chartData = getTaskChartData(statusFilter, priorityFilter, start, end);
        ImageView chart = ChartUtils.createChartImage(chartData, "Liczba zadań wg statusu");
        reportPreviewContainer.getChildren().add(chart);
    }




    private List<Map<String, String>> getTaskData(String sortKey, String statusFilter, String priorityFilter, LocalDate start, LocalDate end) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT z.nazwa, s.nazwa AS status, p.nazwa AS priority, z.data_rozpoczecia " +
                        "FROM zadania z " +
                        "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                        "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 "
        );

        if (start != null) query.append("AND z.data_rozpoczecia >= ? ");
        if (end != null) query.append("AND z.data_rozpoczecia <= ? ");
        if (statusFilter != null) query.append("AND s.nazwa = ? ");
        if (priorityFilter != null) query.append("AND p.nazwa = ? ");

        query.append("ORDER BY ");
        query.append(switch (sortKey) {
            case "Priorytet" -> "p.nazwa";
            case "Status" -> "s.nazwa";
            default -> "z.data_rozpoczecia";
        });

        System.out.println("Zapytanie SQL: " + query); // Logowanie zapytania SQL

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (start != null) stmt.setDate(index++, Date.valueOf(start));
            if (end != null) stmt.setDate(index++, Date.valueOf(end));
            if (statusFilter != null) stmt.setString(index++, statusFilter);
            if (priorityFilter != null) stmt.setString(index++, priorityFilter);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("task", rs.getString("nazwa"));
                row.put("status", rs.getString("status"));
                row.put("priority", rs.getString("priority"));
                row.put("start", rs.getString("data_rozpoczecia"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Logowanie danych
        System.out.println("Dane zadań po zapytaniu: " + list);
        return list;
    }



    private Map<String, Integer> getTaskChartData(String statusFilter, String priorityFilter, LocalDate start, LocalDate end) {
        Map<String, Integer> data = new LinkedHashMap<>();
        StringBuilder query = new StringBuilder(
                "SELECT s.nazwa AS status, COUNT(*) AS total " +
                        "FROM zadania z " +
                        "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                        "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 "
        );

        if (statusFilter != null) query.append("AND s.nazwa = ? ");
        if (priorityFilter != null) query.append("AND p.nazwa = ? ");
        if (start != null) query.append("AND z.data_rozpoczecia >= ? ");
        if (end != null) query.append("AND z.data_rozpoczecia <= ? ");

        query.append("GROUP BY s.nazwa");

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (statusFilter != null) stmt.setString(index++, statusFilter);
            if (priorityFilter != null) stmt.setString(index++, priorityFilter);
            if (start != null) stmt.setDate(index++, Date.valueOf(start));
            if (end != null) stmt.setDate(index++, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("status"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void generateProductPreview() {
        String sortKey = getFilterValue("sortProduct", "Nazwa");
        List<Map<String, String>> data = getProductData(sortKey);

        addColumn("Produkt", "product");
        addColumn("Stan magazynowy", "stock");
        addColumn("Cena", "price");

        reportTableView.setItems(FXCollections.observableArrayList(data));
    }

    private List<Map<String, String>> getProductData(String sortKey) {
        List<Map<String, String>> list = new ArrayList<>();

        String orderBy = switch (sortKey) {
            case "Stan" -> "stan";
            case "Cena" -> "cena";
            default -> "nazwa";
        };

        String query = "SELECT nazwa, stan, cena FROM produkty ORDER BY " + orderBy;

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("product", rs.getString("nazwa"));
                row.put("stock", rs.getString("stan"));
                row.put("price", rs.getString("cena"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Map<String, Integer> getProductChartData() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT nazwa, SUM(stan) AS total FROM produkty GROUP BY nazwa";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.put(rs.getString("nazwa"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


    private String getFilterValue(String key, String defaultValue) {
        return Optional.ofNullable(dynamicFilters.get(key))
                .filter(c -> c instanceof ComboBox<?>)
                .map(c -> ((ComboBox<String>) c).getValue())
                .orElse(defaultValue);
    }

    private void addColumn(String header, String key) {
        TableColumn<Map<String, String>, String> col = new TableColumn<>(header);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(key)));
        reportTableView.getColumns().add(col);
    }

    @FXML
    public void saveReportAsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                // Pobierz dane z tabeli
                List<Map<String, String>> data = new ArrayList<>(reportTableView.getItems());

                // Pobierz nagłówki kolumn
                String[] headers = reportTableView.getColumns().stream().map(TableColumn::getText).toArray(String[]::new);

                // Pobierz dane wykresu w zależności od typu raportu
                Map<String, Integer> chartData = null;
                switch (currentReportType) {
                    case "Transakcje":
                        chartData = getTransactionChartData(); // Wykres dla transakcji
                        break;
                    case "Zadania":
                        chartData = getTaskChartData(null, null, null, null); // Wykres dla zadań
                        break;
                    case "Produkty":
                        chartData = getProductChartData(); // Wykres dla produktów
                        break;
                }

                // Generowanie raportu PDF
                PDFGenerator.generateReport(currentReportType, data, headers, file, chartData);
                showAlert("Zapisano raport do pliku: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Błąd zapisu PDF.");
            }
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Parent root = loader.load();

            // Pobranie aktualnej sceny
            Stage stage = (Stage) mainReportTypeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
