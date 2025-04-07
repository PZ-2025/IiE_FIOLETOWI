package com.example.projekt;

import java.sql.Connection;

public class TestPołączenia {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("✅ Połączenie działa!");
        } catch (Exception e) {
            System.out.println("❌ Coś poszło nie tak:");
            e.printStackTrace();
        }
    }
}
