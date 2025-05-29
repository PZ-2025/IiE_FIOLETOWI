package com.example.projekt;

import javafx.beans.property.*;

/**
 * Model danych reprezentujący produkt w aplikacji.
 * Zawiera właściwości powiązane z interfejsem JavaFX (JavaFX properties),
 * umożliwiające wiązanie danych z komponentami UI (np. TableView).
 */
public class Product {
    private final IntegerProperty id;
    private final StringProperty nazwa;
    private final IntegerProperty stan;
    private final DoubleProperty cena;
    private final IntegerProperty limitStanow;
    private final IntegerProperty idTypuProduktu;
    private final StringProperty typProduktuNazwa;

    /**
     * Tworzy nowy obiekt produktu z podanymi danymi.
     *
     * @param id                identyfikator produktu
     * @param nazwa             nazwa produktu
     * @param stan              bieżący stan magazynowy
     * @param cena              cena jednostkowa
     * @param limitStanow       limit minimalnego stanu magazynowego
     * @param idTypuProduktu    identyfikator typu produktu
     * @param typProduktuNazwa  nazwa typu produktu
     */
    public Product(int id, String nazwa, int stan, double cena, int limitStanow, int idTypuProduktu, String typProduktuNazwa) {
        this.id = new SimpleIntegerProperty(id);
        this.nazwa = new SimpleStringProperty(nazwa);
        this.stan = new SimpleIntegerProperty(stan);
        this.cena = new SimpleDoubleProperty(cena);
        this.limitStanow = new SimpleIntegerProperty(limitStanow);
        this.idTypuProduktu = new SimpleIntegerProperty(idTypuProduktu);
        this.typProduktuNazwa = new SimpleStringProperty(typProduktuNazwa);
    }

    // --- Gettery ---

    /** @return identyfikator produktu */
    public int getId() { return id.get(); }

    /** @return nazwa produktu */
    public String getNazwa() { return nazwa.get(); }

    /** @return stan magazynowy produktu */
    public int getStan() { return stan.get(); }

    /** @return cena produktu */
    public double getCena() { return cena.get(); }

    /** @return minimalny limit stanu magazynowego */
    public int getLimitStanow() { return limitStanow.get(); }

    /** @return identyfikator typu produktu */
    public int getIdTypuProduktu() { return idTypuProduktu.get(); }

    /** @return nazwa typu produktu */
    public String getTypProduktuNazwa() { return typProduktuNazwa.get(); }

    // --- Settery ---

    public void setId(int id) { this.id.set(id); }
    public void setNazwa(String nazwa) { this.nazwa.set(nazwa); }
    public void setStan(int stan) { this.stan.set(stan); }
    public void setCena(double cena) { this.cena.set(cena); }
    public void setLimitStanow(int limitStanow) { this.limitStanow.set(limitStanow); }
    public void setIdTypuProduktu(int idTypuProduktu) { this.idTypuProduktu.set(idTypuProduktu); }
    public void setTypProduktuNazwa(String typProduktuNazwa) { this.typProduktuNazwa.set(typProduktuNazwa); }

    // --- Właściwości do powiązań z komponentami UI ---

    /** @return właściwość identyfikatora produktu */
    public IntegerProperty idProperty() { return id; }

    /** @return właściwość nazwy produktu */
    public StringProperty nazwaProperty() { return nazwa; }

    /** @return właściwość stanu magazynowego */
    public IntegerProperty stanProperty() { return stan; }

    /** @return właściwość ceny */
    public DoubleProperty cenaProperty() { return cena; }

    /** @return właściwość limitu stanu magazynowego */
    public IntegerProperty limitStanowProperty() { return limitStanow; }

    /** @return właściwość identyfikatora typu produktu */
    public IntegerProperty idTypuProduktuProperty() { return idTypuProduktu; }

    /** @return właściwość nazwy typu produktu */
    public StringProperty typProduktuNazwaProperty() { return typProduktuNazwa; }
}
