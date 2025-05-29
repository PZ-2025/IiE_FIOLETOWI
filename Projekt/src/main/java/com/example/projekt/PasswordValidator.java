package com.example.projekt;

/**
 * Klasa pomocnicza do walidacji haseł użytkowników.
 * Sprawdza zgodność haseł z podstawowymi wymaganiami bezpieczeństwa.
 */
public class PasswordValidator {

    /**
     * Sprawdza, czy podane hasło spełnia wymagania:
     * <ul>
     *     <li>minimum 8 znaków,</li>
     *     <li>co najmniej jedna mała litera,</li>
     *     <li>co najmniej jedna wielka litera,</li>
     *     <li>co najmniej jedna cyfra.</li>
     * </ul>
     *
     * @param password hasło do sprawdzenia
     * @return true jeśli hasło spełnia wszystkie wymagania, false w przeciwnym razie
     */
    public static boolean isPasswordValid(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;

        if (!password.matches(".*[a-z].*")) return false; // mała litera
        if (!password.matches(".*[A-Z].*")) return false; // wielka litera
        if (!password.matches(".*\\d.*")) return false;   // cyfra

        return true;
    }

    /**
     * Zwraca komunikat z wymaganiami dotyczącymi hasła,
     * przydatny do wyświetlenia użytkownikowi.
     *
     * @return komunikat z wymaganiami dotyczącymi hasła
     */
    public static String getPasswordRequirementsMessage() {
        return "Hasło musi mieć min. 8 znaków, " +
                "zawierać małą literę, wielką literę i cyfrę";
    }
}
