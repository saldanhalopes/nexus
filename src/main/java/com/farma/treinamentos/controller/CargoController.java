package com.farma.treinamentos.controller;

import com.farma.treinamentos.model.Cargo;
import com.farma.treinamentos.repository.CargoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoRepository cargoRepository;

    public CargoController(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Cargo>> findAll() {
        return ResponseEntity.ok(cargoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> findById(@PathVariable String id) {
        return cargoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
