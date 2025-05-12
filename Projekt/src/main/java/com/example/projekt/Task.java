package com.example.projekt;

import javafx.beans.property.SimpleStringProperty;

public class Task {
    private int id;
    private final SimpleStringProperty nazwa, status, priorytet, data;

    public Task(int id, String nazwa, String status, String priorytet, String data) {
        this.id = id;
        this.nazwa = new SimpleStringProperty(nazwa);
        this.status = new SimpleStringProperty(status);
        this.priorytet = new SimpleStringProperty(priorytet);
        this.data = new SimpleStringProperty(data);
    }

    public int getId() { return id; }
    public SimpleStringProperty nazwaProperty() { return nazwa; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty priorytetProperty() { return priorytet; }
    public SimpleStringProperty dataProperty() { return data; }
}
