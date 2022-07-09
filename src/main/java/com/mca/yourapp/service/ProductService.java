package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import org.springframework.lang.Nullable;

import java.util.List;


public interface ProductService {
    List<ProductDetail> getSimilarProducts(@Nullable final String productId) throws EntityNotFound;
}
