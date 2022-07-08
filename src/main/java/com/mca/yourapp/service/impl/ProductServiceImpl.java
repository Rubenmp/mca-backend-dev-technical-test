package com.mca.yourapp.service.impl;

import com.mca.yourapp.interfaces.converter.InterfaceDtoMapper;
import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.external.mocks.MocksConnector;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.utils.converter.ServiceDtoMapper;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import fr.xebia.extras.selma.Selma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private MocksConnector mocksConnector;

    @Autowired
    private LogService logService;

    final ServiceDtoMapper dtoMapper = Selma.builder(ServiceDtoMapper.class).build();


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

        return dtoMapper.toProductDetails(returnedProducts);
    }
}
