package com.mca.yourapp.testconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

public class IntegrationTestConfig {
    public static final String TEST_PROFILE = "test";
    private static final int TEST_API_CALL_TIMEOUT_IN_MILLISECONDS = 15000;

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    private WebTestClient webTestClient;

    public String getTestUri() {
        return "http://localhost:" + randomServerPort;
    }

    public URI getUri(final String endpoint) {
        final UriComponentsBuilder path = UriComponentsBuilder.fromUriString(getTestUri())
                .path(endpoint);

        return path.build()
                .encode()
                .toUri();
    }

    protected ApiResponse apiCall(URI uri) {
        final FluxExchangeResult<String> response = getWebTestClient().build().get().uri(uri).exchange().returnResult(String.class);
        return new ApiResponse(response);
    }

    protected WebTestClient.Builder getWebTestClient() {
        return webTestClient.mutate().responseTimeout(Duration.ofMillis(TEST_API_CALL_TIMEOUT_IN_MILLISECONDS));
    }
}
