package com.example.inventorygenius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
public class InventoryGeniusApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryGeniusApplication.class, args);
	}

}
