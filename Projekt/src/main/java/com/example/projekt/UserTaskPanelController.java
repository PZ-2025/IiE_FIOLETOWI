package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kontroler panelu zadań użytkownika odpowiedzialny za zarządzanie zadaniami i produktami.
 * Umożliwia użytkownikowi przeglądanie przypisanych zadań, zmianę ich statusów,
 * dodawanie komentarzy oraz monitorowanie stanu produktów.
 *
 * @author System Zarządzania Zadaniami
 * @version 1.0
 */
public class UserTaskPanelController {

    /** Tabela wyświetlająca produkty */
    @FXML private TableView<Product> productTable;

    /** Kolumna z nazwą produktu */
    @FXML private TableColumn<Product, String> productNameColumn;

    /** Kolumna ze stanem produktu */
    @FXML private TableColumn<Product, Integer> productStockColumn;

    /** Kolumna z limitem stanu produktu */
    @FXML private TableColumn<Product, Integer> productLimitColumn;

    /** Kolumna z ceną produktu */
    @FXML private TableColumn<Product, Double> productPriceColumn;

    /** Tabela wyświetlająca zadania */
    @FXML private TableView<Task> taskTable;

    /** Kolumna z nazwą zadania */
    @FXML private TableColumn<Task, String> nameColumn;

    /** Kolumna ze statusem zadania */
    @FXML private TableColumn<Task, String> statusColumn;

    /** Kolumna z priorytetem zadania */
    @FXML private TableColumn<Task, String> priorityColumn;

    /** Kolumna z datą zadania */
    @FXML private TableColumn<Task, String> dateColumn;

    /** ComboBox do wyboru statusu zadania */
    @FXML private ComboBox<String> statusComboBox;

    /** Lista historii wykonanych zadań */
    @FXML private ListView<String> historyList;

    /** Pole tekstowe do wprowadzania komentarzy */
    @FXML private TextArea commentTextArea;

    /** Główny panel aplikacji */
    @FXML private BorderPane userTaskRoot;

    /** Logger do rejestrowania błędów i zdarzeń */
    private static final Logger LOGGER = Logger.getLogger(UserTaskPanelController.class.getName());

    /** Aktualnie wybrane zadanie */
    private Task selectedTask;

    /** Mapa definiująca kolejność statusów zadań */
    private Map<String, Integer> statusOrder = new LinkedHashMap<>();

