package com.example.projekt;

import java.io.*;
import java.util.Properties;

public class AppSettings {
    private static final String CONFIG_FILE = "user-settings.properties"; // np. w katalogu domowym

    private static final Properties props = new Properties();

    static {
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("Używanie domyślnych ustawień.");
        }
    }

    public static String getTheme() {
        return props.getProperty("theme", "Domyślny");
    }

    public static int getFontSize() {
        return Integer.parseInt(props.getProperty("fontSize", "14"));
    }

    public static void setTheme(String theme) {
        props.setProperty("theme", theme);
        save();
    }

    public static void setFontSize(int size) {
        props.setProperty("fontSize", String.valueOf(size));
        save();
    }

    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
