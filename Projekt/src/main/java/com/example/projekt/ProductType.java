package com.example.projekt;

/**
 * Klasa modelowa reprezentująca typ produktu.
 * Używana m.in. do przypisywania produktów do określonej kategorii.
 */
public class ProductType {
    private int id;
    private String nazwa;

    /**
     * Tworzy nowy typ produktu.
     *
     * @param id    identyfikator typu produktu
     * @param nazwa nazwa typu produktu
     */
    public ProductType(int id, String nazwa) {
        this.id = id;
        this.nazwa = nazwa;
    }

    /**
     * Zwraca identyfikator typu produktu.
     *
     * @return identyfikator typu
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca nazwę typu produktu.
     *
     * @return nazwa typu
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Zwraca nazwę typu jako reprezentację tekstową.
     * Przydatne np. w komponentach ComboBox w JavaFX.
     *
     * @return nazwa typu produktu
     */
    @Override
    public String toString() {
        return nazwa;
    }
}
