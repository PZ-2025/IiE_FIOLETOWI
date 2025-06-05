package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.reportlib.ChartUtils;
import com.example.reportlib.PDFGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Kontroler odpowiedzialny za zarządzanie generowaniem i wyświetlaniem raportów w aplikacji.
 * Obsługuje różne typy raportów: transakcje, zadania i produkty.
 * Zapewnia funkcje filtrowania danych, generowania wykresów oraz eksportu do PDF.
 *
 * @author Twój autor
 * @version 1.0
 */
public class ReportController {

    /** Pole wyboru typu głównego raportu */
    @FXML
    protected ComboBox<String> mainReportTypeComboBox;

    /** Kontener dla dynamicznych filtrów */
    @FXML
    protected VBox filterContainer;

    /** Kontener dla podglądu raportu */
    @FXML
    protected VBox reportPreviewContainer;

    /** Tabela wyświetlająca dane raportu */
    @FXML
    protected TableView<Map<String, String>> reportTableView;

    /** Główny kontener interfejsu raportów */
    @FXML
    protected VBox reportRoot;

    /** Mapa przechowująca dynamiczne kontrolki filtrów */
    public final Map<String, Control> dynamicFilters = new HashMap<>();

    /** Aktualnie wybrany typ raportu */
    protected String currentReportType;

    /** Ostatnie wygenerowane dane raportu */
    protected List<Map<String, String>> lastReportData;

    /** Ostatnie dane wykresu */
    protected Map<String, Integer> lastChartData;

    /** Mapa mapująca nagłówki kolumn na klucze danych */
    protected final Map<String, String> headerKeyMap = new LinkedHashMap<>();

    /** Lista CheckBox-ów dla statusów */
    private List<CheckBox> statusCheckBoxes = new ArrayList<>();

    /** Lista CheckBox-ów dla priorytetów */
    private List<CheckBox> priorityCheckBoxes = new ArrayList<>();

    /** Lista CheckBox-ów dla typów produktów */
    private List<CheckBox> productTypeCheckBoxes = new ArrayList<>();

    private User currentUser;

    private boolean hasPermissionToViewAll() {
        return currentUser.isAdmin() || currentUser.isManager();
    }

