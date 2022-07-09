package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;


public interface ProductService {
    /**
     * Return the products information related to a given product sorted by similarity (descending)
     */
    @NonNull
    List<ProductDetail> getSimilarProducts(@Nullable String productId) throws EntityNotFound;
}
