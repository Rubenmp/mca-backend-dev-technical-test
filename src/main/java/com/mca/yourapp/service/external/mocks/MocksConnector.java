package com.mca.yourapp.service.external.mocks;

import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;

import java.util.Collection;
import java.util.List;

public interface MocksConnector {
    /**
     * List of similar product ids to a given one ordered by similarity
     */
    List<String> getSimilarProductIds(final String productId);

    /**
     * Returns the product detail for a given productId
     */
    ProductDetailMock getProduct(final String productId);

    /**
     * Returns the product details for a list of productId using multi-threading.
     */
    List<ProductDetailMock> getProductsInParallel(final Collection<String> productIds);
}
