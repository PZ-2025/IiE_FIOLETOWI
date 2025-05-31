package com.example.projekt;

import java.io.*;
import java.util.Properties;

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

    // Motyw graficzny: Jasny / Ciemny / Domyślny
    public static String getTheme() {
        return props.getProperty("theme", "Domyślny");
    }

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

    // Czcionka jako tekst: Mała / Średnia / Duża
    public static String getFontSizeLabel() {
        return props.getProperty("fontSizeLabel", "Średnia");
    }

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

    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
