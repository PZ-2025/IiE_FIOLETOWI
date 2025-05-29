package com.example.projekt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Klasa odpowiedzialna za połączenie z bazą danych MySQL.
 * Udostępnia statyczną metodę umożliwiającą nawiązanie połączenia z bazą danych HurtPolSan.
 */
public class DatabaseConnector {

    /** Adres URL bazy danych MySQL. */
    private static final String URL = "jdbc:mysql://localhost:3306/HurtPolSan";

    /** Nazwa użytkownika bazy danych. */
    private static final String USER = "root";

    /** Hasło do bazy danych. */
    private static final String PASSWORD = "";

    /**
     * Nawiązuje połączenie z bazą danych MySQL przy użyciu ustawionych parametrów.
     *
     * @return Obiekt {@link Connection} umożliwiający komunikację z bazą danych
     * @throws SQLException jeśli połączenie nie może zostać nawiązane
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
