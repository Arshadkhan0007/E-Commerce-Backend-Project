package com.example.OrderService.activities;

import com.example.OrderService.client.inventoryServiceClient.InventoryServiceClient;
import com.example.OrderService.client.inventoryServiceClient.dto.ProductDto;
import com.example.OrderService.dto.OrderRequestDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.InsufficientStockException;
import com.example.OrderService.exception.ResourceNotFoundException;
import com.example.OrderService.repository.OrderRepository;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ActivityImpl(taskQueues = "ORDER_TASK_QUEUE")
public class OrderActivitiesImplementation implements OrderActivities {

    private final InventoryServiceClient inventoryService;
    private final OrderRepository orderRepository;

    public OrderActivitiesImplementation(InventoryServiceClient inventoryService, OrderRepository orderRepository) {
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<ProductDto> retrieveProductsFromInventory(List<OrderRequestDto> orderRequestDtoList) {
        return inventoryService.getAllProductsById(orderRequestDtoList.stream()
                .map(OrderRequestDto::getProductId)
                .toList());
    }

    @Override
    public void verifyStockAvailability(List<ProductDto> productList, List<OrderRequestDto> orderRequestDtoList) {
        for(OrderRequestDto orderRequestDto : orderRequestDtoList) {
            ProductDto product = productList.stream()
                    .filter(productDto -> productDto.getProductId() == orderRequestDto.getProductId())
                    .findAny()
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID:" + orderRequestDto.getProductId() + " does not exist"));
            if(product.getStock() < orderRequestDto.getQuantity()) {
                throw new InsufficientStockException("Required: " + orderRequestDto.getQuantity() + ", Available: " + product.getStock());
            }
        }
    }

    @Override
    public double calculateTotalPrice(List<ProductDto> productList, List<OrderRequestDto> orderRequestDtoList) {
        double totalPrice = 0;
        for(ProductDto productDto : productList) {
            int quantity = orderRequestDtoList.stream().filter(orderRequestDto -> orderRequestDto.getProductId() == productDto.getProductId()).findAny().get().getQuantity();
            totalPrice += productDto.getPrice() * quantity;
        }
        return totalPrice;
    }

    @Override
    public void updateInventory(List<OrderRequestDto> orderRequestDtoList) {
        inventoryService.updateStock(orderRequestDtoList);
    }

    @Override
    public void restoreInventory(List<OrderRequestDto> orderRequestDtoList) {
        inventoryService.restoreStock(orderRequestDtoList);
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
