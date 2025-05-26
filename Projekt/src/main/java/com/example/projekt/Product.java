package com.example.projekt;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty nazwa;
    private final IntegerProperty stan;
    private final DoubleProperty cena;
    private final IntegerProperty limitStanow;
    private final IntegerProperty idTypuProduktu;
    private final StringProperty typProduktuNazwa;

    public Product(int id, String nazwa, int stan, double cena, int limitStanow, int idTypuProduktu, String typProduktuNazwa) {
        this.id = new SimpleIntegerProperty(id);
        this.nazwa = new SimpleStringProperty(nazwa);
        this.stan = new SimpleIntegerProperty(stan);
        this.cena = new SimpleDoubleProperty(cena);
        this.limitStanow = new SimpleIntegerProperty(limitStanow);
        this.idTypuProduktu = new SimpleIntegerProperty(idTypuProduktu);
        this.typProduktuNazwa = new SimpleStringProperty(typProduktuNazwa);
    }

    // Gettery
    public int getId() { return id.get(); }
    public String getNazwa() { return nazwa.get(); }
    public int getStan() { return stan.get(); }
    public double getCena() { return cena.get(); }
    public int getLimitStanow() { return limitStanow.get(); }
    public int getIdTypuProduktu() { return idTypuProduktu.get(); }
    public String getTypProduktuNazwa() { return typProduktuNazwa.get(); }

    // Settery
    public void setId(int id) { this.id.set(id); }
    public void setNazwa(String nazwa) { this.nazwa.set(nazwa); }
    public void setStan(int stan) { this.stan.set(stan); }
    public void setCena(double cena) { this.cena.set(cena); }
    public void setLimitStanow(int limitStanow) { this.limitStanow.set(limitStanow); }
    public void setIdTypuProduktu(int idTypuProduktu) { this.idTypuProduktu.set(idTypuProduktu); }
    public void setTypProduktuNazwa(String typProduktuNazwa) { this.typProduktuNazwa.set(typProduktuNazwa); }

    // Właściwości do powiązań z UI
    public IntegerProperty idProperty() { return id; }
    public StringProperty nazwaProperty() { return nazwa; }
    public IntegerProperty stanProperty() { return stan; }
    public DoubleProperty cenaProperty() { return cena; }
    public IntegerProperty limitStanowProperty() { return limitStanow; }
    public IntegerProperty idTypuProduktuProperty() { return idTypuProduktu; }
    public StringProperty typProduktuNazwaProperty() { return typProduktuNazwa; }
}
