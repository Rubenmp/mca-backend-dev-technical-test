package com.mca.yourapp.interfaces;

import com.mca.yourapp.interfaces.dto.ProductDetail;
import com.mca.yourapp.service.ProductService;
import com.mca.yourapp.service.utils.exception.EntityNotFound;
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

    /**
     * Similar products
     *
     * @param productId required path parameter
     */
    @GetMapping(value = GET_SIMILAR_PRODUCTS_URL, produces = "application/json")
    public ResponseEntity<String> getSimilarProducts(@PathVariable(required = true) String productId) {
        try {
            final List<ProductDetail> similarProducts = productService.getSimilarProducts(productId);
            return new ResponseEntity<>(similarProducts.toString(), OK);
        } catch (final EntityNotFound e) {
            return new ResponseEntity<>("{\"message\":\"Product Not found\"}", HttpStatus.NOT_FOUND);
        }
    }
}
