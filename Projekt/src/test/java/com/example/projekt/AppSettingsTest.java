package com.example.projekt;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsTest {

    private static final String TEST_CONFIG_FILE = "test-user-settings.properties";

    @BeforeEach
    void setUp() throws IOException {
        // Tworzymy tymczasowy plik z domyślną zawartością
        Properties testProps = new Properties();
        testProps.setProperty("theme", "Ciemny");
        testProps.setProperty("fontSizeLabel", "Duża");

        try (OutputStream out = new FileOutputStream(TEST_CONFIG_FILE)) {
            testProps.store(out, "Test config");
        }

        // Podmieniamy plik produkcyjny na testowy (tymczasowo)
        Files.copy(Path.of(TEST_CONFIG_FILE), Path.of("user-settings.properties"), StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("user-settings.properties"));
        Files.deleteIfExists(Path.of(TEST_CONFIG_FILE));
    }


    @Test
    void setTheme_ShouldUpdatePropertyAndSetUserSessionTheme() {
        AppSettings.setTheme("Jasny");
        assertEquals("Jasny", AppSettings.getTheme());
        assertEquals("/com/example/projekt/styles/themes/light.css", UserSession.getCurrentTheme());
    }

    @Test
    void setFontSizeLabel_ShouldUpdatePropertyAndSetUserSessionFontSize() {
        AppSettings.setFontSizeLabel("Mała");
        assertEquals("Mała", AppSettings.getFontSizeLabel());
        assertEquals("/com/example/projekt/styles/fonts/small.css", UserSession.getCurrentFontSize());
    }

    @Test
    void setTheme_ShouldFallbackToDefaultForUnknownValue() {
        AppSettings.setTheme("cośdziwnego");
        assertEquals("/com/example/projekt/styles/themes/default.css", UserSession.getCurrentTheme());
    }

    @Test
    void setFontSizeLabel_ShouldFallbackToDefaultForUnknownValue() {
        AppSettings.setFontSizeLabel("superduża");
        assertEquals("/com/example/projekt/styles/fonts/medium.css", UserSession.getCurrentFontSize());
    }
}
