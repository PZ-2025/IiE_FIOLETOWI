CREATE DATABASE IF NOT EXISTS HurtPolSan;
USE HurtPolSan;

-- Tabela 'role'
CREATE TABLE role (
    id_roli INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(15) NOT NULL
);

-- Tabela 'grupy'
CREATE TABLE grupy (
    id_grupy INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

-- Tabela 'pracownicy'
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
    FOREIGN KEY (id_roli) REFERENCES role(id_roli),
    archiwizacja BOOLEAN,
);

-- Tabela 'typ_produktu'
CREATE TABLE typ_produktu (
    id_typu_produktu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

-- Tabela 'produkty'
CREATE TABLE produkty (
    id_produktu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL,
    stan INT NOT NULL,
    cena DECIMAL(10,2) NOT NULL,
    id_typu_produktu INT NOT NULL,
    limit_stanow INT NOT NULL,
    FOREIGN KEY (id_typu_produktu) REFERENCES typ_produktu(id_typu_produktu)
);

-- Tabela 'statusy'
CREATE TABLE statusy (
    id_statusu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

-- Tabela 'priorytety'
CREATE TABLE priorytety (
    id_priorytetu INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

-- Tabela 'kierunki'
CREATE TABLE kierunki (
    id_kierunku INT PRIMARY KEY AUTO_INCREMENT,
    nazwa VARCHAR(50) NOT NULL
);

-- Tabela 'transakcje'
CREATE TABLE transakcje (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_produktu INT NOT NULL,
    id_pracownika INT NOT NULL,
    ilosc INT NOT NULL,
    data_transakcji DATE NOT NULL,
    id_kierunku INT NOT NULL,
    FOREIGN KEY (id_produktu) REFERENCES produkty(id_produktu),
    FOREIGN KEY (id_kierunku) REFERENCES kierunki(id_kierunku),
    FOREIGN KEY (id_pracownika) REFERENCES pracownicy(id_pracownika)

);



-- Tabela 'zadania'
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
    id_produktu INT NULL,
    ilosc INT NULL,
    id_kierunku INT NULL,
    FOREIGN KEY (id_pracownika) REFERENCES pracownicy(id_pracownika),
    FOREIGN KEY (id_statusu) REFERENCES statusy(id_statusu),
    FOREIGN KEY (id_priorytetu) REFERENCES priorytety(id_priorytetu),
    FOREIGN KEY (id_produktu) REFERENCES produkty(id_produktu),
    FOREIGN KEY (id_kierunku) REFERENCES kierunki(id_kierunku)
);



-- Wstawianie danych do tabeli 'role'
INSERT INTO role (nazwa) VALUES
('Admin'), ('Pracownik'), ('Kierownik');

-- Wstawianie danych do tabeli 'grupy'
INSERT INTO grupy (nazwa) VALUES
('IT'), ('Finanse'), ('HR'), ('Logistyka'), ('Sprzedaż'), ('Marketing'), ('Serwis'), ('Kontrola Jakości'), ('Obsługa Klienta');

-- Wstawianie danych do tabeli 'statusy'
INSERT INTO statusy (nazwa) VALUES
('Oczekujące'), ('Rozpoczęte'), ('W trakcie'), ('Zakończone'), ('Zarchiwizowane');

-- Wstawianie danych do tabeli 'priorytety'
INSERT INTO priorytety (nazwa) VALUES
('Niski'), ('Średni'), ('Wysoki');

-- Wstawianie danych do tabeli 'typ_produktu'
INSERT INTO typ_produktu (nazwa) VALUES
('Elektronika'),
('Akcesoria komputerowe'),
('Peryferia'),
('Meble biurowe'),
('Foto / Wideo');

-- Wstawianie danych do tabeli 'pracownicy'
INSERT INTO pracownicy (imie, nazwisko, login, haslo, placa, id_grupy, id_roli) VALUES
('Admin', 'Admin', 'Admin123', 'rDlt1lrSXRTUhaYdrtrHPQ==:0JzlTGgCm3V3x9XgTCV1hIyXdr7UA45FgeNdyr4EgW0=', 5000.00, 1, 1),
('Jan', 'Kowalski', 'jkowalski', '9VnZWBzzv0Fwgis+dia9Sg==:ytBL2OvbM0FUNgHQ/8Vk4xj6Ifuin9q/p8Pn+Ve2RHE=', 5000.00, 1, 2),
('Anna', 'Nowak', 'anowak', 'gFFyTO4esJ8OO0Ml9ZeJXA==:E0ytSeKCkJ37zSXPC5FSi1n6H8QpXdjwEGmP8mYccno=', 5200.00, 2, 2),
('Piotr', 'Zieliński', 'pzielinski', 'ctHDoZWGPyMZNAk+spWvVg==:h++8x391tbhAeV1rsBWUd49X+a1Y/wqL//iCJuaY2hU=', 4800.00, 3, 3),
('Katarzyna', 'Wiśniewska', 'kwisniewska', 'EeHt2TjXgQTMyT0efrGb0g==:Vh2VSvZfL4U64ny2Xk8QCAVQf1TmFACwGToCDaWwnvk=', 5300.00, 4, 2);

-- Wstawianie danych do tabeli 'produkty'
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

-- Wstawianie danych do tabeli 'kierunki'
INSERT INTO kierunki (nazwa) VALUES
('Przychodząca'),
('Wychodząca');

-- Wstawianie danych do tabeli 'transakcje'
INSERT INTO transakcje (id_produktu, id_pracownika, ilosc, data_transakcji, id_kierunku) VALUES 
(1, 1, 2, '2025-03-01', 1),
(2, 2, 1, '2025-03-02', 1),
(3, 3, 3, '2025-03-03', 1),
(4, 4, 1, '2025-03-04', 2),
(5, 1, 2, '2025-03-05', 1),
(6, 2, 1, '2025-03-06', 1),
(7, 3, 3, '2025-03-07', 1),
(8, 4, 1, '2025-03-08', 2),
(9, 1, 2, '2025-03-09', 1),
(10, 2, 1, '2025-03-10', 1),
(11, 3, 3, '2025-03-11', 1),
(12, 4, 1, '2025-03-12', 2);


-- Wstawianie danych do tabeli 'zadania'
INSERT INTO zadania (
    id_pracownika, nazwa, id_statusu, id_priorytetu,
    data_rozpoczecia, data_zakonczenia, komentarz, powiadomienia,
    id_produktu, ilosc, id_kierunku
) VALUES
(1, 'Przygotowanie raportu kwartalnego', 1, 2, '2025-03-01', '2025-03-08', NULL, FALSE, NULL, NULL, NULL),
(2, 'Audyt wewnętrzny IT', 2, 3, '2025-03-02', '2025-03-09', NULL, FALSE, NULL, NULL, NULL),
(3, 'Weryfikacja danych klientów', 3, 1, '2025-03-03', '2025-03-10', NULL, FALSE, NULL, NULL, NULL),
(4, 'Aktualizacja systemu ERP', 4, 2, '2025-03-04', '2025-03-11', NULL, FALSE, 4, 5, 1),
(5, 'Szkolenie BHP dla zespołu', 1, 2, '2025-03-05', '2025-03-12', NULL, FALSE, NULL, NULL, NULL),
(1, 'Zamówienie sprzętu komputerowego', 2, 1, '2025-03-06', '2025-03-13', NULL, FALSE, 1, 5, 1),
(2, 'Migracja serwera bazy danych', 3, 3, '2025-03-07', '2025-03-14', NULL, FALSE, NULL, NULL, NULL),
(3, 'Wdrożenie polityki bezpieczeństwa', 4, 2, '2025-03-08', '2025-03-15', NULL, FALSE, 3, 5, 1),
(4, 'Przygotowanie prezentacji dla zarządu', 1, 1, '2025-03-09', '2025-03-16', NULL, FALSE, NULL, NULL, NULL),
(5, 'Konfiguracja drukarek sieciowych', 2, 2, '2025-03-10', '2025-03-17', NULL, FALSE, NULL, NULL, NULL),
(1, 'Weryfikacja faktur', 3, 1, '2025-03-11', '2025-03-18', NULL, FALSE, NULL, NULL, NULL),
(2, 'Inwentaryzacja sprzętu w magazynie', 4, 3, '2025-03-12', '2025-03-19', NULL, FALSE, 2, 5, 1),
(3, 'Rekrutacja nowych pracowników', 1, 2, '2025-03-13', '2025-03-20', NULL, FALSE, NULL, NULL, NULL),
(4, 'Optymalizacja procesu zakupowego', 2, 2, '2025-03-14', '2025-03-21', NULL, FALSE, NULL, NULL, NULL),
(5, 'Testy nowego oprogramowania', 3, 3, '2025-03-15', '2025-03-22', NULL, FALSE, NULL, NULL, NULL),
(1, 'Aktualizacja dokumentacji technicznej', 4, 1, '2025-03-16', '2025-03-23', NULL, FALSE, 1, 5, 1),
(2, 'Przygotowanie szkoleń e-learningowych', 1, 2, '2025-03-17', '2025-03-24', NULL, FALSE, NULL, NULL, NULL),
(3, 'Analiza sprzedaży Q1', 2, 3, '2025-03-18', '2025-03-25', NULL, FALSE, NULL, NULL, NULL),
(4, 'Udział w konferencji branżowej', 3, 2, '2025-03-19', '2025-03-26', NULL, FALSE, 4, 5, 1),
(5, 'Modernizacja sieci biurowej', 4, 3, '2025-03-20', '2025-03-27', NULL, FALSE, NULL, NULL, NULL),
(1, 'Przegląd sprzętu serwisowego', 1, 1, '2025-03-21', '2025-03-28', NULL, FALSE, NULL, NULL, NULL),
(2, 'Stworzenie bazy danych kontaktów', 2, 2, '2025-03-22', '2025-03-29', NULL, FALSE, NULL, NULL, NULL),
(3, 'Wsparcie klienta VIP', 3, 3, '2025-03-23', '2025-03-30', NULL, FALSE, NULL, NULL, NULL),
(4, 'Projekt nowej strony internetowej', 4, 2, '2025-03-24', '2025-03-31', NULL, FALSE, 4, 5, 1),
(5, 'Analiza kosztów operacyjnych', 1, 1, '2025-03-25', '2025-04-01', NULL, FALSE, NULL, NULL, NULL),
(1, 'Wprowadzenie polityki RODO', 2, 2, '2025-03-26', '2025-04-02', NULL, FALSE, NULL, NULL, NULL),
(2, 'Zakup licencji oprogramowania', 3, 3, '2025-03-27', '2025-04-03', NULL, FALSE, 2, 5, 1),
(3, 'Serwis drukarek w dziale HR', 4, 1, '2025-03-28', '2025-04-04', NULL, FALSE, NULL, NULL, NULL),
(4, 'Przygotowanie do audytu ISO', 1, 2, '2025-03-29', '2025-04-05', NULL, FALSE, NULL, NULL, NULL),
(5, 'Badanie satysfakcji pracowników', 2, 2, '2025-03-30', '2025-04-06', NULL, FALSE, NULL, NULL, NULL);

-- TRIGGER: Walidacja dat rozpoczęcia i zakończenia
DELIMITER //

CREATE TRIGGER waliduj_daty_zadania
BEFORE INSERT ON zadania
FOR EACH ROW
BEGIN
    IF NEW.data_zakonczenia < NEW.data_rozpoczecia THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.';
    END IF;
END;
//

-- Trigger: Tworzenie transakcji po dodaniu zadania "Realizacja transakcji"
CREATE TRIGGER tworzenie_transakcji_po_zmianie_statusu
AFTER UPDATE ON zadania
FOR EACH ROW
BEGIN
    -- Sprawdzamy, czy nazwa zadania to 'Realizacja transakcji' i czy status zmienił się na 'Zakończone' (id_statusu = 4)
    IF NEW.nazwa = 'Realizacja transakcji' AND NEW.id_statusu = 4 AND OLD.id_statusu <> 4 THEN
        INSERT INTO transakcje (id_produktu, id_pracownika, ilosc, data_transakcji, id_kierunku)
        VALUES (NEW.id_produktu, NEW.id_pracownika, NEW.ilosc, CURDATE(), NEW.id_kierunku);
    END IF;
END;
//
//

CREATE TRIGGER aktualizuj_stan_produktu
AFTER INSERT ON transakcje
FOR EACH ROW
BEGIN
    IF NEW.id_kierunku = 1 THEN
        -- Przychodząca - dodaj ilość do stanu
        UPDATE produkty
        SET stan = stan + NEW.ilosc
        WHERE id_produktu = NEW.id_produktu;
    ELSEIF NEW.id_kierunku = 2 THEN
        -- Wychodząca - odejmij ilość od stanu
        UPDATE produkty
        SET stan = stan - NEW.ilosc
        WHERE id_produktu = NEW.id_produktu;
    END IF;
END;
//

DELIMITER ;
