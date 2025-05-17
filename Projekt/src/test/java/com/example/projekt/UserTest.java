package com.example.projekt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testIsAdminReturnsTrueForAdminRole() {
        User user = new User(1, "Jan", "Kowalski", "jkowalski", "haslo123", 5000.0, 1, 1, "Admin");
        assertTrue(user.isAdmin());
    }

    @Test
    void testIsAdminReturnsFalseForNonAdminRole() {
        User user = new User(2, "Anna", "Nowak", "anowak", "haslo123", 5200.0, 2, 3, "Pracownik");
        assertFalse(user.isAdmin());
    }
}