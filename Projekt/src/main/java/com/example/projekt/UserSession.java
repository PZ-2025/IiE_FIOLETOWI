package com.example.projekt;

/**
 * Klasa zarządzająca sesją zalogowanego użytkownika.
 * <p>
 * Przechowuje aktualnie zalogowanego użytkownika i umożliwia globalny dostęp do tych danych
 * w ramach działania aplikacji. Sesję można zainicjować tylko raz, a następnie uzyskać dostęp
 * poprzez metodę {@link #getInstance()}.
 */
public class UserSession {
    private static UserSession instance;
    private User user;

    private static String currentTheme = "/com/example/projekt/styles/themes/default.css";
    private static String currentFontSize = "/com/example/projekt/styles/fonts/medium.css"; // domyślny rozmiar

    private UserSession(User user) {
        this.user = user;
    }

    public static void init(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public static void clearSession() {
        instance = null;
    }

    // Styl motywu (dark/light/default)
    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String themePath) {
        currentTheme = themePath;
    }

    // Styl czcionki (small/medium/large)
    public static String getCurrentFontSize() {
        return currentFontSize;
    }

    public static void setCurrentFontSize(String fontSizePath) {
        currentFontSize = fontSizePath;
    }
}
