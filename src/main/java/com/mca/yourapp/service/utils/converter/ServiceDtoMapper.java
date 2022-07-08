package com.mca.yourapp.service.utils.converter;

import com.mca.yourapp.service.dto.ProductDetail;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import fr.xebia.extras.selma.Mapper;

import java.util.List;

@Mapper
public interface ServiceDtoMapper {
    List<ProductDetail> toProductDetails(final List<ProductDetailMock> returnedProducts);
}