package com.example.projekt;

import java.io.*;
import java.util.Properties;

/**
 * Klasa odpowiedzialna za zarządzanie ustawieniami aplikacji.
 * Umożliwia ładowanie, przechowywanie i modyfikację ustawień takich jak motyw graficzny czy rozmiar czcionki.
 * Ustawienia są przechowywane w pliku properties i automatycznie ładowane przy starcie aplikacji.
 */
public class AppSettings {
    private static final String CONFIG_FILE = "user-settings.properties";
    private static final Properties props = new Properties();

    static {
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("Używanie domyślnych ustawień.");
        }

        // Ustaw motyw i czcionkę globalnie po starcie
        setTheme(getTheme());
        setFontSizeLabel(getFontSizeLabel());
    }

    /**
     * Pobiera aktualnie ustawiony motyw graficzny.
     * @return Nazwa aktualnego motywu ("Jasny", "Ciemny" lub "Domyślny" jeśli nie ustawiono)
     */
    public static String getTheme() {
        return props.getProperty("theme", "Domyślny");
    }

    /**
     * Ustawia nowy motyw graficzny i zapisuje go w konfiguracji.
     * Automatycznie aktualizuje motyw w całej aplikacji poprzez UserSession.
     * @param theme Nazwa motywu do ustawienia ("Jasny", "Ciemny" lub "Domyślny")
     */
    public static void setTheme(String theme) {
        props.setProperty("theme", theme);
        save();

        // Automatycznie ustaw motyw globalnie
        String themePath = switch (theme.toLowerCase()) {
            case "jasny" -> "/com/example/projekt/styles/themes/light.css";
            case "ciemny" -> "/com/example/projekt/styles/themes/dark.css";
            default -> "/com/example/projekt/styles/themes/default.css";
        };
        UserSession.setCurrentTheme(themePath);
    }

    /**
     * Pobiera aktualnie ustawioną etykietę rozmiaru czcionki.
     * @return Etykieta rozmiaru czcionki ("Mała", "Średnia" lub "Duża" jeśli nie ustawiono)
     */
    public static String getFontSizeLabel() {
        return props.getProperty("fontSizeLabel", "Średnia");
    }

    /**
     * Ustawia nowy rozmiar czcionki na podstawie etykiety i zapisuje go w konfiguracji.
     * Automatycznie aktualizuje rozmiar czcionki w całej aplikacji poprzez UserSession.
     * @param label Etykieta rozmiaru czcionki do ustawienia ("Mała", "Średnia" lub "Duża")
     */
    public static void setFontSizeLabel(String label) {
        props.setProperty("fontSizeLabel", label);
        save();

        String fontPath = switch (label.toLowerCase()) {
            case "mała" -> "/com/example/projekt/styles/fonts/small.css";
            case "duża" -> "/com/example/projekt/styles/fonts/large.css";
            default -> "/com/example/projekt/styles/fonts/medium.css";
        };
        UserSession.setCurrentFontSize(fontPath);
    }

    /**
     * Zapisuje aktualne ustawienia do pliku konfiguracyjnego.
     * Metoda jest wywoływana automatycznie przy każdej zmianie ustawień.
     */
    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}