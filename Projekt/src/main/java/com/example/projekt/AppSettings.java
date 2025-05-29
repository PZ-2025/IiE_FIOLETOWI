package com.example.projekt;

import java.io.*;
import java.util.Properties;

/**
 * Klasa AppSettings odpowiada za zarządzanie ustawieniami aplikacji,
 * takimi jak motyw graficzny i rozmiar czcionki.
 * Ustawienia są przechowywane w pliku properties i ładowane podczas uruchomienia aplikacji.
 */
public class AppSettings {
    /** Ścieżka do pliku konfiguracyjnego z ustawieniami użytkownika. */
    private static final String CONFIG_FILE = "user-settings.properties";

    /** Obiekt właściwości przechowujący ustawienia aplikacji. */
    private static final Properties props = new Properties();

    // Blok statyczny do ładowania ustawień z pliku
    static {
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("Używanie domyślnych ustawień.");
        }
    }

    /**
     * Zwraca aktualny motyw aplikacji.
     *
     * @return nazwa motywu, domyślnie "Domyślny" jeśli nie ustawiono
     */
    public static String getTheme() {
        return props.getProperty("theme", "Domyślny");
    }

    /**
     * Zwraca aktualny rozmiar czcionki.
     *
     * @return rozmiar czcionki, domyślnie 14 jeśli nie ustawiono
     */
    public static int getFontSize() {
        return Integer.parseInt(props.getProperty("fontSize", "14"));
    }

    /**
     * Ustawia nowy motyw aplikacji i zapisuje zmianę do pliku konfiguracyjnego.
     *
     * @param theme nowy motyw aplikacji
     */
    public static void setTheme(String theme) {
        props.setProperty("theme", theme);
        save();
    }

    /**
     * Ustawia nowy rozmiar czcionki i zapisuje zmianę do pliku konfiguracyjnego.
     *
     * @param size nowy rozmiar czcionki
     */
    public static void setFontSize(int size) {
        props.setProperty("fontSize", String.valueOf(size));
        save();
    }

    /**
     * Zapisuje bieżące ustawienia do pliku konfiguracyjnego.
     */
    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
