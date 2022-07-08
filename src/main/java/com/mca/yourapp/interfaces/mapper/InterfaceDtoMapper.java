package com.mca.yourapp.interfaces.mapper;

import com.mca.yourapp.interfaces.dto.ProductDetail;
import fr.xebia.extras.selma.Mapper;

import java.util.List;

@Mapper
public interface InterfaceDtoMapper {
    List<ProductDetail> toProductDetails(final List<com.mca.yourapp.service.dto.ProductDetail> similarProducts);
}