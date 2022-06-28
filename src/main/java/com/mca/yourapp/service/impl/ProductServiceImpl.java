package com.mca.yourapp.service.impl;

import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.external.mocks.MocksConnector;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private MocksConnector mocksConnector;

    @Autowired
    private LogService logService;

    @Override
    public List<ProductDetail> getSimilarProducts(final String productId) throws EntityNotFound {
        final List<ProductDetail> products = getSimilarProductsInternal(productId);

        if (products == null) {
            throw new EntityNotFound("Input product \"" + productId + "\" not found.");
        }

        return products;
    }

    private List<ProductDetail> getSimilarProductsInternal(final String productId) {
        if (productId == null || mocksConnector.getProduct(productId) == null) {
            return null; // Sending null in order to allow method caching in this scenario
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
