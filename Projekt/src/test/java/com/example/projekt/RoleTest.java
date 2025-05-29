package com.example.projekt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testConstructorAndGetters() {
        Role role = new Role(1, "Admin");

        assertEquals(1, role.getId());
        assertEquals("Admin", role.getName());
    }

    @Test
    void testToStringReturnsRoleName() {
        Role role = new Role(2, "Pracownik");

        assertEquals("Pracownik", role.toString());
    }
}
