package com.smartwatt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartWattApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartWattApplication.class, args);
    }
}
