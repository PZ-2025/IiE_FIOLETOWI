package com.example.projekt;

import javafx.beans.property.SimpleStringProperty;

public class Task {
    private int id;
    private final SimpleStringProperty nazwa;
    private final SimpleStringProperty status;
    private final SimpleStringProperty priorytet;
    private final SimpleStringProperty data;
    private final SimpleStringProperty koniec;
    private final SimpleStringProperty komentarz;
    private final SimpleStringProperty pracownik;
    private final SimpleStringProperty produkt;
    private final SimpleStringProperty kierunek;

    public Task(int id, String nazwa, String status, String priorytet, String data, String produkt, String kierunek, String komentarz) {
        this.id = id;
        this.nazwa = new SimpleStringProperty(nazwa);
        this.status = new SimpleStringProperty(status);
        this.priorytet = new SimpleStringProperty(priorytet);
        this.data = new SimpleStringProperty(data);
        this.koniec = new SimpleStringProperty("");
        this.pracownik = new SimpleStringProperty("");
        this.komentarz = new SimpleStringProperty("");
        this.produkt = new SimpleStringProperty(produkt);
        this.kierunek = new SimpleStringProperty(kierunek);
    }

    // Gettery
    public int getId() { return id; }
    public String getNazwa() { return nazwa.get(); }
    public String getStatus() { return status.get(); }
    public String getPriorytet() { return priorytet.get(); }
    public String getData() { return data.get(); }
    public String getKoniec() { return koniec.get(); }
    public String getPracownik() { return pracownik.get(); }
    public String getKomentarz() { return komentarz.get(); }
    public String getProdukt() {return produkt.get();}
    public String getKierunek() {return kierunek.get();}


    // Settery
    public void setEndDate(String koniec) {
        this.koniec.set(koniec);
    }

    public void setAssignedTo(String pracownik) {
        this.pracownik.set(pracownik);
    }

    // Property dla TableView
    public SimpleStringProperty nazwaProperty() { return nazwa; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty priorytetProperty() { return priorytet; }
    public SimpleStringProperty dataProperty() { return data; }
    public SimpleStringProperty komentarzProperty() { return komentarz; }
    public SimpleStringProperty koniecProperty() { return koniec; }
    public SimpleStringProperty pracownikProperty() { return pracownik; }
}
