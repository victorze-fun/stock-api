package com.victorze.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApplication {
    private static String age = "Victor";

	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
		System.out.println(age);
	}
}
