package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.external.mocks.MocksConnector;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.impl.ProductServiceImpl;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final String PRODUCT_ID = "id";
    private static final List<String> SIMILAR_PRODUCT_IDS = List.of("id1", "id2", "id3");
    @InjectMocks
    private ProductService productService = new ProductServiceImpl();

    @Mock
    private MocksConnector mocksConnector;


    @Test
    void getSimilarProducts_happyPath_success() {
        final List<ProductDetailMock> inputProductDetails = SIMILAR_PRODUCT_IDS.stream().map(this::newProductDetailMock).toList();
        when(mocksConnector.getProduct(PRODUCT_ID)).thenReturn(newProductDetailMock(PRODUCT_ID));
        when(mocksConnector.getSimilarProductIds(PRODUCT_ID)).thenReturn(SIMILAR_PRODUCT_IDS);
        when(mocksConnector.getProductsInParallel(SIMILAR_PRODUCT_IDS)).thenReturn(inputProductDetails);

        List<ProductDetail> similarProducts = null;
        try {
            similarProducts = productService.getSimilarProducts(PRODUCT_ID);
        } catch (final EntityNotFound e) {
            fail(e.getMessage());
        }

        assertNotNull(similarProducts, "Similar products result");
        verify(mocksConnector, times(1)).getProduct(any());
        verify(mocksConnector, times(1)).getProduct(argThat(PRODUCT_ID::equals));
        verify(mocksConnector, times(1)).getSimilarProductIds(any());
        verify(mocksConnector, times(1)).getSimilarProductIds(argThat(PRODUCT_ID::equals));
        verify(mocksConnector, times(1)).getProductsInParallel(any());
        verify(mocksConnector, times(1)).getProductsInParallel(argThat(SIMILAR_PRODUCT_IDS::equals));

        assertEquals(inputProductDetails.size(), similarProducts.size(), "Products size");
        assertEquals(SIMILAR_PRODUCT_IDS, similarProducts.stream().map(ProductDetail::getId).toList());
        for (int i = 0; i < inputProductDetails.size(); ++i) {
            checkEqualProduct(inputProductDetails.get(i), similarProducts.get(i));
        }
    }

    private void checkEqualProduct(final ProductDetailMock actualProduct, final ProductDetail expectedProduct) {
        assertEquals(actualProduct.getId(), expectedProduct.getId(), "Product id");
        assertEquals(actualProduct.getName(), expectedProduct.getName(), "Product name");
        assertEquals(actualProduct.getPrice(), expectedProduct.getPrice(), "Product price");
        assertEquals(actualProduct.isAvailability(), expectedProduct.isAvailability(), "Product availability");
    }

    private ProductDetailMock newProductDetailMock(final String productId) {
        final ProductDetailMock productMock = new ProductDetailMock();
        productMock.setId(productId);
        productMock.setName("Product name");
        productMock.setPrice(19);
        productMock.setAvailability(false);

        return productMock;
    }

}
