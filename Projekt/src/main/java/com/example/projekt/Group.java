package com.example.projekt;

/**
 * Reprezentuje grupę z unikalnym identyfikatorem i nazwą.
 * Klasa może być wykorzystywana np. do organizowania użytkowników, kategorii lub innych jednostek logicznych.
 */
public class Group {
    /** Unikalny identyfikator grupy. */
    private int id;

    /** Nazwa grupy. */
    private String nazwa;

    /**
     * Tworzy nową instancję klasy Group.
     *
     * @param id    unikalny identyfikator grupy
     * @param nazwa nazwa grupy
     */
    public Group(int id, String nazwa) {
        this.id = id;
        this.nazwa = nazwa;
    }

    /**
     * Zwraca identyfikator grupy.
     *
     * @return identyfikator grupy
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca nazwę grupy.
     *
     * @return nazwa grupy
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Zwraca nazwę grupy jako reprezentację tekstową obiektu.
     *
     * @return nazwa grupy
     */
    @Override
    public String toString() {
        return nazwa;
    }
}
