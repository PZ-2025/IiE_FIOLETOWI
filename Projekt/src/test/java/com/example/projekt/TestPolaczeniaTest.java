package com.example.projekt;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test jednostkowy sprawdzający poprawność połączenia z bazą danych.
 */
public class TestPolaczeniaTest {

    @Test
    void testDatabaseConnection() {
        try (Connection conn = DatabaseConnector.connect()) {
            assertNotNull(conn, "Połączenie nie może być nullem");
            assertFalse(conn.isClosed(), "Połączenie nie powinno być zamknięte");
        } catch (Exception e) {
            fail("Nie udało się nawiązać połączenia z bazą danych: " + e.getMessage());
        }
    }
}
