package com.example.OrderService.client.inventoryServiceClient.configuration;

import com.example.OrderService.client.inventoryServiceClient.filters.WebClientFilters;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    private final String inventoryServiceBaseUrl;

    public WebClientConfiguration(@Value("${inventory-base-url}") String inventoryServiceBaseUrl) {
        this.inventoryServiceBaseUrl = inventoryServiceBaseUrl;
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder webClientBuilder) {

        ConnectionProvider provider = ConnectionProvider.builder("inventory-service-pool")
                .maxConnectionPools(50)
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(5))
                .evictInBackground(Duration.ofMinutes(1))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        final int size = 10 * 1024 * 1024;
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(size))
                .build();

        return webClientBuilder
                .clone()
                .baseUrl(inventoryServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientFilters.addAuthHeader());
                    exchangeFilterFunctions.add(WebClientFilters.logRequest());
                    exchangeFilterFunctions.add(WebClientFilters.logResponse());
                })
                .build();



    }

}
