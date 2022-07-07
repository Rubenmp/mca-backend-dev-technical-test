package com.mca.yourapp.interfaces;

import com.mca.yourapp.interfaces.dto.ProductDetail;
import com.mca.yourapp.service.SerializationService;
import com.mca.yourapp.testconfig.ApiResponse;
import com.mca.yourapp.testconfig.IntegrationTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mca.yourapp.interfaces.ProductInterface.GET_SIMILAR_PRODUCTS_URL;
import static com.mca.yourapp.testconfig.IntegrationTestConfig.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductInterfaceIT extends IntegrationTestConfig {
    private static final String INVALID_PRODUCT_ID = "-1";
    private static final String PRODUCT_ID_TO_USE_IN_CACHE = "2";

    @Autowired
    private SerializationService serializationService;

    @Test
    void getSimilarProducts_basicScenarios_success() {
        final List<String> allInputProductIds = List.of("1", "2");

        final Map<String, List<String>> similarProductIds = new HashMap<>();
        for (final String inputProductId : allInputProductIds) {
            final ApiResponse response = getSimilarProductsRequest(inputProductId);
            assertEquals(HttpStatus.OK, response.getStatus(), "Response status");
            similarProductIds.put(inputProductId, getProductIds(response));
        }

        assertArrayEquals(List.of("2", "3", "4").toArray(), similarProductIds.get("1").toArray());
        assertArrayEquals(List.of("3", "100", "1000").toArray(), similarProductIds.get("2").toArray());
    }

    @Test
    void getSimilarProducts_externalServerError_success() {
        final String productId = "4";

        final ApiResponse response = getSimilarProductsRequest(productId);
        assertEquals(HttpStatus.OK, response.getStatus(), "Response status");

        assertArrayEquals(List.of("1", "2").toArray(), getProductIds(response).toArray(), "Product ids"); // Similar product ids [1,2,5]
    }

    @Test
    void getSimilarProducts_externalServerDoesNotAlwaysRespond_success() {
        final String productId = "3";

        final ApiResponse response = getSimilarProductsRequest(productId);
        assertEquals(HttpStatus.OK, response.getStatus(), "Response status");

        assertArrayEquals(List.of("100", "1000").toArray(), getProductIds(response).toArray(), "Product ids");
    }


    private List<String> getProductIds(final ApiResponse getSimilarProductsResponse) {
        return serializationService.deserializeList(getSimilarProductsResponse.getBody(), ProductDetail.class).stream().map(ProductDetail::getId).toList();
    }

    private ApiResponse getSimilarProductsRequest(final String productId) {
        final String url = GET_SIMILAR_PRODUCTS_URL.replace("{productId}", productId);

        return apiCall(getUri(url));
    }

    @Test
    void getSimilarProducts_invalidProducts_badRequest() {
        final ApiResponse response1 = getSimilarProductsRequest("-1");
        checkProductNotFoundResponse(response1);

        final ApiResponse response2 = getSimilarProductsRequest("5");
        checkProductNotFoundResponse(response2);
    }

    private void checkProductNotFoundResponse(final ApiResponse response) {
        assertNotNull(response, "Response");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Response status");
        assertEquals("{\"message\":\"Product Not found\"}", response.getBody(), "Response body");
    }

    @Test
    void getSimilarProducts_cachePerformanceAndConsistency_improveResponseTIme() {
        clearCache();

        long startTime1 = System.nanoTime();
        final ApiResponse responseWithoutCache = getSimilarProductsRequest(PRODUCT_ID_TO_USE_IN_CACHE);
        long estimatedTime1 = System.nanoTime() - startTime1;

        long startTime2 = System.nanoTime();
        final ApiResponse responseWithCache = getSimilarProductsRequest(PRODUCT_ID_TO_USE_IN_CACHE);
        long estimatedTime2 = System.nanoTime() - startTime2;

        assertEquals(HttpStatus.OK, responseWithoutCache.getStatus(), "Response without cache status");
        assertEquals(HttpStatus.OK, responseWithCache.getStatus(), "Response with cache status");

        final List<String> productIdsWithoutCache = getProductIds(responseWithoutCache);
        final List<String> productIdsWithCache = getProductIds(responseWithCache);
        assertArrayEquals(List.of("3", "100", "1000").toArray(), productIdsWithoutCache.toArray(), "Returned product ids");
        assertEquals(productIdsWithoutCache, productIdsWithCache, "Products");
        assertTrue(estimatedTime2 < estimatedTime1, "Cache must improve response time");
        assertTrue(estimatedTime2 < 35 * (1000000), "Second access must be faster than 35 milliseconds. Actual time: " + estimatedTime2/1000000 + " milliseconds");
    }


    @Test
    void getSimilarProducts_cacheForInvalidValues_improveResponseTIme() {
        clearCache();

        long startTime1 = System.nanoTime();
        final ApiResponse product1Str = getSimilarProductsRequest(INVALID_PRODUCT_ID);
        long estimatedTime1 = System.nanoTime() - startTime1;

        long startTime2 = System.nanoTime();
        final ApiResponse product2Str = getSimilarProductsRequest(INVALID_PRODUCT_ID);
        long estimatedTime2 = System.nanoTime() - startTime2;

        checkProductNotFoundResponse(product1Str);
        checkProductNotFoundResponse(product2Str);

        assertTrue(estimatedTime2 < estimatedTime1, "Cache must improve response time");
        assertTrue(estimatedTime2 < 35 * (1000000), "Second access must be faster than 35 milliseconds. Actual time: " + estimatedTime2/1000000 + " milliseconds");
    }

    private void clearCache() {
        try (final Jedis jedis = new Jedis()) {
            jedis.connect();
            jedis.flushAll();
            jedis.disconnect();
        }
    }
}
