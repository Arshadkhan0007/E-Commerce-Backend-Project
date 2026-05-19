package com.example.InventoryService.service;

import com.example.InventoryService.dto.OrderRequestDto;
import com.example.InventoryService.dto.ProductDto;
import com.example.InventoryService.entity.Product;
import com.example.InventoryService.exception.InsufficientStockException;
import com.example.InventoryService.exception.ResourceNotFoundException;
import com.example.InventoryService.mapper.ProductMapper;
import com.example.InventoryService.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public InventoryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Product addProduct(ProductDto productDto) {
        return productRepository.save(productMapper.productDtoToProduct(productDto));
    }

    @Transactional
    public void updateStock(List<OrderRequestDto> orderRequestDtoList) {

        log.info("Updating stock");

        Map<Integer, Integer> quantityMap = orderRequestDtoList.stream()
                .collect(Collectors.toMap(
                        OrderRequestDto::getProductId,
                        OrderRequestDto::getQuantity));

        List<Product> productList = productRepository.findAllById(quantityMap.keySet());

        for(Product product : productList) {
            int requiredQuantity = quantityMap.get(product.getProductId());
            if (product.getStock() < requiredQuantity) {
                throw new InsufficientStockException(
                        "Required stock: " + requiredQuantity +
                                ", available stock: " + product.getStock()
                );
            }
            product.setStock(product.getStock() - requiredQuantity);
        }
    }

    @Transactional
    public void restoreInventory(List<OrderRequestDto> orderRequestDtoList) {

        log.info("Restoring stock");

        Map<Integer, Integer> quantityMap = orderRequestDtoList.stream()
                .collect(Collectors.toMap(
                        OrderRequestDto::getProductId,
                        OrderRequestDto::getQuantity));

        List<Product> productList = productRepository.findAllById(quantityMap.keySet());

        for(Product product : productList) {
            int requiredQuantity = quantityMap.get(product.getProductId());
            product.setStock(product.getStock() + requiredQuantity);
        }

        productRepository.saveAll(productList);
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + productId + " does not exist!"));
    }

    public List<Product> getAllProductsById(Set<Integer> productIdSet) {
        return productRepository.findAllById(productIdSet);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllProductsWithPagination(int startRow, int endRow) {
        return productRepository.findAll(startRow, endRow);
    }

}
