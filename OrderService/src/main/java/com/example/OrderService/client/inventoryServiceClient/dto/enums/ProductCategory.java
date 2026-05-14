package com.example.OrderService.client.inventoryServiceClient.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductCategory {
    ELECTRONICS,
    FURNITURE,
    ACCESSORIES,
    CLOTHING,
    SPORTS;

    @JsonCreator
    public static ProductCategory from(String value) {
        return ProductCategory.valueOf(value.toUpperCase());
    }
}
