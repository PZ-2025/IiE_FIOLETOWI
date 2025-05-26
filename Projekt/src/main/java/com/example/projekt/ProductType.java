package com.example.projekt;

public class ProductType {
    private int id;
    private String nazwa;

    public ProductType(int id, String nazwa) {
        this.id = id;
        this.nazwa = nazwa;
    }

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}
