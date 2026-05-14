package com.example.OrderService.client.inventoryServiceClient.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientFilters {

    // 1. Intercepting the Request (WITH SECURITY MASKING)
    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Outgoing Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    // 2. Intercepting the Response
    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            // Include the HTTP method and URL so you know WHICH response failed/succeeded
            log.info("Incoming Response Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    // 3. Adding the Auth Header
    public static ExchangeFilterFunction addAuthHeader() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {

            // WARNING: This relies on ThreadLocal. It only works if the block() method is used
            // It might fail on retries because, WebClient might use multiple threads while retrying
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null || attributes.getRequest() == null) {
                log.warn("RequestContext is null. Cannot attach Auth Header. (Are you on a Netty thread?)");
                return Mono.just(clientRequest);
            }

            String authHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || authHeader.isBlank()) {
                return Mono.just(clientRequest);
            }

            // Safely attach the header
            var modifiedRequest = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .build();

            return Mono.just(modifiedRequest);
        });
    }
}