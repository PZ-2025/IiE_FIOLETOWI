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
    FOREIGN KEY (id_roli) REFERENCES role(id_roli)
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
('Oczekujące'), ('Rozpoczęte'), ('W trakcie'), ('Zakończone');

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
('Laptop Dell', 20, 2500, 5, 1),
('Smartfon iPhone 13', 35, 4500, 8, 1),
('Router TP-Link', 18, 250, 4, 1),
('Monitor Samsung', 15, 1400, 3, 1),
('Drukarka HP', 10, 1600, 2, 1);

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
(5, 1, 2, '2025-03-05', 1);

-- Wstawianie danych do tabeli 'zadania'
INSERT INTO zadania (
    id_pracownika, nazwa, id_statusu, id_priorytetu,
    data_rozpoczecia, data_zakonczenia, komentarz, powiadomienia, id_produktu, ilosc, id_kierunku
) VALUES
(1, 'Przygotowanie raportu kwartalnego', 1, 2, '2025-03-01', '2025-03-08', NULL, FALSE, NULL, NULL, NULL),
(2, 'Zamówienie sprzętu komputerowego', 2, 1, '2025-03-06', '2025-03-13', NULL, FALSE, NULL, NULL, NULL),
(3, 'Realizacja transakcji', 3, 2, '2025-03-10', '2025-03-11', NULL, FALSE, 1, 15, 1);

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
