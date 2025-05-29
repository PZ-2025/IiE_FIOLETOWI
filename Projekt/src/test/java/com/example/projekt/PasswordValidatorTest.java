package com.example.projekt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        assertTrue(PasswordValidator.isPasswordValid("StrongPass1"));
    }

    @Test
    void testNullPassword() {
        assertFalse(PasswordValidator.isPasswordValid(null));
    }

    @Test
    void testTooShortPassword() {
        assertFalse(PasswordValidator.isPasswordValid("S1a"));
    }

    @Test
    void testMissingLowercase() {
        assertFalse(PasswordValidator.isPasswordValid("PASSWORD1"));
    }

    @Test
    void testMissingUppercase() {
        assertFalse(PasswordValidator.isPasswordValid("password1"));
    }

    @Test
    void testMissingDigit() {
        assertFalse(PasswordValidator.isPasswordValid("Password"));
    }

    @Test
    void testPasswordExactly8Characters() {
        assertTrue(PasswordValidator.isPasswordValid("Aa1abcde"));
    }

    @Test
    void testMessageText() {
        String expected = "Hasło musi mieć min. 8 znaków, zawierać małą literę, wielką literę i cyfrę";
        assertEquals(expected, PasswordValidator.getPasswordRequirementsMessage());
    }
}
