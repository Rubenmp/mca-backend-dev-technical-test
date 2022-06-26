package com.mca.yourapp.service.impl;

import com.mca.yourapp.interfaces.dto.ProductDetail;
import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.SerializationService;
import com.mca.yourapp.service.external.mocks.MocksConnector;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mca.yourapp.conf.CacheConfig.GET_SIMILAR_PRODUCTS_CACHE;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private MocksConnector mocksConnector;

    @Autowired
    private LogService logService;

    @Autowired
    private SerializationService serializationService;

    @Override
    @Cacheable(value = GET_SIMILAR_PRODUCTS_CACHE)
    public List<ProductDetail> getSimilarProducts(final String productId) throws EntityNotFound {
        if (productId == null || mocksConnector.getProduct(productId) == null) {
            throw new EntityNotFound("Input product \"" + productId + "\" not found.");
        }

        final List<String> productIds = mocksConnector.getSimilarProductIds(productId);
        final List<ProductDetailMock> returnedProducts = mocksConnector.getProductsInParallel(productIds);

        return toProductDetails(returnedProducts);
    }


    private List<ProductDetail> toProductDetails(final List<ProductDetailMock> returnedProducts) {
        return returnedProducts.stream().map(this::toProductDetails).toList();
    }

    private ProductDetail toProductDetails(final ProductDetailMock product) {
        final ProductDetail productDetail = new ProductDetail();
        productDetail.setId(product.getId());
        productDetail.setName(product.getName());
        productDetail.setPrice(product.getPrice());
        productDetail.setAvailability(product.isAvailability());

        return productDetail;
    }
}
