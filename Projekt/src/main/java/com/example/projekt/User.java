package com.example.projekt;

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



    // Konstruktor
    public User(int id, String imie, String nazwisko, String login, String haslo,
                double placa, int idGrupy, int idRoli, String nazwaRoli) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.placa = placa;
        this.idGrupy = idGrupy;
        this.idRoli = idRoli;
        this.nazwaRoli = nazwaRoli;
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
    public boolean isAdmin() {return "Admin".equalsIgnoreCase(nazwaRoli);}


}
