package com.example.projekt;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsTest {

    private static final String CONFIG_FILE = "user-settings.properties";
    private static File backupFile;

    @BeforeAll
    static void backupOriginalConfig() throws IOException {
        File config = new File(CONFIG_FILE);
        if (config.exists()) {
            backupFile = new File(CONFIG_FILE + ".bak");
            Files.copy(config.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterAll
    static void restoreOriginalConfig() throws IOException {
        if (backupFile != null && backupFile.exists()) {
            Files.copy(backupFile.toPath(), new File(CONFIG_FILE).toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupFile.delete();
        } else {
            // Jeśli plik nie istniał wcześniej, usuń utworzony w testach
            File config = new File(CONFIG_FILE);
            if (config.exists()) {
                config.delete();
            }
        }
    }

    @BeforeEach
    void cleanConfigFile() {
        File config = new File(CONFIG_FILE);
        if (config.exists()) {
            config.delete();
        }
        // Ponieważ AppSettings ładuje statycznie przy starcie klasy, trzeba wymusić przeładowanie klasy,
        // ale bez refaktoru jest to bardzo trudne. Dlatego testujemy, że metody działają z nowym plikiem,
        // choć konfiguracja może być z cache Properties.
    }

    @Test
    void testDefaultValues() {
        // Domyślne wartości jeśli plik nie istnieje
        assertEquals("Domyślny", AppSettings.getTheme(), "Domyślny motyw powinien być 'Domyślny'");
        assertEquals(14, AppSettings.getFontSize(), "Domyślny rozmiar czcionki powinien być 14");
    }

    @Test
    void testSetAndGetTheme() {
        AppSettings.setTheme("TestMotyw");
        assertEquals("TestMotyw", AppSettings.getTheme(), "Motyw po ustawieniu powinien być 'TestMotyw'");
    }

    @Test
    void testSetAndGetFontSize() {
        AppSettings.setFontSize(22);
        assertEquals(22, AppSettings.getFontSize(), "Rozmiar czcionki po ustawieniu powinien być 22");
    }
}
