package com.mca.yourapp.conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class MultiModuleConfig {
    public static final int EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS = 10000;

    /**
     * Bean used to send async rest requests to another systems with default timeout.
     *
     * See <a href="https://www.baeldung.com/spring-rest-timeout">...</a>
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(getReactorClientHttpConnector())
                .build();
    }

    private ReactorClientHttpConnector getReactorClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.newConnection().responseTimeout(Duration.ofMillis(EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS)));
    }
}
