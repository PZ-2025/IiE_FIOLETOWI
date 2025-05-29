package com.example.projekt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class DatabaseConnectorTest {

    @Test
    void testConnectReturnsConnection() throws SQLException {
        // Tworzymy mock Connection
        Connection mockConnection = mock(Connection.class);

        // Mockujemy statyczną metodę DriverManager.getConnection
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            // Definiujemy zachowanie mocka
            mockedDriverManager.when(() -> DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/HurtPolSan", "root", ""))
                    .thenReturn(mockConnection);

            // Wywołujemy testowaną metodę
            Connection conn = DatabaseConnector.connect();

            // Sprawdzamy, czy zwrócony obiekt to nasz mock
            assertNotNull(conn);
            assertEquals(mockConnection, conn);

            // Weryfikujemy, że statyczna metoda została wywołana dokładnie raz z podanymi argumentami
            mockedDriverManager.verify(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/HurtPolSan", "root", ""), times(1));
        }
    }
}
