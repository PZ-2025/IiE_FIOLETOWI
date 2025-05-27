package com.example.projekt;

import javafx.beans.property.SimpleStringProperty;

public class Task {
    private int id;
    private final SimpleStringProperty nazwa;
    private final SimpleStringProperty status;
    private final SimpleStringProperty priorytet;
    private final SimpleStringProperty data;
    private final SimpleStringProperty koniec;
    private final SimpleStringProperty pracownik;

    public Task(int id, String nazwa, String status, String priorytet, String data) {
        this.id = id;
        this.nazwa = new SimpleStringProperty(nazwa);
        this.status = new SimpleStringProperty(status);
        this.priorytet = new SimpleStringProperty(priorytet);
        this.data = new SimpleStringProperty(data);
        this.koniec = new SimpleStringProperty("");
        this.pracownik = new SimpleStringProperty("");
    }

    // Gettery
    public int getId() { return id; }
    public String getNazwa() { return nazwa.get(); }
    public String getStatus() { return status.get(); }
    public String getPriorytet() { return priorytet.get(); }
    public String getData() { return data.get(); }
    public String getKoniec() { return koniec.get(); }
    public String getPracownik() { return pracownik.get(); }

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
    public SimpleStringProperty koniecProperty() { return koniec; }
    public SimpleStringProperty pracownikProperty() { return pracownik; }
}
