package com.example.OrderService;

import com.example.OrderService.client.inventoryServiceClient.InventoryServiceClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication implements CommandLineRunner {

	private final InventoryServiceClient client;

    public OrderServiceApplication(InventoryServiceClient client) {
        this.client = client;
    }

    public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
