package com.example.projekt;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.*;
import java.util.function.UnaryOperator;

/**
 * Kontroler zarządzający modułem produktów w aplikacji.
 * Odpowiada za:
 * - Wyświetlanie listy produktów w formie tabeli
 * - Dodawanie, edycję i usuwanie produktów
 * - Walidację danych wprowadzanych przez użytkownika
 * - Zarządzanie typami produktów
 * - Integrację z bazą danych
 * - Stosowanie motywów i stylów czcionek
 */
public class ProductManagementController {

    /** Tabela wyświetlająca listę produktów */
    @FXML private TableView<Product> productTable;

    /** Kolumna z nazwą produktu */
    @FXML private TableColumn<Product, String> nazwaColumn;

    /** Kolumna z stanem magazynowym */
    @FXML private TableColumn<Product, Integer> stanColumn;

    /** Kolumna z ceną produktu */
    @FXML private TableColumn<Product, Double> cenaColumn;

    /** Kolumna z limitem stanu magazynowego */
    @FXML private TableColumn<Product, Integer> limitColumn;

    /** Kolumna z typem produktu */
    @FXML private TableColumn<Product, String> typColumn;

    /** Pole tekstowe dla nazwy produktu */
    @FXML private TextField nazwaField;

    /** Pole tekstowe dla stanu magazynowego */
    @FXML private TextField stanField;

    /** Pole tekstowe dla ceny produktu */
    @FXML private TextField cenaField;

    /** Pole tekstowe dla limitu stanu magazynowego */
    @FXML private TextField limitField;

    /** ComboBox z typami produktów */
    @FXML private ComboBox<ProductType> typComboBox;

    /** Główny kontener VBox */
    @FXML private VBox productRoot;

    /** Aktualnie wybrany produkt do edycji */
    private Product selectedProduct = null;

    /** Lista typów produktów */
    private ObservableList<ProductType> productTypes = FXCollections.observableArrayList();

