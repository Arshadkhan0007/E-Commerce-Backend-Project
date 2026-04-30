package com.example.InventoryService.service;

import com.example.InventoryService.dto.OrderRequestDto;
import com.example.InventoryService.entity.Product;
import com.example.InventoryService.exception.InsufficientStockException;
import com.example.InventoryService.mapper.ProductMapper;
import com.example.InventoryService.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product1;
    private Product product2;
    private OrderRequestDto order1;
    private OrderRequestDto order2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductId(1);
        product1.setStock(10);

        product2 = new Product();
        product2.setProductId(2);
        product2.setStock(5);

        order1 = new OrderRequestDto();
        order1.setProductId(1);
        order1.setProductQuantity(3);

        order2 = new OrderRequestDto();
        order2.setProductId(2);
        order2.setProductQuantity(2);
    }

    @Test
    void updateStock_SuccessfulUpdate() {
        // Arrange
        List<OrderRequestDto> orders = List.of(order1);
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product1));

        // Act
        inventoryService.updateStock(orders);

        // Assert
        assertEquals(7, product1.getStock());
        verify(productRepository).saveAll(Set.of(product1));
    }

    @Test
    void updateStock_MultipleProducts() {
        // Arrange
        List<OrderRequestDto> orders = Arrays.asList(order1, order2);
        when(productRepository.findAllById(anySet())).thenReturn(Arrays.asList(product1, product2));

        // Act
        inventoryService.updateStock(orders);

        // Assert
        assertEquals(7, product1.getStock());
        assertEquals(3, product2.getStock());
        verify(productRepository).saveAll(Set.of(product1, product2));
    }

    @Test
    void updateStock_InsufficientStock() {
        // Arrange
        order1.setProductQuantity(15); // More than stock
        List<OrderRequestDto> orders = List.of(order1);
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product1));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> inventoryService.updateStock(orders));
        assertEquals("Required stock: 15, available stock: 10", exception.getMessage());
        verify(productRepository, never()).saveAll(anySet());
    }

    @Test
    void updateStock_EmptyOrderList() {
        // Arrange
        List<OrderRequestDto> orders = Collections.emptyList();
        when(productRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        // Act
        inventoryService.updateStock(orders);

        // Assert
        verify(productRepository, never()).saveAll(anySet());
    }

    @Test
    void updateStock_ProductNotFound() {
        // Arrange: Order for product not in DB
        List<OrderRequestDto> orders = List.of(order1);
        when(productRepository.findAllById(anySet())).thenReturn(Collections.emptyList()); // No products found

        // Act
        inventoryService.updateStock(orders);

        // Assert: No update, no save
        verify(productRepository, never()).saveAll(anySet());
    }

    @Test
    void updateStock_ZeroQuantity() {
        // Arrange
        order1.setProductQuantity(0);
        List<OrderRequestDto> orders = List.of(order1);
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product1));

        // Act
        inventoryService.updateStock(orders);

        // Assert
        assertEquals(10, product1.getStock()); // Stock unchanged
        verify(productRepository).saveAll(Set.of(product1));
    }

    @Test
    void updateStock_ExactStock() {
        // Arrange
        order1.setProductQuantity(10); // Exact stock
        List<OrderRequestDto> orders = List.of(order1);
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product1));

        // Act
        inventoryService.updateStock(orders);

        // Assert
        assertEquals(0, product1.getStock());
        verify(productRepository).saveAll(Set.of(product1));
    }

    @Test
    void updateStock_DuplicateProductIds() {
        // Arrange: Two orders for same product, takes first
        OrderRequestDto order1Duplicate = new OrderRequestDto();
        order1Duplicate.setProductId(1);
        order1Duplicate.setProductQuantity(5); // Different quantity
        List<OrderRequestDto> orders = Arrays.asList(order1, order1Duplicate); // order1 has 3
        when(productRepository.findAllById(anySet())).thenReturn(Arrays.asList(product1));

        // Act
        inventoryService.updateStock(orders);

        // Assert: Uses first order's quantity (3)
        assertEquals(7, product1.getStock());
        verify(productRepository).saveAll(Set.of(product1));
    }

    @Test
    void updateStock_PartialProductNotFound() {
        // Arrange: One product found, one not
        List<OrderRequestDto> orders = Arrays.asList(order1, order2);
        when(productRepository.findAllById(anySet())).thenReturn(Arrays.asList(product1)); // Only product1 found

        // Act
        inventoryService.updateStock(orders);

        // Assert: Only product1 updated
        assertEquals(7, product1.getStock());
        verify(productRepository).saveAll(Set.of(product1));
    }
}
