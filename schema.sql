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

CREATE TABLE typ_produktu (
    id_typu_produktu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

CREATE TABLE produkty (
    id_produktu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL,
    stan INT NOT NULL,
    cena DECIMAL(10,2) NOT NULL,
    id_typu_produktu INT NOT NULL,
    limit_stanow INT NOT NULL,
    FOREIGN KEY (id_typu_produktu) REFERENCES typ_produktu(id_typu_produktu)
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
    data_zakonczenia DATE NOT NULL,
    komentarz VARCHAR(250),
    powiadomienia BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_pracownika) REFERENCES pracownicy(id_pracownika),
    FOREIGN KEY (id_statusu) REFERENCES statusy(id_statusu),
    FOREIGN KEY (id_priorytetu) REFERENCES priorytety(id_priorytetu)
);



INSERT INTO role (nazwa) VALUES 
('Admin'), ('Pracownik'), ('Kierownik');

INSERT INTO grupy (nazwa) VALUES 
('IT'), ('Finanse'), ('HR'), ('Logistyka'), ('Sprzedaż'), ('Marketing'), ('Serwis'), ('Kontrola Jakości'), ('Obsługa Klienta');

INSERT INTO statusy (nazwa) VALUES 
('Oczekujące'), ('Rozpoczęte'), ('W trakcie'), ('Zakończone');

INSERT INTO priorytety (nazwa) VALUES 
('Niski'), ('Średni'), ('Wysoki');

INSERT INTO typ_produktu (nazwa) VALUES
('Elektronika'),          
('Akcesoria komputerowe'),
('Peryferia'),          
('Meble biurowe'),      
('Foto / Wideo');         


INSERT INTO pracownicy (imie, nazwisko, login, haslo, placa, id_grupy, id_roli) VALUES 
('Admin', 'Admin', 'Admin123', 'rDlt1lrSXRTUhaYdrtrHPQ==:0JzlTGgCm3V3x9XgTCV1hIyXdr7UA45FgeNdyr4EgW0=', 5000.00, 1, 1),
('Jan', 'Kowalski', 'jkowalski', 'haslo123', 5000.00, 1, 2),
('Anna', 'Nowak', 'anowak', 'haslo123', 5200.00, 2, 2),
('Piotr', 'Zieliński', 'pzielinski', 'haslo123', 4800.00, 3, 3),
('Katarzyna', 'Wiśniewska', 'kwisniewska', 'haslo123', 5300.00, 4, 2);

INSERT INTO produkty (nazwa, stan, cena, limit_stanow, id_typu_produktu) VALUES
-- Elektronika (1)
('Laptop Dell', 20, 2500, 5, 1),
('Smartfon iPhone 13', 35, 4500, 8, 1),
('Router TP-Link', 18, 250, 4, 1),
('Monitor Samsung', 15, 1400, 3, 1),
('Drukarka HP', 10, 1600, 2, 1),
('Tablet Lenovo', 12, 2000, 4, 1),
('SSD 1TB Samsung', 18, 250, 3, 1),
('Dysk HDD 2TB', 14, 200, 3, 1),
('Router ASUS', 20, 300, 4, 1),
('Monitor LG 27"', 17, 2300, 4, 1),
('Drukarka laserowa Brother', 7, 2332, 2, 1),
('Smartwatch Samsung', 25, 540, 5, 1),

-- Akcesoria komputerowe (2)
('Klawiatura Logitech', 30, 80, 5, 2),
('Mysz bezprzewodowa', 40, 70, 10, 2),
('Powerbank Xiaomi', 22, 120, 6, 2),
('Pendrive 128GB', 50, 45, 10, 2),
('Ładowarka Anker', 30, 30, 8, 2),
('Hub USB-C', 25, 55, 5, 2),
('Kabel HDMI 3m', 40, 20, 12, 2),
('Torba na laptopa', 33, 35, 7, 2),
('Mysz gamingowa Razer', 22, 234, 6, 2),

-- Peryferia (3)
('Kamera Logitech', 25, 150, 6, 3),
('Słuchawki Sony', 28, 200, 5, 3),
('Głośniki JBL', 16, 250, 4, 3),
('Mikrofon Razer', 12, 200, 2, 3),
('Webcam Logitech', 35, 140, 6, 3),
('Stacja dokująca Dell', 13, 345, 3, 3),

-- Meble biurowe (4)
('Krzesło biurowe', 18, 400, 4, 4),
('Biurko gamingowe', 8, 700, 2, 4),

-- Foto / Wideo (5)
('Obiektyw Canon', 9, 650, 2, 5);


