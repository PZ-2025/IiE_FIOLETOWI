package com.example.projekt;

/**
 * Klasa zarządzająca sesją zalogowanego użytkownika.
 * <p>
 * Przechowuje aktualnie zalogowanego użytkownika i umożliwia globalny dostęp do tych danych
 * w ramach działania aplikacji. Sesję można zainicjować tylko raz, a następnie uzyskać dostęp
 * poprzez metodę {@link #getInstance()}.
 * @author KrzysztofDrozda
 * @version 1.0
 * @since 2025-04-25
 */

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    /**
     * Inicjalizuje sesję użytkownika, jeśli nie została jeszcze utworzona.
     *
     * @param user Obiekt użytkownika do powiązania z bieżącą sesją.
     */
    public static void init(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    /**
     * Zwraca instancję bieżącej sesji użytkownika.
     *
     * @return Instancja klasy {@code UserSession}, lub {@code null}, jeśli nie została zainicjalizowana.
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Zwraca użytkownika przypisanego do sesji.
     *
     * @return Obiekt {@link User} reprezentujący zalogowanego użytkownika.
     */
    public User getUser() {
        return user;
    }
    /**
     * Czyści bieżącą sesję użytkownika.
     * <p>
     * Po wywołaniu tej metody instancja zostaje ustawiona na {@code null}.
     */
    public static void clearSession() {
        instance = null;
    }
}
