package com.example.projekt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User(1, "Jan", "Kowalski", "jkowalski", "tajneHaslo", 3500.50, 2, 3, "Admin");

        assertEquals(1, user.getId());
        assertEquals("Jan", user.getImie());
        assertEquals("Kowalski", user.getNazwisko());
        assertEquals("jkowalski", user.getLogin());
        assertEquals("tajneHaslo", user.getHaslo());
        assertEquals(3500.50, user.getPlaca());
        assertEquals(2, user.getIdGrupy());
        assertEquals(3, user.getIdRoli());
        assertEquals("Admin", user.getRole());
    }

    @Test
    void testSetRole() {
        User user = new User(1, "Anna", "Nowak", "anowak", "1234", 4200.0, 1, 2, "Pracownik");
        assertEquals("Pracownik", user.getRole());

        user.setRole("Kierownik");
        assertEquals("Kierownik", user.getRole());
    }

    @Test
    void testIsAdmin() {
        User admin = new User(1, "Adam", "Admin", "admin", "pass", 5000.0, 1, 1, "Admin");
        User user = new User(2, "Ewa", "User", "euser", "pass", 3000.0, 1, 2, "Pracownik");

        assertTrue(admin.isAdmin());
        assertFalse(user.isAdmin());

        User adminCaseInsensitive = new User(3, "Piotr", "Admin", "piotradm", "pass", 5000.0, 1, 1, "admin");
        assertTrue(adminCaseInsensitive.isAdmin());
    }

    @Test
    void testIsManager() {
        User manager = new User(1, "Marek", "Kierownik", "mkier", "pass", 4500.0, 2, 2, "Kierownik");
        User user = new User(2, "Julia", "Pracownik", "jprac", "pass", 3200.0, 2, 3, "Pracownik");

        assertTrue(manager.isManager());
        assertFalse(user.isManager());

        User managerCaseInsensitive = new User(3, "Olga", "Kierownik", "okier", "pass", 4500.0, 2, 2, "kierownik");
        assertTrue(managerCaseInsensitive.isManager());
    }
}
