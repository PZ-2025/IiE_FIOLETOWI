package com.example.projekt;

import javafx.application.Platform;
import javafx.beans.property.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Inicjalizacja JavaFX (konieczne w środowiskach testowych bez GUI)
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp() {
        product = new Product(1, "Monitor", 10, 799.99, 3, 100, "Elektronika");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, product.getId());
        assertEquals("Monitor", product.getNazwa());
        assertEquals(10, product.getStan());
        assertEquals(799.99, product.getCena());
        assertEquals(3, product.getLimitStanow());
        assertEquals(100, product.getIdTypuProduktu());
        assertEquals("Elektronika", product.getTypProduktuNazwa());
    }

    @Test
    void testSetters() {
        product.setId(2);
        product.setNazwa("Klawiatura");
        product.setStan(50);
        product.setCena(199.99);
        product.setLimitStanow(5);
        product.setIdTypuProduktu(200);
        product.setTypProduktuNazwa("Akcesoria");

        assertEquals(2, product.getId());
        assertEquals("Klawiatura", product.getNazwa());
        assertEquals(50, product.getStan());
        assertEquals(199.99, product.getCena());
        assertEquals(5, product.getLimitStanow());
        assertEquals(200, product.getIdTypuProduktu());
        assertEquals("Akcesoria", product.getTypProduktuNazwa());
    }

    @Test
    void testPropertyBindings() {
        // Weryfikacja powiązania właściwości
        StringProperty nazwaProperty = product.nazwaProperty();
        nazwaProperty.set("Laptop");

        assertEquals("Laptop", product.getNazwa());
        assertEquals("Laptop", product.nazwaProperty().get());
    }
}
