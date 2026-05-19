package com.example.InventoryService.controller;

import com.example.InventoryService.dto.OrderRequestDto;
import com.example.InventoryService.dto.ProductDto;
import com.example.InventoryService.entity.Product;
import com.example.InventoryService.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(service.addProduct(productDto), HttpStatus.OK);
    }

    @PutMapping("/update-inventory")
    public ResponseEntity<Void> updateStock(@RequestBody List<OrderRequestDto> orderRequestDtoList) {
        log.info("Updating stock");
        service.updateStock(orderRequestDtoList);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore-inventory")
    public ResponseEntity<Void> restoreStock(@RequestBody List<OrderRequestDto> orderRequestDtoList) {
        log.info("Re-stocking");
        service.restoreInventory(orderRequestDtoList);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductId(@PathVariable Integer id) {
        return new ResponseEntity<>(service.getProductById(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/all/pagination")
    public ResponseEntity<List<Product>> getAllProductsWithPagination(@RequestParam int startRow, @RequestParam int endRow) {
        return new ResponseEntity<>(service.getAllProductsWithPagination(startRow, endRow), HttpStatus.OK);
    }

    @PostMapping("/all/by-id")
    public ResponseEntity<List<Product>> getAllProductsById(@RequestBody Set<Integer> productIdSet) {
        log.info("Retrieving products by ID");
        return new ResponseEntity<>(service.getAllProductsById(productIdSet), HttpStatus.OK);
    }
}
