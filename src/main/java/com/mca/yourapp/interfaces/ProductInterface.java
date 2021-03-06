package com.mca.yourapp.interfaces;

import com.mca.yourapp.interfaces.mapper.InterfaceDtoMapper;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
import fr.xebia.extras.selma.Selma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ProductInterface {
    public static final String GET_SIMILAR_PRODUCTS_URL = "/product/{productId}/similar";

    @Autowired
    private ProductService productService;

    final InterfaceDtoMapper interfaceDtoMapper = Selma.builder(InterfaceDtoMapper.class).build();


    /**
     * Similar products to the requested product
     *
     * @param productId required path parameter
     * @return {@link org.springframework.http.ResponseEntity ResponseEntity<String>} due to the restriction
     * '404':
     * description: Product Not found
     * that makes strong typing counterproductive.
     * The response actually returns a list of {@link com.mca.yourapp.interfaces.dto.ProductDetail ProductDetail}
     * in json format.
     * If the product id does not exist, then HTTP status NOT_FOUND will be returned.
     */
    @GetMapping(value = GET_SIMILAR_PRODUCTS_URL, produces = "application/json")
    public ResponseEntity<String> getSimilarProducts(@PathVariable(required = true) String productId) {
        try {
            final List<com.mca.yourapp.service.dto.ProductDetail> similarProducts = productService.getSimilarProducts(productId);
            return new ResponseEntity<>(interfaceDtoMapper.toProductDetails(similarProducts).toString(), OK);
        } catch (final EntityNotFound e) {
            return new ResponseEntity<>("{\"message\":\"Product Not found\"}", HttpStatus.NOT_FOUND);
        }
    }
}
