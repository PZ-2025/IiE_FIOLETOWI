package com.example.projekt;
/**
 * Klasa reprezentuje użytkownika systemu.
 * Przechowuje podstawowe informacje o pracowniku, takie jak imię, nazwisko,
 * login, hasło, płaca oraz przypisanie do grupy i roli.
 * @author KrzysztofDrozda
 * @version 1.0
 * @since 2025-04-25
 */
public class User {
    private int id;
    private String imie;
    private String nazwisko;
    private String login;
    private String haslo;
    private double placa;
    private int idGrupy;
    private int idRoli;
    private Role role;
    private String nazwaRoli;
    private String nazwaGrupy;
    boolean archiwizacja;
    /**
     * Tworzy nową instancję użytkownika.
     *
     * @param id         identyfikator użytkownika
     * @param imie       imię użytkownika
     * @param nazwisko   nazwisko użytkownika
     * @param login      login użytkownika
     * @param haslo      hasło użytkownika
     * @param placa      wysokość wynagrodzenia
     * @param idGrupy    identyfikator grupy
     * @param idRoli     identyfikator roli
     * @param nazwaRoli  nazwa roli (np. "Admin")
     */
    public User(int id, String imie, String nazwisko, String login, String haslo,
                double placa, int idGrupy, int idRoli, String nazwaRoli, String nazwaGrupy, boolean archiwizacja) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.placa = placa;
        this.idGrupy = idGrupy;
        this.idRoli = idRoli;
        this.nazwaRoli = nazwaRoli;
        this.nazwaGrupy=nazwaGrupy;
        this.archiwizacja=archiwizacja;
    }

    // Gettery
    public int getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getLogin() { return login; }
    public String getHaslo() { return haslo; }
    public double getPlaca() { return placa; }
    public int getIdGrupy() { return idGrupy; }
    public int getIdRoli() { return idRoli; }


    public String getRole() {return nazwaRoli;}
    public void setRole(String nazwaRoli) {
        this.nazwaRoli = nazwaRoli;
    }

    public String getGroup() {return nazwaGrupy;}
    public void setGroup(String nazwaGrupy) {
        this.nazwaGrupy = nazwaGrupy;
    }
    public boolean isArchiwizacja() {
        return archiwizacja;
    }
    public void setArchiwizacja(boolean archiwizacja) {
        this.archiwizacja = archiwizacja;
    }
    /**
     * Sprawdza, czy użytkownik ma rolę administratora.
     * @return {@code true} jeśli rola to "Admin", w przeciwnym razie {@code false}
     */
    public boolean isAdmin() {return "Admin".equalsIgnoreCase(nazwaRoli);}
    public boolean isManager() {return "Kierownik".equalsIgnoreCase(nazwaRoli);}

    @Override
    public String toString() {
        return imie + " " + nazwisko;
    }

}
