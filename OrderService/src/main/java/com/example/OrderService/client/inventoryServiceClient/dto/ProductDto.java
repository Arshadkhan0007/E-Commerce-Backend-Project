package com.example.OrderService.client.inventoryServiceClient.dto;

import com.example.OrderService.client.inventoryServiceClient.dto.enums.ProductCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Integer productId;
    private String productName;
    private ProductCategory productCategory;
    private double price;
    private double rating;
    private int stock;

}
