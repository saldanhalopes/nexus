package com.farma.treinamentos.controller;

import com.farma.treinamentos.model.Departamento;
import com.farma.treinamentos.repository.DepartamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoController(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> findAll() {
        return ResponseEntity.ok(departamentoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamento> findById(@PathVariable String id) {
        return departamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
