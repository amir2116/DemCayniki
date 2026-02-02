package com.example.demcayniki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemCaynikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemCaynikiApplication.class, args);
    }

}
