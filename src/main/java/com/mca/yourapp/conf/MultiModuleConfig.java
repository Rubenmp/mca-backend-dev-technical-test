package com.mca.yourapp.conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class MultiModuleConfig {
    public static final int EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS = 8000;

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

    /**
     * There is a current issue with this connector: <a href="https://github.com/spring-projects/spring-framework/issues/22142">...</a>
     * Supposedly it is solved using
     *
     * ReactorClientHttpConnector sharedConnector = new ReactorClientHttpConnector(
     *     HttpClient.create().runOn(LoopResources.create("reactor-webclient")
     * )
     *
     * but it does not work. It is still better to use this async web client approach than blocking calls with rest template, though.
     * */
    private ReactorClientHttpConnector getReactorClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.newConnection().responseTimeout(Duration.ofMillis(EXTERNAL_API_CALL_TIMEOUT_IN_MILLISECONDS)));
    }
}
