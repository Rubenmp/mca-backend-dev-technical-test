package com.mca.yourapp.service.external.mocks;

import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

public interface MocksConnector {
    /**
     * List of similar product ids to a given one ordered by similarity
     */
    List<String> getSimilarProductIds(@Nullable String productId);

    /**
     * Returns the product detail for a given productId or null if the product does not exist.
     */
    ProductDetailMock getProduct(@Nullable String productId);

    /**
     * Returns the product details for a list of productId using multi-threading.
     */
    List<ProductDetailMock> getProductsInParallel(@Nullable Collection<String> productIds);
}
