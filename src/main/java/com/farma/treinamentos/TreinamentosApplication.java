 package com.farma.treinamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TreinamentosApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TreinamentosApplication.class, args);
    }
}