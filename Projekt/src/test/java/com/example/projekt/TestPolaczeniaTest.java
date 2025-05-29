package com.example.projekt;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class TestPolaczeniaTest {

    @Test
    void testConnection() {
        try (Connection conn = DatabaseConnector.connect()) {
            assertNotNull(conn, "Połączenie nie może być null");
            assertFalse(conn.isClosed(), "Połączenie nie może być zamknięte");
        } catch (Exception e) {
            fail("Połączenie z bazą danych nie powiodło się: " + e.getMessage());
        }
    }
}