    /**
     * Inicjalizuje kontroler po załadowaniu FXML.
     * Konfiguruje nasłuchiwacze zdarzeń, ustawia opcje raportów i stosuje motyw aplikacji.
     */
    @FXML
    public void initialize() {
        reportRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSizeLabel());
            }
        });

        // Ustawienie polityki zmiany rozmiaru tabeli
        reportTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainReportTypeComboBox.getItems().addAll("Transakcje", "Zadania", "Produkty");
        mainReportTypeComboBox.setOnAction(e -> {
            currentReportType = mainReportTypeComboBox.getValue();
            renderFilterUI(currentReportType);

            // Czyszczenie podglądu po zmianie typu raportu
            reportTableView.getColumns().clear();
            reportTableView.getItems().clear();
            reportPreviewContainer.getChildren().clear();
            headerKeyMap.clear();
            lastReportData = null;
            lastChartData = null;
        });

        currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/login.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Czyści wszystkie filtry i resetuje wyświetlanie raportu.
     * Resetuje wartości w polach ComboBox i DatePicker do wartości domyślnych.
     */
    @FXML
    private void clearFilters() {
        for (Map.Entry<String, Control> entry : dynamicFilters.entrySet()) {
            Control control = entry.getValue();
            if (control instanceof ComboBox<?>) {
                ((ComboBox<?>) control).setValue(null);
            } else if (control instanceof DatePicker) {
                ((DatePicker) control).setValue(null);
            }
        }

        // Resetowanie CheckBox-ów
        statusCheckBoxes.forEach(cb -> cb.setSelected(false));
        priorityCheckBoxes.forEach(cb -> cb.setSelected(false));
        productTypeCheckBoxes.forEach(cb -> cb.setSelected(false));

        reportTableView.getItems().clear();
        reportTableView.getColumns().clear();
        reportPreviewContainer.getChildren().clear();
        lastReportData = null;
        lastChartData = null;
    }

    /**
     * Renderuje interfejs filtrów na podstawie wybranego typu raportu.
     * Dynamicznie tworzy odpowiednie kontrolki filtrujące.
     *
     * @param type typ raportu ("Transakcje", "Zadania", "Produkty")
     */
    protected void renderFilterUI(String type) {
        filterContainer.getChildren().clear();
        dynamicFilters.clear();
        statusCheckBoxes.clear();
        priorityCheckBoxes.clear();
        productTypeCheckBoxes.clear();

        switch (type) {
            case "Transakcje" -> setupTransactionFilters();
            case "Zadania" -> setupTaskFilters();
            case "Produkty" -> setupProductFilters();
        }
    }

    /**
     * Konfiguruje filtry dla raportów transakcji.
     * Dodaje pola wyboru zakresu dat i opcje sortowania.
     */
    private void setupTransactionFilters() {
        addDateRangePickers();
        addProductTypeCheckboxes();
        addComboBox("Sortuj po", "sortTransaction", List.of("Data", "Produkt", "Ilość", "Typ produktu"));

        // Dodaj filtry w zależności od roli
        if (hasPermissionToViewAll()) {
            addComboBox("Zakres danych", "transactionScope",
                    List.of("Moje transakcje", "Wszystkie transakcje"));
        }
    }

    /**
     * Konfiguruje filtry dla raportów zadań.
     * Dodaje pola wyboru zakresu dat, statusu, priorytetu i opcje sortowania.
     */
    private void setupTaskFilters() {
        addDateRangePickers();

        // Dodawanie CheckBox-ów dla statusów
        Label statusLabel = new Label("Status:");
        VBox statusBox = new VBox(5);
        statusBox.getChildren().add(statusLabel);

        List<String> statuses = getDataFromDatabase("SELECT nazwa FROM statusy");
        statuses.forEach(status -> {
            CheckBox cb = new CheckBox(status);
            statusCheckBoxes.add(cb);
            statusBox.getChildren().add(cb);
        });
        filterContainer.getChildren().add(statusBox);

        // Dodawanie CheckBox-ów dla priorytetów
        Label priorityLabel = new Label("Priorytet:");
        VBox priorityBox = new VBox(5);
        priorityBox.getChildren().add(priorityLabel);

        List<String> priorities = getDataFromDatabase("SELECT nazwa FROM priorytety");
        priorities.forEach(priority -> {
            CheckBox cb = new CheckBox(priority);
            priorityCheckBoxes.add(cb);
            priorityBox.getChildren().add(cb);
        });
        filterContainer.getChildren().add(priorityBox);

        addComboBox("Sortuj po", "sortTask", List.of("Data rozpoczęcia", "Priorytet", "Status"));

        if (hasPermissionToViewAll()) {
            addComboBox("Zakres danych", "taskScope",
                    List.of("Moje zadania", "Wszystkie zadania"));
        }
    }

    /**
     * Konfiguruje filtry dla raportów produktów.
     * Dodaje filtry stanu magazynowego i opcje sortowania.
     */
    private void setupProductFilters() {
        addProductTypeCheckboxes();
        addComboBox("Stan magazynowy", "stockFilter", List.of("Wszystkie", "Tylko poniżej limitu"));
        addComboBox("Sortuj po", "sortProduct", List.of("Nazwa", "Stan", "Cena", "Typ produktu"));
    }

    /**
     * Dodaje pola wyboru zakresu dat (od-do) do interfejsu filtrów.
     * Tworzy dwa DatePickery dla daty początkowej i końcowej.
     */
    private void addDateRangePickers() {
        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();
        start.setPromptText("Data od");
        end.setPromptText("Data do");
        dynamicFilters.put("startDate", start);
        dynamicFilters.put("endDate", end);
        filterContainer.getChildren().add(new HBox(10, start, end));
    }

    /**
     * Dodaje pole ComboBox z etykietą do interfejsu filtrów.
     *
     * @param label etykieta wyświetlana obok pola
     * @param key klucz używany do identyfikacji filtru
     * @param items lista opcji do wyboru
     */
    private void addComboBox(String label, String key, List<String> items) {
        Label lbl = new Label(label);
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(items));
        cb.setPrefWidth(200);
        filterContainer.getChildren().add(new HBox(10, lbl, cb));
        dynamicFilters.put(key, cb);
    }

    /**
     * Dodaje CheckBox-y dla typów produktów do interfejsu filtrów.
     */
    private void addProductTypeCheckboxes() {
        Label typeLabel = new Label("Typ produktu:");
        VBox typeBox = new VBox(5);
        typeBox.getChildren().add(typeLabel);

        List<String> types = getDataFromDatabase("SELECT nazwa FROM typ_produktu");
        types.forEach(type -> {
            CheckBox cb = new CheckBox(type);
            productTypeCheckBoxes.add(cb);
            typeBox.getChildren().add(cb);
        });
        filterContainer.getChildren().add(typeBox);
    }

    /**
     * Pobiera dane z bazy danych na podstawie zapytania SQL.
     *
     * @param query zapytanie SQL do wykonania
     * @return lista ciągów znaków będących wynikiem zapytania
     */
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

    /**
     * Generuje raport na podstawie aktualnie wybranego typu i filtrów.
     * Czyści poprzednie dane i wyświetla nowy raport z wykresem.
     */
    @FXML
    public void generateReport() {
        reportTableView.getColumns().clear();
        reportTableView.getItems().clear();
        reportPreviewContainer.getChildren().clear();

        headerKeyMap.clear();

        switch (currentReportType) {
            case "Transakcje" -> generateTransactionPreview();
            case "Zadania"     -> generateTaskPreview();
            case "Produkty"    -> generateProductPreview();
        }

        reportPreviewContainer.getChildren().add(reportTableView);
    }

    /**
     * Generuje podgląd raportu transakcji.
     * Pobiera dane transakcji z bazy, tworzy tabelę i wykres sprzedaży dziennej.
     */
    private void generateTransactionPreview() {
        String sortKey = getFilterValue("sortTransaction","Data");

        LocalDate start = ((DatePicker) dynamicFilters.get("startDate")).getValue();
        LocalDate end = ((DatePicker) dynamicFilters.get("endDate")).getValue();

        List<Map<String, String>> data = getTransactionData(sortKey, start, end);

        addColumn("Produkt", "Produkt");
        addColumn("Typ produktu", "TypProduktu");
        addColumn("Data", "Data");
        addColumn("Ilosc", "Ilosc");

        if (!data.isEmpty()) {
            reportTableView.setItems(FXCollections.observableArrayList(data));
        } else {
            showAlert("Brak danych spełniających kryteria wyszukiwania.");
        }

        lastReportData = data;

        Map<String, Integer> chartData = getTransactionChartData(start, end);
        lastChartData = chartData;

        if (!chartData.isEmpty()) {
            ImageView chart = ChartUtils.createChartImage(chartData, "Sprzedaż dzienna");
            reportPreviewContainer.getChildren().removeIf(node -> node instanceof ImageView);
            reportPreviewContainer.getChildren().add(chart);
        }
    }

    /**
     * Pobiera dane transakcji z bazy danych z zastosowaniem filtrów.
     *
     * @param sortKey klucz sortowania ("Produkt", "Data", "Ilość")
     * @param start data początkowa zakresu (może być null)
     * @param end data końcowa zakresu (może być null)
     * @return lista map zawierających dane transakcji
     */
    private List<Map<String, String>> getTransactionData(String sortKey, LocalDate start, LocalDate end) {
        List<Map<String, String>> list = new ArrayList<>();
        String orderBy = switch (sortKey) {
            case "Produkt" -> "p.nazwa";
            case "Data" -> "t.data_transakcji";
            case "Ilość" -> "t.ilosc";
            case "Typ produktu" -> "tp.nazwa";
            default -> "t.data_transakcji";
        };

        // Pobierz wybrane typy produktów
        List<String> selectedTypes = productTypeCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        StringBuilder query = new StringBuilder(
                "SELECT p.nazwa AS produkt, t.data_transakcji AS data, t.ilosc AS ilosc, tp.nazwa AS typ_produktu " +
                        "FROM transakcje t " +
                        "JOIN produkty p ON t.id_produktu = p.id_produktu " +
                        "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                        "WHERE 1=1 "
        );

        // Domyślnie pokazuj tylko swoje transakcje
        query.append("AND t.id_pracownika = ").append(currentUser.getId()).append(" ");

        // Jeśli użytkownik ma uprawnienia i wybrał "Wszystkie transakcje"
        if (hasPermissionToViewAll() && dynamicFilters.containsKey("transactionScope")) {
            String scope = ((ComboBox<String>)dynamicFilters.get("transactionScope")).getValue();
            if ("Wszystkie transakcje".equals(scope)) {
                query = new StringBuilder(
                        "SELECT p.nazwa AS produkt, t.data_transakcji AS data, t.ilosc AS ilosc, tp.nazwa AS typ_produktu " +
                                "FROM transakcje t " +
                                "JOIN produkty p ON t.id_produktu = p.id_produktu " +
                                "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                                "WHERE 1=1 ");
            }
        }

        if (start != null) query.append("AND t.data_transakcji >= ? ");
        if (end != null) query.append("AND t.data_transakcji <= ? ");

        if (!selectedTypes.isEmpty()) {
            query.append("AND tp.nazwa IN (");
            for (int i = 0; i < selectedTypes.size(); i++) {
                query.append("?");
                if (i < selectedTypes.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        query.append("ORDER BY ").append(orderBy);

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (start != null) stmt.setDate(index++, Date.valueOf(start));
            if (end != null) stmt.setDate(index++, Date.valueOf(end));

            for (String type : selectedTypes) {
                stmt.setString(index++, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("Produkt", rs.getString("produkt"));
                row.put("Data", rs.getString("data"));
                row.put("Ilosc", rs.getString("ilosc"));
                row.put("TypProduktu", rs.getString("typ_produktu"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Pobiera dane do wykresu transakcji - sumaryczne ilości sprzedaży dziennie.
     *
     * @param start data początkowa zakresu (może być null)
     * @param end data końcowa zakresu (może być null)
     * @return mapa zawierająca daty jako klucze i sumaryczne ilości jako wartości
     */
    private Map<String, Integer> getTransactionChartData(LocalDate start, LocalDate end) {
        Map<String, Integer> data = new LinkedHashMap<>();

        // Pobierz wybrane typy produktów
        List<String> selectedTypes = productTypeCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        StringBuilder query = new StringBuilder(
                "SELECT t.data_transakcji, SUM(t.ilosc) AS total " +
                        "FROM transakcje t " +
                        "JOIN produkty p ON t.id_produktu = p.id_produktu " +
                        "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                        "WHERE 1=1 "
        );

        // Domyślnie pokazuj tylko swoje transakcje
        query.append("AND t.id_pracownika = ").append(currentUser.getId()).append(" ");

        // Jeśli użytkownik ma uprawnienia i wybrał "Wszystkie transakcje"
        if (hasPermissionToViewAll() && dynamicFilters.containsKey("transactionScope")) {
            String scope = ((ComboBox<String>)dynamicFilters.get("transactionScope")).getValue();
            if ("Wszystkie transakcje".equals(scope)) {
                query = new StringBuilder(
                        "SELECT t.data_transakcji, SUM(t.ilosc) AS total " +
                                "FROM transakcje t " +
                                "JOIN produkty p ON t.id_produktu = p.id_produktu " +
                                "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                                "WHERE 1=1 ");
            }
        }

        if (start != null) query.append("AND t.data_transakcji >= ? ");
        if (end != null) query.append("AND t.data_transakcji <= ? ");

        if (!selectedTypes.isEmpty()) {
            query.append("AND tp.nazwa IN (");
            for (int i = 0; i < selectedTypes.size(); i++) {
                query.append("?");
                if (i < selectedTypes.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        query.append("GROUP BY t.data_transakcji ORDER BY t.data_transakcji");

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (start != null) stmt.setDate(index++, Date.valueOf(start));
            if (end != null) stmt.setDate(index++, Date.valueOf(end));

            for (String type : selectedTypes) {
                stmt.setString(index++, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("data_transakcji"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd podczas generowania wykresu transakcji: " + e.getMessage());
        }
        return data;
    }

    /**
     * Generuje podgląd raportu zadań.
     * Pobiera dane zadań z bazy, tworzy tabelę i wykres liczby zadań według statusu.
     */
    private void generateTaskPreview() {
        String sortKey = Optional.ofNullable(getFilterValue("sortTask", "Data rozpoczęcia")).orElse("Data rozpoczęcia");

        // Pobranie wybranych statusów
        List<String> selectedStatuses = statusCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        // Pobranie wybranych priorytetów
        List<String> selectedPriorities = priorityCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        LocalDate start = ((DatePicker) dynamicFilters.get("startDate")).getValue();
        LocalDate end = ((DatePicker) dynamicFilters.get("endDate")).getValue();

        System.out.println("Filtracja zadań: sortKey=" + sortKey + ", statusy=" + selectedStatuses + ", priorytety=" + selectedPriorities);
        System.out.println("Zakres dat: start=" + start + ", end=" + end);

        List<Map<String, String>> data = getTaskData(sortKey, selectedStatuses, selectedPriorities, start, end);

        addColumn("Zadanie", "task");
        addColumn("Status", "status");
        addColumn("Priorytet", "priority");
        addColumn("Data rozpoczęcia", "start");

        reportTableView.setItems(FXCollections.observableArrayList(data));
        lastReportData = data;

        Map<String, Integer> chartData = getTaskChartData(selectedStatuses, selectedPriorities, start, end);
        lastChartData = chartData;

        ImageView chart = ChartUtils.createChartImage(chartData, "Liczba zadań wg statusu");
        reportPreviewContainer.getChildren().add(chart);
    }

    /**
     * Pobiera dane zadań z bazy danych z zastosowaniem filtrów.
     *
     * @param sortKey klucz sortowania ("Priorytet", "Status", "Data rozpoczęcia")
     * @param selectedStatuses lista wybranych statusów (może być pusta)
     * @param selectedPriorities lista wybranych priorytetów (może być pusta)
     * @param start data początkowa zakresu (może być null)
     * @param end data końcowa zakresu (może być null)
     * @return lista map zawierających dane zadań
     */
    private List<Map<String, String>> getTaskData(String sortKey, List<String> selectedStatuses, List<String> selectedPriorities, LocalDate start, LocalDate end) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT z.nazwa, s.nazwa AS status, p.nazwa AS priority, z.data_rozpoczecia " +
                        "FROM zadania z " +
                        "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                        "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 "
        );

        // Domyślnie pokazuj tylko swoje zadania
        query.append("AND z.id_pracownika = ").append(currentUser.getId()).append(" ");

        // Jeśli użytkownik ma uprawnienia i wybrał "Wszystkie zadania"
        if (hasPermissionToViewAll() && dynamicFilters.containsKey("taskScope")) {
            String scope = ((ComboBox<String>)dynamicFilters.get("taskScope")).getValue();
            if ("Wszystkie zadania".equals(scope)) {
                query = new StringBuilder(
                        "SELECT z.nazwa, s.nazwa AS status, p.nazwa AS priority, z.data_rozpoczecia " +
                                "FROM zadania z " +
                                "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                                "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 ");
            }
        }

        if (start != null) query.append("AND z.data_rozpoczecia >= ? ");
        if (end != null) query.append("AND z.data_rozpoczecia <= ? ");

        if (!selectedStatuses.isEmpty()) {
            query.append("AND s.nazwa IN (");
            for (int i = 0; i < selectedStatuses.size(); i++) {
                query.append("?");
                if (i < selectedStatuses.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        if (!selectedPriorities.isEmpty()) {
            query.append("AND p.nazwa IN (");
            for (int i = 0; i < selectedPriorities.size(); i++) {
                query.append("?");
                if (i < selectedPriorities.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        query.append("ORDER BY ");
        query.append(switch (sortKey) {
            case "Priorytet" -> "p.nazwa";
            case "Status" -> "s.nazwa";
            default -> "z.data_rozpoczecia";
        });

        System.out.println("Zapytanie SQL: " + query);

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (start != null) stmt.setDate(index++, Date.valueOf(start));
            if (end != null) stmt.setDate(index++, Date.valueOf(end));

            for (String status : selectedStatuses) {
                stmt.setString(index++, status);
            }

            for (String priority : selectedPriorities) {
                stmt.setString(index++, priority);
            }

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

        System.out.println("Dane zadań po zapytaniu: " + list);
        return list;
    }

    /**
     * Pobiera dane do wykresu zadań - liczba zadań według statusu.
     *
     * @param selectedStatuses lista wybranych statusów (może być pusta)
     * @param selectedPriorities lista wybranych priorytetów (może być pusta)
     * @param start data początkowa zakresu (może być null)
     * @param end data końcowa zakresu (może być null)
     * @return mapa zawierająca statusy jako klucze i liczby zadań jako wartości
     */
    private Map<String, Integer> getTaskChartData(List<String> selectedStatuses, List<String> selectedPriorities, LocalDate start, LocalDate end) {
        Map<String, Integer> data = new LinkedHashMap<>();
        StringBuilder query = new StringBuilder(
                "SELECT s.nazwa AS status, COUNT(*) AS total " +
                        "FROM zadania z " +
                        "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                        "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 "
        );

        // Domyślnie pokazuj tylko swoje zadania
        query.append("AND z.id_pracownika = ").append(currentUser.getId()).append(" ");

        // Jeśli użytkownik ma uprawnienia i wybrał "Wszystkie zadania"
        if (hasPermissionToViewAll() && dynamicFilters.containsKey("taskScope")) {
            String scope = ((ComboBox<String>)dynamicFilters.get("taskScope")).getValue();
            if ("Wszystkie zadania".equals(scope)) {
                query = new StringBuilder(
                        "SELECT s.nazwa AS status, COUNT(*) AS total " +
                                "FROM zadania z " +
                                "JOIN statusy s ON z.id_statusu = s.id_statusu " +
                                "JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu WHERE 1=1 ");
            }
        }

        if (!selectedStatuses.isEmpty()) {
            query.append("AND s.nazwa IN (");
            for (int i = 0; i < selectedStatuses.size(); i++) {
                query.append("?");
                if (i < selectedStatuses.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        if (!selectedPriorities.isEmpty()) {
            query.append("AND p.nazwa IN (");
            for (int i = 0; i < selectedPriorities.size(); i++) {
                query.append("?");
                if (i < selectedPriorities.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        if (start != null) query.append("AND z.data_rozpoczecia >= ? ");
        if (end != null) query.append("AND z.data_rozpoczecia <= ? ");

        query.append("GROUP BY s.nazwa");

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            for (String status : selectedStatuses) {
                stmt.setString(index++, status);
            }

            for (String priority : selectedPriorities) {
                stmt.setString(index++, priority);
            }

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

    /**
     * Generuje podgląd raportu produktów.
     * Pobiera dane produktów z bazy, tworzy tabelę i wykres stanu magazynowego.
     */
    private void generateProductPreview() {
        String sortKey = getFilterValue("sortProduct", "Nazwa");
        String stockFilter = getFilterValue("stockFilter", "Wszystkie");
        List<Map<String, String>> data = getProductData(sortKey, stockFilter);

        // Czyszczenie i konfiguracja tabeli
        reportTableView.getColumns().clear();
        reportTableView.getItems().clear();

        addColumn("Produkt", "product");
        addColumn("Typ produktu", "type");
        addColumn("Stan magazynowy", "stock");
        addColumn("Cena", "price");

        if (!data.isEmpty()) {
            reportTableView.setItems(FXCollections.observableArrayList(data));
        } else {
            showAlert("Brak danych spełniających kryteria wyszukiwania.");
        }

        lastReportData = data;

        Map<String, Integer> chartData = getProductChartData(stockFilter);
        lastChartData = chartData;

        if (!chartData.isEmpty()) {
            ImageView chart = ChartUtils.createChartImage(chartData, "Stan magazynowy produktów");
            reportPreviewContainer.getChildren().removeIf(node -> node instanceof ImageView);
            reportPreviewContainer.getChildren().add(chart);
        }
    }

    /**
     * Pobiera dane produktów z bazy danych z zastosowaniem sortowania.
     *
     * @param sortKey klucz sortowania ("Stan", "Cena", "Nazwa")
     * @return lista map zawierających dane produktów
     */
    private List<Map<String, String>> getProductData(String sortKey, String stockFilter) {
        List<Map<String, String>> list = new ArrayList<>();

        String orderBy = switch (sortKey) {
            case "Stan" -> "p.stan";
            case "Cena" -> "p.cena";
            case "Typ produktu" -> "tp.nazwa";
            default -> "p.nazwa";
        };

        // Pobierz wybrane typy produktów
        List<String> selectedTypes = productTypeCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        StringBuilder query = new StringBuilder(
                "SELECT p.nazwa AS product, p.stan AS stock, p.cena AS price, tp.nazwa AS type, p.limit_stanow AS min_stock " +
                        "FROM produkty p " +
                        "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                        "WHERE 1=1 "
        );

        if (!selectedTypes.isEmpty()) {
            query.append("AND tp.nazwa IN (");
            for (int i = 0; i < selectedTypes.size(); i++) {
                query.append("?");
                if (i < selectedTypes.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        // Dodaj warunek dla produktów poniżej limitu
        if ("Tylko poniżej limitu".equals(stockFilter)) {
            query.append("AND p.stan < p.limit_stanow ");
        }

        query.append("ORDER BY ").append(orderBy);

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            for (String type : selectedTypes) {
                stmt.setString(index++, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("product", rs.getString("product"));

                int stock = rs.getInt("stock");
                int minStock = rs.getInt("min_stock");
                String stockText = String.valueOf(stock);
                if (stock < minStock) {
                    stockText += " (poniżej limitu)";
                }
                row.put("stock", stockText);

                row.put("price", String.format("%.2f", rs.getDouble("price")));
                row.put("type", rs.getString("type"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd podczas pobierania danych produktów: " + e.getMessage());
        }
        return list;
    }

    /**
     * Pobiera dane do wykresu produktów - sumaryczny stan magazynowy według nazw produktów.
     *
     * @return mapa zawierająca nazwy produktów jako klucze i stany magazynowe jako wartości
     */
    private Map<String, Integer> getProductChartData(String stockFilter) {
        Map<String, Integer> data = new LinkedHashMap<>();

        // Pobierz wybrane typy produktów
        List<String> selectedTypes = productTypeCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        StringBuilder query = new StringBuilder(
                "SELECT p.nazwa, SUM(p.stan) AS total " +
                        "FROM produkty p " +
                        "JOIN typ_produktu tp ON p.id_typu_produktu = tp.id_typu_produktu " +
                        "WHERE 1=1 "
        );

        if (!selectedTypes.isEmpty()) {
            query.append("AND tp.nazwa IN (");
            for (int i = 0; i < selectedTypes.size(); i++) {
                query.append("?");
                if (i < selectedTypes.size() - 1) query.append(",");
            }
            query.append(") ");
        }

        // Dodaj warunek dla produktów poniżej limitu
        if ("Tylko poniżej limitu".equals(stockFilter)) {
            query.append("AND p.stan < p.limit_stanow ");
        }

        query.append("GROUP BY p.nazwa");

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            for (String type : selectedTypes) {
                stmt.setString(index++, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("nazwa"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd podczas generowania wykresu: " + e.getMessage());
        }
        return data;
    }

    /**
     * Pobiera wartość filtru o podanym kluczu.
     *
     * @param key klucz filtru w mapie dynamicFilters
     * @param defaultValue wartość domyślna zwracana gdy filtr nie istnieje lub jest pusty
     * @return wartość filtru lub wartość domyślna
     */
    public String getFilterValue(String key, String defaultValue) {
        return Optional.ofNullable(dynamicFilters.get(key))
                .filter(c -> c instanceof ComboBox<?>)
                .map(c -> ((ComboBox<String>) c).getValue())
                .orElse(defaultValue);
    }

    /**
     * Dodaje kolumnę do tabeli raportu.
     *
     * @param header nagłówek kolumny wyświetlany w tabeli
     * @param key klucz używany do pobierania danych z map wierszy
     */
    protected void addColumn(String header, String key) {
        TableColumn<Map<String, String>, String> col = new TableColumn<>(header);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(key)));

        // Ustawienie proporcjonalnego rozkładu szerokości kolumn
        col.prefWidthProperty().bind(reportTableView.widthProperty().divide(reportTableView.getColumns().size() + 1));

        reportTableView.getColumns().add(col);
        headerKeyMap.put(header, key);
    }

    /**
     * Zapisuje aktualny raport jako plik PDF.
     * Otwiera okno dialogowe wyboru pliku i generuje PDF zawierający dane raportu i wykres.
     */
    @FXML
    public void saveReportAsPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        // Pobranie ścieżki do folderu Dokumenty
        Path documentsPath = Paths.get(System.getProperty("user.home"), "Documents");

        // Sprawdzenie czy folder Documents istnieje (wersja angielska)
        if (!Files.exists(documentsPath)) {
            // Jeśli nie, spróbuj z polską wersją "Dokumenty"
            documentsPath = Paths.get(System.getProperty("user.home"), "Dokumenty");
        }

        // Jeśli nadal nie istnieje, użyj folderu domowego
        if (!Files.exists(documentsPath)) {
            documentsPath = Paths.get(System.getProperty("user.home"));
        }

        fileChooser.setInitialDirectory(documentsPath.toFile());
        fileChooser.setInitialFileName("raport_" + currentReportType.toLowerCase() + ".pdf");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                List<Map<String, String>> data = lastReportData;
                Map<String, Integer> chartData = lastChartData;

                String[] headers = headerKeyMap.keySet().toArray(new String[0]);
                String[] keys = headerKeyMap.values().toArray(new String[0]);

                PDFGenerator.generateReport(currentReportType, data, headers, keys, file, chartData);
                showAlert("Zapisano raport do pliku: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Błąd zapisu PDF: " + e.getMessage());
            }
        }
    }

    /**
     * Wyświetla okno dialogowe z informacją dla użytkownika.
     *
     * @param message wiadomość do wyświetlenia
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Powraca do głównego pulpitu aplikacji.
     * Ładuje widok dashboard.fxml i przełącza scenę.
     */
    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mainReportTypeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stosuje motyw do interfejsu użytkownika na podstawie ustawień aplikacji.
     *
     * @param theme nazwa motywu ("Jasny", "Ciemny" lub wartość domyślna)
     */
    private void applyTheme(String theme) {
        Scene scene = reportRoot.getScene();
        if (scene == null) return;

        scene.getStylesheets().clear();
        String cssFile = switch (theme) {
            case "Jasny" -> "/com/example/projekt/styles/themes/light.css";
            case "Ciemny" -> "/com/example/projekt/styles/themes/dark.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };

        URL cssUrl = getClass().getResource(cssFile);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    /**
     * Stosuje rozmiar czcionki do interfejsu użytkownika na podstawie etykiety rozmiaru.
     *
     * @param label etykieta rozmiaru czcionki ("mała", "duża" lub wartość domyślna)
     */
    private void applyFontSize(String label) {
        Scene scene = reportRoot.getScene();
        if (scene == null) return;

        scene.getStylesheets().removeIf(css -> css.contains("/styles/fonts/"));

        String fontCss = switch (label.toLowerCase()) {
            case "mała" -> "/com/example/projekt/styles/fonts/small.css";
            case "duża" -> "/com/example/projekt/styles/fonts/large.css";
            default -> "/com/example/projekt/styles/fonts/medium.css";
        };

        UserSession.setCurrentFontSize(fontCss); // aktualizacja sesji

        URL fontUrl = getClass().getResource(fontCss);
        if (fontUrl != null) {
            scene.getStylesheets().add(fontUrl.toExternalForm());
        }
    }
}