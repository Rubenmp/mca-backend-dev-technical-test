package com.mca.yourapp.service.external.mocks;

import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

public interface MocksConnector {
    /**
     * List of similar product ids to a given one sorted by similarity (descending)
     */
    @NonNull
    List<String> getSimilarProductIds(@Nullable String productId);

    /**
     * Returns the product detail for a given productId or null if the product does not exist.
     */
    @Nullable
    ProductDetailMock getProduct(@Nullable String productId);

    /**
     * Returns the product details for a list of productId using multi-threading.
     */
    @NonNull
    List<ProductDetailMock> getProductsInParallel(@Nullable Collection<String> productIds);
}
