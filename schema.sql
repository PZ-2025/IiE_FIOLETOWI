CREATE DATABASE IF NOT EXISTS HurtPolSan;
USE HurtPolSan;

CREATE TABLE role (
    id_roli INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(15) NOT NULL
);

CREATE TABLE grupy (
    id_grupy INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

CREATE TABLE pracownicy (
    id_pracownika INT PRIMARY KEY AUTO_INCREMENT,
    imie VARCHAR(100) NOT NULL,
    nazwisko VARCHAR(100) NOT NULL,
    login VARCHAR(200) UNIQUE NOT NULL,
    haslo VARCHAR(255) NOT NULL,
    placa DECIMAL(10,2) NOT NULL,
    id_grupy INT NOT NULL,
    id_roli INT NOT NULL,
    FOREIGN KEY (id_grupy) REFERENCES grupy(id_grupy),
    FOREIGN KEY (id_roli) REFERENCES role(id_roli)
);

CREATE TABLE produkty (
    id_produktu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL,
    stan INT NOT NULL,
    cena DECIMAL(10,2) NOT NULL,
    limit_stanow INT NOT NULL
);

CREATE TABLE statusy (
    id_statusu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

CREATE TABLE priorytety (
    id_priorytetu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

CREATE TABLE transakcje (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_produktu INT NOT NULL,
    id_pracownika INT NOT NULL,
    ilosc INT NOT NULL,
    data_transakcji DATE NOT NULL,
    FOREIGN KEY (id_produktu) REFERENCES produkty(id_produktu),
    FOREIGN KEY (id_pracownika) REFERENCES pracownicy(id_pracownika)
);

CREATE TABLE zadania (
    id_zadania INT PRIMARY KEY AUTO_INCREMENT,
    id_pracownika INT NOT NULL,
    nazwa VARCHAR(100) NOT NULL,
    id_statusu INT NOT NULL,
    id_priorytetu INT NOT NULL,
    data_rozpoczecia DATE NOT NULL,
    FOREIGN KEY (id_pracownika) REFERENCES pracownicy(id_pracownika),
    FOREIGN KEY (id_statusu) REFERENCES statusy(id_statusu),
    FOREIGN KEY (id_priorytetu) REFERENCES priorytety(id_priorytetu)
);


INSERT INTO role (nazwa) VALUES 
('Admin'), ('Manager'), ('Pracownik'), ('Kierownik'), ('Specjalista'), ('Asystent'), ('Technik'), ('Inżynier'), ('Operator'), ('Analityk');

INSERT INTO grupy (nazwa) VALUES 
('IT'), ('Finanse'), ('HR'), ('Logistyka'), ('Sprzedaż'), ('Marketing'), ('Serwis'), ('Kontrola Jakości'), ('Obsługa Klienta');

INSERT INTO statusy (nazwa) VALUES 
('Oczekujące'), ('Rozpoczęte'), ('W trakcie'), ('Zakończone');

INSERT INTO priorytety (nazwa) VALUES 
('Niski'), ('Średni'), ('Wysoki');

INSERT INTO pracownicy (imie, nazwisko, login, haslo, placa, id_grupy, id_roli) VALUES 
('Admin', 'Admin', 'admin', 'admin', 5000.00, 1, 1),
('Jan', 'Kowalski', 'jkowalski', 'haslo123', 5000.00, 1, 2),
('Anna', 'Nowak', 'anowak', 'haslo123', 5200.00, 2, 3),
('Piotr', 'Zieliński', 'pzielinski', 'haslo123', 4800.00, 3, 4),
('Katarzyna', 'Wiśniewska', 'kwisniewska', 'haslo123', 5300.00, 4, 5);

INSERT INTO produkty (nazwa, stan, cena, limit_stanow) VALUES 
('Laptop Dell', 20, 2500, 5), ('Smartfon iPhone 13', 35, 4500, 8), ('Router TP-Link', 18, 250, 4), ('Kamera Logitech', 25, 150, 6),
('Monitor Samsung', 15, 1400, 3), ('Drukarka HP', 10, 1600, 2), ('Tablet Lenovo', 12, 2000, 4), ('Klawiatura Logitech', 30, 80, 5),
('Mysz bezprzewodowa', 40, 70, 10), ('Powerbank Xiaomi', 22, 120, 6), ('SSD 1TB Samsung', 18, 250, 3), ('Pendrive 128GB', 50, 45, 10),
('Słuchawki Sony', 28, 200, 5), ('Głośniki JBL', 16, 250, 4), ('Mikrofon Razer', 12, 200, 2), ('Webcam Logitech', 35, 140, 6),
('Dysk HDD 2TB', 14, 200, 3), ('Router ASUS', 20, 300, 4), ('Ładowarka Anker', 30, 30, 8), ('Hub USB-C', 25, 55, 5),
('Obiektyw Canon', 9, 650, 2), ('Stacja dokująca Dell', 13, 345, 3), ('Kabel HDMI 3m', 40, 20, 12), ('Torba na laptopa', 33, 35, 7),
('Krzesło biurowe', 18, 400, 4), ('Biurko gamingowe', 8, 700, 2), ('Monitor LG 27"', 17, 2300, 4), ('Drukarka laserowa Brother', 7, 2332, 2),
('Smartwatch Samsung', 25, 540, 5), ('Mysz gamingowa Razer', 22, 234, 6);

INSERT INTO transakcje (id_produktu, id_pracownika, ilosc, data_transakcji) VALUES 
(1, 1, 2, '2025-03-01'), (2, 2, 1, '2025-03-02'), (3, 3, 3, '2025-03-03'), (4, 4, 1, '2025-03-04'),
(5, 1, 2, '2025-03-05'), (6, 2, 1, '2025-03-06'), (7, 3, 3, '2025-03-07'), (8, 4, 1, '2025-03-08'),
(9, 1, 2, '2025-03-09'), (10, 2, 1, '2025-03-10'), (11, 3, 3, '2025-03-11'), (12, 4, 1, '2025-03-12');

INSERT INTO zadania (id_pracownika, nazwa, id_statusu, id_priorytetu, data_rozpoczecia) VALUES
(1, 'Przygotowanie raportu kwartalnego', 1, 2, '2025-03-01'),
(2, 'Audyt wewnętrzny IT', 2, 3, '2025-03-02'),
(3, 'Weryfikacja danych klientów', 3, 1, '2025-03-03'),
(4, 'Aktualizacja systemu ERP', 4, 2, '2025-03-04'),
(5, 'Szkolenie BHP dla zespołu', 1, 2, '2025-03-05'),
(1, 'Zamówienie sprzętu komputerowego', 2, 1, '2025-03-06'),
(2, 'Migracja serwera bazy danych', 3, 3, '2025-03-07'),
(3, 'Wdrożenie polityki bezpieczeństwa', 4, 2, '2025-03-08'),
(4, 'Przygotowanie prezentacji dla zarządu', 1, 1, '2025-03-09'),
(5, 'Konfiguracja drukarek sieciowych', 2, 2, '2025-03-10'),
(1, 'Weryfikacja faktur', 3, 1, '2025-03-11'),
(2, 'Inwentaryzacja sprzętu w magazynie', 4, 3, '2025-03-12'),
(3, 'Rekrutacja nowych pracowników', 1, 2, '2025-03-13'),
(4, 'Optymalizacja procesu zakupowego', 2, 2, '2025-03-14'),
(5, 'Testy nowego oprogramowania', 3, 3, '2025-03-15'),
(1, 'Aktualizacja dokumentacji technicznej', 4, 1, '2025-03-16'),
(2, 'Przygotowanie szkoleń e-learningowych', 1, 2, '2025-03-17'),
(3, 'Analiza sprzedaży Q1', 2, 3, '2025-03-18'),
(4, 'Udział w konferencji branżowej', 3, 2, '2025-03-19'),
(5, 'Modernizacja sieci biurowej', 4, 3, '2025-03-20'),
(1, 'Przegląd sprzętu serwisowego', 1, 1, '2025-03-21'),
(2, 'Stworzenie bazy danych kontaktów', 2, 2, '2025-03-22'),
(3, 'Wsparcie klienta VIP', 3, 3, '2025-03-23'),
(4, 'Projekt nowej strony internetowej', 4, 2, '2025-03-24'),
(5, 'Analiza kosztów operacyjnych', 1, 1, '2025-03-25'),
(1, 'Wprowadzenie polityki RODO', 2, 2, '2025-03-26'),
(2, 'Zakup licencji oprogramowania', 3, 3, '2025-03-27'),
(3, 'Serwis drukarek w dziale HR', 4, 1, '2025-03-28'),
(4, 'Przygotowanie do audytu ISO', 1, 2, '2025-03-29'),
(5, 'Badanie satysfakcji pracowników', 2, 2, '2025-03-30');
