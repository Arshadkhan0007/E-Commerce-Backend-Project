package com.example.OrderService.workflow;

import com.example.OrderService.client.inventoryServiceClient.InventoryServiceClient;
import com.example.OrderService.client.inventoryServiceClient.dto.ProductDto;
import com.example.OrderService.dto.OrderRequestDto;
import io.temporal.spring.boot.WorkflowImpl;

import java.util.List;

@WorkflowImpl
public class OrderWorkflowImplementation implements OrderWorkflow {

    private final InventoryServiceClient inventoryService;

    public OrderWorkflowImplementation(InventoryServiceClient inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void placeOrder(List<OrderRequestDto> orderRequestDtoList) {
        List<ProductDto> productList = inventoryService.getAllProductsById(orderRequestDtoList.stream()
                .map(OrderRequestDto::getProductId)
                .toList());
        orderRequestDtoList.stream()
                

    }
}
