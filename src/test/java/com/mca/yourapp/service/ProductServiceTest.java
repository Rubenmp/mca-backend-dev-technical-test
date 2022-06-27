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

import static org.junit.jupiter.api.Assertions.*;
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
        final List<ProductDetailMock> productDetails = SIMILAR_PRODUCT_IDS.stream().map(this::newProductDetailMock).toList();
        when(mocksConnector.getProduct(PRODUCT_ID)).thenReturn(newProductDetailMock(PRODUCT_ID));
        when(mocksConnector.getSimilarProductIds(PRODUCT_ID)).thenReturn(SIMILAR_PRODUCT_IDS);
        when(mocksConnector.getProductsInParallel(SIMILAR_PRODUCT_IDS)).thenReturn(productDetails);

        List<ProductDetail> similarProducts = null;
        try {
            similarProducts = productService.getSimilarProducts(PRODUCT_ID);
        } catch (final EntityNotFound e) {
            fail(e.getMessage());
        }

        assertNotNull(similarProducts);
        assertEquals(productDetails.size(), similarProducts.size(), "Products size");
        assertEquals(SIMILAR_PRODUCT_IDS, similarProducts.stream().map(ProductDetail::getId).toList());
        for (int i = 0; i < productDetails.size(); ++i) {
            assertEquals(productDetails.get(i).getId(), similarProducts.get(i).getId(), "Product id");
            assertEquals(productDetails.get(i).getName(), similarProducts.get(i).getName(), "Product name");
            assertEquals(productDetails.get(i).getPrice(), similarProducts.get(i).getPrice(), "Product price");
            assertEquals(productDetails.get(i).isAvailability(), similarProducts.get(i).isAvailability(), "Product availability");
        }
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
