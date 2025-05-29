package com.example.projekt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTypeTest {

    @Test
    void testConstructorAndGetters() {
        ProductType productType = new ProductType(1, "Elektronika");

        assertEquals(1, productType.getId());
        assertEquals("Elektronika", productType.getNazwa());
    }

    @Test
    void testToStringReturnsName() {
        ProductType productType = new ProductType(2, "Peryferia");

        assertEquals("Peryferia", productType.toString());
    }
}
