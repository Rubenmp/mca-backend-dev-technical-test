package com.mca.yourapp.interfaces;

import com.mca.yourapp.converter.SelmaMapper;
import com.mca.yourapp.interfaces.dto.ProductDetail;
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

    //@Autowired
    SelmaMapper mapper; // = Selma.builder(SelmaMapper.class).build();


    /**
     * Similar products.
     * <p>
     * It returns {@link org.springframework.http.ResponseEntity ResponseEntity<String>} due to the restriction
     * '404':
     * description: Product Not found
     * that makes strong typing counterproductive.
     * The response actually returns a list of {@link com.mca.yourapp.interfaces.dto.ProductDetail ProductDetail}
     * in json format.
     *
     * @param productId required path parameter
     */
    @GetMapping(value = GET_SIMILAR_PRODUCTS_URL, produces = "application/json")
    public ResponseEntity<String> getSimilarProducts(@PathVariable(required = true) String productId) {
        try {
            mapper = Selma.builder(SelmaMapper.class).build();

            final List<com.mca.yourapp.service.dto.ProductDetail> similarProducts = productService.getSimilarProducts(productId);
            return new ResponseEntity<>(toProduct(similarProducts).toString(), OK);
        } catch (final EntityNotFound e) {
            return new ResponseEntity<>("{\"message\":\"Product Not found\"}", HttpStatus.NOT_FOUND);
        }
    }

    private List<ProductDetail> toProduct(final List<com.mca.yourapp.service.dto.ProductDetail> similarProducts) {
        return similarProducts.stream().map(this::toProduct).toList();
    }

    private ProductDetail toProduct(final com.mca.yourapp.service.dto.ProductDetail product) {
        final ProductDetail productDetail = new ProductDetail();
        productDetail.setId(product.getId());
        productDetail.setName(product.getName());
        productDetail.setPrice(product.getPrice());
        productDetail.setAvailability(product.isAvailability());

        return productDetail;
    }
}
