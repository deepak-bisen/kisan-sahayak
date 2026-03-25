package com.kisan.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.kisan.marketplace") // This tells Spring to find your Feign Clients
public class MarketplaceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceAppApplication.class, args);
    }
}