package com.example.projekt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1, "Napisać raport", "Nowe", "Wysoki", "2025-06-01");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, task.getId());
        assertEquals("Napisać raport", task.getNazwa());
        assertEquals("Nowe", task.getStatus());
        assertEquals("Wysoki", task.getPriorytet());
        assertEquals("2025-06-01", task.getData());
        assertEquals("", task.getKoniec());
        assertEquals("", task.getPracownik());
    }

    @Test
    void testSetEndDate() {
        task.setEndDate("2025-06-10");
        assertEquals("2025-06-10", task.getKoniec());
    }

    @Test
    void testSetAssignedTo() {
        task.setAssignedTo("Jan Kowalski");
        assertEquals("Jan Kowalski", task.getPracownik());
    }

    @Test
    void testPropertiesReflectChanges() {
        task.setEndDate("2025-06-12");
        assertEquals("2025-06-12", task.koniecProperty().get());

        task.setAssignedTo("Ewa Nowak");
        assertEquals("Ewa Nowak", task.pracownikProperty().get());
    }
}
