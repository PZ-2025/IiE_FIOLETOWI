package com.example.projekt;

/**
 * Klasa reprezentująca rolę użytkownika w systemie.
 * <p>
 * Rola określa uprawnienia i poziom dostępu danego użytkownika. Każda rola ma unikalne ID oraz nazwę.
 * @author KrzysztofDrozda
 * @version 1.0
 * @since 2025-04-25
 * */
public class Role {
    private int id;
    private String name;

    /**
     * Tworzy nową rolę o podanym identyfikatorze i nazwie.
     *
     * @param id   Unikalny identyfikator roli.
     * @param name Nazwa roli (np. "Admin", "Pracownik").
     */
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
