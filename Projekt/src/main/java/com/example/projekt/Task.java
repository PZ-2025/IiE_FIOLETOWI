package com.example.projekt;

import javafx.beans.property.SimpleStringProperty;

/**
 * Klasa reprezentująca zadanie (Task) w systemie.
 * Zawiera informacje o nazwie, statusie, priorytecie, datach, przypisanym pracowniku,
 * produkcie, kierunku, komentarzu oraz ilości.
 * Właściwości oparte na JavaFX SimpleStringProperty umożliwiają łatwe powiązanie z UI.
 */
public class Task {

    /** Unikalny identyfikator zadania */
    private int id;

    /** Nazwa zadania */
    private final SimpleStringProperty nazwa;

    /** Status zadania */
    private final SimpleStringProperty status;

    /** Priorytet zadania */
    private final SimpleStringProperty priorytet;

    /** Data utworzenia lub rozpoczęcia zadania (w formacie tekstowym) */
    private final SimpleStringProperty data;

    /** Data zakończenia zadania (może być pusta) */
    private final SimpleStringProperty koniec;

    /** Komentarz do zadania */
    private final SimpleStringProperty komentarz;

    /** Pracownik przypisany do zadania */
    private final SimpleStringProperty pracownik;

    /** Produkt powiązany z zadaniem */
    private final SimpleStringProperty produkt;

    /** Kierunek powiązany z zadaniem */
    private final SimpleStringProperty kierunek;

    /** Ilość (jako tekst, może być null lub pusta) */
    private final SimpleStringProperty ilosc;

    /**
     * Konstruktor tworzący nowe zadanie z podanymi wartościami.
     *
     * @param id           unikalny identyfikator zadania
     * @param nazwa        nazwa zadania
     * @param status       status zadania
     * @param priorytet    priorytet zadania
     * @param data         data rozpoczęcia/utworzenia zadania
     * @param produkt      nazwa produktu powiązanego z zadaniem
     * @param kierunek     kierunek powiązany z zadaniem
     * @param komentarz    komentarz do zadania
     * @param ilosc        ilość (może być null lub pusta)
     * @param pracownik    nazwa pracownika przypisanego do zadania
     */
    public Task(int id, String nazwa, String status, String priorytet, String data, String produkt, String kierunek, String komentarz, String ilosc, String pracownik) {
        this.id = id;
        this.nazwa = new SimpleStringProperty(nazwa);
        this.status = new SimpleStringProperty(status);
        this.priorytet = new SimpleStringProperty(priorytet);
        this.data = new SimpleStringProperty(data);
        this.koniec = new SimpleStringProperty("");
        this.pracownik = new SimpleStringProperty(pracownik);
        this.komentarz = new SimpleStringProperty(komentarz);
        this.produkt = new SimpleStringProperty(produkt);
        this.kierunek = new SimpleStringProperty(kierunek);
        this.ilosc = new SimpleStringProperty(ilosc);
    }

    /** @return identyfikator zadania */
    public int getId() { return id; }

    /** @return nazwa zadania */
    public String getNazwa() { return nazwa.get(); }

    /** @return status zadania */
    public String getStatus() { return status.get(); }

    /** @return priorytet zadania */
    public String getPriorytet() { return priorytet.get(); }

    /** @return data rozpoczęcia lub utworzenia zadania */
    public String getData() { return data.get(); }

    /** @return data zakończenia zadania */
    public String getKoniec() { return koniec.get(); }

    /** @return nazwa pracownika przypisanego do zadania */
    public String getPracownik() { return pracownik.get(); }

    /** @return komentarz do zadania */
    public String getKomentarz() { return komentarz.get(); }

    /** @return nazwa produktu powiązanego z zadaniem */
    public String getProdukt() { return produkt.get(); }

    /** @return kierunek powiązany z zadaniem */
    public String getKierunek() { return kierunek.get(); }

    /** @return ilość (tekstowa reprezentacja) */
    public String getIlosc() { return ilosc.get(); }

    /**
     * Ustawia datę zakończenia zadania.
     *
     * @param koniec data zakończenia jako String
     */
    public void setEndDate(String koniec) {
        this.koniec.set(koniec);
    }

    /**
     * Ustawia pracownika przypisanego do zadania.
     *
     * @param pracownik nazwa pracownika
     */
    public void setAssignedTo(String pracownik) {
        this.pracownik.set(pracownik);
    }

    /** @return właściwość nazwy zadania do powiązania z UI */
    public SimpleStringProperty nazwaProperty() { return nazwa; }

    /** @return właściwość statusu zadania do powiązania z UI */
    public SimpleStringProperty statusProperty() { return status; }

    /** @return właściwość priorytetu zadania do powiązania z UI */
    public SimpleStringProperty priorytetProperty() { return priorytet; }

    /** @return właściwość daty zadania do powiązania z UI */
    public SimpleStringProperty dataProperty() { return data; }

    /** @return właściwość komentarza zadania do powiązania z UI */
    public SimpleStringProperty komentarzProperty() { return komentarz; }

    /** @return właściwość daty zakończenia do powiązania z UI */
    public SimpleStringProperty koniecProperty() { return koniec; }

    /** @return właściwość pracownika do powiązania z UI */
    public SimpleStringProperty pracownikProperty() { return pracownik; }
}
