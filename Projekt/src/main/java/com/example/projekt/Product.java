package com.example.projekt;
import javafx.beans.property.*;

public class Product {
    private final StringProperty nazwa;
    private final IntegerProperty stan;
    private final IntegerProperty limitStanow;
    private final DoubleProperty cena;

    public Product(String nazwa, int stan, int limitStanow, double cena) {
        this.nazwa = new SimpleStringProperty(nazwa);
        this.stan = new SimpleIntegerProperty(stan);
        this.limitStanow = new SimpleIntegerProperty(limitStanow);
        this.cena = new SimpleDoubleProperty(cena);
    }

    public String getNazwa() { return nazwa.get(); }
    public int getStan() { return stan.get(); }
    public int getLimitStanow() { return limitStanow.get(); }
    public double getCena() { return cena.get(); }

    public StringProperty nazwaProperty() { return nazwa; }
    public IntegerProperty stanProperty() { return stan; }
    public IntegerProperty limitStanowProperty() { return limitStanow; }
    public DoubleProperty cenaProperty() { return cena; }
}