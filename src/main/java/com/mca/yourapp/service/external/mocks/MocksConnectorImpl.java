package com.mca.yourapp.service.external.mocks;

import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.SerializationService;
import com.mca.yourapp.service.dto.LogType;
import com.mca.yourapp.service.external.mocks.dto.ProductDetailMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mca.yourapp.conf.CacheConfig.GET_PRODUCTS_IN_PARALLEL_CACHE;
import static com.mca.yourapp.conf.CacheConfig.GET_PRODUCT_CACHE;
import static com.mca.yourapp.conf.CacheConfig.GET_SIMILAR_PRODUCT_IDS_CACHE;
import static com.mca.yourapp.conf.MultiModuleConfig.EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS;
import static com.mca.yourapp.service.utils.CollectionUtils.splitToListsWithSize;

@Service
public class MocksConnectorImpl implements MocksConnector {
    private static final String BASE_MOCKS_MODULE_URL = "http://localhost:3001/";
    private static final String PRODUCTS_SEPARATOR = ",";
    private static final int MAX_PRODUCTS_TO_REQUEST = 1000;
    private static final int MAX_THREADS_PER_REQUEST = 4;
    private static final Duration API_CALL_TIMEOUT = Duration.ofSeconds(EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS / 1000);


    @Autowired
    private WebClient webClient;

    @Autowired
    private LogService logService;

    @Autowired
    private SerializationService serializationService;

    @Override
    @Cacheable(value = GET_SIMILAR_PRODUCT_IDS_CACHE)
    public List<String> getSimilarProductIds(final String productId) {
        if (!isValidProductId(productId)) {
            logService.log(LogType.WARNING, "Trying to getSimilarProductIds for invalid product id: " + productId);
            return List.of();
        }

        final String url = getSimilarProductIdsUrl(productId);

        try {
            final String productIdsStr = webClient.get().uri(url).retrieve().bodyToMono(String.class)
                    .timeout(API_CALL_TIMEOUT)
                    .onErrorContinue((e, i) -> {
                        final String exceptionMessage = e == null ? null : e.getMessage();
                        logService.log(LogType.ERROR, "In getSimilarProductIds there was an exception for productId \"" + productId + "\":" + exceptionMessage);
                    })
                    .onErrorReturn("").block();
            return toProductIds(productIdsStr);
        } catch (Exception e) {
            logService.log(e);
            return List.of();
        }
    }


    private boolean isValidProductId(final String productId) {
        return productId != null && !productId.isEmpty();
    }

    private List<String> toProductIds(final String productIdsStr) {
        if (productIdsStr == null || !productIdsStr.startsWith("[") || !productIdsStr.endsWith("]")) {
            return List.of();
        }

        return Arrays.stream(productIdsStr.substring(1, productIdsStr.length() - 1).split(PRODUCTS_SEPARATOR)).filter(this::isValidProductId).toList();
    }

    private String getSimilarProductIdsUrl(final String productId) {
        return BASE_MOCKS_MODULE_URL + "product/" + productId + "/similarids";
    }

    @Override
    @Cacheable(value = GET_PRODUCT_CACHE)
    public ProductDetailMock getProduct(final String productId) {
        final List<String> productIds = new ArrayList<>();
        productIds.add(productId);
        return getProductsInParallel(productIds).stream().findFirst().orElse(null);
    }

    private Flux<String> getProductAsyncHandlingErrors(final String productId) {
        final String url = getProductUrl(productId);

        return webClient.get().uri(url).retrieve().bodyToFlux(String.class)
                .timeout(API_CALL_TIMEOUT)
                .onErrorContinue((e, i) -> {
                    final String exceptionMessage = e == null ? null : e.getMessage();
                    logService.log(LogType.ERROR, "In getProductAsyncHandlingErrors there was an exception for productId \"" + productId + "\":" + exceptionMessage);
                })
                .onErrorReturn("");
    }


    private ProductDetailMock toProduct(final String productStr) {
        return serializationService.deserialize(productStr, ProductDetailMock.class);
    }

    private String getProductUrl(final String productId) {
        return BASE_MOCKS_MODULE_URL + "product/" + productId;
    }

    @Override
    @Cacheable(value = GET_PRODUCTS_IN_PARALLEL_CACHE)
    public List<ProductDetailMock> getProductsInParallel(final Collection<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (productIds.size() > MAX_PRODUCTS_TO_REQUEST) {
            logService.log(LogType.WARNING, "Maximum number of products to request reached: " + productIds.size());
        }

        final Set<String> requestedProductIds = new HashSet<>(productIds.size());
        final List<Flux<String>> productAsyncCalls = new ArrayList<>();
        for (final String productId : productIds) {
            if (productId != null && !productId.isEmpty() && !requestedProductIds.contains(productId)) {
                requestedProductIds.add(productId);
                productAsyncCalls.add(getProductAsyncHandlingErrors(productId));
            }
        }

        final List<ProductDetailMock> receivedProducts = wait(productAsyncCalls);
        if (receivedProducts.size() < requestedProductIds.size()) {
            logMissingProductIds(requestedProductIds, receivedProducts);
        }

        return receivedProducts;
    }

    private void logMissingProductIds(Set<String> requestedProductIds, List<ProductDetailMock> receivedProducts) {
        final Set<String> receivedProductIds = receivedProducts.stream().map(ProductDetailMock::getId).collect(Collectors.toSet());
        final List<String> missingProductIds = requestedProductIds.stream().filter(id -> !receivedProductIds.contains(id)).toList();
        logService.log(LogType.WARNING, "It was not possible to getProducts \"" + String.join("\",\"", missingProductIds) + "\" in mocks connector.");
    }

    private List<ProductDetailMock> wait(final List<Flux<String>> productAsyncCalls) {
        final List<List<Flux<String>>> groupedAsyncCalls = splitToListsWithSize(productAsyncCalls, MAX_THREADS_PER_REQUEST);
        return groupedAsyncCalls.stream().flatMap(calls -> waitWithoutThreadLimit(calls).stream()).toList();
    }

    private List<ProductDetailMock> waitWithoutThreadLimit(final Collection<Flux<String>> productAsyncCalls) {
        final List<List<String>> products;
        try {
            products = Flux.zip(productAsyncCalls, resultList -> Arrays.stream(resultList).map(String.class::cast).toList()).collectList().block();
        } catch (Exception e) {
            logService.log(e);
            return List.of();
        }

        if (products == null) {
            return Collections.emptyList();
        }

        return products.stream().findFirst().orElse(List.of()).stream().map(this::toProduct).filter(Objects::nonNull).toList();
    }
}
