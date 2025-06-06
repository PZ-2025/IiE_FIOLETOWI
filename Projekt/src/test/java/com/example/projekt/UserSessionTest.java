package com.example.projekt;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        // Czyszczenie singletona i resetowanie stylów przed każdym testem
        UserSession.clearSession();
        UserSession.setCurrentTheme("/com/example/projekt/styles/themes/default.css");
        UserSession.setCurrentFontSize("/com/example/projekt/styles/fonts/medium.css");

        // Testowy użytkownik
        testUser = new User(
                1, "Jan", "Kowalski", "janek", "1234",
                5000.0, 2, 1, "Admin", "Zespół IT", true
        );
    }

    @Test
    void init_ShouldCreateSession_WhenNoSessionExists() {
        UserSession.init(testUser);
        UserSession session = UserSession.getInstance();

        assertNotNull(session);
        assertEquals("janek", session.getUser().getLogin());
        assertEquals("Jan", session.getUser().getImie());
    }

    @Test
    void init_ShouldNotOverwriteExistingSession() {
        User firstUser = new User(1, "A", "A", "user1", "pass", 4000.0, 1, 1, "Admin", "Grupa1", true);
        User secondUser = new User(2, "B", "B", "user2", "pass", 4500.0, 2, 2, "Uzytkownik", "Grupa2", true);

        UserSession.init(firstUser);
        UserSession.init(secondUser); // Próba nadpisania

        assertEquals("user1", UserSession.getInstance().getUser().getLogin()); // nie powinno się zmienić
    }

    @Test
    void clearSession_ShouldNullifyInstance() {
        UserSession.init(testUser);
        assertNotNull(UserSession.getInstance());

        UserSession.clearSession();
        assertNull(UserSession.getInstance());
    }

    @Test
    void themeManagement_ShouldUpdateAndReturnThemePath() {
        assertEquals("/com/example/projekt/styles/themes/default.css", UserSession.getCurrentTheme());

        String newTheme = "/com/example/projekt/styles/themes/dark.css";
        UserSession.setCurrentTheme(newTheme);
        assertEquals(newTheme, UserSession.getCurrentTheme());
    }

    @Test
    void fontSizeManagement_ShouldUpdateAndReturnFontSizePath() {
        assertEquals("/com/example/projekt/styles/fonts/medium.css", UserSession.getCurrentFontSize());

        String newFontSize = "/com/example/projekt/styles/fonts/large.css";
        UserSession.setCurrentFontSize(newFontSize);
        assertEquals(newFontSize, UserSession.getCurrentFontSize());
    }
}
