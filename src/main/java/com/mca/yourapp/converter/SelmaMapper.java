package com.mca.yourapp.converter;

import com.mca.yourapp.interfaces.dto.ProductDetail;
import fr.xebia.extras.selma.Mapper;

@Mapper
public interface SelmaMapper {

    ProductDetail toProductDetailInterface(com.mca.yourapp.service.dto.ProductDetail input);

}