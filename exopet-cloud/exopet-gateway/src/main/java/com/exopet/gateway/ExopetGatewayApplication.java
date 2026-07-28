package com.exopet.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ExopetGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExopetGatewayApplication.class, args);
    }
}
