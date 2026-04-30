package com.example.InventoryService.service;

import com.example.InventoryService.dto.OrderRequestDto;
import com.example.InventoryService.dto.ProductDto;
import com.example.InventoryService.entity.Product;
import com.example.InventoryService.exception.InsufficientStockException;
import com.example.InventoryService.exception.ResourceNotFoundException;
import com.example.InventoryService.mapper.ProductMapper;
import com.example.InventoryService.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<Product> productList = getAllProductsById(orderRequestDtoList.stream()
                .map(OrderRequestDto::getProductId)
                .collect(Collectors.toSet()));
        Set<Product> updatedProducts = productList.stream()
                .map(product -> {
                    int productQuantity = orderRequestDtoList.stream()
                            .filter(orderRequestDto -> orderRequestDto.getProductId() == product.getProductId())
                            .findFirst().get().getProductQuantity();
                    if (product.getStock() - productQuantity < 0)
                        throw new InsufficientStockException("Required stock: " + productQuantity + ", available stock: " + product.getStock());
                    product.setStock(product.getStock() - productQuantity);
                    return product;
                }).collect(Collectors.toSet());
        productRepository.saveAll(updatedProducts);
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
