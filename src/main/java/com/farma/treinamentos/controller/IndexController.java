package com.farma.treinamentos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
            "app", "Gestão de Treinamentos API",
            "version", "1.0.0",
            "status", "UP",
            "timestamp", LocalDateTime.now()
        );
    }
}