    /**
     * Metoda inicjalizująca kontroler.
     * Konfiguruje tabelę produktów, ładowanie typów produktów oraz ustawienia walidacji.
     */
    @FXML
    public void initialize() {
        productRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(AppSettings.getTheme());
                applyFontSize(AppSettings.getFontSizeLabel());
            }
        });

        // Konfiguracja tabeli produktów
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nazwaColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNazwa()));

        stanColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStan()).asObject());
        stanColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        limitColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLimitStanow()).asObject());
        limitColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        cenaColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCena()).asObject());
        cenaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        typColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypProduktuNazwa()));

        // Ładowanie typów produktów
        productTypes = loadProductTypes();
        typComboBox.setItems(productTypes);

        // Ładowanie produktów i konfiguracja interfejsu
        loadProducts();
        Platform.runLater(() -> productRoot.requestFocus());
        productTable.getSelectionModel().clearSelection();
        typComboBox.getSelectionModel().clearSelection();

        // Obsługa wyboru wiersza w tabeli
        productTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectedProduct = row.getItem();
                    fillForm(selectedProduct);
                }
            });
            return row;
        });

        // Walidacja pola ceny - tylko liczby z maksymalnie 2 miejscami po przecinku
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*(\\.\\d{0,2})?") ? change : null;
        };
        cenaField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Wypełnia formularz danymi wybranego produktu.
     * @param p Produkt, którego dane mają zostać wczytane do formularza
     */
    private void fillForm(Product p) {
        nazwaField.setText(p.getNazwa());
        stanField.setText(String.valueOf(p.getStan()));
        cenaField.setText(String.format("%.2f", p.getCena()));
        limitField.setText(String.valueOf(p.getLimitStanow()));
        typComboBox.getSelectionModel().select(
                productTypes.stream()
                        .filter(t -> t.getId() == p.getIdTypuProduktu())
                        .findFirst()
                        .orElse(null)
        );
    }

    /**
     * Obsługuje dodawanie nowego produktu.
     * Zapisuje produkt do bazy danych i czyści formularz.
     */
    @FXML
    private void handleAddProduct() {
        selectedProduct = null;
        saveProduct();
        clearForm();
    }

    /**
     * Obsługuje usuwanie produktu.
     * Wyświetla okno dialogowe z prośbą o potwierdzenie przed usunięciem.
     */
    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Potwierdzenie usunięcia");
            confirmAlert.setHeaderText("Czy na pewno chcesz usunąć ten produkt?");
            confirmAlert.setContentText("Produkt: " + selected.getNazwa());

            ButtonType buttonYes = new ButtonType("Tak", ButtonBar.ButtonData.YES);
            ButtonType buttonNo = new ButtonType("Nie", ButtonBar.ButtonData.NO);
            confirmAlert.getButtonTypes().setAll(buttonYes, buttonNo);

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == buttonYes) {
                    try (Connection conn = DatabaseConnector.connect();
                         PreparedStatement stmt = conn.prepareStatement("DELETE FROM produkty WHERE id_produktu = ?")) {

                        stmt.setInt(1, selected.getId());
                        stmt.executeUpdate();

                        loadProducts();
                        clearForm();

                    } catch (SQLIntegrityConstraintViolationException ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Błąd usuwania");
                        errorAlert.setHeaderText("Nie można usunąć produktu");
                        errorAlert.setContentText("Ten produkt jest przypisany do istniejących transakcji i nie może zostać usunięty.");
                        errorAlert.showAndWait();
                    } catch (SQLException e) {
                        // Inne błędy SQL
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Błąd bazy danych");
                        errorAlert.setHeaderText("Wystąpił błąd podczas usuwania produktu");
                        errorAlert.setContentText("Szczegóły: " + e.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });

        } else {
            showAlert("Brak wyboru", "Nie wybrano produktu do usunięcia.");
        }
    }


    /**
     * Ładuje listę produktów z bazy danych i wyświetla je w tabeli.
     */
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id_produktu, p.nazwa, p.stan, p.cena, p.limit_stanow, p.id_typu_produktu, t.nazwa AS typ
            FROM produkty p
            JOIN typ_produktu t ON p.id_typu_produktu = t.id_typu_produktu
        """;

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id_produktu"),
                        rs.getString("nazwa"),
                        rs.getInt("stan"),
                        rs.getDouble("cena"),
                        rs.getInt("limit_stanow"),
                        rs.getInt("id_typu_produktu"),
                        rs.getString("typ")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        productTable.setItems(products);
    }

    /**
     * Ładuje typy produktów z bazy danych.
     * @return Lista typów produktów
     */
    private ObservableList<ProductType> loadProductTypes() {
        ObservableList<ProductType> types = FXCollections.observableArrayList();
        String sql = "SELECT * FROM typ_produktu";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(new ProductType(rs.getInt("id_typu_produktu"), rs.getString("nazwa")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return types;
    }

    /**
     * Zapisuje produkt do bazy danych.
     * Wykonuje operację aktualizacji dla istniejącego produktu lub wstawienia dla nowego.
     */
    private void saveProduct() {
        String nazwa = nazwaField.getText();
        int stan, limit;
        BigDecimal cena;

        // Walidacja stanu magazynowego
        try {
            stan = Integer.parseInt(stanField.getText());
            if (stan < 0) {
                showAlert("Błąd walidacji", "Stan nie może być ujemny.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawny stan.");
            return;
        }

        // Walidacja ceny
        try {
            cena = new BigDecimal(cenaField.getText()).setScale(2, RoundingMode.HALF_UP);
            if (cena.compareTo(BigDecimal.ZERO) < 0) {
                showAlert("Błąd walidacji", "Cena nie może być ujemna.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawną cenę.");
            return;
        }

        // Walidacja limitu stanu
        try {
            limit = Integer.parseInt(limitField.getText());
            if (limit < 0) {
                showAlert("Błąd walidacji", "Limit nie może być ujemny.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawny limit.");
            return;
        }

        // Walidacja typu produktu
        ProductType typ = typComboBox.getValue();
        if (typ == null) {
            showAlert("Błąd danych", "Wybierz typ produktu.");
            return;
        }

        try (Connection conn = DatabaseConnector.connect()) {
            String checkSql = "SELECT id_produktu, stan FROM produkty WHERE nazwa = ? AND id_typu_produktu = ? AND cena = ? AND limit_stanow = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, nazwa);
                checkStmt.setInt(2, typ.getId());
                checkStmt.setDouble(3, cena.doubleValue());
                checkStmt.setInt(4, limit);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Produkt istnieje ze wszystkimi tymi samymi danymi — zwiększamy stan
                        int existingId = rs.getInt("id_produktu");
                        int existingStan = rs.getInt("stan");

                        String updateSql = "UPDATE produkty SET stan = ? WHERE id_produktu = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, existingStan + stan);
                            updateStmt.setInt(2, existingId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Brak identycznego produktu — tworzymy nowy rekord
                        String insertSql = "INSERT INTO produkty (nazwa, stan, cena, limit_stanow, id_typu_produktu) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, nazwa);
                            insertStmt.setInt(2, stan);
                            insertStmt.setDouble(3, cena.doubleValue());
                            insertStmt.setInt(4, limit);
                            insertStmt.setInt(5, typ.getId());
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            clearForm();
            loadProducts();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd bazy danych", "Nie udało się zapisać produktu.");
        }
    }

    /**
     * Czyści formularz produktu.
     */
    @FXML
    private void clearForm() {
        nazwaField.clear();
        stanField.clear();
        cenaField.clear();
        limitField.clear();
        typComboBox.setValue(null);
        selectedProduct = null;
    }

    /**
     * Wyświetla okno dialogowe z komunikatem o błędzie.
     * @param title Tytuł okna dialogowego
     * @param content Treść komunikatu
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Stosuje wybrany motyw do interfejsu.
     * @param theme Nazwa motywu do zastosowania
     */
    private void applyTheme(String theme) {
        Scene scene = productRoot.getScene();
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
     * Stosuje wybrany rozmiar czcionki do interfejsu.
     * @param label Etykieta określająca rozmiar czcionki
     */
    private void applyFontSize(String label) {
        Scene scene = productRoot.getScene();
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
    @FXML
    private void editProduct() {
        if (selectedProduct == null) {
            showAlert("Błąd", "Nie wybrano produktu do edycji.");
            return;
        }

        String nazwa = nazwaField.getText();
        int stan, limit;
        BigDecimal cena;

        // Walidacja stanu magazynowego
        try {
            stan = Integer.parseInt(stanField.getText());
            if (stan < 0) {
                showAlert("Błąd walidacji", "Stan nie może być ujemny.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawny stan.");
            return;
        }

        // Walidacja ceny
        try {
            cena = new BigDecimal(cenaField.getText()).setScale(2, RoundingMode.HALF_UP);
            if (cena.compareTo(BigDecimal.ZERO) < 0) {
                showAlert("Błąd walidacji", "Cena nie może być ujemna.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawną cenę.");
            return;
        }

        // Walidacja limitu stanu
        try {
            limit = Integer.parseInt(limitField.getText());
            if (limit < 0) {
                showAlert("Błąd walidacji", "Limit nie może być ujemny.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd danych", "Wprowadź poprawny limit.");
            return;
        }

        // Walidacja typu produktu
        ProductType typ = typComboBox.getValue();
        if (typ == null) {
            showAlert("Błąd danych", "Wybierz typ produktu.");
            return;
        }


        // Edycja produktu
        try (Connection conn = DatabaseConnector.connect()) {
            String updateSql = "UPDATE produkty SET nazwa = ?, stan = ?, cena = ?, limit_stanow = ?, id_typu_produktu = ? WHERE id_produktu = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, nazwa);
                updateStmt.setInt(2, stan);
                updateStmt.setDouble(3, cena.doubleValue());
                updateStmt.setInt(4, limit);
                updateStmt.setInt(5, typ.getId());
                updateStmt.setInt(6, selectedProduct.getId());
                updateStmt.executeUpdate();
            }

            loadProducts();
            clearForm();
            selectedProduct = null;

        } catch (SQLException e) {
            showAlert("Błąd", "Błąd bazy danych: " + e.getMessage());
        }
    }
}