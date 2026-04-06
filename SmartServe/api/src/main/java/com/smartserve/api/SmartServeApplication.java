package com.smartserve.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartServeApplication {

    public static void main(String[] args) {
        // This line starts the entire SmartServe backend
        SpringApplication.run(SmartServeApplication.class, args);
        
        // Console confirmation message
        System.out.println("\n==========================================");
        System.out.println("   SMART SERVE BACKEND SYSTEM ONLINE      ");
        System.out.println("   Access: http://localhost:8080          ");
        System.out.println("==========================================\n");
    }
}