INSERT INTO transakcje (id_produktu, id_pracownika, ilosc, data_transakcji) VALUES 
(1, 1, 2, '2025-03-01'), (2, 2, 1, '2025-03-02'), (3, 3, 3, '2025-03-03'), (4, 4, 1, '2025-03-04'),
(5, 1, 2, '2025-03-05'), (6, 2, 1, '2025-03-06'), (7, 3, 3, '2025-03-07'), (8, 4, 1, '2025-03-08'),
(9, 1, 2, '2025-03-09'), (10, 2, 1, '2025-03-10'), (11, 3, 3, '2025-03-11'), (12, 4, 1, '2025-03-12');

INSERT INTO zadania (
    id_pracownika, nazwa, id_statusu, id_priorytetu,
    data_rozpoczecia, data_zakonczenia, komentarz, powiadomienia
) VALUES
(1, 'Przygotowanie raportu kwartalnego', 1, 2, '2025-03-01', '2025-03-08', NULL, FALSE),
(2, 'Audyt wewnętrzny IT', 2, 3, '2025-03-02', '2025-03-09', NULL, FALSE),
(3, 'Weryfikacja danych klientów', 3, 1, '2025-03-03', '2025-03-10', NULL, FALSE),
(4, 'Aktualizacja systemu ERP', 4, 2, '2025-03-04', '2025-03-11', NULL, FALSE),
(5, 'Szkolenie BHP dla zespołu', 1, 2, '2025-03-05', '2025-03-12', NULL, FALSE),
(1, 'Zamówienie sprzętu komputerowego', 2, 1, '2025-03-06', '2025-03-13', NULL, FALSE),
(2, 'Migracja serwera bazy danych', 3, 3, '2025-03-07', '2025-03-14', NULL, FALSE),
(3, 'Wdrożenie polityki bezpieczeństwa', 4, 2, '2025-03-08', '2025-03-15', NULL, FALSE),
(4, 'Przygotowanie prezentacji dla zarządu', 1, 1, '2025-03-09', '2025-03-16', NULL, FALSE),
(5, 'Konfiguracja drukarek sieciowych', 2, 2, '2025-03-10', '2025-03-17', NULL, FALSE),
(1, 'Weryfikacja faktur', 3, 1, '2025-03-11', '2025-03-18', NULL, FALSE),
(2, 'Inwentaryzacja sprzętu w magazynie', 4, 3, '2025-03-12', '2025-03-19', NULL, FALSE),
(3, 'Rekrutacja nowych pracowników', 1, 2, '2025-03-13', '2025-03-20', NULL, FALSE),
(4, 'Optymalizacja procesu zakupowego', 2, 2, '2025-03-14', '2025-03-21', NULL, FALSE),
(5, 'Testy nowego oprogramowania', 3, 3, '2025-03-15', '2025-03-22', NULL, FALSE),
(1, 'Aktualizacja dokumentacji technicznej', 4, 1, '2025-03-16', '2025-03-23', NULL, FALSE),
(2, 'Przygotowanie szkoleń e-learningowych', 1, 2, '2025-03-17', '2025-03-24', NULL, FALSE),
(3, 'Analiza sprzedaży Q1', 2, 3, '2025-03-18', '2025-03-25', NULL, FALSE),
(4, 'Udział w konferencji branżowej', 3, 2, '2025-03-19', '2025-03-26', NULL, FALSE),
(5, 'Modernizacja sieci biurowej', 4, 3, '2025-03-20', '2025-03-27', NULL, FALSE),
(1, 'Przegląd sprzętu serwisowego', 1, 1, '2025-03-21', '2025-03-28', NULL, FALSE),
(2, 'Stworzenie bazy danych kontaktów', 2, 2, '2025-03-22', '2025-03-29', NULL, FALSE),
(3, 'Wsparcie klienta VIP', 3, 3, '2025-03-23', '2025-03-30', NULL, FALSE),
(4, 'Projekt nowej strony internetowej', 4, 2, '2025-03-24', '2025-03-31', NULL, FALSE),
(5, 'Analiza kosztów operacyjnych', 1, 1, '2025-03-25', '2025-04-01', NULL, FALSE),
(1, 'Wprowadzenie polityki RODO', 2, 2, '2025-03-26', '2025-04-02', NULL, FALSE),
(2, 'Zakup licencji oprogramowania', 3, 3, '2025-03-27', '2025-04-03', NULL, FALSE),
(3, 'Serwis drukarek w dziale HR', 4, 1, '2025-03-28', '2025-04-04', NULL, FALSE),
(4, 'Przygotowanie do audytu ISO', 1, 2, '2025-03-29', '2025-04-05', NULL, FALSE),
(5, 'Badanie satysfakcji pracowników', 2, 2, '2025-03-30', '2025-04-06', NULL, FALSE);
