package com.example.projekt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Test
    void testGetters() {
        Group group = new Group(1, "TestowaGrupa");

        assertEquals(1, group.getId(), "getId() powinno zwrócić poprawny identyfikator");
        assertEquals("TestowaGrupa", group.getNazwa(), "getNazwa() powinno zwrócić poprawną nazwę");
    }

    @Test
    void testToString() {
        Group group = new Group(2, "GrupaXYZ");

        assertEquals("GrupaXYZ", group.toString(), "toString() powinno zwrócić nazwę grupy");
    }
}
