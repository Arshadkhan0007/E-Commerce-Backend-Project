package com.example.OrderService.client.inventoryServiceClient;

import com.example.OrderService.client.inventoryServiceClient.dto.ProductDto;
import com.example.OrderService.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class InventoryServiceClient {

    private final WebClient webClient;

    public InventoryServiceClient(@Qualifier("inventoryWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public ProductDto getProductById(int productId) {
        return webClient.method(HttpMethod.GET)
                .uri(UriComponentsBuilder
                        .fromUriString("")
                        .path("{id}")
                        .buildAndExpand(productId)
                        .toUriString())
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new ResourceNotFoundException("Product with ID: " + productId + " does not exist in inventory!")))
                .bodyToMono(ProductDto.class)
                .block();
    }

    public List<ProductDto> getAllProductsWithPagination(int startRow, int endRow) {
        return webClient.method(HttpMethod.GET)
                .uri(UriComponentsBuilder
                        .fromUriString("")
                        .path("/all/pagination")
                        .queryParam("startRow", startRow)
                        .queryParam("endRow", endRow)
                        .toUriString())
                .retrieve()
                .bodyToFlux(ProductDto.class).collectList()
                .block();
    }

    public List<ProductDto> getAllProductsById(List<Integer> productIdList) {
        return webClient.method(HttpMethod.POST)
                .uri(UriComponentsBuilder
                        .fromUriString("")
                        .path("/all/by-id")
                        .toUriString())
                .bodyValue(productIdList)
                .retrieve()
                .bodyToFlux(ProductDto.class).collectList()
                .block();
    }
}
