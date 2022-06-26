package com.mca.yourapp.testconfig;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.FluxExchangeResult;

public class ApiResponse {
    private final FluxExchangeResult<String> response;

    public ApiResponse(final FluxExchangeResult<String> response) {
        this.response = response;
    }

    public HttpStatus getStatus() {
        return this.response.getStatus();
    }

    public String getBody() {
        byte[] bytes = response.getResponseBodyContent();
        return bytes == null ? null : new String(bytes);
    }
}
