package com.example.InventoryService.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductCategory {
    ELECTRONICS,
    FURNITURE,
    ACCESSORIES,
    CLOTHING,
    SPORTS;

    @JsonCreator // Helps in deserialization
    public static ProductCategory from(String value) {
        return ProductCategory.valueOf(value.toUpperCase());
    }
}
