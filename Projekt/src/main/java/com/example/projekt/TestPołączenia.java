package com.example.projekt;

import java.sql.Connection;

/**
 * Prosta klasa testowa do sprawdzenia poprawności połączenia z bazą danych.
 * Próbuje nawiązać połączenie za pomocą {@link DatabaseConnector}.
 * Jeśli połączenie zostanie nawiązane, wypisuje komunikat potwierdzający,
 * w przeciwnym razie wyświetla szczegóły błędu.
 */
public class TestPołączenia {

    /**
     * Metoda główna uruchamiająca test połączenia z bazą danych.
     *
     * @param args argumenty linii poleceń (nieużywane)
     */
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("✅ Połączenie działa!");
        } catch (Exception e) {
            System.out.println("❌ Coś poszło nie tak:");
            e.printStackTrace();
        }
    }
}
