package com.exopet.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class ExopetAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExopetAuthApplication.class, args);
    }
}
