package com.example.InventoryService;

import com.example.InventoryService.entity.Product;
import com.example.InventoryService.enums.ProductCategory;
import com.example.InventoryService.repository.ProductRepository;
import com.example.InventoryService.service.InventoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final InventoryService inventoryService;

	public InventoryServiceApplication(ProductRepository productRepository, InventoryService inventoryService) {
        this.productRepository = productRepository;
		this.inventoryService = inventoryService;
	}

    public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		productRepository.saveAll(List.of(
//				new Product(null, "Basketball", ProductCategory.SPORTS, 10.00, 4.5, 12),
//				new Product(null, "Football", ProductCategory.SPORTS, 12.00, 4.3, 15),
//				new Product(null, "Tennis Racket", ProductCategory.SPORTS, 45.00, 4.6, 8),
//				new Product(null, "Cricket Bat", ProductCategory.SPORTS, 30.00, 4.4, 10),
//				new Product(null, "Laptop", ProductCategory.ELECTRONICS, 800.00, 4.7, 5),
//				new Product(null, "Smartphone", ProductCategory.ELECTRONICS, 600.00, 4.5, 20),
//				new Product(null, "Headphones", ProductCategory.ELECTRONICS, 50.00, 4.2, 25),
//				new Product(null, "Keyboard", ProductCategory.ELECTRONICS, 25.00, 4.1, 30),
//				new Product(null, "Mouse", ProductCategory.ELECTRONICS, 15.00, 4.0, 40),
//				new Product(null, "Office Chair", ProductCategory.FURNITURE, 120.00, 4.3, 7),
//				new Product(null, "Dining Table", ProductCategory.FURNITURE, 300.00, 4.6, 3),
//				new Product(null, "Bed Frame", ProductCategory.FURNITURE, 250.00, 4.4, 4),
//				new Product(null, "Sofa", ProductCategory.FURNITURE, 500.00, 4.7, 2),
//				new Product(null, "T-shirt", ProductCategory.CLOTHING, 20.00, 4.1, 50),
//				new Product(null, "Jeans", ProductCategory.CLOTHING, 40.00, 4.3, 35),
//				new Product(null, "Jacket", ProductCategory.CLOTHING, 60.00, 4.5, 18),
//				new Product(null, "Sneakers", ProductCategory.CLOTHING, 70.00, 4.6, 22),
//				new Product(null, "Backpack", ProductCategory.ACCESSORIES, 35.00, 4.2, 27),
//				new Product(null, "Watch", ProductCategory.ACCESSORIES, 150.00, 4.4, 9),
//				new Product(null, "Sunglasses", ProductCategory.ACCESSORIES, 80.00, 4.3, 14)
//		));

	}
}
