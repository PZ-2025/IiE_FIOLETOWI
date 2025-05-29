package com.example.projekt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Klasa pomocnicza do bezpiecznego haszowania haseł użytkowników.
 * Wykorzystuje algorytm PBKDF2WithHmacSHA256 z losową solą.
 */
public class PasswordHasher {

    /** Liczba iteracji używana do generowania klucza. */
    private static final int ITERATIONS = 100_000;

    /** Długość klucza w bitach. */
    private static final int KEY_LENGTH = 256;

    /** Używany algorytm haszujący. */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Generuje skrót hasła z podaną solą przy użyciu algorytmu PBKDF2.
     *
     * @param password hasło w postaci tekstowej
     * @param salt tablica bajtów zawierająca sól
     * @return ciąg znaków zawierający zakodowaną solę i skrót, oddzielone dwukropkiem
     * @throws RuntimeException jeśli wystąpi błąd algorytmu lub generowania klucza
     */
    public static String hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Błąd haszowania hasła", e);
        }
    }

    /**
     * Generuje losową sól do haszowania hasła.
     *
     * @return 16-bajtowa tablica zawierająca losową sól
     */
    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Weryfikuje hasło użytkownika przez porównanie skrótu wprowadzonego hasła
     * z zapisanym hashem.
     *
     * @param password hasło podane przez użytkownika
     * @param stored zapisany hash w formacie base64(salt):base64(hash)
     * @return true jeśli hasło jest poprawne, false w przeciwnym razie
     */
    public static boolean verifyPassword(String password, String stored) {
        String[] parts = stored.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String hashOfInput = hashPassword(password, salt).split(":")[1];
        return hashOfInput.equals(parts[1]);
    }
}
