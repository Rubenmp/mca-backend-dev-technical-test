package com.mca.yourapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.impl.SerializationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SerializationServiceTest {
    private static final String VALID_PRODUCT_JSON = "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}";
    @InjectMocks
    private SerializationService serializationService = new SerializationServiceImpl();

    @Mock
    private LogService logService;


    @Test
    void deserialize_validProductDetailMock_success() {
        final ProductDetailMock result = serializationService.deserialize(VALID_PRODUCT_JSON, ProductDetailMock.class);

        checkValidProduct(result);
    }

    private void checkValidProduct(final ProductDetailMock result) {
        assertNotNull(result, "Deserialized result");
        assertEquals("2", result.getId(), "Id");
        assertEquals("Dress", result.getName(), "Name");
        assertNotNull(result.getPrice(), "Price");
        assertEquals("19.99", result.getPrice().toString(), "Price");
        assertTrue(result.isAvailability(), "Availability");
    }


    @Test
    void deserialize_invalidProductDetailMock_returnNull() {
        final String serializedProductDetailMock = "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":treue}";

        final ProductDetailMock result = serializationService.deserialize(serializedProductDetailMock, ProductDetailMock.class);

        assertNull(result, "Deserialized result");
        verify(logService, times(1)).log(any());
        verify(logService, times(1)).log(argThat(x -> x instanceof JsonProcessingException));
    }

    @Test
    void deserialize_invalidJson_returnNull() {
        final String serializedProductDetailMock = "{invalid-json}";

        final ProductDetailMock result = serializationService.deserialize(serializedProductDetailMock, ProductDetailMock.class);

        assertNull(result, "Deserialized result");
        verify(logService, times(1)).log(any());
        verify(logService, times(1)).log(argThat(x -> x instanceof JsonProcessingException));
    }

    @Test
    void deserialize_null_returnNull() {
        final ProductDetailMock result = serializationService.deserialize(null, ProductDetailMock.class);

        assertNull(result, "Deserialized result");
        verify(logService, times(1)).log(any());
        verify(logService, times(1)).log(argThat(x -> x instanceof IllegalArgumentException));
    }


    @Test
    void deserializeList_validProductDetailMock_success() {
        final String serializedProductDetailMock = "[" + VALID_PRODUCT_JSON + "]";

        List<ProductDetailMock> result = serializationService.deserializeList(serializedProductDetailMock, ProductDetailMock.class);

        assertNotNull(result, "Deserialized result");
        assertEquals(1, result.size(), "List size");
        checkValidProduct(result.get(0));
    }
}
