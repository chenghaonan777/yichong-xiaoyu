package com.exopet.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.exopet.auth", "com.exopet.user"},
        exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
public class ExopetAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExopetAuthApplication.class, args);
    }
}