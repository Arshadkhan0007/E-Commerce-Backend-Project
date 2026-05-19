package com.example.OrderService.activities;

import com.example.OrderService.client.inventoryServiceClient.dto.ProductDto;
import com.example.OrderService.dto.OrderRequestDto;
import com.example.OrderService.entity.Order;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface OrderActivities {

    @ActivityMethod
    public List<ProductDto> retrieveProductsFromInventory(List<OrderRequestDto> orderRequestDtoList);

    @ActivityMethod
    public void verifyStockAvailability(List<ProductDto> productDtoList, List<OrderRequestDto> orderRequestDtoList);

    @ActivityMethod
    public double calculateTotalPrice(List<ProductDto> productDtoList, List<OrderRequestDto> orderRequestDtoList);

    @ActivityMethod
    public void updateInventory(List<OrderRequestDto> orderRequestDtoList);

    @ActivityMethod
    public void restoreInventory(List<OrderRequestDto> orderRequestDtoList);

    @ActivityMethod
    public void saveOrder(Order order);
}
