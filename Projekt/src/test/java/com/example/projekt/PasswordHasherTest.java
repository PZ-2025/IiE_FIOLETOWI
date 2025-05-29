package com.example.projekt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    public void testGenerateSalt_lengthIs16() {
        byte[] salt = PasswordHasher.generateSalt();
        assertNotNull(salt, "Sól nie powinna być nullem");
        assertEquals(16, salt.length, "Sól powinna mieć 16 bajtów");
    }

    @Test
    public void testHashPassword_formatIsCorrect() {
        byte[] salt = PasswordHasher.generateSalt();
        String password = "mojeHaslo123";
        String hashed = PasswordHasher.hashPassword(password, salt);

        assertNotNull(hashed, "Hasz nie powinien być nullem");
        assertTrue(hashed.contains(":"), "Hasz powinien zawierać znak ':' oddzielający sól od hash");
        String[] parts = hashed.split(":");
        assertEquals(2, parts.length, "Hasz powinien mieć dwie części: sól i hash");
        assertFalse(parts[0].isEmpty(), "Część soli nie może być pusta");
        assertFalse(parts[1].isEmpty(), "Część hasha nie może być pusta");
    }

    @Test
    public void testVerifyPassword_correctPassword_returnsTrue() {
        String password = "tajneHaslo!";
        byte[] salt = PasswordHasher.generateSalt();
        String storedHash = PasswordHasher.hashPassword(password, salt);

        assertTrue(PasswordHasher.verifyPassword(password, storedHash),
                "Weryfikacja powinna zwrócić true dla poprawnego hasła");
    }

    @Test
    public void testVerifyPassword_wrongPassword_returnsFalse() {
        String correctPassword = "tajneHaslo!";
        String wrongPassword = "zleHaslo";
        byte[] salt = PasswordHasher.generateSalt();
        String storedHash = PasswordHasher.hashPassword(correctPassword, salt);

        assertFalse(PasswordHasher.verifyPassword(wrongPassword, storedHash),
                "Weryfikacja powinna zwrócić false dla niepoprawnego hasła");
    }
}
