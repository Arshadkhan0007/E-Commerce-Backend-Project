package com.example.InventoryService.dto;

import com.example.InventoryService.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private String productName;
    private ProductCategory productCategory;
    private double price;
    private float rating;
    private int stock;

}
