package com.exopet.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author 23278
 * @Date 2026/7/29 18:41
 * @PackageName:com.exopet.notification
 * @ClassName:ExopetNotificationApplication
 * @Description: TODO
 * @Version 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.exopet.notification","com.exopet.common"})
public class ExopetNotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExopetNotificationApplication.class,args);
    }
}

