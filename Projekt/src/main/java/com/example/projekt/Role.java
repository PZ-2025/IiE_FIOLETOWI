package com.example.projekt;

public class Role {
    private int id;
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return name; 
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