    /**
     * Inicjalizuje kontroler po załadowaniu FXML.
     * Konfiguruje tabele, ładuje dane oraz ustawia listenery.
     */
    @FXML
    public void initialize() {
        // Nasłuchiwanie zmian sceny w celu zastosowania motywu i rozmiaru czcionki
        userTaskRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSizeLabel());
            }
        });

        // Inicjalizacja kolejności statusów
        initializeStatusOrder();

        // Konfiguracja tabeli produktów
        configureProductTable();
        loadProducts();

        // Konfiguracja tabeli zadań
        configureTaskTable();
        loadTasks();
        loadStatuses();
        loadTaskHistory();

        // Listener dla wyboru zadania
        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedTask = newSel;
            if (newSel != null) {
                loadTaskComment(newSel.getId());
                updateAvailableStatuses(newSel.getStatus());
            } else {
                commentTextArea.clear();
                statusComboBox.getItems().clear();
            }
        });

        checkForNotifications();
    }

    /**
     * Inicjalizuje mapę kolejności statusów zadań.
     * Definiuje logiczny przepływ zadań od oczekujących do zakończonych.
     */
    private void initializeStatusOrder() {
        statusOrder.put("Oczekujące", 1);
        statusOrder.put("Rozpoczęte", 2);
        statusOrder.put("W trakcie", 3);
        statusOrder.put("Zakończone", 4);
    }

    /**
     * Konfiguruje tabelę produktów, ustawiając fabryki komórek
     * i formatowanie wyświetlanych danych.
     */
    private void configureProductTable() {
        productNameColumn.setCellValueFactory(data -> data.getValue().nazwaProperty());
        productStockColumn.setCellValueFactory(data -> data.getValue().stanProperty().asObject());
        productLimitColumn.setCellValueFactory(data -> data.getValue().limitStanowProperty().asObject());
        productPriceColumn.setCellValueFactory(data -> data.getValue().cenaProperty().asObject());

        // Formatowanie ceny do 2 miejsc po przecinku
        productPriceColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f zł", price));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // Wyrównanie kolumn liczbowych do prawej
        productStockColumn.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                } else {
                    setText(stock.toString());
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        productLimitColumn.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer limit, boolean empty) {
                super.updateItem(limit, empty);
                if (empty || limit == null) {
                    setText(null);
                } else {
                    setText(limit.toString());
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });
    }

    /**
     * Konfiguruje tabelę zadań, ustawiając fabryki komórek
     * dla każdej kolumny z danymi zadania.
     */
    private void configureTaskTable() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nazwaProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorytetProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dataProperty());
    }

    /**
     * Aktualizuje dostępne statusy w ComboBox na podstawie aktualnego statusu zadania.
     * Pozwala tylko na przejście do statusów o wyższej kolejności.
     *
     * @param currentStatus aktualny status zadania
     */
    private void updateAvailableStatuses(String currentStatus) {
        statusComboBox.getItems().clear();

        // Znajdź aktualną pozycję statusu
        Integer currentOrder = statusOrder.get(currentStatus);
        if (currentOrder == null) return;

        // Dodaj tylko statusy o wyższej kolejności
        statusOrder.entrySet().stream()
                .filter(entry -> entry.getValue() > currentOrder)
                .forEach(entry -> statusComboBox.getItems().add(entry.getKey()));
    }

    /**
     * Ładuje produkty z bazy danych i wyświetla je w tabeli.
     * Pobiera informacje o produktach wraz z ich typami.
     */
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.id_produktu, p.nazwa, p.stan, p.cena, p.limit_stanow, p.id_typu_produktu, t.nazwa AS typ_nazwa " +
                "FROM produkty p " +
                "LEFT JOIN typ_produktu t ON p.id_typu_produktu = t.id_typu_produktu";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id_produktu"),
                        rs.getString("nazwa"),
                        rs.getInt("stan"),
                        rs.getDouble("cena"),
                        rs.getInt("limit_stanow"),
                        rs.getInt("id_typu_produktu"),
                        rs.getString("typ_nazwa")
                ));
            }
            productTable.setItems(products);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania produktów", e);
            showAlert("Błąd", "Nie udało się załadować produktów");
        }
    }

    /**
     * Ładuje zadania przypisane do aktualnego użytkownika z bazy danych.
     * Wyświetla tylko zadania, które nie są zakończone.
     */
    private void loadTasks() {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        String sql = """
        SELECT 
            z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet,
            z.data_rozpoczecia, z.komentarz, pk.nazwa AS produkt, 
            z.ilosc, k.nazwa AS kierunek, 
            CONCAT(pr.imie, ' ', pr.nazwisko) AS pracownik
        FROM zadania z
        LEFT JOIN statusy s ON z.id_statusu = s.id_statusu
        LEFT JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
        LEFT JOIN produkty pk ON z.id_produktu = pk.id_produktu
        LEFT JOIN kierunki k ON z.id_kierunku = k.id_kierunku
        LEFT JOIN pracownicy pr ON z.id_pracownika = pr.id_pracownika
        WHERE z.id_pracownika = ? 
        AND s.nazwa != 'Zakończone'
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ilosc = "";
                int iloscInt = rs.getInt("ilosc");
                if (!rs.wasNull()) {
                    ilosc = String.valueOf(iloscInt);
                }

                Task task = new Task(
                        rs.getInt("id_zadania"),
                        rs.getString("nazwa"),
                        rs.getString("status"),
                        rs.getString("priorytet"),
                        rs.getDate("data_rozpoczecia") != null ? rs.getDate("data_rozpoczecia").toString() : "",
                        rs.getString("produkt"),
                        rs.getString("kierunek"),
                        rs.getString("komentarz") != null ? rs.getString("komentarz") : "",
                        ilosc,
                        rs.getString("pracownik")
                );
                tasks.add(task);
            }

            taskTable.setItems(tasks);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się pobrać zadań.");
        }
    }

    /**
     * Ładuje dostępne statusy zadań z bazy danych.
     * Metoda przygotowana do rozszerzenia funkcjonalności.
     */
    private void loadStatuses() {
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT nazwa FROM statusy");
             ResultSet rs = stmt.executeQuery()) {

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania statusów", e);
        }
    }

    /**
     * Ładuje komentarz dla wybranego zadania z bazy danych.
     *
     * @param taskId identyfikator zadania
     */
    private void loadTaskComment(int taskId) {
        String sql = "SELECT komentarz FROM zadania WHERE id_zadania = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                commentTextArea.setText(rs.getString("komentarz"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania komentarza", e);
        }
    }

    /**
     * Zapisuje komentarz dla aktualnie wybranego zadania do bazy danych.
     * Metoda wywoływana po kliknięciu przycisku zapisu komentarza.
     */
    @FXML
    private void saveTaskComment() {
        if (selectedTask == null) {
            showAlert("Błąd", "Nie wybrano zadania");
            return;
        }

        String sql = "UPDATE zadania SET komentarz = ? WHERE id_zadania = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, commentTextArea.getText());
            stmt.setInt(2, selectedTask.getId());
            stmt.executeUpdate();

            showAlert("Sukces", "Komentarz został zapisany");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd zapisu komentarza", e);
            showAlert("Błąd", "Nie udało się zapisać komentarza");
        }
    }

    /**
     * Zmienia status wybranego zadania na podstawie wyboru z ComboBox.
     * Aktualizuje również datę zakończenia dla zadań zakończonych
     * i ustawia flagę powiadomień.
     */
    @FXML
    private void changeTaskStatus() {
        if (selectedTask == null || statusComboBox.getValue() == null) {
            showAlert("Błąd", "Nie wybrano zadania lub statusu");
            return;
        }

        String newStatus = statusComboBox.getValue();
        String sql;

        if ("Zakończone".equals(newStatus)) {
            sql = """
            UPDATE zadania SET 
                id_statusu = (SELECT id_statusu FROM statusy WHERE nazwa = ?),
                data_zakonczenia = CURRENT_DATE,
                powiadomienia = 1
            WHERE id_zadania = ?
            """;
        } else {
            sql = """
            UPDATE zadania SET 
                id_statusu = (SELECT id_statusu FROM statusy WHERE nazwa = ?),
                powiadomienia = 1
            WHERE id_zadania = ?
            """;
        }

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            if ("Zakończone".equals(newStatus)) {
                stmt.setInt(2, selectedTask.getId());
            } else {
                stmt.setInt(2, selectedTask.getId());
            }
            stmt.executeUpdate();

            loadTasks();
            loadTaskHistory();
            showAlert("Sukces", "Status zadania został zaktualizowany");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd zmiany statusu", e);
            showAlert("Błąd", "Nie udało się zmienić statusu zadania: " + e.getMessage());
        }
    }

    /**
     * Ładuje historię zakończonych zadań użytkownika z bazy danych.
     * Wyświetla zadania posortowane według daty zakończenia.
     */
    private void loadTaskHistory() {
        String sql = """
            SELECT z.nazwa, z.data_rozpoczecia, z.data_zakonczenia 
            FROM zadania z
            JOIN statusy s ON z.id_statusu = s.id_statusu
            WHERE z.id_pracownika = ? AND s.nazwa = 'Zakończone'
            ORDER BY z.data_zakonczenia DESC
            """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();

            historyList.getItems().clear();
            while (rs.next()) {
                String taskInfo = String.format("%s (Rozpoczęto: %s, Zakończono: %s)",
                        rs.getString("nazwa"),
                        rs.getDate("data_rozpoczecia"),
                        rs.getDate("data_zakonczenia"));
                historyList.getItems().add(taskInfo);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd ładowania historii", e);
        }
    }

    /**
     * Sprawdza czy istnieją powiadomienia o nowych zadaniach dla użytkownika.
     * Wyświetla alert z listą nowych zadań i zeruje flagę powiadomień.
     */
    private void checkForNotifications() {
        String sql = "SELECT nazwa FROM zadania WHERE id_pracownika = ? AND powiadomienia = 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, UserSession.getInstance().getUser().getId());
            ResultSet rs = stmt.executeQuery();

            StringBuilder message = new StringBuilder();
            while (rs.next()) {
                message.append("- ").append(rs.getString("nazwa")).append("\n");
            }

            if (!message.isEmpty()) {
                showAlert("Powiadomienia", "Nowe zadania:\n" + message.toString());

                // Wyczyść powiadomienia
                try (PreparedStatement clear = conn.prepareStatement(
                        "UPDATE zadania SET powiadomienia = 0 WHERE id_pracownika = ?")) {
                    clear.setInt(1, UserSession.getInstance().getUser().getId());
                    clear.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Błąd sprawdzania powiadomień", e);
        }
    }

    /**
     * Przechodzi do głównego dashboardu aplikacji.
     * Metoda wywoływana po kliknięciu przycisku powrotu.
     *
     * @param event wydarzenie akcji
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projekt/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCurrentUser(UserSession.getInstance().getUser());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Błąd powrotu do dashboardu", e);
            showAlert("Błąd", "Nie udało się przejść do dashboardu");
        }
    }

    /**
     * Wyświetla okno dialogowe z informacją dla użytkownika.
     *
     * @param title tytuł okna dialogowego
     * @param message treść wiadomości
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Stosuje wybrany motyw do interfejsu użytkownika.
     * Ładuje odpowiedni plik CSS na podstawie nazwy motywu.
     *
     * @param theme nazwa motywu do zastosowania
     */
    private void applyTheme(String theme) {
        Scene scene = userTaskRoot.getScene();
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
     * Stosuje wybrany rozmiar czcionki do interfejsu użytkownika.
     * Ładuje odpowiedni plik CSS z definicjami rozmiaru czcionki.
     *
     * @param label etykieta rozmiaru czcionki (mała, średnia, duża)
     */
    private void applyFontSize(String label) {
        Scene scene = userTaskRoot.getScene();
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