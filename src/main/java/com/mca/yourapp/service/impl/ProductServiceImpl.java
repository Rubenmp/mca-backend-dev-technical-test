package com.mca.yourapp.service.impl;

import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.external.mocks.MocksConnector;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import com.mca.yourapp.service.utils.mapper.ServiceDtoMapper;
import fr.xebia.extras.selma.Selma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private MocksConnector mocksConnector;

    @Autowired
    private LogService logService;

    final ServiceDtoMapper serviceDtoMapper = Selma.builder(ServiceDtoMapper.class).build();


    @Override
    public @NonNull List<ProductDetail> getSimilarProducts(@Nullable final String productId) throws EntityNotFound {
        if (productId == null || mocksConnector.getProduct(productId) == null) {
            throw new EntityNotFound("Input product \"" + productId + "\" not found.");
        }

        final List<String> productIds = mocksConnector.getSimilarProductIds(productId);
        final List<ProductDetailMock> returnedProducts = mocksConnector.getProductsInParallel(productIds);

        return serviceDtoMapper.toProductDetails(returnedProducts);
    }
}
