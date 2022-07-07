package com.mca.yourapp.service.external.mocks;

import com.mca.yourapp.testconfig.IntegrationTestConfig;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.mca.yourapp.testconfig.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MocksConnectorIT extends IntegrationTestConfig {
    private static final String PRODUCT_ID = "1";
    private static final String INVALID_PRODUCT_ID = "-1";

    @Autowired
    private MocksConnector mocksConnector;

    @Test
    void getProduct_happyPath_success() {
        final ProductDetailMock product = mocksConnector.getProduct(PRODUCT_ID);

        assertNotNull(product, "Product cannot be null");
        assertEquals(PRODUCT_ID, product.getId(), "Product id");
        assertEquals("Shirt", product.getName(), "Product name");
        assertNotNull(product.getPrice(), "Product price");
        assertEquals("9.99", product.getPrice().toString(), "Product price");
        assertTrue(product.isAvailability(), "Product availability");
    }

    @Test
    void getProduct_invalid_returnNull() {
        final ProductDetailMock product = mocksConnector.getProduct(INVALID_PRODUCT_ID);

        assertNull(product, "Product");
    }

    @Test
    void getProduct_internalErrorOfExternalModule_returnNull() {
        final ProductDetailMock product = mocksConnector.getProduct("6");

        assertNull(product, "Product");
    }


    @Test
    void getProduct_externalModuleNotResponding_returnNull() {
        final ProductDetailMock product = mocksConnector.getProduct("10000");

        assertNull(product, "Product");
    }


    @Test
    void getSimilarProductIds_happyPath_success() {
        final List<String> similarProductIds = mocksConnector.getSimilarProductIds(PRODUCT_ID);

        assertNotNull(similarProductIds, "Product ids");
        assertArrayEquals(List.of("2", "3", "4").toArray(), similarProductIds.toArray(), "Product ids");
    }

    @Test
    void getSimilarProductIds_invalidProductId_emptyList() {
        final List<String> similarProductIds = mocksConnector.getSimilarProductIds(INVALID_PRODUCT_ID);

        assertNotNull(similarProductIds, "Product ids");
        assertArrayEquals(List.of().toArray(), similarProductIds.toArray(), "Product list must be empty");
    }
}
