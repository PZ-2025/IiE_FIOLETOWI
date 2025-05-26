package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.*;

public class ProductManagementController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nazwaColumn;
    @FXML private TableColumn<Product, Integer> stanColumn;
    @FXML private TableColumn<Product, Double> cenaColumn;
    @FXML private TableColumn<Product, Integer> limitColumn;
    @FXML private TableColumn<Product, String> typColumn;

    @FXML private TextField nazwaField;
    @FXML private TextField stanField;
    @FXML private TextField cenaField;
    @FXML private TextField limitField;
    @FXML private ComboBox<ProductType> typComboBox;

    private Product selectedProduct = null;
    private ObservableList<ProductType> productTypes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nazwaColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNazwa()));
        stanColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStan()).asObject());
        cenaColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCena()).asObject());
        limitColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLimitStanow()).asObject());
        typColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypProduktuNazwa()));

        productTypes = loadProductTypes();
        typComboBox.setItems(productTypes);

        loadProducts();

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
    }

    private void fillForm(Product p) {
        nazwaField.setText(p.getNazwa());
        stanField.setText(String.valueOf(p.getStan()));
        cenaField.setText(String.valueOf(p.getCena()));
        limitField.setText(String.valueOf(p.getLimitStanow()));
        typComboBox.getSelectionModel().select(
                productTypes.stream()
                        .filter(t -> t.getId() == p.getIdTypuProduktu())
                        .findFirst()
                        .orElse(null)
        );
    }

    @FXML
    private void handleAddProduct() {
        selectedProduct = null;  // sygnał, że dodajemy nowy produkt
        clearForm();             // wyczyść formularz
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM produkty WHERE id_produktu = ?")) {
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                loadProducts();
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Nie zaznaczono produktu do usunięcia.");
        }
    }

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

    @FXML
    private void saveProduct() {
        String nazwa = nazwaField.getText();
        int stan = Integer.parseInt(stanField.getText());
        double cena = Double.parseDouble(cenaField.getText());
        int limit = Integer.parseInt(limitField.getText());
        ProductType typ = typComboBox.getValue();

        if (typ == null) {
            System.out.println("Wybierz typ produktu.");
            return;
        }

        if (selectedProduct != null) {
            // aktualizacja
            String sql = "UPDATE produkty SET nazwa=?, stan=?, cena=?, limit_stanow=?, id_typu_produktu=? WHERE id_produktu=?";
            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nazwa);
                stmt.setInt(2, stan);
                stmt.setDouble(3, cena);
                stmt.setInt(4, limit);
                stmt.setInt(5, typ.getId());
                stmt.setInt(6, selectedProduct.getId());

                stmt.executeUpdate();
                clearForm();
                loadProducts();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // dodanie nowego produktu
            String sql = "INSERT INTO produkty (nazwa, stan, cena, limit_stanow, id_typu_produktu) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nazwa);
                stmt.setInt(2, stan);
                stmt.setDouble(3, cena);
                stmt.setInt(4, limit);
                stmt.setInt(5, typ.getId());

                stmt.executeUpdate();
                clearForm();
                loadProducts();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void clearForm() {
        nazwaField.clear();
        stanField.clear();
        cenaField.clear();
        limitField.clear();
        typComboBox.setValue(null);
        selectedProduct = null;
    }
}
