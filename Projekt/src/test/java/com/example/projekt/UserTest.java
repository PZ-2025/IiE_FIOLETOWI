package com.example.projekt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_ShouldSetAllFieldsCorrectly() {
        User user = new User(
                10, "Anna", "Nowak", "anowak", "haslo123",
                6000.0, 3, 2, "Uzytkownik", "Marketing", true
        );

        assertEquals(10, user.getId());
        assertEquals("Anna", user.getImie());
        assertEquals("Nowak", user.getNazwisko());
        assertEquals("anowak", user.getLogin());
        assertEquals("haslo123", user.getHaslo());
        assertEquals(6000.0, user.getPlaca());
        assertEquals(3, user.getIdGrupy());
        assertEquals(2, user.getIdRoli());
        assertEquals("Uzytkownik", user.getRole());
        assertEquals("Marketing", user.getGroup());
    }

    @Test
    void isAdmin_ShouldReturnTrue_WhenRoleIsAdmin() {
        User admin = new User(1, "Admin", "User", "admin", "pass",
                7000, 1, 1, "Admin", "IT", true);

        assertTrue(admin.isAdmin());
    }

    @Test
    void isAdmin_ShouldReturnFalse_WhenRoleIsNotAdmin() {
        User user = new User(2, "Test", "User", "test", "pass",
                5000, 2, 2, "Uzytkownik", "HR", true);

        assertFalse(user.isAdmin());
    }

    @Test
    void isManager_ShouldReturnTrue_WhenRoleIsKierownik() {
        User kierownik = new User(3, "Kasia", "Szef", "kasia", "pass",
                8000, 4, 3, "Kierownik", "Sprzedaż", true);

        assertTrue(kierownik.isManager());
    }

    @Test
    void isManager_ShouldReturnFalse_WhenRoleIsNotKierownik() {
        User user = new User(4, "Maciej", "Pracownik", "maciej", "pass",
                4000, 5, 4, "Uzytkownik", "Obsługa klienta", true);

        assertFalse(user.isManager());
    }

    @Test
    void setRole_ShouldUpdateRole() {
        User user = new User(5, "Tomasz", "Nowy", "tomek", "pass",
                4500, 6, 5, "Uzytkownik", "Produkcja", true);

        user.setRole("Admin");
        assertEquals("Admin", user.getRole());
        assertTrue(user.isAdmin());
    }

    @Test
    void setGroup_ShouldUpdateGroupName() {
        User user = new User(6, "Zofia", "Grupa", "zofia", "pass",
                4700, 7, 6, "Uzytkownik", "StaraGrupa", true);

        user.setGroup("NowaGrupa");
        assertEquals("NowaGrupa", user.getGroup());
    }
}
