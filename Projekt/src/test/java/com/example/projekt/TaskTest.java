package com.example.projekt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void testTaskCreationAndGetters() {
        Task task = new Task(1, "Test Task", "Open", "High", "2025-05-29", "ProduktX", "KierunekY", "Komentarz", "10", "Jan Kowalski");

        assertEquals(1, task.getId());
        assertEquals("Test Task", task.getNazwa());
        assertEquals("Open", task.getStatus());
        assertEquals("High", task.getPriorytet());
        assertEquals("2025-05-29", task.getData());
        assertEquals("ProduktX", task.getProdukt());
        assertEquals("KierunekY", task.getKierunek());
        assertEquals("Komentarz", task.getKomentarz());
        assertEquals("10", task.getIlosc());
        assertEquals("Jan Kowalski", task.getPracownik());

        // Data zakończenia powinna być domyślnie pusta
        assertEquals("", task.getKoniec());
    }

    @Test
    public void testSetters() {
        Task task = new Task(2, "Inne Zadanie", "W trakcie", "Medium", "2025-05-28", "ProduktY", "KierunekZ", "Brak komentarza", "5", "Anna Nowak");

        // Sprawdź ustawianie daty zakończenia
        task.setEndDate("2025-06-01");
        assertEquals("2025-06-01", task.getKoniec());

        // Sprawdź ustawianie pracownika
        task.setAssignedTo("Piotr Wiśniewski");
        assertEquals("Piotr Wiśniewski", task.getPracownik());
    }
}
