package com.example.OrderService.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    INITIATED,
    PLACED,
    FAILED,
    CANCELED,
    DELIVERED;

    @JsonCreator
    public static OrderStatus from (String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }
}
