package com.example.projekt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @AfterEach
    void tearDown() {
        // Czyści sesję po każdym teście
        UserSession.clearSession();
    }

    private User createTestUser(String login) {
        return new User(
                1,
                "Jan",
                "Kowalski",
                login,
                "haslo123",
                5000.0,
                101,
                1,
                "Admin"
        );
    }

    @Test
    void testInitCreatesSession() {
        User user = createTestUser("jan.kowalski");
        UserSession.init(user);

        UserSession session = UserSession.getInstance();
        assertNotNull(session);
        assertNotNull(session.getUser());
        assertEquals("jan.kowalski", session.getUser().getLogin());
    }

    @Test
    void testInitDoesNotOverrideExistingSession() {
        User firstUser = createTestUser("jan.kowalski");
        User secondUser = createTestUser("adam.nowak");

        UserSession.init(firstUser);
        UserSession.init(secondUser);

        UserSession session = UserSession.getInstance();
        assertEquals("jan.kowalski", session.getUser().getLogin());
    }

    @Test
    void testGetInstanceReturnsNullIfNotInitialized() {
        assertNull(UserSession.getInstance());
    }

    @Test
    void testClearSessionResetsInstance() {
        User user = createTestUser("jan.kowalski");
        UserSession.init(user);

        assertNotNull(UserSession.getInstance());

        UserSession.clearSession();

        assertNull(UserSession.getInstance());
    }
}
